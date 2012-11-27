package com.injoit.examplepush.qblox.networking;

import net.rim.device.api.system.*;
import java.io.*;
import javax.microedition.io.*;
import net.rim.device.api.collection.util.*;
import net.rim.blackberry.api.browser.*;

public class QBHTTPOut
{
    private QBHTTPAnswerListener listener;
    private int handler;
    private int type;
    private BigVector postData;
    
    public QBHTTPOut(int _type, BigVector _postData, Connection conn, final int _handler, final QBHTTPAnswerListener _listener)
    {
        listener = _listener;
        handler = _handler;
        postData = _postData;
        ContentReaderThread t = new ContentReaderThread(_type, postData, conn, handler, _listener);
        t.start();
    }

    private final class ContentReaderThread extends Thread 
    {
        private Connection _connection;
        private QBHTTPAnswerListener listener;
        private int handler;
        private int type;
        private BigVector postData;
        private final int GET  = 0;
        private final int POST = 1;
        private final int PUT = 2;
        private final int DELETE = 3;
        
        private final int REQUEST_SESSION = 0; //registration: get token
        private final int REQUEST_USERS   = 1; //registration: sent credentials 
        private final int REQUEST_LOGIN   = 2; //registration: login
        private final int REQUEST_RATING  = 3; //rating
        private final int REQUEST_GAME_MODE_CREATE   = 4;
        private final int REQUEST_GAME_MODE_AVERAGE   = 5;
        private final int REQUEST_SESSION_WITH_USER_AND_DEVICE_PARAMS  = 6;
        private final int REQUEST_PUSH_TOKEN = 7;
        private final int REQUEST_PUSH_SUBSCRIBE = 8; // subscription for retriving push notifications
        private final int REQUEST_RETRIEVE_SUBSCRIPTIONS = 9; //Retrieve subscriptions
        private final int REQUEST_PUSH_UNSUBSCRIBE = 10; // Unsubscription from push notifications
        
        
        ContentReaderThread(final int _type, final BigVector _postData, Connection conn, final int _handler, final QBHTTPAnswerListener _listener) 
        {
            _connection = conn;
            listener = _listener;
            handler = _handler;
            type = _type;
            postData = _postData;
        }

