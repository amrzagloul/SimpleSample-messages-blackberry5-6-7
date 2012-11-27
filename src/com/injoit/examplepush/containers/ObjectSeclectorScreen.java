package com.injoit.examplepush.containers;

import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.decor.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.image.*;
import net.rim.device.api.collection.util.*;

import com.injoit.examplepush.utils.ShareButtonField;
import com.injoit.examplepush.tvguide.controls.*;
import com.injoit.examplepush.bbm.datastorages.*;

public class ObjectSeclectorScreen extends PopupScreen implements FieldChangeListener
{
    private ObjectSelectorListener listener;
    private BigVector objects;
    
    public ObjectSeclectorScreen(ObjectSelectorListener _listener, BigVector _objects, String _title) 
    {
        super(new VerticalFieldManager(Manager.VERTICAL_SCROLL), DEFAULT_MENU|DEFAULT_CLOSE);
         listener = _listener;
        objects = _objects;
        
        setBackground(BackgroundFactory.createSolidTransparentBackground(0x000000, 0));
        setBorder(BorderFactory.createSimpleBorder(new XYEdges(), Border.STYLE_TRANSPARENT));

        try 
        {
            ListStyleButtonSet set = new ListStyleButtonSet();
            
            if (_title.equals("")) {}
            else
            {
                ShareButtonField bHeader = new ShareButtonField(_title, ShareButtonField.SHARE_HEADER);
                bHeader.setEditable(false);
                set.add(bHeader);
            } 
          
            for(int i=0; i<_objects.size(); i++)
            {
                Object ob =  _objects.elementAt(i);
              
                
                if(ob instanceof TVGuideListMenuObject){
                    TVGuideListMenuObject tvGuideListMenuObject = (TVGuideListMenuObject) ob;
                    ShareButtonField b = new ShareButtonField(tvGuideListMenuObject.getName() , tvGuideListMenuObject.getBitmap()); 
                    b.setEditable(true);
                    b.setCookie(_objects.elementAt(i));
                    b.setChangeListener(this);
                    set.add(b);
                }
                else
                {
                    ShareButtonField b = new ShareButtonField( _objects.elementAt(i).toString() , null); 
                    b.setEditable(true);
                    b.setCookie(_objects.elementAt(i));
                    b.setChangeListener(this);
                    set.add(b);
                }
            }

            add(set);
        } 
        catch (Exception ex) 
        {
            DebugStorage.getInstance().Log(0, "<SharePopupScreen> error ", ex);
        }

        // Show popup screen
        UiApplication.getUiApplication().pushModalScreen(this);
    }

    public boolean onClose() 
    {
        try 
        {
            UiApplication.getUiApplication().popScreen(this);
        } 
        catch (Exception ex) 
        {
            DebugStorage.getInstance().Log(0, "<SharePopupScreen.onClose()> error ", ex);
        }
        return true;
    }
    
    public void fieldChanged(Field field, int context) 
    {
        if(listener!=null)
        {
            if(field instanceof ShareButtonField)
            {
                final ShareButtonField b = (ShareButtonField) field;
                UiApplication.getUiApplication().invokeLater(new Runnable()
                {
                    public void run() 
                    {
                        onClose();
                        listener.onObjectSelected(b.getCookie()); 
                        //onClose();
                    }
                });
            }
        }
    }
        
    public boolean keyChar(char key, int status, int time) 
    {
        boolean retval = false;
        switch (key) 
        {
                case Characters.ESCAPE: 
                {
                    //if it is not country and packet selection, then allow close
                    boolean allowClose = true;
                    if(objects != null)
                    {
                        for(int i=0; i<objects.size(); i++)
                        {
                            Object ob =  objects.elementAt(i);
                      
                        }
                    }
                    
                    if(allowClose == true)
                    {
                        close();
                    }
                    
                    retval = true;
                    break;
                }
                default:
                        retval = super.keyChar(key, status, time);
                        break;
        }
        return retval;
    }
    
    public void makeMenu(Menu menu, int instance) 
    {
        // do nothing here
    }
}
