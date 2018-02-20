package com.opedea.mechanics;

public class Settings {

    private SettingSaver dataSource;

    public Settings(SettingSaver dataSource){

        this.dataSource = dataSource;
    }
    /*
    Finds the setting, if none found it will create it with the default value
     */
    public String getSetting(String settingName,String defaultValue){
        return dataSource.findOrCreate(settingName,defaultValue);
    }
    public void changeSetting(String settingName,String newValue){
        if(!dataSource.updateSetting(settingName,newValue))
            throw new RuntimeException("Error changing setting, Are you sure it exists?");
    }
    public interface SettingSaver{
        /*
        Tries to find a setting, if it cannot find it (create it and) return defaultValue
         */
        String findOrCreate(String setting,String defaultValue);

        /*
        changes a setting
        returns: if the setting was found and changed or if there was an error of some sort
         */
        boolean updateSetting(String setting,String newValue);
    }
}
