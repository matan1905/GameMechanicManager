package com.opedea.datasource;

import java.util.List;

/**
 * Model is a base class for data manipulation and search
 * extend this when you want to create your own data type
 */
public class Model {
    private static DataSource dataSource;
    private String id;

    /**
     * Used by datasource to instantiate new objects, if you extend, please provide an empty constructor as well
     */
    public Model(){}

    /**
     * finds all of the objects of type T.
     * Example usage:
     * List<Items> items =Items.findAll(Items.class);
     * @param type class of the model to find
     * @param <T> the model to find
     * @return a list of T models
     */
    public static <T extends Model> List<T> findAll(Class<T> type){
        return dataSource.find( type, null, null, 0);
    }

    /**
     * finds all of the objects of type T with a where clause
     * Example usage:
     * List<Items> items =Items.find(Items.class,"quantity>?",new String[]{"1"});
     * @param type class of the model to find
     * @param where where clauses
     * @param <T> the model to find
     * @return list of models
     */
    public static <T extends Model> List<T> find(Class<T> type,Where where){
        return dataSource.find( type, where, null, 0);
    }

    /**
     * finds all of the objects of type T with a where clause
     * Example usage:
     * List<Items> items =Items.find(Items.class,"quantity>?",new String[]{"1"},"name","100");
     * @param type class of the model to find
     * @param where where clauses
     * @param orderBy a field in the model to order by it
     * @param limit maximum amount of items in the returned list
     * @param <T> the model type
     * @return list of models
     */
    public static <T extends Model> List<T> find(Class<T> type,Where where ,String orderBy,int limit){
        return dataSource.find( type, where, orderBy, limit);
    }

    /**
     *
     * @param type class of the model to find
     * @param id id of the model to find
     * @param <T> the model type
     * @return single model or null
     */
    public static <T extends Model> T findById(Class<T> type,String id){
        return dataSource.findById( type, id);
    }

    /**
     * save / updates the model
     */
    public void save(){
        dataSource.save(this);
    }

    /**
     * delete the model
     */
    public void delete(){
        dataSource.delete(this);
        setId(null);
    }

    /**
     * Deletes all models of this type
     * Example Item.deleteAll(Item.class,"name =?",new String[]{"Super sword"})
     * @param type The type of model to delete
     * @param where where clauses
     * @see Where
     */
    public static <T extends Model> void deleteAll(Class<T> type,Where where){
        dataSource.deleteAll(type,where);
    }

    /**
     * sets the datasource, used directly from the datasource
     * @param datasource datasource to set
     */
    public static void setDataSource(DataSource datasource){
        dataSource=datasource;
    }

    /**
     *
     * @return id of this model or null
     */
    public String getId() {
        return id;
    }

    /**
     * sets the id
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
}
