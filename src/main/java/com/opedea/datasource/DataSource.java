package com.opedea.datasource;


import com.opedea.mechanics.Settings;
import com.sun.istack.internal.Nullable;

import java.lang.reflect.Method;
import java.util.List;

/**
 * A data source stores all the value and can issue search queries and data manipulation
 */
public abstract class DataSource implements Settings.SettingSaver {


    /**
     * execute a find query, cast all results into type and return a list of said casted results
     * @param type The class of the model to find
     * @param whereClause any specific where filters, Usage example: "size > ?" where ? is argument
     * @param whereArgs replaces every ? in the whereClause by the appropriate argument
     * @param orderBy expects a model field to order by, for example "age"
     * @param limit String repesentation of a limit, simply the maximum of results in the list
     * @param <T>  model being queried
     * @return a list of models
     */
    public abstract  <T extends Model> List<T> find(Class<T> type, @Nullable String whereClause
            ,@Nullable String[] whereArgs,@Nullable String orderBy,@Nullable String limit);

    /**
     *    Find and cast a model using Id
     * @param type the class of the model finding
     * @param id the id of the desired model
     * @param <T> the model to find
     * @return a single model or null
     */
    public abstract <T extends Model> T findById(Class<T> type, String id);

    /**
     * Checks if the datasoruce uses the additional sql queries in deleteAll and find
     * @return can you run additional sql queries (where, order, limit).
     *
     */
    public abstract boolean SupportFindQueries();

    /**
     * Runs a full query if supported
     * @param query the query to run
     * @return query success or failure, defaults for false.
     */
    public boolean query(String query){
        return false;
    }

    /**
     *  Using reflection to manually add.
     * one could use the Model.setDatasource(db) directly, but using it this way
     * allow for special behavior if required (such as making sure the table is created & valid for it in SQL based source)
     * @param modelClass The class of the model to add
     * @param <T> any model
     */
    public  <T extends Model> void addModel(Class<T> modelClass){
        try {
            Method method = modelClass.getMethod("setDataSource", DataSource.class);
            method.invoke(null,this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Updates/Saves the model to the data source
     * @param model the model to save / update
     */
    public abstract void save(Model model);



    /**
     * delete the model from a data source (if id!=null)
     * @param model the model to be deleted.
     */
    public abstract void delete(Model model);



    /**
     * delete all models using a where query
     * @param type the class of the deleted model
     * @param where any specific where filters, Usage example: "size > ?" where ? is argument
     * @param whereArgs replaces every ? in the whereClause by the appropriate argument
     */
    public abstract  <T extends Model>  void deleteAll(Class<T> type,String where, String[] whereArgs);


}
