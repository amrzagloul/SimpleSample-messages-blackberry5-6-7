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

import com.injoit.examplepush.Main;
import com.injoit.examplepush.bbm.controls.*;
import com.injoit.examplepush.bbm.constants.*;
import com.injoit.examplepush.containers.*;
import com.injoit.examplepush.bbm.containers.VerticalScrollManager;
import com.injoit.examplepush.push.models.PushNotificationObject;
import com.injoit.examplepush.push.storages.PushNotificationsStorage;
import com.injoit.examplepush.push.controls.PushNotificationField;
import com.injoit.examplepush.tvguide.controls.*;
import com.injoit.examplepush.push.*;
import com.injoit.examplepush.bbm.datastorages.OptionStorage;

public class PushNotificationsScreen extends MainScreen implements FieldChangeListener, ObjectSelectorListener
{
    private Main app; 
    private PushQbAuth pQbSubscr;
    private PushNotificationsScreen me;   
    private static final String MESSAGE_STATISTICS_LABEL = "Message Statistics";
    private static final String MESSAGE_COUNT_LABEL = "Message Count (unread/total): ";
    private static final String LAST_MESSAGE_LABEL = "Last message on: ";

    private LabelField messageStatLabel;
    private LabelField messageCountLabel;
    private LabelField timeLabel;
    private int amountUnreadNotifications = 0;
    private  VerticalScrollManager vman = new VerticalScrollManager();
    private ListStyleButtonSet set = new ListStyleButtonSet();
    private SimpleSortingVector pnos = null;
    private PushNotificationField pNF = null;
    private PushNotificationObject selectedPNO = null; 

    private ButtonField viewMessages;
    private BigVector menuObjects;
    private Bitmap view_details;
    private Bitmap remove_message;
    private int pushNotificationOption;

    public PushNotificationsScreen()
    {
        super(NO_VERTICAL_SCROLL);
        //app = _app;
        me = this;
        
        view_details = Bitmap.getBitmapResource("view_details.png");
        remove_message = Bitmap.getBitmapResource("view_details.png");
        
        menuObjects = new BigVector();
        menuObjects.addElement(new TVGuideListMenuObject(view_details, "View details"));
        menuObjects.addElement(new TVGuideListMenuObject(remove_message, "Remove message"));
   
        getMainManager().setBackground(BackgroundFactory.createSolidBackground(Constants.MAIN_BACKGROUND_COLOR));
        TitleBarManager titleMan = new TitleBarManager(Constants.PUSH_NOTIFICATIONS_SCREEN_TITLE);
        add(titleMan);
        
        messageStatLabel = new LabelField(MESSAGE_STATISTICS_LABEL)
        {
            public void paint(Graphics g)
            {
                g.setColor(0xffffff);
                super.paint(g);
            }
        };
        messageStatLabel.setPadding(5,5,0,5);
        messageStatLabel.setFont( messageStatLabel.getFont().derive( Font.BOLD ) );
        add(messageStatLabel);
        
        messageCountLabel = new LabelField( MESSAGE_COUNT_LABEL + 0 + "/" + 0 )
        {
            public void paint(Graphics g)
            {
                g.setColor(0xffffff);
                super.paint(g);
            }
        };
        messageCountLabel.setPadding(0,5,0,5);
        add(messageCountLabel);
        
        timeLabel = new LabelField( LAST_MESSAGE_LABEL + "-" )
        {
            public void paint(Graphics g)
            {
                g.setColor(0xffffff);
                g.drawLine(0, 50,100, 100);
                super.paint(g);
            }
        };
        timeLabel.setPadding(0,5,5,5);
        add(timeLabel);
        
        add(new SeparatorField());
        showNotifications();
        
    }
    
    private void showNotifications()
    {
        try
        {
            pnos = PushNotificationsStorage.getInstance().getPushNotifications();
            try 
            {
                amountUnreadNotifications = PushNotificationsStorage.getInstance().getAmountUnreadNotifications();
            }
            catch (Exception ex)
            {
                System.out.println("<getAmountUnreadNotifications exception> = " + ex);    
            }
            messageCountLabel.setText( MESSAGE_COUNT_LABEL + amountUnreadNotifications + "/" + pnos.size());            
            if( pnos.size() != 0 )
            {
                PushNotificationObject pNO = (PushNotificationObject) pnos.elementAt(0);
                timeLabel.setText( LAST_MESSAGE_LABEL + pNO.getCreationTime() );
            }
            else
            {
                timeLabel.setText( LAST_MESSAGE_LABEL + "-" );
            }
                        
            if (vman.getFieldCount() == 0) 
            {
                UiApplication.getUiApplication().invokeLater(new Runnable() 
                {
                    public void run() 
                    {
                        for (int j = 0; j < pnos.size(); j++)
                        {
                            PushNotificationObject pNO = (PushNotificationObject) pnos.elementAt(j);
                            
                            pNF = new PushNotificationField(pNO);
                            pNF.setEditable(true);
                            pNF.setChangeListener(me);
                            set.add(pNF); 
                        }
                        vman.add(set);
                        add(vman);
                    }
                });
            }
            else
            {
                UiApplication.getUiApplication().invokeLater(new Runnable() 
                {
                    public void run() 
                    {
                        set.deleteAll();
                        vman.deleteAll();
                        for (int j = 0; j < pnos.size(); j++)
                        {
                            PushNotificationObject pNO = (PushNotificationObject) pnos.elementAt(j);
                            pNF = new PushNotificationField(pNO);
                            pNF.setEditable(true);
                            pNF.setChangeListener(me);
                            set.add(pNF); 
                        }
                        vman.add(set);
                    }
                });                
            }
        if (pnos.size() == 0)
            {
                UiApplication.getUiApplication().invokeLater(new Runnable() 
                {
                    public void run() 
                    {
                        LabelField noMessageLabel = new LabelField(" *No push notification messages* ")
                        {
                            public void paint(Graphics g)
                            {
                                g.setColor(0xffffff);
                                super.paint(g);
                            }
                        };
                        noMessageLabel.setPadding(5,5,0,5);
                        noMessageLabel.setFont( messageStatLabel.getFont().derive( Font.BOLD ) );
                        vman.add(noMessageLabel);
                    }
                });
            }
        }
        catch (Exception ex)
        {
            System.out.println("<getPushNotifications() exception> = " + ex);    
        }
    };
    
