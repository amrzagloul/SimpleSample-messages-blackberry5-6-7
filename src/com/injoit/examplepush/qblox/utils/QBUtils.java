package com.injoit.examplepush.qblox.utils;

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

import com.injoit.examplepush.qblox.*;
import com.injoit.examplepush.utils.*;
import com.injoit.examplepush.containers.*;
import com.injoit.examplepush.bbm.utils.*;

public class QBUtils 
{
    static public void RateOnQBlox(RateListener rateListener, Object object, int value)
    {
        String username = "";
        String password = "";
        
        Vector up = SavedData.getUserInfo();
        if (up != null) 
        {
            username = (String)up.elementAt(0);
            password = (String)up.elementAt(1);
            
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
        
        if(username.length()>0 && password.length()>0)
        {
        //    QBRatingSenderScreen rscr = new QBRatingSenderScreen(rateListener, object, value);
          //  Utils.GetAppHandler().pushScreen(rscr);
        }
        /*else
        {
            AuthScreen ascr = new AuthScreen(rateListener, object, new Integer(value));
            Utils.GetAppHandler().pushScreen(ascr);
        }*/
    }
    
    static public void UpdateRatings(RatingsUpdateListener rateListener, BigVector data)
    {
        String username = "";
        String password = "";
        
        Vector up = SavedData.getUserInfo();
        if (up != null) 
        {
            username = (String)up.elementAt(0);
            password = (String)up.elementAt(1);
            
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
        
        if(username.length()>0 && password.length()>0)
        {
            if(data != null)
            {
                Date now = new Date();
                QBRatingsSyncScreen rscr = new QBRatingsSyncScreen(rateListener, data);
                Utils.GetAppHandler().pushScreen(rscr);
            }
        }
        /*else
        {
            AuthScreen ascr = new AuthScreen(rateListener, null, data);
            Utils.GetAppHandler().pushScreen(ascr);
        }*/
    }
    
    public static String hmacsha1(String key, String message) throws CryptoTokenException, CryptoUnsupportedOperationException, IOException 
    {
        HMACKey k = new HMACKey(key.getBytes());
        HMAC hmac = new HMAC(k, new SHA1Digest());
        hmac.update(message.getBytes());
        byte[] mac = hmac.getMAC();
        
        return convertToHex(mac);
    }
    
    public static String convertToHex(byte[] data) 
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
}

