package com.injoit.examplepush.bbm.utils;

import net.rim.device.api.ui.*;
import net.rim.device.api.system.*;
import net.rim.blackberry.api.homescreen.*;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.collection.util.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.xml.parsers.*;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.component.Dialog;
import java.lang.Exception;

public class Debug
{
        public static long LOG_KEY = 0x1e67d2b516557d4cL;

        public static String ExceptionMessage(Exception ex)
        {
            String msg = ex.getMessage();
            if(msg == null)
            {
                msg = ex.getClass().getName().concat(" exception.");
            }       
            return msg;
        }
        
        public static void Log(String message)
        {
            message = "<".concat(Thread.currentThread().getName()).concat("> ").concat(message);
            EventLogger.logEvent(LOG_KEY, message.getBytes(), EventLogger.DEBUG_INFO);
        }
        
        public static void Log(String message, Exception ex)
        {
            ProcessException(ex);
            
            message = message.concat(" {").concat(ExceptionMessage(ex)).concat("}");
            message = "<".concat(Thread.currentThread().getName()).concat("> ").concat(message);
            EventLogger.logEvent(LOG_KEY, message.getBytes(), EventLogger.ERROR);
        }
        
        public static void Log(Exception ex)
        {
            ProcessException(ex);
            
            String message = ExceptionMessage(ex).concat(" <").concat(Thread.currentThread().getName()).concat("> ");
            EventLogger.logEvent(LOG_KEY, message.getBytes(), EventLogger.ERROR);
        }

        public static void Dbg(String message)
        {
            message = "<".concat(Thread.currentThread().getName()).concat("> ").concat(message);
            System.err.println(message);
        }
        
        public static void Error(String message)
        {
            Dialog.alert("An error has occured:\n\n".concat(message));
        }
        
        public static void Error(Exception ex)
        {
            ProcessException(ex);
            Error(ExceptionMessage(ex));
        }
        
        static private void ProcessException(Exception ex)
        {
            System.err.println(ExceptionMessage(ex));
            ex.printStackTrace();
        }
}

