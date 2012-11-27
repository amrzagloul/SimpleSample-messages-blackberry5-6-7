package com.injoit.examplepush.push;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.ServerSocketConnection;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.io.http.HttpServerConnection;
import net.rim.device.api.io.http.MDSPushInputStream;
import net.rim.device.api.io.http.PushInputStream;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;

/**
 * Registers and receives bis push messages.<br/>
 * Based on PushLib43.java found in (standard installtion) <br/>
 * C:\Program Files\BPSS\pushsdk\client-sample-app\pushdemo\com\rim\samples\device\push\lib<br/>
 * Please note that the registration to your content provider is not included.<br/>
 * You have to enter your port and app id, and change the connection suffix<br/>
 * Unregister-code is left if needed, but not implemented.<br/>
 * It is suggested to replace the sysouts with a real logger service.
 * 
 * @author simon_hain of supportforums.blackberry.com
 */
public class PushAgent {
        private static final String REGISTER_SUCCESSFUL = "rc=200";
        private static final String DEREGISTER_SUCCESSFUL = REGISTER_SUCCESSFUL;
        private static final String USER_ALREADY_SUBSCRIBED = "rc=10003";
        private static final String ALREADY_UNSUSCRIBED_BY_USER = "rc=10004";
        private static final String ALREADY_UNSUSCRIBED_BY_PROVIDER = "rc=10005";

        private static final String PUSH_PORT = "32051";
        private static final String BPAS_URL = "http://pushapi.eval.blackberry.com";
        private static final String APP_ID = "2746-767258BB0rm354O88ar665013hM7e4793e5";
        private static final String CONNECTION_SUFFIX = ";deviceside=false;ConnectionType=mds-public";

        private MessageReadingThread messageReadingThread = null;

