package com.injoit.examplepush.push.screens;

import java.util.*;

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

import com.injoit.examplepush.qblox.networking.*;
import com.injoit.examplepush.qblox.utils.*;
import com.injoit.examplepush.utils.json.me.*;
import com.injoit.examplepush.utils.*;
import com.injoit.examplepush.bbm.datastorages.*;

public class PushAuthScreen extends PopupScreen implements QBHTTPAnswerListener, FieldChangeListener
{
    private String TOKEN = "";
    private String timestamp = "";
    
    private final String PUSH_APP_ID = Constants.PUSH_APP_ID;
    private final String PUSH_AUTH_KEY = Constants.PUSH_AUTH_KEY;
    private final String PUSH_AUTH_SECRET = Constants.PUSH_AUTH_SECRET;
    private final String PUSH_SERVER_API = Constants.PUSH_SERVER_API;
    
    private final static String nonce = new Long(new Date().getTime()).toString();
    
    private PushAuthScreen me;
    private VerticalFieldManager listManager = new VerticalFieldManager(Manager.USE_ALL_HEIGHT|Manager.USE_ALL_WIDTH|Manager.VERTICAL_SCROLL);
    
    private ButtonField logButton;
    private final int REQUEST_SESSION = 0; //get token
    private final int REQUEST_SIGN_UP_USER = 1; //registration: sign up new user
    private final int REQUEST_SESSION_WITH_USER_AND_DEVICE_PARAMS = 6; //authorisation with user and device parameters: get token
    private final int REQUEST_PUSH_TOKEN = 7; // get push token for push subscriptions
    private final int REQUEST_PUSH_SUBSCRIBE = 8; // subscription for retriving push notifications
    
    private final int POST = 1;
    
    public PushAuthScreen()
    {
        super(new VerticalFieldManager(),Field.FOCUSABLE);
        me = this;
        String time = new Long(new Date().getTime()).toString();
        timestamp = time.substring(0, time.length()-3);
        
        add(new LabelField("You will ressieve push notifications"));
        
        logButton = new ButtonField("OK", ButtonField.CONSUME_CLICK);
        logButton.setChangeListener(me);
        add(logButton);
      
        logButton.setFocus();
    }
    
