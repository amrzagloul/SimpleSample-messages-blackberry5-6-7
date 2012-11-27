package com.injoit.examplepush.qblox;

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
import com.injoit.examplepush.containers.*;
import com.injoit.examplepush.bbm.datastorages.*;

public class QBRatingsSyncScreen extends PopupScreen implements QBHTTPAnswerListener
{
    private QBRatingsSyncScreen me;
    private LabelField _ourLabelField = null;
    
    private final int REQUEST_SESSION = 0; //registration: get token
    private final int REQUEST_USERS   = 1; //registration: sent credentials 
    private final int REQUEST_LOGIN   = 2; //login
    private final int REQUEST_GET_RATINGS  = 3;
    
    private final int GET  = 0;
    private final int POST = 1;
    private final int PUT = 2;
    private String TOKEN = "";
    
    private final static String nonce = new Long(new Date().getTime()).toString();
    private RatingsUpdateListener rateListener;
    
    private String username = "";
    private String password = "";
    private String user_id = "";
    private boolean userAuthorized = true;
    
    private Object ch;


    public QBRatingsSyncScreen(RatingsUpdateListener _rateListener, Object _ch) 
    {
        super(new VerticalFieldManager(VerticalFieldManager.VERTICAL_SCROLL | VerticalFieldManager.VERTICAL_SCROLLBAR));
        me = this;
        rateListener = _rateListener;
        ch = _ch;
        
        Vector up = SavedData.getUserInfo();
        boolean isSaved = false;
        if (up != null) 
        {
            isSaved = true;
            username = (String)up.elementAt(0);
            password = (String)up.elementAt(1);
            //user_id = (String)up.elementAt(2);
            
            if(username == null || password == null || password.length()==0 || username.length()==0)
            {
                String pin = Integer.toHexString(DeviceInfo.getDeviceId()).toUpperCase();
                if(pin.length()>0)
                {
                    username = pin;
                    password = TextUtils.reverse(pin);
                    SavedData.setUserInfo(pin, password, "");
                }
            }
        }
        else
        {
            String pin = Integer.toHexString(DeviceInfo.getDeviceId()).toUpperCase();
            if(pin.length()>0)
            {
                username = pin;
                password = TextUtils.reverse(pin);
                SavedData.setUserInfo(pin, password, "");
            }
        }
        
     //   EvenlySpacedHorizontalFieldManager spinners = new EvenlySpacedHorizontalFieldManager(FIELD_VCENTER);
     //   spinners.add(new ProgressAnimationField( Bitmap.getBitmapResource("spinner.png" ), 12, Field.FIELD_VCENTER ) ); 
        
        _ourLabelField = new LabelField("Please wait...", Field.FIELD_VCENTER);
        _ourLabelField.setPadding(0,0,0,10);
        
        //spinners.add(_ourLabelField);
  //      add( spinners );
        
        UiApplication.getUiApplication().invokeLater(new Runnable() 
        {
            public void run() 
            {
                doRequest(REQUEST_SESSION);
            }
        });
    }
    