        public void run() 
        {
            long start = System.currentTimeMillis();
            String result = "";
            OutputStream os = null;
            InputStream is = null;

            try 
            {
                // Send HTTP GET to the server.
                //OutputConnection outputConn = (OutputConnection) _connection;
                //os = outputConn.openOutputStream();
                HttpConnection httpConnection = (HttpConnection) _connection;
                
                byte[] outData = new byte[0];
                switch(type)
                {
                    case GET:
                    {
                        httpConnection.setRequestMethod(HttpConnection.GET);
                        
                        httpConnection.setRequestProperty("Content-Type", "application/json");
                        httpConnection.setRequestProperty("QuickBlox-REST-API-Version", "0.1.0");
                        httpConnection.setRequestProperty("QB-Token", (String) postData.elementAt(0));
                                
                        break;
                    }
                    case POST:
                    {
                        switch(handler)
                        {
                            case REQUEST_SESSION:
                            {                                
                                httpConnection.setRequestMethod(HttpConnection.POST);
                                 
                                String data = (String) postData.elementAt(0);
                                outData = data.getBytes();
                                
                                httpConnection.setRequestProperty("Content-Type", "application/json");
                                httpConnection.setRequestProperty("QuickBlox-REST-API-Version", "0.1.0");
                                
                                break;
                            }
                            case REQUEST_USERS:
                            {
                                httpConnection.setRequestMethod(HttpConnection.POST);
                                 
                                String data = (String) postData.elementAt(1);
                                outData = data.getBytes();
                                
                                httpConnection.setRequestProperty("Content-Type", "application/json");
                                httpConnection.setRequestProperty("QuickBlox-REST-API-Version", "0.1.0");
                                httpConnection.setRequestProperty("QB-Token", (String) postData.elementAt(0));
                                
                                break;
                            }
                            case REQUEST_LOGIN:
                            {
                                httpConnection.setRequestMethod(HttpConnection.POST);
                                 
                                String data = (String) postData.elementAt(1);
                                outData = data.getBytes();
                                
                                httpConnection.setRequestProperty("Content-Type", "application/json");
                                httpConnection.setRequestProperty("QuickBlox-REST-API-Version", "0.1.0");
                                httpConnection.setRequestProperty("QB-Token", (String) postData.elementAt(0));
                                
                                break;
                            }
                            case REQUEST_SESSION_WITH_USER_AND_DEVICE_PARAMS:
                            {
                                httpConnection.setRequestMethod(HttpConnection.POST);
                                 
                                String data = (String) postData.elementAt(0);
                                outData = data.getBytes();
                                
                                httpConnection.setRequestProperty("Content-Type", "application/json");
                                httpConnection.setRequestProperty("QuickBlox-REST-API-Version", "0.1.0");
                                
                                break;
                            }
                            case REQUEST_PUSH_TOKEN:
                            {
                                httpConnection.setRequestMethod(HttpConnection.POST);
                                 
                                String data = (String) postData.elementAt(1);
                                outData = data.getBytes();
                                
                                httpConnection.setRequestProperty("Content-Type", "application/json");
                                httpConnection.setRequestProperty("QuickBlox-REST-API-Version", "0.1.0");
                                httpConnection.setRequestProperty("QB-Token", (String) postData.elementAt(0));
                                
                                break;
                            }
                            case REQUEST_PUSH_SUBSCRIBE:
                            {
                                httpConnection.setRequestMethod(HttpConnection.POST);
                                 
                                String data = (String) postData.elementAt(1);
                                outData = data.getBytes();
                                
                                httpConnection.setRequestProperty("Content-Type", "application/json");
                                httpConnection.setRequestProperty("QuickBlox-REST-API-Version", "0.1.0");
                                httpConnection.setRequestProperty("QB-Token", (String) postData.elementAt(0));
                                
                                break;
                            }
                            default:
                            {
                                httpConnection.setRequestMethod(HttpConnection.POST);
                                 
                                String data = (String) postData.elementAt(1);
                                outData = data.getBytes();
                                
                                httpConnection.setRequestProperty("Content-Type", "application/json");
                                httpConnection.setRequestProperty("QuickBlox-REST-API-Version", "0.1.0");
                                httpConnection.setRequestProperty("QB-Token", (String) postData.elementAt(0));
                                
                                break;
                            }
                        }
                        
                        break;
                    }
                    case PUT:
                    {
                        httpConnection.setRequestMethod("PUT");
                                 
                        String data = (String) postData.elementAt(1);
                        outData = data.getBytes();
                        
                        httpConnection.setRequestProperty("Content-Type", "application/json");
                        httpConnection.setRequestProperty("QuickBlox-REST-API-Version", "0.1.0");
                        httpConnection.setRequestProperty("QB-Token", (String) postData.elementAt(0));
                        break;
                    }
                    case DELETE:
                    {
                        httpConnection.setRequestMethod("DELETE");
                                 
                        httpConnection.setRequestProperty("Content-Type", "application/json");
                        httpConnection.setRequestProperty("QuickBlox-REST-API-Version", "0.1.0");
                        httpConnection.setRequestProperty("QB-Token", (String) postData.elementAt(0));
                        break;
                    }
                    default: break;
                }
                
                os = httpConnection.openOutputStream();
                os.write(outData);
                os.flush();
                
                int state = httpConnection.getResponseCode();
                
                if(state == HttpsConnection.HTTP_OK || state == HttpsConnection.HTTP_CREATED || state == HttpsConnection.HTTP_ACCEPTED)
                {
                    // Get InputConnection and read the server's response.
                    InputConnection inputConn = (InputConnection) _connection;
                    is = inputConn.openInputStream();
                    
                    byte[] data = net.rim.device.api.io.IOUtilities.streamToBytes(is);
                    result = new String(data);
                    
                    listener.ProcessAnswer(handler, result);
                } 
                else if( state == HttpConnection.HTTP_TEMP_REDIRECT
                        || state == HttpConnection.HTTP_MOVED_TEMP
                        || state == HttpConnection.HTTP_MOVED_PERM ) 
                {
                    String location = httpConnection.getHeaderField("location").trim();
                    new QBHTTPConnManager(type, location, postData, handler, listener);
                }
                else
                {
                    final String message = httpConnection.getResponseMessage();
                    
                    listener.ProcessError(handler, message);
                }
            } 
            catch (Exception ex) 
            {
                listener.ProcessError(handler, ex.getMessage());
            } 
            finally 
            {
                // Close OutputStream
                if (os != null) 
                {
                    try 
                    {
                        os.close();
                    }
                    catch (IOException e) 
                    {
                        
                    }
                }

                // Close InputStream.
                if (is != null) 
                {
                    try 
                    {
                        is.close();
                    }
                    catch (IOException e) 
                    {
                    }
                }

                // Close Connection.
                try 
                {
                    _connection.close();
                } 
                catch (IOException ioe)
                {
                }
            }
        }
    }
}

