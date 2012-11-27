package com.injoit.examplepush.push.screens;

import com.injoit.examplepush.containers.*;
import com.injoit.examplepush.push.models.PushNotificationObject;
import com.injoit.examplepush.push.storages.PushNotificationsStorage;
import com.injoit.examplepush.push.screens.PushNotificationsScreen;

import java.util.*;
import java.io.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.database.*;
import net.rim.device.api.io.*;
import net.rim.device.api.system.*;
import net.rim.device.api.util.*;
import net.rim.device.api.ui.decor.*;
import net.rim.device.api.crypto.*; 
import net.rim.device.api.collection.util.*;

public class PushSimulateSettingsScreen //extends PopupScreen implements FieldChangeListener
{    
   /* private PushSimulateSettingsScreen me;
    private VerticalFieldManager listManager = new VerticalFieldManager(Manager.USE_ALL_HEIGHT|Manager.USE_ALL_WIDTH|Manager.VERTICAL_SCROLL);
    
    private TextField pushTextField;
    private TextField pushDelayField;
    private ButtonField startPushSimButton;
    
    public PushSimulateSettingsScreen()
    {
        super(new VerticalFieldManager(),Field.FOCUSABLE);
        me = this;
        
        add(new LabelField("Text of Push:"));
        pushTextField = new TextField(TextField.NO_COMPLEX_INPUT | TextField.NO_NEWLINE);
        pushTextField.setText("Test push message");
        add(pushTextField);
        add(new LabelField("Push delay in milisec:"));
        pushDelayField = new TextField(TextField.NO_COMPLEX_INPUT | TextField.NO_NEWLINE);
        pushDelayField.setText("5000");
        add(pushDelayField);
        
        startPushSimButton = new ButtonField("Start push simulation ", ButtonField.CONSUME_CLICK | ButtonField.NEVER_DIRTY | FIELD_HCENTER );
        startPushSimButton.setChangeListener(me);
        add(startPushSimButton);

        startPushSimButton.setFocus();
    }
   
    public void fieldChanged(Field field, int context)
    {
        if(field == startPushSimButton)
        {
            PushSimulationTread pST = new PushSimulationTread(pushTextField.getText(), Integer.parseInt(pushDelayField.getText()));
            pST.start();
            onClose();
        }
    }
    
    public boolean onClose()
    {
        Object obj = null;
        
        UiApplication.getUiApplication().invokeLater(new Runnable() 
        {
            public void run() 
            {
                UiApplication.getUiApplication().popScreen(me);
            }
        });
        return true;
    };
    
    
     private static class PushSimulationTread extends Thread {

                private boolean running;
                private int pushDelay;
                private String pushText;
                
                public PushSimulationTread(String _pushText, int _pushDelay)
                {
                    this.running = true;
                    this.pushDelay = _pushDelay;
                    this.pushText = _pushText;
                }

                public void run() {                        
                    
                    while (running)
                    {
                        try
                        {
                            for (int i = 0; i < pushDelay; i++)
                            {
                                System.out.println("------i = " + i);
                            } 
                            
                            try
                            {
                                PushNotificationObject pno = new PushNotificationObject(pushText);
                                PushNotificationsStorage.getInstance().addPushNotification(pno);
                            }
                            catch (Exception ex)
                            {
                                System.out.println("<addPushNotification> Exception = " + ex);
                            }
                              
                            UiApplication.getUiApplication().invokeLater(new Runnable()
                            {
                                public void run()
                                {
                                    Screen screen = UiApplication.getUiApplication().getActiveScreen();
                                    if (screen instanceof PushNotificationsScreen)
                                    {
                                        PushNotificationsScreen pns = (PushNotificationsScreen) screen;
                                        pns.update();
                                    }
                                }
                            });
                            
                            UiApplication.getUiApplication().invokeLater(new Runnable()
                            {
                                public void run()
                                {
                                    Dialog.alert("Push Notification: " + "\n" + pushText);                  
                                }
                            });
                            
                            running = false;
                        } 
                        catch (Exception e)
                        {
                            if (running)
                            {
                                running = false;
                            }
                        }
                    }
                }
            }; */
}
