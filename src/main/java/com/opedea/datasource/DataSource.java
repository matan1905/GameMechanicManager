package com.opedea.datasource;


import com.opedea.mechanics.Settings;
import com.sun.istack.internal.Nullable;

import java.lang.reflect.Method;
import java.util.List;


public abstract class DataSource implements Settings.SettingSaver {

    /*
        execute a find query, cast all results into type and return a list of said casted results
         */
    public abstract  <T extends Model> List<T> find(Class<T> type, @Nullable String whereClause
            ,@Nullable String[] whereArgs,@Nullable String orderBy,@Nullable String limit);
    //Find and cast a model using Id
    public abstract <T extends Model> T findById(Class<T> type, String id);

    /*
    Using reflection to manually add.
    one could use the Model.setDatasource(db) directly, but using it this way
    allow for special behavior if required (such as making sure the table is created & valid for it in SQL based source)
     */
    public  <T extends Model> void addModel(Class<T> modelClass){
        try {
            Method method = modelClass.getMethod("setDataSource", DataSource.class);
            method.invoke(null,this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
      Updates/Saves the model to the data source
     */
    public abstract void save(Model model);

    /*
    delete the model from a data source (if id!=null)
     */
    public abstract void delete(Model model);

    /*
    delete all models using a where query
     */
    public abstract void deleteAll(String where, String[] whereArgs);


}
