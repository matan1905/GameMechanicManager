package com.opedea.datasource;

import java.util.ArrayList;
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
    public <T extends Model> List<T> find(Class<T> aClass, String s, String[] strings, String s1, String s2) {
        ArrayList<T> list = new ArrayList<>();
        //a terrible way but it's just for testing amrite
        for (Model model : memory.get(aClass.getSimpleName())) {
            list.add((T)model);
        }
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
    public boolean SupportFindQueries() {
        return false;
    }

    @Override
    public <T extends Model> void addModel(Class<T> modelClass) {
        super.addModel(modelClass);
        memory.put(modelClass.getSimpleName(),new ArrayList<>());
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
