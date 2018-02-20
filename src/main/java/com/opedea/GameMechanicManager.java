package com.opedea;

import com.opedea.datasource.DataSource;
import com.opedea.mechanics.EventReceiver;
import com.opedea.mechanics.GameMechanic;
import com.opedea.mechanics.MechanicNotificationReceiver;
import com.opedea.mechanics.Settings;

import java.util.ArrayList;
import java.util.HashMap;

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

    /*
    Datasource - a wrapper to a database of any sort
    MechanicNotificationReciever - a receiving medium for mechanic induced events
     */
    public GameMechanicManager(DataSource dataSource, MechanicNotificationReceiver mechanicDrivenEventReceiver) {
       this(dataSource,mechanicDrivenEventReceiver,new Settings(dataSource));
    }

    /*
    Datasource - a wrapper to a database of any sort
    MechanicNotificationReciever - a receiving medium for mechanic induced events
    settings - a place that saves
     */
    public GameMechanicManager(DataSource dataSource, MechanicNotificationReceiver mechanicDrivenEventReceiver,Settings settings) {
        this.dataSource = dataSource;
        this.mechanicNotificationReceiver = mechanicDrivenEventReceiver;
        this.settings=settings;
    }
    //sends the notification, look at the interface for more details
    public void sendNotification(String eventName, Object... args) {
        if (mechanicNotificationReceiver == null) return;
        mechanicNotificationReceiver.receive(eventName, args);
    }

    /*
    Fires an event, finds all suitable receivers to handle it.
     */
    public void fireEvent(String eventName, EventReceiver.Callback callback, Object... arguments) {

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

    /*
    Fires an event in a different thread, be careful with the callback as it will be called from that different thread.
    Make sure you read about thread safety and implement it properly before using this with a callback.
     */
    public void fireEventAsnync(String eventName, EventReceiver.Callback callback, Object... arguments){
        new Thread(new Runnable() {
            @Override
            public void run() {
                fireEvent(eventName,callback,arguments);
            }
        }).run();
    }

    /*
    ticker for mechanics passage of time, calculating delta alone
     */
    public void tick(){
        long newTime = System.currentTimeMillis();
        float delta =0;
        if(prevTime!=0){
             delta =(prevTime-newTime)/1000;}
        prevTime=newTime;
        tick(delta);
    }
    /*
    Ticker for mechanics passage of time, with delta provided.
    Delta - how much time (in seconds) has passed since the last tick. first tick should be 0.
     */
    public void tick(float delta){
        for (GameMechanic gameMechanic : mechanicList) {
            gameMechanic.tick(delta);
        }
    }
    /*
    registers a mechanic into the game
     */
    public void addMechanic(GameMechanic mechanic) {
        mechanic.addedToGameMechanicManager(this,settings,dataSource);
        this.mechanicList.add(mechanic);
    }
    /*
    After changing a setting you probably want them to be changed in the mechanic object too.
    Considering they don't always call getSetting, which is a bad practice
     */
    public void refreshSettings(){
        for (GameMechanic gameMechanic : mechanicList) {
            gameMechanic.reloadSettings(settings);
        }
    }
    /*
    register a unique event handler (override previous)
     */
    public void registerReceiver(String eventName, EventReceiver receiver) {
        eventReceiver.put(eventName, receiver);
    }

    public Settings getSettings() {
        return settings;
    }

    /*
        register a common event handler (append to previous)
         */
    public void registerCommonReceiver(String eventName, EventReceiver receiver) {
        if (commonEventReceiver.get(eventName) == null) {
            commonEventReceiver.put(eventName,new ArrayList<>());
        }
        commonEventReceiver.get(eventName).add(receiver);
    }
}
