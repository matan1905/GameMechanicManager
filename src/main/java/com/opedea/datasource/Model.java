package com.opedea.datasource;

import java.util.List;

public class Model {
    private static DataSource dataSource;
    private String id;
    public Model(){}//Used by datasource to instantiate new objects

    public static <T extends Model> List<T> findAll(Class<T> type){
        return dataSource.find( type, null, null, null, null);
    }
    public static <T extends Model> List<T> find(Class<T> type,String where,String[] whereArgs){
        return dataSource.find( type, where, whereArgs, null, null);
    }
    public static <T extends Model> List<T> find(Class<T> type,String where,String[] whereArgs ,String orderBy,String limit){
        return dataSource.find( type, where, whereArgs, orderBy, limit);
    }
    public static <T extends Model> T findById(Class<T> type,String id){
        return dataSource.findById( type, id);
    }

    public void save(){
        dataSource.save(this);
    }

    public void delete(){
        dataSource.delete(this);
    }

    public static void deleteAll(String where,String[] whereArgs){
        dataSource.deleteAll(where,whereArgs);
    }

    public static void setDataSource(DataSource datasource){
        dataSource=datasource;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
