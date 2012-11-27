package com.injoit.examplepush.qblox;

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

import com.injoit.examplepush.qblox.networking.*;
import com.injoit.examplepush.qblox.utils.*;
import com.injoit.examplepush.utils.*;
import com.injoit.examplepush.containers.*;
import com.injoit.examplepush.bbm.datastorages.*;
import com.injoit.examplepush.utils.json.me.*;

/**
 * A class extending the MainScreen class, which provides default standard
 * behavior for BlackBerry GUI applications.
 */
public class AuthScreen extends PopupScreen implements QBHTTPAnswerListener, FieldChangeListener
{
    private String TOKEN = "";
    private String USER_ID = "";
    
    private final static String nonce = new Long(new Date().getTime()).toString();
    
    private AuthScreen me;
    private VerticalFieldManager listManager = new VerticalFieldManager(Manager.USE_ALL_HEIGHT|Manager.USE_ALL_WIDTH|Manager.VERTICAL_SCROLL);
    
    public TextField usernameField;
    public PasswordEditField passwordField;
    
    public CheckboxField saveField;
    private ButtonField logButton;
    private ButtonField regButton;
    
    private int PADDING = 6;
    private final int REQUEST_SESSION = 0; //registration: get token
    private final int REQUEST_USERS   = 1; //registration: sent credentials 
    private final int REQUEST_LOGIN   = 2; //login
    
    private final int GET  = 0;
    private final int POST = 1;
    private boolean isRegistration = false;
    
    private Object rateListener;
    private Object object;
    private Object ratingValue;
    
    public AuthScreen(Object _rateListener, Object _object, Object _ratingValue)
    {
        this();
        rateListener = _rateListener;
        object = _object;
        ratingValue = _ratingValue;
    }
    
    public AuthScreen()
    {
        super(new VerticalFieldManager(),Field.FOCUSABLE);
        me = this;
        
        String username = "";
        String password = "";
        
        Vector up = SavedData.getUserInfo();
        boolean isSaved = false;
        if (up != null) 
        {
            isSaved = true;
            username = (String)up.elementAt(0);
            password = (String)up.elementAt(1);
        }
                
        add(new LabelField("Username:"));
        usernameField = new TextField(TextField.NO_COMPLEX_INPUT | TextField.NO_NEWLINE);
        add(usernameField);
        add(new LabelField("Password:"));
        passwordField = new PasswordEditField();
        add(passwordField);
        
        saveField = new CheckboxField("Save username&password", isSaved);
        add(saveField);
        
        logButton = new ButtonField("Login", ButtonField.CONSUME_CLICK);
        logButton.setChangeListener(me);
        add(logButton);
        
        regButton = new ButtonField("Register", ButtonField.CONSUME_CLICK);
        regButton.setChangeListener(me);
        add(regButton);

        usernameField.setText(username);
        passwordField.setText(password);
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
                String postParam = "application_id=" + Constants.APP_ID + "&auth_key=" + Constants.AUTH_KEY + "&nonce=" + nonce  + "&timestamp=" + timestamp;
                String signature = "";
                try
                {
                    signature = hmacsha1(Constants.AUTH_SECRET, postParam);
                }
                catch(Exception ex)
                {
                    DebugStorage.getInstance().Log(0, "<AuthScreen> doRequest REQUEST_SESSION ", ex);
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
                }
                catch(Exception ex)
                {
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
                    credentialsObject.put("login", usernameField.getText());
                    credentialsObject.put("password", passwordField.getText());
                    postObject.put("user", credentialsObject);
                }
                catch(Exception ex)
                {
                }
                BigVector postData = new BigVector();
                postData.addElement(TOKEN);
                postData.addElement(postObject.toString());
                
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
                    signature = hmacsha1(Constants.AUTH_SECRET, postParam);
                }
                catch(Exception ex)
                {
                    DebugStorage.getInstance().Log(0, "<AuthScreen> doRequest REQUEST_LOGIN ", ex);
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
                    postObject.put("login", usernameField.getText());
                    postObject.put("password", passwordField.getText());
                }
                catch(Exception ex)
                {
                    DebugStorage.getInstance().Log(0, "<AuthScreen> doRequest REQUEST_LOGIN JSON ERROR ", ex);
                }
                BigVector postData = new BigVector();
                postData.addElement(TOKEN);
                postData.addElement(postObject.toString());
                
                final BigVector out = postData;
                final int outType = type;
                UiApplication.getUiApplication().invokeLater(new Runnable() 
                {
                    public void run() 
                    {
                        QBHTTPConnManager man = new QBHTTPConnManager(POST, Constants.API_SERVER + "login.json", out, outType, me);
                    }
                });

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
                    
