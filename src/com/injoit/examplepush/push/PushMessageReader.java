package com.injoit.examplepush.push;

import javax.microedition.io.Connection;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.io.Base64InputStream;
import net.rim.device.api.io.http.HttpServerConnection;
import net.rim.device.api.io.http.PushInputStream;
import net.rim.device.api.util.Arrays;

import com.injoit.examplepush.push.models.PushNotificationObject;
import com.injoit.examplepush.push.storages.PushNotificationsStorage;
import com.injoit.examplepush.push.screens.PushNotificationsScreen;

/**
 * Source (on standard installation): C:\Program Files\BPSS\pushsdk\client-sample-app\pushdemo\com\rim\samples\device\push <br/>
 * Reads incoming push messages and extracts texts and images.
 */
public final class PushMessageReader {

        // HTTP header property that carries unique push message ID
        private static final String MESSAGE_ID_HEADER = "Push-Message-ID";
        // content type constant for text messages
        private static final String MESSAGE_TYPE_TEXT = "text";
        // content type constant for image messages
        private static final String MESSAGE_TYPE_IMAGE = "image";

        private static final int MESSAGE_ID_HISTORY_LENGTH = 10;
        private static String[] messageIdHistory = new String[MESSAGE_ID_HISTORY_LENGTH];
        private static byte historyIndex;

        private static byte[] buffer = new byte[15 * 1024];
        private static byte[] imageBuffer = new byte[10 * 1024];
        

        /**
         * Utility classes should have a private constructor.
         */
        private PushMessageReader() {
        }

        /**
         * Reads the incoming push message from the given streams in the current thread and 
                 * notifies controller to display the information.
         * 
         * @param pis
         *            the pis
         * @param conn
         *            the conn
         */
        public static void process(PushInputStream pis, Connection conn) {
                String pushText = "";

                try {
                        HttpServerConnection httpConn;
                        if (conn instanceof HttpServerConnection) {
                                httpConn = (HttpServerConnection) conn;
                        } else {
                                throw new IllegalArgumentException("Can not process non-http pushes, expected HttpServerConnection but have "
                                                + conn.getClass().getName());
                        }

                        String msgId = httpConn.getHeaderField(MESSAGE_ID_HEADER);
                        String msgType = httpConn.getType();
                        String encoding = httpConn.getEncoding();

                        System.out.println("-----Message props: ID=" + msgId + ", Type=" + msgType + ", Encoding=" + encoding);

                        boolean accept = true;
                        if (!alreadyReceived(msgId)) {
                                byte[] binaryData;

                                if (msgId == null) {
                                    msgId = String.valueOf(System.currentTimeMillis());
                                }

                                if (msgType == null) {
                                        System.out.println("Message content type is NULL");
                                        accept = false;
                                } else if (msgType.indexOf(MESSAGE_TYPE_TEXT) >= 0)
                                {
                                        // a string
                                        int size = pis.read(buffer);
                                        binaryData = new byte[size];
                                        System.arraycopy(buffer, 0, binaryData, 0, size);
                                        pushText = new String(binaryData);
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
                                        
                                        final String alertPushText = pushText;
                                        
                                        UiApplication.getUiApplication().invokeLater(new Runnable()
                                        {
                                            public void run()
                                            {
                                                Dialog.alert("Push Notification: " + "\n" + alertPushText);                  
                                            }
                                        });
                                        
                                } 
                                 
                                else {
                                        System.out.println("-----Unknown message type " + msgType);
                                        accept = false;
                                }
                        } else {
                                System.out.println("-----Received duplicate message with ID " + msgId);
                        }
                        pis.accept();
                } catch (Exception e) {
                        System.out.println("-----Failed to process push message: " + e);
                } finally {
                        PushAgent.close(conn, pis, null);
                }
        }

        /**
         * Check whether the message with this ID has been already received.
         * 
         * @param id
         *            the id
         * @return true, if successful
         */
        private static boolean alreadyReceived(String id) {
                if (id == null) {
                        return false;
                }

                if (Arrays.contains(messageIdHistory, id)) {
                        return true;
                }

                // new ID, append to the history (oldest element will be eliminated)
                messageIdHistory[historyIndex++] = id;
                if (historyIndex >= MESSAGE_ID_HISTORY_LENGTH) {
                        historyIndex = 0;
                }
                return false;
        }

}
