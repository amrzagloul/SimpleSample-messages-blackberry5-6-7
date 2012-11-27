package com.injoit.examplepush.bbm.utils;

import java.io.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.system.*;
import net.rim.blackberry.api.homescreen.*;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.collection.util.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.xml.parsers.*;
import net.rim.device.api.notification.*;
import net.rim.device.api.compress.*;
import net.rim.device.api.util.*;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.blackberry.api.mail.*;

import com.injoit.examplepush.bbm.datastorages.*;

public class Utils
{
    static private UiApplication appHandler;
   // private static FontFamily typeface;    
    
    static public void SetAppHandler(UiApplication app)
    {
        appHandler = app;
    }
    
    static public UiApplication GetAppHandler()
    {
        return appHandler;
    }


    public static String getUserInfo()
    {
        String info = new String();
        String soft = new String();
        
        ApplicationManager appMan = ApplicationManager.getApplicationManager();
        ApplicationDescriptor[] appDes = appMan.getVisibleApplications();

        //check for the version of a standard RIM app. I like to use the ribbon app but you can check the version of any RIM module as they will all be the same.
        int size = appDes.length;
        String out = new String();
        for (int i = size-1; i>=0; --i)
        {
            if ((appDes[i].getModuleName()).equals("net_rim_bb_ribbon_app"))
            {
                soft = appDes[i].getVersion();
                break;
            }
        }
 
        // Example <userdata platform="4.2.1.78" device="8700g" camera="true" app="7.3.21"/>
        
        try
        {
            info = info.concat("<userdata ");
            info = info.concat("platform=\"");                    
            //info = info.concat(DeviceInfo.getPlatformVersion()); // it is java platform
            info = info.concat(soft);                              // it is RIM OS version
            info = info.concat("\" device=\"");
            info = info.concat(DeviceInfo.getDeviceName());
            info = info.concat("\" camera=");
            if(DeviceInfo.hasCamera())
            {
                info = info.concat("\"true\"");
            }
            else
            {
                 info = info.concat("\"false\"");
            }
            info = info.concat(" app=\"");
            info = info.concat(getVersion());
            //info = info.concat(ApplicationDescriptor.currentApplicationDescriptor().getVersion());
            info = info.concat("\"/>");
        }
        catch(Exception ex)
        {
            info = "<userdata>error</userdata>";
        }
        return info;
    }
    
    public static String getVersion()
    {
        return "1.0.0";
    }
    
    public static int byteArrayToInt(byte[] b, int start, int length)
    {
        int dt = 0;
        if ((b[start] & 0x80) != 0)
        {
            dt = Integer.MAX_VALUE;
        }
        
        for (int i = 0; i < length; i++)
        {
            dt = (dt << 8) + (b[start++] & 255);
        }
        return dt;
    }

    public static byte[] intToByteArray(int n, int byteCount)
    {
        byte[] res = new byte[byteCount];
        for (int i = 0; i < byteCount; i++)
        {
            res[byteCount - i - 1] = (byte) ((n >> i * 8) & 255);
        }
        return res;
    }
    
    public static String doUnZipped(byte[] zipData)
    {
        ByteArrayInputStream in = null;
        GZIPInputStream zin = null;
        try
        {         
            in = new ByteArrayInputStream(zipData);
            zin = new GZIPInputStream(in);
                
            byte[] temp = new byte[1024];
            DataBuffer db = new DataBuffer();
            for( ;; )
            {
                int bytesRead = zin.read( temp , 0 , temp.length);
                if( bytesRead <= 0 )
                {
                    break;
                }
                db.write(temp, 0, bytesRead);
            }
        
            return  new String(db.toArray()).toString();
        }
        catch(Exception ex)
        {
                //DebugStorage.getInstance().Log(0, "<Utils.doUnZipped()> It is OK. Received data is not compressed.", ex);
        }
        
        finally
        {
            try
            {
                if(zin!=null)
                zin.close();
                if(in!=null)
                in.close();
            }
            catch(Exception e)
            {
                // do nothing
            }
        }
        return "nothing";
    } 
    
    public static boolean sendSupportEmail(String address, String ccaddress)
    {
        boolean result = false;
        try
        {    
            //Transport transport = Session.waitForDefaultSession().getTransport();
            //if(!transport.isConnected())
            //{   
                String data = DebugStorage.getInstance().GetLogs();
                System.out.println(data);
                Multipart mp = new Multipart();
        
                //create the file
                SupportedAttachmentPart sap = new SupportedAttachmentPart(mp,"text", "logs.txt", data.getBytes());
                
                String messageBody = "This is DSTV Guide blackberry logs.";
                TextBodyPart tbp = new TextBodyPart(mp,messageBody);
                
                //add the file to the multipart
                mp.addBodyPart(tbp);
                mp.addBodyPart(sap);
                
                //create a message in the sent items folder
                Folder folders[] = Session.getDefaultInstance().getStore().list(Folder.SENT);    
                final Message message = new Message(folders[0]);
                
                //add recipients to the message and send
                try 
                {
                    Address toAdd = new Address(address, address);
                    Address toAddCC = new Address(ccaddress, ccaddress);
                    Address toAdds[] = new Address[2];
                    toAdds[0] = toAdd;
                    toAdds[1] = toAddCC;
                    
                    //TEST COPY
                    //Address toAddCopy = new Address("svat2003@ukr.net","svat2003@ukr.net");
                    //toAdds[1] = toAddCopy;
                    //////////////////////////////////////////////////////////////////////////
                    
                    message.addRecipients(Message.RecipientType.TO,toAdds);
                    
                    String emailAddress = "undefined blackberry user";
                    Session session = Session.getDefaultInstance();
                    if(session != null)
                    {
                        Store store = session.getStore();
                        ServiceConfiguration serviceConfig = store.getServiceConfiguration();
                        emailAddress = serviceConfig.getEmailAddress();
                    }
    
                    Address from = new Address(emailAddress, "Blackberry");
                    message.setFrom(from);
                    message.setSubject("Blackberry logs");
                    
                    message.setContent(mp);
                
                    Transport.send(message);
    
                    result = true;
                } 
                catch (Exception e) 
                {
                    Dialog.inform(e.toString());
                }
           // }
           // else
           // {
           //     Dialog.inform("No available transport");
           // }
        }
        catch (Exception e)
        {
            DebugStorage.getInstance().Log(0, "<Utils.sendEmail()> {support} ", e);
        }

        return result;
    }
    
