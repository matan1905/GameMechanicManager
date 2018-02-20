package com.opedea.mechanics;

import com.opedea.GameMechanicManager;
import com.opedea.datasource.DataSource;

public abstract class GameMechanic {
    private GameMechanicManager gameMechanicManager;

    /*
    Will be called when added to the GameMechanicManager.
    Usage should be registering all of the required event handlers.
    Examples:
    gmm.registerReceiver("items_add_new_item",new EventReceiver(){...})
    or, if other mechanics might use the same event name, use Common:
    gmm.registerCommonReceiver("monster_killed"), new EventReceiver(){...})
    */
    protected abstract void registerAllEvents(GameMechanicManager gmm);

    //called from the manager when added. You probably shouldn't use it.
    public final void addedToGameMechanicManager(GameMechanicManager gameMechanicManager, Settings settings, DataSource dataSource) {
        this.gameMechanicManager = gameMechanicManager;
        registerAllEvents(gameMechanicManager);
        loadSettings(settings);
        loadModels(dataSource);
    }

    /*
    Load all of the datasource models you have
    example usage (where item is a model):
    datasource.addModel(Item.class);
     */
    protected abstract void loadModels(DataSource dataSource);

    /*
    used to load all of the mechanic settings (in a String).
    --use prefix whenever possible to prevent intersection with other mechanics settings--
    Usage example:
    int maxItems = Integer.valueOf(settings.getSetting("prefix_maxItems","10"));
     */
    protected abstract void loadSettings(Settings settings);
    /*
    same as loadSettings, called whenever a user might change a setting mid-game
    Change all the settings that doesn't require a full restart of the game (or just forward it to loadSetting)
    try to avoid from updating useless settings here.
     */
    public abstract void reloadSettings(Settings settings);


    public GameMechanicManager getGameMechanicManager() {
        return gameMechanicManager;
    }

    public void tick(float delta) {
        //Override this to handle time events.
        //Also may be used to execute code on the main thread.
    }
}