    public void doRequest(int type)
    {
        switch(type)
        {
            case REQUEST_SESSION:
            {
                String time = new Long(new Date().getTime()).toString();
                String timestamp = time.substring(0, time.length()-3);
                
                String postParam = "";
                if (userAuthorized == true)
                {
                    postParam = "application_id=" + Constants.APP_ID + "&auth_key=" + Constants.AUTH_KEY + "&nonce=" + nonce  + "&timestamp=" + timestamp + "&user[login]=" + username + "&user[password]=" + password;
                }
                else
                {
                    postParam = "application_id=" + Constants.APP_ID + "&auth_key=" + Constants.AUTH_KEY + "&nonce=" + nonce  + "&timestamp=" + timestamp;
                }
                
                String signature = "";
                try
                {
                    signature = QBUtils.hmacsha1(Constants.AUTH_SECRET, postParam);
                }
                catch(Exception ex)
                {
                    DebugStorage.getInstance().Log(0, "<QBRatingSenderScreen> doRequest REQUEST_SESSION ", ex);
                    break;
                }
                
                JSONObject postObject = new JSONObject();
                try
                {
                    postObject.put("application_id", Constants.APP_ID);
                    postObject.put("auth_key", Constants.AUTH_KEY);
                    postObject.put("nonce", nonce);
                    postObject.put("timestamp", timestamp);
                    postObject.put("signature", signature);
                    if (userAuthorized == true)
                    {
                        JSONObject userObject = new JSONObject();
                        userObject.put("login", username);
                        userObject.put("password", password);
                        postObject.put("user", userObject);
                    }
                }
                catch(Exception ex)
                {
                    DebugStorage.getInstance().Log(0, "<QBRatingSenderScreen> doRequest REQUEST_SESSION ", ex);
                    onClose();
                }
                BigVector postData = new BigVector();
                postData.addElement(postObject.toString());

                QBHTTPConnManager man = new QBHTTPConnManager(POST, Constants.API_SERVER + "session.json", postData, type, this);
                break;
            }
            case REQUEST_USERS:
            {
                JSONObject credentialsObject = new JSONObject();
                JSONObject postObject = new JSONObject();
                try
                {
                    credentialsObject.put("login", username);
                    credentialsObject.put("password", password );
                    postObject.put("user", credentialsObject);
                }
                catch(Exception ex)
                {
                }
                BigVector postData = new BigVector();
                postData.addElement(TOKEN);
                postData.addElement(postObject.toString());
                
                DebugStorage.getInstance().Log(0, "Post data: " + postObject.toString());
                
                QBHTTPConnManager man = new QBHTTPConnManager(POST, Constants.API_SERVER + "users.json", postData , type, this);
                break;
            }
            case REQUEST_LOGIN:
            {
                String time = new Long(new Date().getTime()).toString();
                String timestamp = time.substring(0, time.length()-3);
                String postParam = "application_id=" + Constants.APP_ID + "&auth_key=" + Constants.AUTH_KEY + "&nonce=" + nonce  + "&timestamp=" + timestamp;
                String signature = "";
                try
                {
                    signature = QBUtils.hmacsha1(Constants.AUTH_SECRET, postParam);
                }
                catch(Exception ex)
                {
                    DebugStorage.getInstance().Log(0, "<QBRatingSenderScreen> doRequest REQUEST_LOGIN ", ex);
                    onClose();
                    break;
                }
                
                JSONObject postObject = new JSONObject();
                try
                {
                    postObject.put("application_id", Constants.APP_ID);
                    postObject.put("auth_key", Constants.AUTH_KEY);
                    postObject.put("nonce", nonce);
                    postObject.put("timestamp", timestamp);
                    postObject.put("signature", signature);
                    postObject.put("login", username);
                    postObject.put("password", password);
                }
                catch(Exception ex)
                {
                    DebugStorage.getInstance().Log(0, "<QBRatingSenderScreen> doRequest REQUEST_LOGIN JSON ERROR ", ex);
                    onClose();
                    break;
                }
                BigVector postData = new BigVector();
                postData.addElement(TOKEN);
                postData.addElement(postObject.toString());

                QBHTTPConnManager man = new QBHTTPConnManager(POST, Constants.API_SERVER + "login.json", postData, type, this);
                break;
            }
            case REQUEST_GET_RATINGS:
            {
                BigVector postData = new BigVector();
                postData.addElement(TOKEN);
                 
                String IDs = "";
               
                
                if(IDs.length()>0)
                {
                    if(IDs.lastIndexOf(',') == IDs.length()-1) IDs = IDs.substring(0, IDs.length()-1);
                    QBHTTPConnManager man = new QBHTTPConnManager(GET, Constants.API_SERVER + "data/ProgramRatings.json?sProg_name[in]=" + IDs /*+ ".json"*/, postData, type, this);
                }
                
                break;
            }
            default: break;
        }
    }
    
