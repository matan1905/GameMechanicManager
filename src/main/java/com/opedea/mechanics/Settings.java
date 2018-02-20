package com.opedea.mechanics;

/**
 * Key-value settings
 */
public class Settings {

    private SettingSaver dataSource;

    /**
     * constructor
     * @param dataSource a datasource of any sort to save settings in
     */
    public Settings(SettingSaver dataSource){

        this.dataSource = dataSource;
    }

    /**
     * Finds the setting, if none found it will create it with the default value
     * @param settingName the setting name to be found
     * @param defaultValue the default value in case it wasn't found
     * @return the settings value
     */
    public String getSetting(String settingName,String defaultValue){
        return dataSource.findOrCreate(settingName,defaultValue);
    }

    /**
     * change a setting, if it wasn't found throw exception
     * @param settingName the name of setting to find
     * @param newValue the value to change the setting to
     */
    public void changeSetting(String settingName,String newValue) throws Exception {
        if(!dataSource.updateSetting(settingName,newValue))
            throw new SettingChangeException("Error changing setting, Are you sure it exists?");
    }
    public interface SettingSaver{

        /**
         * Tries to find a setting, if it cannot find it (create it and) return defaultValue
         * @param setting the setting name to try and find
         * @param defaultValue the default value to return if it wasnt found
         * @return the value of the setting
         */
        String findOrCreate(String setting,String defaultValue);



        /**
         * changes a setting
         * @param setting the setting to change
         * @param newValue the value of the setting to change to
         * @return if the setting was found and changed or not
         */
        boolean updateSetting(String setting,String newValue);
    }

    public class SettingChangeException extends Exception{
        public SettingChangeException(String s){super(s);}
    }
}
