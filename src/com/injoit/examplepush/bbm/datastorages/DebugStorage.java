package com.injoit.examplepush.bbm.datastorages;

import java.util.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.database.*;
import net.rim.device.api.io.*;
import net.rim.device.api.system.*;
import net.rim.device.api.util.*;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.collection.util.*;

import com.injoit.examplepush.bbm.models.*;
import com.injoit.examplepush.bbm.utils.*;

public class DebugStorage
{
        public static final long STORE_KEY = 0xaeba8beb21aa95d7L;
        private static final long GUID = 0xaabdd61c5d004c17L;
        
        private static DebugStorage instance;
        private static BigVector records = new BigVector();
        private static Boolean lock = new Boolean(true);  
        private static PersistentObject  persist;
        private static int MAX_ROWS = 2500;
        
        private static long oldTime = 0;
        
        public static String[] LOG_LEVELS = new String[]
        {
            "Info",   //0
            "Warning",//1
            "Debug"   //2
        };

        static
        {
            persist = PersistentStore.getPersistentObject( STORE_KEY );
            if( persist.getContents() == null )
            {
                persist.setContents(new BigVector());
                persist.commit();
            } 
            records = (BigVector)persist.getContents();
        }

        public static synchronized DebugStorage getInstance()
        {
            if(instance == null)
            {
                instance = (DebugStorage)RuntimeStore.getRuntimeStore().get(GUID);
                if(instance == null)
                {
                    DebugStorage newInstance = new DebugStorage();
                    RuntimeStore.getRuntimeStore().put(GUID, newInstance);
                    instance = newInstance;
                }
            }      
            return instance;
        }
        
        public static int GetLogLevel()
        {
            Option o = OptionStorage.getInstance().getOption("Log level");
            return o.getCurrentValueIndex();
        }

        public static void AddRow(String row)
        throws Exception
        {
            synchronized(records)
            {
                Date currentDate = new Date();
                SimpleDateFormat sdFormat = new SimpleDateFormat("dd MMMM, E, HH:mm:ss ");
                Calendar calnd = Calendar.getInstance();
                calnd.setTime(currentDate);
                StringBuffer buffer = new StringBuffer();
                sdFormat.format(calnd, buffer, null);
                
                row = buffer.toString().concat(row);
                records.insertElementAt(row,0);
                if(records.size()>MAX_ROWS)
                {
                    records.removeElementAt(records.size()-1);
                }
                persist.commit();
            }
        }
        
        public static void Log(int lLevel, String data)
        {
            try
            {
                if(lLevel<=GetLogLevel())
                {
                    String message = new String();
                    message = "<".concat(Thread.currentThread().getName()).concat("> ").concat(data);
                
                    AddRow(message);
                }
                System.out.println(data);
                Debug.Log(data);
            }
            catch(Exception ex)
            {
                System.out.println( ex.getMessage() );
                ex.printStackTrace();
            }
        }

        public static void Log(int lLevel, String data, Exception ex)
        {
            String message = new String();
            try
            {        
                if(lLevel<=GetLogLevel())
                {      
                    
                    message = "!!! EXCEPTION: "+"<".concat(Thread.currentThread().getName()).concat("> ").concat(data);
                    AddRow(message + ExceptionMessage(ex));
                }
                
                System.out.println(message +  ExceptionMessage(ex));
                Debug.Log(message +  ExceptionMessage(ex));
            }
            catch(Exception e)
            {
                System.out.println( e.getMessage() );
                e.printStackTrace();
            }
        }
        
        public static void Log(int lLevel, Exception ex)
        {
            try
            {
                if(lLevel<=GetLogLevel())
                {
                    String message = ExceptionMessage(ex).concat(" <").concat(Thread.currentThread().getName()).concat("> ");

                    AddRow(message);
                }
                
                System.out.println(ExceptionMessage(ex).concat(" <").concat(Thread.currentThread().getName()).concat("> "));
            }
            catch(Exception e)
            {
               System.out.println( e.getMessage() );
               e.printStackTrace();
            }
        }

        public static String ExceptionMessage(Exception ex)
        {
            String msg = ex.getMessage();
            if(msg == null)
            {
                msg = ex.getClass().getName().concat(" exception.");
            }
        
            return msg;
        }
        
        public static String GetLogs()
        {
            String data = new String();
            synchronized(records)
            {
                for(int i=0; i<records.size(); i++)
                {
                    String row = (String) records.elementAt(i);
                    data = data.concat(row)+('\r')+('\n');
                }
            }
            return data;
        }
        
        public static void LogTime(String comment)
        {
            Date d = new Date();
            long time = d.getTime() - oldTime;
            oldTime = d.getTime();
            Log(0, new Long(time).toString() + comment);
        }      
} 


