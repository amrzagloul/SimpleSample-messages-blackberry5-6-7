package com.injoit.examplepush.push.controls;

import java.util.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*; 
import net.rim.device.api.system.*;
import net.rim.device.api.collection.util.*;
import net.rim.device.api.ui.decor.*;

import com.injoit.examplepush.utils.*;
import com.injoit.examplepush.bbm.utils.*;
import com.injoit.examplepush.push.models.PushNotificationObject;

public class PushNotificationField extends Field 
{
    private static final int NORMAL = 0;
    private static final int FOCUS = 1;
    private static final int RADIUS = 0;
    
    private static final int FOCUS_COLOR = 0x0ba1c8;     
    private static final int BACKGROUND_COLOR = 0x000000;
    private static final int BORDER_COLOR = 0x000000; 
    private PushNotificationObject pNO;
    private Vector timeAndDescr;
    
    public PushNotificationField(PushNotificationObject _pNO) 
    {
        super(Field.FIELD_HCENTER|DrawStyle.HCENTER);
        this.pNO = _pNO;
    }
    
    public PushNotificationObject getPushNotificationObject() 
    {
        return pNO;
    }
     
    public int getPreferredHeight() 
    {
        return 30;
    }
        
    public int getPreferredWidth() 
    {
        return Display.getWidth();
    }
        
    protected void layout (int width, int height) 
    {
        setExtent(getPreferredWidth(), getPreferredHeight());
    }
    
    protected void paint(Graphics g) 
    {
        int index = g.isDrawingStyleSet( Graphics.DRAWSTYLE_FOCUS ) ? FOCUS : NORMAL;
        g.setBackgroundColor(BACKGROUND_COLOR);
                
        if(index==FOCUS)
        {
            g.setColor(FOCUS_COLOR);
            g.fillRoundRect( 0, 0, getPreferredWidth(), getHeight(), RADIUS, RADIUS );
            g.setColor( BORDER_COLOR );
            g.drawRoundRect( 0, 0, getPreferredWidth(), getHeight(), RADIUS, RADIUS );
        }
        else
        {
            g.setColor( BACKGROUND_COLOR );
            g.fillRoundRect( 0, 0, getPreferredWidth(), getHeight(), RADIUS, RADIUS );
            g.setColor( BORDER_COLOR );
            g.drawRoundRect( 0, 0, getPreferredWidth(), getHeight(), RADIUS, RADIUS );
        }
        
        g.setColor(0xffffff);
        
        int textWidht = getPreferredWidth() - (5 * 2) - 15;
        String notificationStr = pNO.getCreationTime() + " " + pNO.getDescription();

        if (pNO.getNotificationReadStatus() == true) 
        {
            g.setFont(Utils.getFont(8));
            timeAndDescr = TextUtils.wrapText(notificationStr, textWidht, Utils.getFont(8));            
            int normalFontlinewidth = Utils.getFont(8).getAdvance(notificationStr);            
            if (normalFontlinewidth > textWidht)
            {
                timeAndDescr = TextUtils.wrapText(notificationStr, textWidht, Utils.getFont(8));
                g.drawText(timeAndDescr.elementAt(0) + " ... ", 5 ,((getPreferredHeight() - g.getFont().getHeight()) / 2));
            }
            else
            {
                g.drawText(notificationStr, 5 ,((getPreferredHeight() - g.getFont().getHeight()) / 2));
            }
        }
        else
        {
            g.setFont(Utils.getFontBold(8));
            int boldFontlinewidth = Utils.getFontBold(8).getAdvance(notificationStr);
            if (boldFontlinewidth > textWidht)
            {
                timeAndDescr = TextUtils.wrapText(notificationStr, textWidht, Utils.getFontBold(8));
                g.drawText(timeAndDescr.elementAt(0) + " ... ", 5 ,((getPreferredHeight() - g.getFont().getHeight()) / 2));
            }
            else
            {
                g.drawText(notificationStr, 5 ,((getPreferredHeight() - g.getFont().getHeight()) / 2));
            }
        }
    }
    
    protected void drawFocus( Graphics g, boolean on ) 
    {
        g.setDrawingStyle( Graphics.DRAWSTYLE_FOCUS, true );
        paint( g );
    }
    
    protected boolean keyChar( char character, int status, int time ) 
    {
        if( character == Characters.ENTER ) 
        {
            clickButton();
            return true;
        }
        return super.keyChar( character, status, time );
    }
    
    protected boolean navigationClick( int status, int time ) 
    {
        clickButton(); 
        return true;    
    }
    
    protected boolean trackwheelClick( int status, int time )
    {        
        clickButton();    
        return true;
    }
    
    protected boolean invokeAction( int action ) 
    {
        switch( action ) 
        {
            case ACTION_INVOKE: 
            {
                clickButton(); 
                return true;
            }
        }
        return super.invokeAction( action );
    }    

    public void setDirty( boolean dirty )
    {
        
    }
     
    public void setMuddy( boolean muddy ) 
    {

    }
         
    public void clickButton() 
    {
        fieldChangeNotify( 0 );
    }
    
    public boolean isFocusable()
    {
        return isEditable();
    }
    
}
