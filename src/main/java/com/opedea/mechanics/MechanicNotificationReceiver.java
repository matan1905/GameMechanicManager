package com.opedea.mechanics;




/**
 *Used for when a mechanic wants to send information but doesn't have a valid callback to do so.
 *For example - quests mechanic needs to notify when the player has finished\failed a quest
 */
public interface MechanicNotificationReceiver {
    /**
     * receives the notification and use it
     * @param eventName an identifier for the notification
     * @param args any additional data that needs to be sent
     */
    void receive(String eventName, Object... args);
}
