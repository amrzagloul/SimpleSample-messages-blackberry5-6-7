package com.injoit.examplepush.bbm.datastorages;

import java.util.*;
import net.rim.device.api.util.*;
import net.rim.device.api.collection.util.*;
import net.rim.device.api.system.*;
import net.rim.device.api.i18n.SimpleDateFormat;

import com.injoit.examplepush.bbm.models.*;
import com.injoit.examplepush.bbm.constants.*;

public class OptionStorage
{
        public static final long STORE_KEY = 0xb36a8beb21aa95d7L;
        private static final long GUID = 0xbb4dd61c5d004c17L;
        
        private static OptionStorage instance;
        private static Hashtable records;
        private static Boolean lock = new Boolean(true);  
        private static PersistentObject  persist;

        static
        {
            persist = PersistentStore.getPersistentObject( STORE_KEY );
            if( persist.getContents() == null )
            {
                persist.setContents( new Hashtable() );
                persist.commit();
            } 
            records = (Hashtable)persist.getContents();
        }

        public static synchronized OptionStorage getInstance()
        {
            if(instance == null)
            {
                instance = (OptionStorage)RuntimeStore.getRuntimeStore().get(GUID);
                if(instance == null)
                {
                    OptionStorage newInstance = new OptionStorage();
                    RuntimeStore.getRuntimeStore().put(GUID, newInstance);
                    instance = newInstance;
                }
            }      
            return instance;
        }
        
        public void addNew(Option option)
        throws Exception
        {
            synchronized(records)
            {
                records.put(option.getName(), option);
                persist.commit();
            }
        }
        
        public void UpdateOption(String name, String _value)
        throws Exception
        {
            synchronized(records)
            {
                Option item = (Option) records.get(name);
                item.setValue(0, _value);
                persist.commit();
            }
        }
        
        public int size()
        {
            int res = 0;
            synchronized(records)
            {
                res = records.size();
            }
            
            return res;
        }
        
        public void SaveOptions()
        {
            synchronized(records)
            {
                persist.commit();
            }
        } 
        
        public Enumeration EnumerateOptions()
            throws Exception
        {
            Enumeration e = null;
            synchronized(records)
            {
               e = records.elements();
            }
            return e;
        } 
        
        public Option getOption(String name)
        {
            Option item = (Option) records.get(name);
            return item;
        }  
       
        public boolean exists(String name)
        {
            boolean result = false;  
            synchronized(records)
            {
                result = records.containsKey(name);
            }
            return result;
        }  
        
        public void resetOption(String name)
        {
            Option item = (Option) records.get(name);
            item.setCurrentValueIndex(0);
            SaveOptions();    
        }
        
        public void CreateOptions()
        {
            try
            {
                if(exists("Log level")==false)
                {
                    Option item = new Option("Log level", DebugStorage.getInstance().LOG_LEVELS, 2);
                    item.setCurrentValueIndex(2);
                    item.setDescription("Set debug log level");
                    OptionStorage.getInstance().addNew(item);
                }
                
                if(exists("Checkin")==false)
                {
                    Option item = new Option("Checkin", Constants.CHECKIN_SETTINGS, 0);
                    item.setDescription("Checking status");
                    OptionStorage.getInstance().addNew(item);
                }
                
                if(exists("Date Offset")==false)
                {
                    Option item = new Option("Date Offset", new String[]{"0L","0L"}, 0);
                    item.setDescription("Date Offset");
                    OptionStorage.getInstance().addNew(item);
                }
                
                if(exists("Filter")==false)
                {
                    Option item = new Option("Filter", new String[]{"Off","Genre", "Favourites"}, 0);
                    item.setDescription("Filter options");
                    OptionStorage.getInstance().addNew(item);
                }
                
                if(exists("Push notifications")==false)
                {
                    Option item = new Option("Push notifications", Constants.PUSH_NOTIFICARIONS_SETTINGS, 0);
                    item.setDescription("Push notifications");
                    OptionStorage.getInstance().addNew(item);
                }
            }
            catch(Exception ex)
            {
                DebugStorage.getInstance().Log(0, "<OptionStorage.CreateOptions()> error ", ex);
            }
        } 
} 

//////////////////////////////////////////////////////////////////////////////////////////////
// Compares 2 objects
//////////////////////////////////////////////////////////////////////////////////////////////
class ObjectComparator implements Comparator
{
    public int compare(Object o1, Object o2)
    {
        int result = 1;
        if(o1 != null && o2 != null)
        {
            if(o1 == o2)
            {
                result = 0;
            }
        }
        return result;
    }
    
    public boolean equals(Object obj)
    {
        return true;
    }
}
