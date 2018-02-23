package com.opedea.DesktopSQLiteDatasource;


import com.opedea.datasource.Model;
import com.opedea.datasource.Where;

import java.lang.reflect.Field;

public class MainTests {
    public static void main(String[] args){
        DesktopSQLiteDatasource ds = new DesktopSQLiteDatasource(null);
        ds.addModel(TestModel.class);

        System.out.println(ds.findOrCreate("camels","yes"));
        System.out.println(ds.findOrCreate("camels","no"));
        System.out.println(ds.updateSetting("camels","definatly yes"));
        System.out.println(ds.findOrCreate("camels","definatly no"));

    }

    public static class TestModel extends Model{
        String name;
        int amount;

        public TestModel(String name, int amount) {
            this.name = name;
            this.amount = amount;
        }
        public TestModel(){}
    }
}
