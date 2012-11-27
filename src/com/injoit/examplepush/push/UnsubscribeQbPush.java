package com.injoit.examplepush.push;

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
import com.injoit.examplepush.utils.*;
import com.injoit.examplepush.utils.json.me.*;
import com.injoit.examplepush.bbm.datastorages.*;

/**
 * This class implements rejection to receive push notification
 */
public class UnsubscribeQbPush implements QBHTTPAnswerListener
{
    private String TOKEN = "";
    private String SUBSCRIPTION_ID = "";
    private String timestamp = "";
    
    private final String PUSH_APP_ID = Constants.PUSH_APP_ID;
    private final String PUSH_AUTH_KEY = Constants.PUSH_AUTH_KEY;
    private final String PUSH_AUTH_SECRET = Constants.PUSH_AUTH_SECRET;
    private final String PUSH_SERVER_API = Constants.PUSH_SERVER_API;
    
    private final static String nonce = new Long(new Date().getTime()).toString();
    
    private UnsubscribeQbPush me;
    private final int REQUEST_SESSION_WITH_USER_AND_DEVICE_PARAMS = 6; //authorisation with user and device parameters: get token
    private final int REQUEST_RETRIEVE_SUBSCRIPTIONS = 9; //Retrieve subscriptions
    private final int REQUEST_PUSH_UNSUBSCRIBE = 10; // Unsubscription from push notifications    
    
    private final int GET  = 0;
    private final int POST = 1;
    private final int DELETE = 3;
    
    public UnsubscribeQbPush()
    {
        me = this;
        String time = new Long(new Date().getTime()).toString();
        timestamp = time.substring(0, time.length()-3);
    }
    
    public void unSubscribeFromQbPush()
    {
        doRequest(REQUEST_SESSION_WITH_USER_AND_DEVICE_PARAMS);
    }
    
    private void doRequest(int type)
    {
        switch(type)
        {
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
                    DebugStorage.getInstance().Log(0, "<UnsubscribeQbPush> doRequest REQUEST_SESSION ", ex);
                }
                BigVector postData = new BigVector();
                postData.addElement(postObject.toString());

                QBHTTPConnManager man = new QBHTTPConnManager(POST, PUSH_SERVER_API + "session.json", postData, type, this);
                break;
            }
            
            case REQUEST_RETRIEVE_SUBSCRIPTIONS:
            {
                BigVector postData = new BigVector();
                postData.addElement(TOKEN);
                QBHTTPConnManager man = new QBHTTPConnManager(GET, PUSH_SERVER_API + "subscriptions.json", postData , type, this);
                break;
            }
            
            case REQUEST_PUSH_UNSUBSCRIBE:
            {
                BigVector postData = new BigVector();
                postData.addElement(TOKEN);

                QBHTTPConnManager man = new QBHTTPConnManager(DELETE, PUSH_SERVER_API + "subscriptions/" + SUBSCRIPTION_ID + ".json", postData , type, this);
                break;
            }
                        
            default: break;
        }
    }
    
    public void ProcessAnswer(int type, String result)
    {
        switch(type)
        {
            case REQUEST_SESSION_WITH_USER_AND_DEVICE_PARAMS:
            {
                try
                {
                    JSONObject jsonResponse = new JSONObject(result);
                    JSONObject jsonSessionItem = jsonResponse.getJSONObject("session");
                    TOKEN = jsonSessionItem.getString("token");
                    doRequest(REQUEST_RETRIEVE_SUBSCRIPTIONS);
                }
                catch(Exception ex)
                {
                    DebugStorage.getInstance().Log(0, "<UnsubscribeQbPush> ProcessAnswer REQUEST_SESSION ", ex);
                }
                break;
            }
            case REQUEST_RETRIEVE_SUBSCRIPTIONS:
            {
                try
                {
                    String correctJSONStr = result.substring(1, result.length() - 1);
                    JSONObject jsonResponse = new JSONObject(correctJSONStr);
                    JSONObject jsonSessionItem = jsonResponse.getJSONObject("subscription");
                    SUBSCRIPTION_ID = jsonSessionItem.getString("id");
                    doRequest(REQUEST_PUSH_UNSUBSCRIBE);
                }
                catch(Exception ex)
                {
                    DebugStorage.getInstance().Log(0, "<UnsubscribeQbPush> ProcessAnswer REQUEST_RETRIEVE_SUBSCRIPTIONS ", ex);
                }
                break;
            }
            case REQUEST_PUSH_UNSUBSCRIBE:
            {
                try
                {
                    System.out.println("----- Successfullly UNSUBSCRIBED FROM PUSH");
                    // Push Notification ON = 0
                    // Push Notification OFF = 1
                    OptionStorage.getInstance().getOption("Push notifications").setCurrentValueIndex(1);
                }
                catch(Exception ex)
                {
                    DebugStorage.getInstance().Log(0, "<UnsubscribeQbPush> ProcessAnswer REQUEST_PUSH_UNSUBSCRIBE ", ex);
                }
                break;
            }
            
            default: break;
        }
    }
    
    public void ProcessError(int type, final String message)
    {
        UiApplication.getUiApplication().invokeLater(new Runnable() 
        {
            public void run() 
            {
                try 
                {
                    Status.show(message);
                } 
                catch (Exception e) 
                {
                   
                }
            }
        }); 
    };    
};