    public void doRequest(int type)
    {
        switch(type)
        {
            case REQUEST_SESSION:
            {
                String time = new Long(new Date().getTime()).toString();
                String timestamp = time.substring(0, time.length()-3);
                String postParam = "application_id=" + PUSH_APP_ID + "&auth_key=" + PUSH_AUTH_KEY + "&nonce=" + nonce  + "&timestamp=" + timestamp;
                String signature = "";
                try
                {
                    signature = QBUtils.hmacsha1(PUSH_AUTH_SECRET, postParam);
                }
                catch(Exception ex)
                {
                    DebugStorage.getInstance().Log(0, "<PushAuthScreen> doRequest REQUEST_SESSION ", ex);
                    break;
                }
                
                JSONObject postObject = new JSONObject();
                try
                {
                    postObject.put("application_id", PUSH_APP_ID);
                    postObject.put("auth_key", PUSH_AUTH_KEY);
                    postObject.put("nonce", nonce);
                    postObject.put("timestamp", timestamp);
                    postObject.put("signature", signature);
                }
                catch(Exception ex)
                {
                    DebugStorage.getInstance().Log(0, "<PushAuthScreen> doRequest REQUEST_SESSION ", ex);
                }
                BigVector postData = new BigVector();
                postData.addElement(postObject.toString());
                
                QBHTTPConnManager man = new QBHTTPConnManager(POST, PUSH_SERVER_API + "session.json", postData, type, this);
                break;
            }
            case REQUEST_SIGN_UP_USER:
            {
                String pin = Integer.toHexString(DeviceInfo.getDeviceId()).toUpperCase();
                
                JSONObject credentialsObject = new JSONObject();
                JSONObject postObject = new JSONObject();
                try
                {
                    credentialsObject.put("login", pin);
                    credentialsObject.put("password", TextUtils.reverse(pin));
                    postObject.put("user", credentialsObject);
                }
                catch(Exception ex)
                {
                }
                BigVector postData = new BigVector();
                postData.addElement(TOKEN);
                postData.addElement(postObject.toString());
                
                DebugStorage.getInstance().Log(0, "Post data: " + postObject.toString());
                
                QBHTTPConnManager man = new QBHTTPConnManager(POST, PUSH_SERVER_API + "users.json", postData , type, this);
                break;
            }
            case REQUEST_SESSION_WITH_USER_AND_DEVICE_PARAMS:
            {
                JSONObject postObject = new JSONObject();
                JSONObject userObject = new JSONObject();
                JSONObject deviceObject = new JSONObject();
                try
                {
                    String pin = Integer.toHexString(DeviceInfo.getDeviceId()).toUpperCase();
                    String postParam = 
                    "application_id=" + PUSH_APP_ID +
                    "&auth_key=" + PUSH_AUTH_KEY +
                    "&device[platform]=" + "blackberry"  +
                    "&device[udid]=" + pin +
                    "&nonce=" + nonce  +
                    "&timestamp=" + timestamp +
                    "&user[login]=" + pin  +
                    "&user[password]=" + TextUtils.reverse(pin);

                    String signature = "";
                    try
                    {
                        signature = QBUtils.hmacsha1(PUSH_AUTH_SECRET, postParam);
                    }
                    catch (Exception ex)
                    {
                        System.out.println("QBUtils.hmacsha1(PUSH_AUTH_SECRET, postParam) exception = " + ex);
                    }
                    
                    postObject.put("application_id", PUSH_APP_ID);
                    postObject.put("auth_key", PUSH_AUTH_KEY);
                    postObject.put("timestamp", timestamp);
                    postObject.put("nonce", nonce);
                    postObject.put("signature", signature);
                    
                    userObject.put("login", pin );
                    userObject.put("password", TextUtils.reverse(pin));
                    postObject.put("user", userObject);
                    
                    deviceObject.put("platform", "blackberry");
                    deviceObject.put("udid", pin);
                    postObject.put("device", deviceObject);
                        
                }
                catch(Exception ex)
                {
                    DebugStorage.getInstance().Log(0, "<PushAuthScreen> doRequest REQUEST_SESSION ", ex);
                    onClose();
                }
                BigVector postData = new BigVector();
                postData.addElement(postObject.toString());

                QBHTTPConnManager man = new QBHTTPConnManager(POST, PUSH_SERVER_API + "session.json", postData, type, this);
                break;
            }
            
            case REQUEST_PUSH_TOKEN:
            {
                String pin = Integer.toHexString(DeviceInfo.getDeviceId()).toUpperCase();
                JSONObject credentialsObject = new JSONObject();
                JSONObject postObject = new JSONObject();
                try
                {
                    //credentialsObject.put("environment", "production");
                    credentialsObject.put("environment", "development");
                    credentialsObject.put("client_identification_sequence", pin);
                    
                    postObject.put("push_token", credentialsObject);
                }
                catch(Exception ex)
                {
                    System.out.println("-----<doRequest><REQUEST_PUSH_TOKEN> Exception = " + ex);
                }
                BigVector postData = new BigVector();
                postData.addElement(TOKEN);
                postData.addElement(postObject.toString());
                
                QBHTTPConnManager man = new QBHTTPConnManager(POST, PUSH_SERVER_API + "push_tokens.json", postData , type, this);
                break;
            }
            
            //    REQUEST_PUSH_SUBSCRIBE
            
            case REQUEST_PUSH_SUBSCRIBE:
            {
                System.out.println("-----<doRequest><REQUEST_PUSH_SUBSCRIBE>");
                JSONObject postObject = new JSONObject();
                try
                {
                    postObject.put("notification_channels", "bbps");
                }
                catch(Exception ex)
                {
                    System.out.println("-----<doRequest><REQUEST_PUSH_SUBSCRIBE> Exception = " + ex);
                }
                BigVector postData = new BigVector();
                postData.addElement(TOKEN);
                postData.addElement(postObject.toString());
                
                QBHTTPConnManager man = new QBHTTPConnManager(POST, PUSH_SERVER_API + "subscriptions.json", postData , type, this);
                break;
            }
            
            
            default: break;
        }
    }
    