                    if(isRegistration == true)
                    {
                        doRequest(REQUEST_USERS);
                    }
                    else
                    {
                        doRequest(REQUEST_LOGIN);
                    }
                }
                catch(Exception ex)
                {
                    DebugStorage.getInstance().Log(0, "<AuthScreen> ProcessAnswer REQUEST_SESSION ", ex);
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
                    DebugStorage.getInstance().Log(0, "<AuthScreen> ProcessAnswer REQUEST_USERS ", ex);
                }
                break;
            }
            case REQUEST_LOGIN:
            {
                try
                {
                    JSONObject jsonResponse = new JSONObject(result);
                    JSONObject jsonSessionItem = jsonResponse.getJSONObject("user");
                    USER_ID = jsonSessionItem.getString("id");
                    System.out.println("USER_ID  = " + USER_ID);
                    
                    //Storing credentials
                    if(saveField.getChecked()) 
                    {
                        SavedData.setUserInfo(usernameField.getText(), passwordField.getText(), USER_ID);
                    } 
                    else 
                    {
                        SavedData.destroyUserInfo();
                    }
                    
                    onClose();
                    
                    if(this.rateListener != null)
                    {
                        if(me.ratingValue instanceof Integer)
                        {
                            //Send rating to QB Server
                            UiApplication.getUiApplication().invokeLater(new Runnable() 
                            {
                                public void run() 
                                {
                                    Integer val = (Integer) me.ratingValue;
                                    QBUtils.RateOnQBlox((RateListener)me.rateListener, me.object, val.intValue());
                                }
                            });
                        }
                       
                        else if(me.ratingValue instanceof BigVector)
                        {
                             //update ratings
                            UiApplication.getUiApplication().invokeLater(new Runnable() 
                            {
                                public void run() 
                                {
                                    QBUtils.UpdateRatings((RatingsUpdateListener)me.rateListener, (BigVector)me.ratingValue);
                                }
                            });
                        }
                       /* else if(me.ratingValue instanceof BillboardItem)
                        {
                             //update ratings
                            UiApplication.getUiApplication().invokeLater(new Runnable() 
                            {
                                public void run() 
                                {
                                    QBUtils.UpdateBillboardRatings((RatingsUpdateListener)me.rateListener, (BillboardItem)me.ratingValue);
                                }
                            });
                        }*/
                    }
                    else
                    {
                        //RUN CHAT HERE
                    }
                    
                   //onClose();
                }
                catch(Exception ex)
                {
                    DebugStorage.getInstance().Log(0, "<AuthScreen> ProcessAnswer REQUEST_LOGIN ", ex);
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
                    
                     //Storing credentials
                    if(saveField.getChecked()) 
                    {
                        SavedData.setUserInfo(usernameField.getText(), passwordField.getText(), "");
                    } 
                    else 
                    {
                        SavedData.destroyUserInfo();
                    }
                    
                    onClose();
                } 
                catch (Exception e) 
                {
                   
                }
            }
        }); 
    }
    
    
    private static String hmacsha1(String key, String message) throws CryptoTokenException, CryptoUnsupportedOperationException, IOException 
    {
        HMACKey k = new HMACKey(key.getBytes());
        HMAC hmac = new HMAC(k, new SHA1Digest());
        hmac.update(message.getBytes());
        byte[] mac = hmac.getMAC();
        
        return convertToHex(mac);
    }
    
    private static String convertToHex(byte[] data) 
    {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) 
        {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do 
            {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
    
    public void makeMenu(Menu menu, int instance) 
    {
        super.makeMenu(menu, instance);
    }
    
    
    public void fieldChanged(Field field, int context)
    {
        if(field == logButton)
        {
            if (usernameField.getText().length() == 0 || passwordField.getText().length() == 0) 
            {
                Dialog.alert("Invalid username/password!");
                return;
            }
            if(passwordField.getText().length()<8)
            {
                Dialog.alert("Password: min 8 characters required!");
                return;
            }
            doRequest(REQUEST_SESSION);
        }
        else if(field == regButton)
        {
            if (usernameField.getText().length() == 0 || passwordField.getText().length() == 0) 
            {
                Dialog.alert("Invalid username/password!");
                return;
            }
            if(passwordField.getText().length()<8)
            {
                Dialog.alert("Password: min 8 characters required!");
                return;
            }
            isRegistration = true;         
            doRequest(REQUEST_SESSION);
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
    }
}
