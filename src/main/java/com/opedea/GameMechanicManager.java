package com.opedea;

import com.opedea.datasource.DataSource;
import com.opedea.mechanics.EventReceiver;
import com.opedea.mechanics.GameMechanic;
import com.opedea.mechanics.MechanicNotificationReceiver;
import com.opedea.mechanics.Settings;
import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The main class that handles all of the game mechanics and their events
 */
public class GameMechanicManager {

    private long prevTime;//Used for ticks
    private DataSource dataSource;
    private MechanicNotificationReceiver mechanicNotificationReceiver;

    //Holds a list of receivers for an event, used in common types of event that might have more than one receiver.
    private HashMap<String, ArrayList<EventReceiver>> commonEventReceiver = new HashMap<>();

    //Holds a single receiver for an event
    private HashMap<String, EventReceiver> eventReceiver = new HashMap<>();
    private Settings settings;
    private ArrayList<GameMechanic> mechanicList= new ArrayList<>();



    /**
     * creates a new instance of GameMechanicManager
     * @param dataSource a wrapper to a database of any sort
     * @param mechanicDrivenEventReceiver a receiving medium for mechanic induced events
     */
    public GameMechanicManager(DataSource dataSource, MechanicNotificationReceiver mechanicDrivenEventReceiver) {
       this(dataSource,mechanicDrivenEventReceiver,new Settings(dataSource));
    }



    /**
     * creates a new instance of GameMechanicManager
     * @param dataSource a wrapper to a database of any sort
     * @param mechanicDrivenEventReceiver  a receiving medium for mechanic induced events
     * @param settings a Setting object that responsible for settings
     */
    public GameMechanicManager(DataSource dataSource, MechanicNotificationReceiver mechanicDrivenEventReceiver,Settings settings) {
        this.dataSource = dataSource;
        this.mechanicNotificationReceiver = mechanicDrivenEventReceiver;
        this.settings=settings;
    }

    /**
     * sends a notification for more processing
     * @param eventName identifier of a notification
     * @param args any additional data
     */
    public void sendNotification(String eventName, Object... args) {
        if (mechanicNotificationReceiver == null) return;
        mechanicNotificationReceiver.receive(eventName, args);
    }


    /**
     * Fires an event, finds all suitable receivers to handle it.
     * @param eventName the event name
     * @param callback a function that will be called if any response is needed
     * @param arguments any additional data that the event might need
     */
    public void fireEvent(String eventName, @Nullable EventReceiver.Callback callback,@Nullable Object... arguments) {

        ArrayList<EventReceiver> listOfEvents = commonEventReceiver.get(eventName);
        if (listOfEvents != null) {
            for (EventReceiver event : listOfEvents) {
                event.receive(callback, arguments);
            }
        }

        EventReceiver singleEvent = eventReceiver.get(eventName);
        if (singleEvent != null) {
            singleEvent.receive(callback, arguments);
        }

    }


    /**
     * Fires an event in a different thread, be careful with the callback as it will be called from that different thread.
     * Make sure you read about thread safety and implement it properly before using this with a callback.
     * @param eventName the event name
     * @param callback a function that will be called if any response is needed
     * @param arguments any additional data that the event might need
     */
    public void fireEventAsnync(String eventName, EventReceiver.Callback callback, Object... arguments){
        new Thread(new Runnable() {
            @Override
            public void run() {
                fireEvent(eventName,callback,arguments);
            }
        }).run();
    }



    /**
     * ticker for mechanics passage of time, calculating delta alone
     */
    public void tick(){
        long newTime = System.currentTimeMillis();
        float delta =0;
        if(prevTime!=0){
             delta =(prevTime-newTime)/1000;}
        prevTime=newTime;
        tick(delta);
    }


    /**
     * Ticker for mechanics passage of time, with delta provided.
     * @param delta how much time (in seconds) has passed since the last tick. first tick should be 0.
     */
    public void tick(float delta){
        for (GameMechanic gameMechanic : mechanicList) {
            gameMechanic.tick(delta);
        }
    }

    /**
     * adds a mechanic to the game
     * @param mechanic the mechanic added
     */
    public void addMechanic(GameMechanic mechanic) {
        mechanic.addedToGameMechanicManager(this,settings,dataSource);
        this.mechanicList.add(mechanic);
    }


    /**
     * After changing a setting you probably want them to be changed in the mechanic object too.
     * Considering they don't always call getSetting, which is a bad practice
     */
    public void refreshSettings(){
        for (GameMechanic gameMechanic : mechanicList) {
            gameMechanic.reloadSettings(settings);
        }
    }


    /**
     *  register a unique event handler (override previous)
     * @param eventName event name  / identifier
     * @param receiver the receiver of that event to add
     */
    public void registerReceiver(String eventName, EventReceiver receiver) {
        eventReceiver.put(eventName, receiver);
    }

    /**
     *
     * @return Settings object
     */
    public Settings getSettings() {
        return settings;
    }



    /**
     * register a common event handler (append to previous)
     * @param eventName event name  / identifier
     * @param receiver the receiver of that event to add
     */
    public void registerCommonReceiver(String eventName, EventReceiver receiver) {
        if (commonEventReceiver.get(eventName) == null) {
            commonEventReceiver.put(eventName,new ArrayList<>());
        }
        commonEventReceiver.get(eventName).add(receiver);
    }
}