        /**
         * Instantiates a new push agent.
         * 
         */
        public PushAgent() {
                // remove the coverage check if compiling for OS < 4.6
                if (!CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_BIS_B)) {
                        UiApplication.getUiApplication().invokeLater(new Runnable()
                        {
                            public void run()
                            {
                                if (! DeviceInfo.isSimulator())
                                {
                                    Dialog.alert("Failed to register for getting push messages. Coverage not sufficient. Please chech is your BIS mobile network avaliable");
                                }
                            }
                        });
                        return;
                }
                if (DeviceInfo.isSimulator()) {
                        return;
                }
                messageReadingThread = new MessageReadingThread();
                messageReadingThread.start();
                registerBpas();
        }
        
        public void stopListeningPushMessages()
        {
            if (messageReadingThread != null)
            {
                messageReadingThread.stopRunning();    
            }
            else
            {
                System.out.println("-----<PushAgent> stopListeningPushMessages()<messageReadingThread == null>");
            }
            
        }
        
        /**
         * Thread that processes incoming connections through {@link PushMessageReader}.
         */
        private static class MessageReadingThread extends Thread {
                private boolean running;
                private ServerSocketConnection socket;
                private HttpServerConnection conn;
                private InputStream inputStream;
                private PushInputStream pushInputStream;

                /**
                 * Instantiates a new message reading thread.
                 */
                public MessageReadingThread() {
                        this.running = true;
                }

                /**
                 * {@inheritDoc}
                 * 
                 * @see java.lang.Thread#run()
                 */
                public void run() {
                        try {
                                String url = "http://:" + PUSH_PORT + CONNECTION_SUFFIX;
                                socket = (ServerSocketConnection) Connector.open(url);
                        } catch (IOException ex) {
                                // can't open the port, probably taken by another application
                                onListenError(ex);
                        }

                        while (running) {
                                try {
                                        Object o = socket.acceptAndOpen();
                                        conn = (HttpServerConnection) o;
                                        inputStream = conn.openInputStream();
                                        pushInputStream = new MDSPushInputStream(conn, inputStream);
                                        PushMessageReader.process(pushInputStream, conn);
                                } catch (Exception e) {
                                        if (running) {
                                            running = false;
                                        }
                                } finally {
                                        close(conn, pushInputStream, null);
                                }
                        }
                }

                /**
                 * Stop running.
                 */
                public void stopRunning() {
                        running = false;
                        close(socket, null, null);
                }

                /**
                 * On listen error.
                 * 
                 * @param ex
                 *            the ex
                 */
                private void onListenError(final Exception ex) {
                        UiApplication.getUiApplication().invokeLater(new Runnable()
                        {
                            public void run()
                            {
                                if (! DeviceInfo.isSimulator())
                                {
                                    Dialog.alert("Failed to open port, caused by " + ex + " Please close or remove another application, that listening port " + PUSH_PORT);
                                }
                            }
                        });
                }
        }

        /**
         * Safely closes connection and streams.
         * 
         * @param conn
         *            the conn
         * @param is
         *            the is
         * @param os
         *            the os
         */
        public static void close(Connection conn, InputStream is, OutputStream os) {
                if (os != null) {
                        try {
                                os.close();
                        } catch (IOException e) {
                        }
                }
                if (is != null) {
                        try {
                                is.close();
                        } catch (IOException e) {
                        }
                }
                if (conn != null) {
                        try {
                                conn.close();
                        } catch (IOException e) {
                        }
                }
        }

        /**
         * Form a register request.
         * 
         * @param bpasUrl
         *            the bpas url
         * @param appId
         *            the app id
         * @param token
         *            the token
         * @return the the built request
         */
        private String formRegisterRequest(String bpasUrl, String appId, String token) {       
                StringBuffer sb = new StringBuffer(bpasUrl);
                sb.append("/mss/PD_subReg?");
                sb.append("serviceid=").append(appId);
                sb.append("&osversion=").append(DeviceInfo.getSoftwareVersion());
                sb.append("&model=").append(DeviceInfo.getDeviceName());
                if (token != null && token.length() > 0) {
                        sb.append("&").append(token);
                }
                
                return sb.toString();
        }

        /**
         * Form an unregister request.
         * 
         * @param bpasUrl
         *            the bpas url
         * @param appId
         *            the app id
         * @param token
         *            the token
         * @return the built request
         */
        private String formUnRegisterRequest(String bpasUrl, String appId, String token) {               
                StringBuffer sb = new StringBuffer(bpasUrl);
                sb.append("/mss/PD_subDereg?");
                sb.append("serviceid=").append(appId);
                if (token != null && token.length() > 0) {
                        sb.append("&").append(token);
                }
                return sb.toString();
        }

        /**
         * Register to the BPAS.
         */
        private void registerBpas() {
                final String registerUrl = formRegisterRequest(BPAS_URL, APP_ID, null) + CONNECTION_SUFFIX;
                
                /**
                 * As the connection suffix is fixed I just use a Thread to call the connection code
                 * 
                 **/
                new Thread() {
                        public void run() {
                                
                                try {
                                        HttpConnection httpConnection = (HttpConnection) Connector.open(registerUrl);
                                        InputStream is = httpConnection.openInputStream();
                                        String response = new String(IOUtilities.streamToBytes(is));
                                        
                                        close(httpConnection, is, null);
                                        String nextUrl = formRegisterRequest(BPAS_URL, APP_ID, response) + CONNECTION_SUFFIX;
                                        
                                        HttpConnection nextHttpConnection = (HttpConnection) Connector.open(nextUrl);
                                        InputStream nextInputStream = nextHttpConnection.openInputStream();
                                        response = new String(IOUtilities.streamToBytes(nextInputStream));
                                        
                                        close(nextHttpConnection, is, null);
                                        if (REGISTER_SUCCESSFUL.equals(response) || USER_ALREADY_SUBSCRIBED.equals(response)) {
                                                System.out.println("-----Registered successfully for BIS push");
                                        } else {
                                                System.out.println("-----BPAS rejected registration");
                                        }
                                } catch (IOException e) {
                                        System.out.println("-----IOException on register() " + e + " " + e.getMessage());
                                        final String registerToPushError = e.getMessage();
                                        
                                        UiApplication.getUiApplication().invokeLater(new Runnable()
                                        {
                                            public void run()
                                            {
                                                if (! DeviceInfo.isSimulator())
                                                {
                                                    Dialog.alert("Failed to register for getting push messages " + registerToPushError + " Please chech is your BIS mobile network avaliable");
                                                }
                                            }
                                        });
                                }
                        }
                }.start();
        }
}
