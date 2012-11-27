package com.injoit.examplepush;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;

import com.injoit.examplepush.push.screens.PushNotificationsScreen;
import com.injoit.examplepush.push.*;
import com.injoit.examplepush.bbm.datastorages.OptionStorage;

/**
 * This class is the start point of program examplepush
 */
public class Main extends UiApplication {
    private static PushAgent pA;
    
    public static void main(String[] args) {
        // Create a new instance of the application.
        Main theApp = new Main();
        // To make the application enter the event thread and start, processing messages,
        // we invoke the enterEventDispatcher() method.
        theApp.enterEventDispatcher();
    }
    
    /**
     * Public constructor
     */
    public Main() {
        pushScreen(new PushNotificationsScreen());
        PushQbAuth pQbSubscr = new PushQbAuth();
        OptionStorage.getInstance().CreateOptions();
        //start thread that is listening push notifications
        pA = new PushAgent();
        OptionStorage.getInstance().getOption("Push notifications").setCurrentValueIndex(0);
        pQbSubscr.subscribeOnQbPush();
    } 
    
    public static void close() {
        pA.stopListeningPushMessages();
    }
}
