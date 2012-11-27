package com.injoit.examplepush.push.models;

import net.rim.device.api.system.*;
import java.io.*;
import java.util.*;
import net.rim.device.api.util.*;
import net.rim.device.api.io.http.*;
import net.rim.device.api.i18n.*;
import net.rim.device.api.util.*;
import net.rim.device.api.collection.util.*;

public class PushNotificationObject implements Persistable
{
    private String description;
    private String creationTime;
    private long timeForSort;
    private boolean isNotificationUnRead = false;
    
    public PushNotificationObject(String _description)
    {
        this.description = _description;
        timeForSort = System.currentTimeMillis();
        
        Date now = new Date();
        SimpleDateFormat sdFormat = new SimpleDateFormat("MMM dd, HH:mm:ss");
        StringBuffer buffer = new StringBuffer();
        sdFormat.format(now, buffer, null);
        String dateStr = buffer.toString();
        this.creationTime = dateStr;
    }

    public String getDescription() 
    {
        return description;
    }
    
    public String getCreationTime() 
    {
        return creationTime;
    }
    
    public boolean getNotificationReadStatus() 
    {
        return isNotificationUnRead;
    }
    
    public void setNotificationReadStatus( boolean _notificationReadStatus ) 
    {
        this.isNotificationUnRead = _notificationReadStatus;
    }
    
    public long getTimeForSort() 
    {
        return timeForSort;
    }
        
        
        
       
}
