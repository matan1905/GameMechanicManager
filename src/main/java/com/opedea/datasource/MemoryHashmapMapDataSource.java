package com.opedea.datasource;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * a simple in-memory datasource designed for testing purposes
 * not meant for production.
 */
public class MemoryHashmapMapDataSource extends DataSource{

    HashMap<String, ArrayList<Model>> memory;
    HashMap<String,String> settings;

    public MemoryHashmapMapDataSource(){
        memory = new HashMap<>();
        settings= new HashMap<>();
    }

    @Override
    public <T extends Model> List<T> find(Class<T> type, Where where, String orderBy, int limit) {
        ArrayList<T> list = new ArrayList<>();
        int counter=0;
        for (Model model : memory.get(type.getSimpleName())) {
            boolean orToAdd=false;
            if(where !=null)
            for (ArrayList<Where.Clause> clauses : where.getWhereClauses()) {
                boolean andToAdd=true;
                for (Where.Clause clause : clauses) {
                    //andToAdd &= result of this clause
                    try {
                        Field leftF = type.getDeclaredField(clause.getLeft());
                        String leftType = leftF.getType().getSimpleName();
                        Object right;
                        if(leftType.equals("Integer")){
                            right = Integer.parseInt(clause.getRight());
                        }else if(leftType.equals("Float")){
                            right=Float.parseFloat(clause.getRight()) ;

                        }else if(leftType.equals("Double")){
                            right=Double.parseDouble(clause.getRight()) ;

                        }else if(leftType.equals("Boolean")){
                            right=Boolean.parseBoolean(clause.getRight()) ;
                        }
                        else{
                            right=clause.getRight();
                        }
                        leftF.setAccessible(true);
                        int result =((Comparable)leftF.get(model)).compareTo(right);

                        switch (clause.getType()){
                            case EQUAL_TO:andToAdd&= (result==0); break;
                            case LESS_THAN:andToAdd&= (result<0);break;
                            case GREATER_THAN:andToAdd&= (result>0);break;
                            case NOT_EQUAL_TO:andToAdd&= (result!=0);break;
                            case LESS_THAN_OR_EQUAL_TO:andToAdd&= (result<=0);break;
                            case GREATER_THAN_OR_EQUAL_TO:andToAdd&= (result>=0);break;
                        }

                    } catch (Exception e) {
                        throw new RuntimeException(e.getCause());
                    }
                }
                orToAdd |= andToAdd;
            }
            if(orToAdd || where==null) {
                list.add((T)model);
                counter++;
            }
            if(counter>limit && limit!=0) break;
        }
        list.sort(new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                try {
                    Field o1Field =o1.getClass().getDeclaredField(orderBy);
                    o1Field.setAccessible(true);
                    Field o2Field =o2.getClass().getDeclaredField(orderBy);
                    o2Field.setAccessible(true);
                    return ((Comparable)o1Field.get(o1)).compareTo(o2Field.get(o2));
                } catch (Exception e) {
                    throw new RuntimeException(e.getCause());
                }
            }
        });
        return list;
    }

    @Override
    public <T extends Model> T findById(Class<T> aClass, String s) {
        for (Model model : memory.get(aClass.getSimpleName())) {
            if(model.getId().equals(s)) return (T)model;
        }
        return null;
    }
    @Override
    public <T extends Model> void addModel(Class<T> modelClass) {
        super.addModel(modelClass);
        memory.put(modelClass.getSimpleName(),new ArrayList<>());
    }

    @Override
    public <T> void ensureIndex(Class<T> type, String index) {
    }

    @Override
    public void save(Model model) {
        //updating is automatic due to the power of reference
        if(!memory.get(model.getClass().getSimpleName()).contains(model))
            memory.get(model.getClass().getSimpleName()).add(model);
    }

    @Override
    public void delete(Model model) {
        if(memory.get(model.getClass().getSimpleName()).contains(model))
            memory.get(model.getClass().getSimpleName()).remove(model);
    }

    @Override
    public <T extends Model> void deleteAll(Class<T> type, String where, String[] whereArgs) {
        memory.replace(type.getSimpleName(),new ArrayList<>());

    }


    @Override
    public String findOrCreate(String key, String def) {
        return settings.getOrDefault(key, def);
    }

    @Override
    public boolean updateSetting(String s, String s1) {
        return settings.replace(s,s1)!=null;
    }



}