    public void ProcessAnswer(int type, String result)
    {
        System.out.println("-----ProcessAnswer");
        System.out.println("-----type = ");
        System.out.println("-----result = " + result);
        switch(type)
        {
            case REQUEST_SESSION:
            {
                try
                {
                    JSONObject jsonResponse = new JSONObject(result);
                    JSONObject jsonSessionItem = jsonResponse.getJSONObject("session");
                    TOKEN = jsonSessionItem.getString("token");
                    System.out.println("-----TOKEN = " + TOKEN);
                    doRequest(REQUEST_SIGN_UP_USER);
                }
                catch(Exception ex)
                {
                    DebugStorage.getInstance().Log(0, "<PushAuthScreen> ProcessAnswer REQUEST_SESSION ", ex);
                    onClose();
                }
                break;
            }
            case REQUEST_SIGN_UP_USER:
            {
                System.out.println("-----<ProcessAnswer> REQUEST_SIGN_UP_USER");
                try
                {
                    doRequest(REQUEST_SESSION_WITH_USER_AND_DEVICE_PARAMS);
                }
                catch(Exception ex)
                {
                    DebugStorage.getInstance().Log(0, "<PushAuthScreen> ProcessAnswer REQUEST_SIGN_UP_USER ", ex);
                }
                break;
            }
            
            case REQUEST_SESSION_WITH_USER_AND_DEVICE_PARAMS:
            {
                try
                {
                    JSONObject jsonResponse = new JSONObject(result);
                    JSONObject jsonSessionItem = jsonResponse.getJSONObject("session");
                    TOKEN = jsonSessionItem.getString("token");
                    System.out.println("----- TOKEN for get push token = " + TOKEN);
                    doRequest(REQUEST_PUSH_TOKEN);
                }
                catch(Exception ex)
                {
                    DebugStorage.getInstance().Log(0, "<PushAuthScreen> ProcessAnswer REQUEST_SESSION ", ex);
                    onClose();
                }
                break;
            }
            case REQUEST_PUSH_TOKEN:
            {
                try
                {
                    System.out.println("-----<ProcessAnswer> REQUEST_PUSH_TOKEN");
                    doRequest(REQUEST_PUSH_SUBSCRIBE);
                }
                catch(Exception ex)
                {
                    DebugStorage.getInstance().Log(0, "<PushAuthScreen> ProcessAnswer REQUEST_PUSH_SUBSCRIBE  Exception ", ex);
                    onClose();
                }
                break;
            }
            case REQUEST_PUSH_SUBSCRIBE:
            {
                try
                {
                    System.out.println("-----<ProcessAnswer> REQUEST_PUSH_SUBSCRIBE");
                    //doRequest(REQUEST_PUSH_SUBSCRIBE);
                }
                catch(Exception ex)
                {
                    DebugStorage.getInstance().Log(0, "<PushAuthScreen> ProcessAnswer REQUEST_PUSH_SUBSCRIBE  Exception ", ex);
                    onClose();
                }
                break;
            }
            
            default: break;
        }
    }
    
    public void ProcessError(int type, final String message)
    {
        System.out.println("-----ProcessError");
        System.out.println("-----type = " + type);
        System.out.println("-----message = " + message);
        
        UiApplication.getUiApplication().invokeLater(new Runnable() 
        {
            public void run() 
            {
                try 
                {
                    if (message.equals("Unauthorized"))
                    {
                        System.out.println("-----ProcessError (message.equals(Unauthorized))");
                        
                        doRequest(REQUEST_SESSION);
                    }
                    else
                    {
                        Status.show(message);
                    }
                    
                    
                   
                } 
                catch (Exception e) 
                {
                   
                }
            }
        }); 
    };
    
    
    public void fieldChanged(Field field, int context)
    {
       
        if(field == logButton)
        { 
            doRequest(REQUEST_SESSION_WITH_USER_AND_DEVICE_PARAMS);
        }
    };
    
    public boolean onClose()
    {
        close();
        return true;
    }
    
};