    public void ProcessAnswer(int type, String result)
    {
        switch(type)
        {
            case REQUEST_SESSION:
            {
                try
                {
                    JSONObject jsonResponse = new JSONObject(result);
                    JSONObject jsonSessionItem = jsonResponse.getJSONObject("session");
                    TOKEN = jsonSessionItem.getString("token");
                    
                    String USER_ID = jsonSessionItem.getString("user_id");
                    
                    if((USER_ID != null) && (! USER_ID.equals("null")))
                    {
                        System.out.println("USER_ID  = " + USER_ID);
                        //Storing credentials
                        SavedData.setUserInfo(username, password, USER_ID);
                        doRequest(REQUEST_LOGIN);
                    }
                    else
                    {
                        doRequest(REQUEST_USERS);
                    }
                }
                catch(Exception ex)
                {
                    DebugStorage.getInstance().Log(0, "<QBRatingSyncScreen> ProcessAnswer REQUEST_SESSION ", ex);
                    onClose();
                }
                break;
            }
            case REQUEST_USERS:
            {
                try
                {
                    doRequest(REQUEST_LOGIN);
                }
                catch(Exception ex)
                {
                    DebugStorage.getInstance().Log(0, "<QBRatingSyncScreen> ProcessAnswer REQUEST_USERS ", ex);
                }
                break;
            }
            case REQUEST_LOGIN:
            {
                try
                {
                 
                    JSONObject jsonResponse = new JSONObject(result);
                    JSONObject jsonSessionItem = jsonResponse.getJSONObject("user");
                    String USER_ID = jsonSessionItem.getString("id");
                    System.out.println("USER_ID  = " + USER_ID);
                    
                    //Storing credentials
                    SavedData.setUserInfo(username, password, USER_ID);
                    doRequest(REQUEST_GET_RATINGS);
                }
                catch(Exception ex)
                {
                    DebugStorage.getInstance().Log(0, "<QBRatingSyncScreen> ProcessAnswer REQUEST_LOGIN ", ex);
                    onClose();
                }
                break;
            }
            case REQUEST_GET_RATINGS:
            {
                try
                {
                    /*result = result.replace('[',' ');
                    result = result.replace(']',' ');
                    result = result.trim();*/
                    if(result.length()>0)
                    {
                        JSONObject ratingsClass = new JSONObject(result);
                        JSONArray jsonResponse = ratingsClass.getJSONArray("items");
                        for(int i=0; i<jsonResponse.length(); i++)
                        {
                             JSONObject item = jsonResponse.getJSONObject(i);
                        }
                        
                       
                        rateListener.RatingsUpdated();
                    }
                }
                catch(Exception ex)
                {
                    DebugStorage.getInstance().Log(0, "<QBRatingSyncScreen> ProcessAnswer REQUEST_GET_RATINGS ", ex);
                }
                finally
                {
                    onClose();
                }
                break; 
            }
            default: break;
        }
    }
    
    public void ProcessError(int type, final String message)
    {
        try 
        {
            if (message.equals("Unauthorized"))
            {
                userAuthorized = false;
                doRequest(REQUEST_SESSION);
            }
            else
            {
                if(this.rateListener != null)
                {
                    //Notify listener about error 
                    this.rateListener.RatingsUpdateError(message);
                }
                onClose();
            }
        } 
        catch (Exception ex) 
        {
            DebugStorage.getInstance().Log(0, "<QBRatingSyncScreen> ProcessError ", ex);
            onClose();
        } 
    }
    
    public boolean onClose()
    {
       // Now dismiss this screen
        UiApplication.getUiApplication().invokeLater(new Runnable() 
        {
            public void run() 
            {
                UiApplication.getUiApplication().popScreen(me);
            }
        });
        return true;
    }
}



