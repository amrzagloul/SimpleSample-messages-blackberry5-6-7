package com.injoit.examplepush.push.screens;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.database.*;
import net.rim.device.api.io.*;
import net.rim.device.api.system.*;
import net.rim.device.api.util.*;
import net.rim.device.api.ui.decor.*;
import net.rim.device.api.ui.image.*;
import net.rim.device.api.collection.util.*;
import net.rim.blackberry.api.bbm.platform.*;
import net.rim.blackberry.api.bbm.platform.service.*;
import net.rim.blackberry.api.bbm.platform.profile.*;
import net.rim.blackberry.api.bbm.platform.ui.*;
import net.rim.blackberry.api.bbm.platform.io.*;
import net.rim.blackberry.api.bbm.platform.ui.chat.component.*;
import net.rim.blackberry.api.bbm.platform.ui.chat.*;
import net.rim.blackberry.api.bbm.platform.ui.chat.container.*;

import com.injoit.examplepush.bbm.controls.*;
import com.injoit.examplepush.bbm.constants.*;
import com.injoit.examplepush.push.models.PushNotificationObject;
import com.injoit.examplepush.push.storages.PushNotificationsStorage;

public class PushDetailScreen extends MainScreen 
{
    private PushDetailScreen me;
    private PushNotificationObject pNO;
    private PushNotificationsScreen pNS;  

    public PushDetailScreen(PushNotificationObject _pNO, PushNotificationsScreen _pNS)
    {
        super(NO_VERTICAL_SCROLL);
        me = this;
        pNO = _pNO;
        pNS = _pNS;
        
        pNO.setNotificationReadStatus(true);
        
        getMainManager().setBackground(BackgroundFactory.createSolidBackground(Constants.MAIN_BACKGROUND_COLOR));
        TitleBarManager titleMan = new TitleBarManager(Constants.PUSH_NOTIFICATIONS_SCREEN_TITLE);
        add(titleMan);        
        
        LabelField pushressievedLabel = new LabelField("Message ressieved on:")
        {
            public void paint(Graphics g)
            {
                g.setColor(0xffffff);
                super.paint(g);
            }
        };
        pushressievedLabel.setPadding(5,5,0,5);
        pushressievedLabel.setFont(pushressievedLabel.getFont().derive(Font.BOLD));
        add(pushressievedLabel);
        
        LabelField pustTimeLabel = new LabelField(pNO.getCreationTime())
        {
            public void paint(Graphics g)
            {
                g.setColor(0xffffff);
                super.paint(g);
            }
        };
        pustTimeLabel.setPadding(5,5,0,5);
        add(pustTimeLabel);
        
        LabelField pushTextLabel = new LabelField("Message text:")
        {
            public void paint(Graphics g)
            {
                g.setColor(0xffffff);
                super.paint(g);
            }
        };
        pushTextLabel.setPadding(5,5,0,5);
        pushTextLabel.setFont(pushTextLabel.getFont().derive(Font.BOLD));
        add(pushTextLabel);
        
        LabelField pusDescriptionLabel = new LabelField(pNO.getDescription())
        {
            public void paint(Graphics g)
            {
                g.setColor(0xffffff);
                super.paint(g);
            }
        };
        pusDescriptionLabel.setPadding(5,5,0,5);
        add(pusDescriptionLabel);
    };
    
    private MenuItem removePushNotification = new MenuItem("Remove message", 100, 100) 
    {
        public void run() 
        {
            try
            {
                PushNotificationsStorage.getInstance().removePushNotification(pNO);
                onClose();
            }
            catch (Exception ex)
            {
                System.out.println("removePushNotification exception = " + ex);
            }
        }
    }; 
    
    public void makeMenu(Menu menu, int instance) 
    {
        menu.add(removePushNotification);
        menu.add(MenuItem.separator(500));
        super.makeMenu(menu, instance);
    };
    
    public boolean onClose()
    {
        try
        {
            UiApplication.getUiApplication().invokeLater(new Runnable()
            {
                public void run()
                {
                    pNS.update();
                }
            });
            close();
            return true;
        }
        catch(Exception ex)
        {
            close();
            return false;
        }
    };    
}

