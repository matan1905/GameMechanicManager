package com.opedea.DesktopSQLiteDatasource;

import com.opedea.datasource.DataSource;
import com.opedea.datasource.Model;
import com.opedea.datasource.Where;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Using JDBC with SQLite Driver, you can download the driver jar online to use this, however it doesn't support android.
 */
public class DesktopSQLiteDatasource extends DataSource {

    private Connection connection;

    /**
     *
     * @param dbName The database name, use null or empty to use memory db
     */
    public DesktopSQLiteDatasource(String dbName){

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Please install org.sqlite.JDBC driver or choose a different datasource");
        }


         //connection = null;
        try
        {
            // create a database connection
            if(dbName==null || dbName.isEmpty())
                connection = DriverManager.getConnection("jdbc:sqlite::memory:");
            else
                connection = DriverManager.getConnection("jdbc:sqlite:"+dbName+".db");
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e.getCause());
        }

        addModel(SettingModel.class);
    }

    @Override
    public <T extends Model> void addModel(Class<T> modelClass) {
        super.addModel(modelClass);
            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE IF NOT EXISTS ")
                    .append(modelClass.getSimpleName())
                    .append(" (id, ");
            for (Field field : modelClass.getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) continue;
                sb.append(field.getName())
                        .append(", ");
            }
            sb.setLength(sb.length()-2);//remove the last comma-space. i find it simpler
            sb.append(")");
            query(sb.toString());
            ensureIndex(modelClass,"id");

    }

    @Override
    public <T extends Model> List<T> find(Class<T> type, Where where, String orderBy, int limit) {
        ArrayList<T> results = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT * FROM ")
                    .append(type.getSimpleName());
            if(where !=null){
                sb.append(" ").append(where.generateSQLQuery());
            }
            if(orderBy!=null &&!orderBy.isEmpty()){
                sb.append(" ORDER BY ")
                        .append(orderBy);
            }
            if(limit>0){
                sb.append(" LIMIT ")
                        .append(limit);
            }
            ResultSet rs = statement.executeQuery(sb.toString());
            while(rs.next())
            {
                T m =extractModel(type,rs);
                m.setId("'"+rs.getString("id")+"'");
                results.add(m);
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(),e.getCause());
        }    }

    @Override
    public <T extends Model> T findById(Class<T> type, String id) {
        Where where = new Where();
        List<T> result = find(type, where.or(Where.Clause.eq("id",id)), null, 0);
        if(result.size()>0)return result.get(0);
        return null;
    }

    protected  <T extends Model> T extractModel(Class<T> type,ResultSet rs){
        try{
            T model =type.newInstance();
        for (Field field : type.getDeclaredFields()) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) continue;

            field.setAccessible(true);
            field.set(model, rs.getObject(field.getName()));
        }
        return model;

        }
        catch (SQLException e){
            throw new RuntimeException(e.getCause());
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e.getMessage() + "(Please create an empty constructor in your model, it might be the reason.)",e.getCause());
        }
    }


    @Override
    public <T> void ensureIndex(Class<T> type, String index) {
        query("CREATE INDEX "+type.getSimpleName()+"_"+index+" ON "+type.getSimpleName()+"("+index+")");
    }

    @Override
    public boolean query(String query) {
        try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            statement.executeUpdate(query);
            statement.closeOnCompletion();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(),e.getCause());
        }
        return  true;
    }

    @Override
    public void save(Model model) {
        StringBuilder sb = new StringBuilder();

        try {
            Field[] fields = model.getClass().getDeclaredFields();
            Object[] objects = new Object[fields.length];
            if(model.getId()==null) {
                String id="'id_"+System.nanoTime()+"'";
                sb.append("INSERT INTO ")
                        .append(model.getClass().getSimpleName())
                        .append("(id, ");
                for (int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) continue;
                    sb.append(field.getName())
                            .append(", ");
                    field.setAccessible(true);
                    objects[i] = field.get(model);
                }
                sb.setLength(sb.length() - 2);
                sb.append(") VALUES (")
                        .append(id)
                        .append(", ");
                for (int i = 0; i < objects.length; i++) {
                    sb.append("?, ");

                }
                sb.setLength(sb.length() - 2);
                sb.append(")");
                model.setId(id);
            }else{
                sb.append("UPDATE ")
                        .append(model.getClass().getSimpleName())
                        .append(" SET ");
                for (int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) continue;
                    sb.append(field.getName())
                            .append("=?")
                            .append(", ");
                    field.setAccessible(true);
                    objects[i] = field.get(model);
                }
                sb.setLength(sb.length() - 2);
                sb.append(" WHERE id=").append(model.getId());

            }

            PreparedStatement ps = connection.prepareStatement(sb.toString());
            for (int i = 0; i < objects.length; i++) {
                ps.setObject(i+1,objects[i]);
            }
            ps.executeUpdate();
        } catch (SQLException | IllegalAccessException e) {
            System.out.println(sb.toString());
            throw new RuntimeException(e.getMessage());
        }


    }

    @Override
    public void delete(Model model) {
        if(model.getId()!=null){
            String sb = "DELETE FROM " +
                    model.getClass().getSimpleName() +
                    " WHERE id=" +
                    model.getId();
            query(sb);
        }
        else System.out.println("Attempt to delete a none existent model");
    }

    @Override
    public <T extends Model> void deleteAll(Class<T> type, Where where) {
        String sb = "DELETE FROM " +
                type.getSimpleName() +
                " " +
                where.generateSQLQuery();
        query(sb);

    }

    @Override
    public String findOrCreate(String setting, String defaultValue) {
        List<SettingModel> s=SettingModel.find(SettingModel.class,new Where().or(Where.Clause.eq("name","'"+setting+"'")));
        if(s.size()>0) return s.get(0).value;
        SettingModel sm = new SettingModel();
        sm.name=setting;
        sm.value=defaultValue;
        sm.save();
        return defaultValue;
    }

    @Override
    public boolean updateSetting(String setting, String newValue) {
        List<SettingModel> s=SettingModel.find(SettingModel.class,new Where().or(Where.Clause.eq("name","'"+setting+"'")));
        if(s.size()>0){
        s.get(0).value=newValue;
        s.get(0).save();
        return true;
        }
        return false;
    }

    public static class SettingModel extends Model{
        String name;
        String value;
    }
}