    public static Bitmap resizeBitmap(Bitmap image, int _width, int _height)
    {    
        int width ;
        int height;
        
        int heightemg = image.getHeight();
        int widthemg = image.getWidth();
        int comp = 1;
        while(heightemg > _height || widthemg > _width)
        {
            heightemg = heightemg -1; 
            widthemg = widthemg -1;
            comp++;
        }
        float p ;
        if(heightemg > _height)
        {
            p = (image.getHeight()-0.5F)/(heightemg-0.5F);
            width = (int)(((image.getWidth()-0.5F)/p)+ 0.5F);
            height =heightemg;
        }
        else
        {
            p = (image.getWidth()-0.5F)/(widthemg-0.5F);
            height = (int)(((image.getHeight()-0.5F)/p)+ 0.5F);
            width =widthemg;
        }
        
        //Need an array (for RGB, with the size of original image)
        int rgb[] = new int[image.getWidth()*image.getHeight()];

        //Get the RGB array of image into "rgb"
        image.getARGB(rgb, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

        //Call to our function and obtain RGB2
        int rgb2[] = rescaleArray(rgb, image.getWidth(), image.getHeight(), width, height);

        //Create an image with that RGB array
        Bitmap temp2 = new Bitmap(width, height);

        temp2.setARGB(rgb2, 0, width, 0, 0, width, height);
    
        return temp2;
    }
    
    public static Bitmap resizedImage(Bitmap imageName, int width, int height) 
    {
        return resizedImage(imageName, width, height, Bitmap.SCALE_STRETCH);
    }
    
    public static Bitmap resizedImage(Bitmap imageName, int width, int height, int aspectRatioOption) {
        Bitmap actualBitmap = imageName;
        Bitmap resizedBitmap = new Bitmap(width, height);
        actualBitmap.scaleInto(resizedBitmap, Bitmap.FILTER_BILINEAR, aspectRatioOption);
        return resizedBitmap;
    }
    
    private static int[] rescaleArray(int[] ini, int x, int y, int x2, int y2)
    {
         int out[] = new int[x2*y2];
         for (int yy = 0; yy < y2; yy++)
         {
             int dy = yy * y / y2;
             for (int xx = 0; xx < x2; xx++)
             {
                  int dx = xx * x / x2;
                  out[(x2 * yy) + xx] = ini[(x * dy) + dx];
             }
         }
         return out;
    }
    
    public static Font getFont(int height)
    {
        Font f = Font.getDefault().derive(Font.PLAIN, height, Ui.UNITS_pt);

        try 
        {
            FontFamily typeface = FontFamily.forName("dstv-regular");
            f =  typeface.getFont(Font.PLAIN, height, Ui.UNITS_pt);
            FontManager.getInstance().unload("dstv");
        }
        catch (ClassNotFoundException e) 
        {
            DebugStorage.getInstance().Log(0, "<Utils.getFont()> ", e);
        }
        
        if(f == null) f = Font.getDefault().derive(Font.PLAIN, height, Ui.UNITS_pt);
        
        return  f;
        //return Font.getDefault().derive(Font.PLAIN, height, Ui.UNITS_pt);;

    }
    
    public static Font getFontBold(int height)
    {
        Font f = Font.getDefault().derive(Font.BOLD, height, Ui.UNITS_pt);

        try 
        {
            FontFamily typeface = FontFamily.forName("dstv-bold");
            f =  typeface.getFont(Font.BOLD, height, Ui.UNITS_pt);
        }
        catch (ClassNotFoundException e) 
        {
            DebugStorage.getInstance().Log(0, "<Utils.getFontBold()> ", e);
        }
        
        if(f == null) f = Font.getDefault().derive(Font.BOLD, height, Ui.UNITS_pt);

        return  f;
        //return Font.getDefault().derive(Font.BOLD, height, Ui.UNITS_pt);
    }
    
    static public Bitmap getBitmap(String name)
    {
        int width = Display.getWidth();
        Bitmap out;
        if(width <= 320)
        {
            out  =  Bitmap.getBitmapResource("320_" + name);
        }
        else //if(width<=480)
        {
            out  =  Bitmap.getBitmapResource("480_" + name);
        }
        //else
        //{
        //    out  =  Bitmap.getBitmapResource("640_" + name);
        //}
        
        return out;
    }
    
} 