    public void makeMenu(Menu menu, int instance) 
    {
        int pushNotCounter = 0;
        try
        {
            pnos = PushNotificationsStorage.getInstance().getPushNotifications();
            pushNotCounter = pnos.size();
        }
        catch (Exception ex)
        {
            System.out.println("<PushNotificationScrteen> getPushNotifications exception = " + ex);
        }
        
        System.out.println("----- pushNotCounter = " + pushNotCounter);
        
        if (pushNotCounter != 0)
        {
            menu.add(viewDetailsMenuItem);
            menu.add(removeMessageMenuItem);
        }
        menu.add(MenuItem.separator(50));
        //menu.add(simPushMenuItem);
        menu.add(MenuItem.separator(80));
        // turn off/on
        pQbSubscr = new PushQbAuth();
        OptionStorage.getInstance().CreateOptions();
        //start thread that is listening push notifications
        if ((OptionStorage.getInstance().getOption("Push notifications").getCurrentValueIndex()) == 0) {
            menu.add(turnOffPushMenuItem);             
        } 
        if ((OptionStorage.getInstance().getOption("Push notifications").getCurrentValueIndex()) == 1) {
            menu.add(turnOnPushMenuItem);             
        }
        super.makeMenu(menu, instance);
    }
    
    private MenuItem viewDetailsMenuItem = new MenuItem("View details", 10, 10) 
    {
        public void run() 
        {
            UiApplication.getUiApplication().pushScreen(new PushDetailScreen(selectedPNO, me));
        }
    };
    
    private MenuItem removeMessageMenuItem = new MenuItem("Remove message", 20, 20) 
    {
        public void run() 
        {
            removeMessageProcessing();
        }
    };    
    
    /*private MenuItem simPushMenuItem = new MenuItem("Simulate push", 100, 100) 
    {
        public void run() 
        {
            UiApplication.getUiApplication().pushScreen(new PushSimulateSettingsScreen());
        }
    };*/
    
    private MenuItem turnOnPushMenuItem = new MenuItem("Turn on push", 120, 120) 
    {
        public void run() 
        {
            pQbSubscr.subscribeOnQbPush();
        }
    };  
    
    private MenuItem turnOffPushMenuItem = new MenuItem("Turn off push", 120, 120) 
    { 
        public void run() 
        {
            System.out.println("----- before TURNOFF ------ " + OptionStorage.getInstance().getOption("Push notifications").getCurrentValueIndex());
            OptionStorage.getInstance().getOption("Push notifications").setCurrentValueIndex(1);
            UnsubscribeQbPush unsubscrQbPush = new UnsubscribeQbPush();
            unsubscrQbPush.unSubscribeFromQbPush();
            System.out.println("----- after TURNOFF ------ " + OptionStorage.getInstance().getOption("Push notifications").getCurrentValueIndex());
        }
    }; 
    
    public void fieldChanged(Field field, int context)
    {
        if(field instanceof PushNotificationField)
        {
            PushNotificationField pnf = (PushNotificationField) field;
            selectedPNO = pnf.getPushNotificationObject();
            new ObjectSeclectorScreen(me, menuObjects, "");
        }
    }
    
    private void removeMessageProcessing()
    {
        int dialogChoice = Dialog.ask("Are you sure, you want to remove this message ?", new String[]{"No", "Yes"}, 0);
        try
        {
            switch(dialogChoice)
            {
                case 1: //Remove message
                {
                    PushNotificationsStorage.getInstance().removePushNotification(selectedPNO); 
                    update();
                }
            }
        }
        catch (Exception ex)
        {
            System.out.println("----removePushNotification exception = " + ex);
        }
    };
    
    public void update()
    {
        showNotifications();
    }
    
    public void onObjectSelected(Object _object) 
    {
        if(((TVGuideListMenuObject) _object).getName().equals("View details"))
        {
            UiApplication.getUiApplication().pushScreen(new PushDetailScreen(selectedPNO, me));
        }
        else if(((TVGuideListMenuObject) _object).getName().equals("Remove message"))
        {
            removeMessageProcessing();
        }       
    }
    
    /**
     * 
     * @return <description>
     */
    public boolean onClose() {
        Main.close();
        System.exit(0);
        return true;
    }
}

