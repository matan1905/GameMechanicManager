package com.opedea.mechanics;


//Used for when a mechanic wants to send information but doesn't have a valid callback to do so.
//For example - quests mechanic needs to notify when the player has finished\failed a quest
public interface MechanicNotificationReceiver {
    void receive(String eventName, Object... args);
}
