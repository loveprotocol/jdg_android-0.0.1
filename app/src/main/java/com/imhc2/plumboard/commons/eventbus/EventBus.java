package com.imhc2.plumboard.commons.eventbus;

/**
 * Created by user on 2018-09-06.
 */

public class EventBus {
    private static com.google.common.eventbus.EventBus eventBus;

    private EventBus(){}

    public static com.google.common.eventbus.EventBus get(){
        if(eventBus == null){
            eventBus = new com.google.common.eventbus.EventBus();
        }
        return eventBus;
    }

    public static void register(Object o){
        eventBus.register(o);
    }
    public static  void unregister(Object o){
        eventBus.unregister(o);
    }

    public static void post(Object event){
        eventBus.post(event);
    }
}
