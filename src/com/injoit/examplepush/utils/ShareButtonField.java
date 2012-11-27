package com.injoit.examplepush.utils;

import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.collection.util.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.util.*;
import net.rim.device.api.i18n.SimpleDateFormat;

import com.injoit.examplepush.bbm.utils.*;
import com.injoit.examplepush.bbm.controls.*;
import com.injoit.examplepush.containers.*;

public class ShareButtonField extends Field implements ListStyleAbstract
{
    private int id=0;
    private int _drawPosition = 5; //single
    
    public static int SHARE_BBM      = 0;
    public static int SHARE_FACEBOOK = 1;
    public static int SHARE_TWITTER  = 2;
    public static int SHARE_EMAIL    = 3; 
    public static int SHARE_HEADER   = 4;

    public static int DRAWPOSITION_TOP = 0;
    public static int DRAWPOSITION_BOTTOM = 1;
    public static int DRAWPOSITION_MIDDLE = 2;
    public static int DRAWPOSITION_SINGLE = 3;
    private static final int CORNER_RADIUS = 18;  
    private static final int HPADDING = Display.getWidth() <= 320 ? 6 : 8;
    private static final int VPADDING = 4;
    private static final int COLOR_BACKGROUND = 0xececec;
    private static final int COLOR_BACKGROUND_HEADER = 0x000000;
    private static final int COLOR_BORDER = 0xaaaaaa;
    private static final int COLOR_BACKGROUND_FOCUS = 0xc8c8c8;
    
    private int tYOff = 0;
    private int picYOff = 0;

    private Bitmap picture;
    private LabelFieldColored _nameField;

    public ShareButtonField(String text, int context)
    {
        super( USE_ALL_WIDTH | Field.FOCUSABLE );
        id = context;
         
        _nameField = new LabelFieldColored(text);
        _nameField.setFont(Utils.getFont(9));
        
        if(id == SHARE_HEADER)
        {
            _nameField.setColor(0xffffff);
        }
        else
        {
            _nameField.setColor(0x000000);
        }
        
      
        switch(context)
        {
            case 0: //SHARE_BBM:
            {
                picture = Utils.getBitmap("share_bbm.png");
                break;
            }
            case 1:  //SHARE_FACEBOOK:
            {
                picture = Utils.getBitmap("share_facebook.png");
                break;
            }
            case 2: //SHARE_TWITTER:
            {
                picture = Utils.getBitmap("share_twitter.png");
                break;
            }
            case 3: //SHARE_EMAIL
            {
                picture = Utils.getBitmap("share_email.png");
                break;
            }
            case 4: //SHARE_HEADER
            {
                _nameField.setFont(Utils.getFontBold(9));
                picture = Utils.getBitmap("share_facebook.png");
                break;
            }
        }
    }
    
    public ShareButtonField(String text, Bitmap _icon)
    {
        super( USE_ALL_WIDTH | Field.FOCUSABLE );
        _nameField = new LabelFieldColored(text);
        _nameField.setFont(Utils.getFont(9));
        _nameField.setColor(0x000000);
        
        if(_icon != null)
        {
            //resize here
            picture = _icon;
        }
        else
        {
            picture = Utils.getBitmap("Mnu_Add2Fav.png");
        }
    }
    
    public int getPreferredWidth()
    {
        return Display.getWidth() - Display.getWidth()/4;
    }
    
    public int getPreferredHeight()
    {
        int height = Utils.getFont(9).getHeight();
        if(picture != null)
        {
            height = picture.getHeight() + HPADDING*2;
        }
        if(Utils.getFont(9).getHeight() + HPADDING*2 > height)
        {
            height = Utils.getFont(9).getHeight() + HPADDING*2;
        }
        
        return height;
    }
    
    public void layout( int width, int height )
    {
        setExtent( getPreferredWidth() , getPreferredHeight());
    }
    
    protected void paint( Graphics g )
    {
        if(id == SHARE_HEADER)
        {
            try 
            {
                g.setFont(_nameField.getFont());
                g.pushRegion( (getPreferredWidth() - _nameField.getFont().getAdvance(_nameField.getText()))/2 , HPADDING, _nameField.getFont().getAdvance(_nameField.getText()) , _nameField.getFont().getHeight(), 0, 0 );
                _nameField.paint2(g);
            } 
            finally 
            {
                g.popContext();
            }
        }
        else
        {
            if(picture != null)
            {
                if(picYOff == 0)
                {
                    picYOff = (getPreferredHeight() - picture.getHeight())/2;
                }
                g.drawBitmap( HPADDING, picYOff, picture.getWidth(), picture.getHeight(), picture, 0, 0 );
            }
            
            try 
            {
                g.setFont(_nameField.getFont());
                int xOff = picture.getWidth() + 2*HPADDING;
                if(tYOff == 0)
                {
                    tYOff = (getPreferredHeight() - _nameField.getFont().getHeight())/2;
                }
                g.pushRegion( xOff , tYOff, getPreferredWidth() - xOff , _nameField.getFont().getHeight() , 0, 0 );
                _nameField.paint2(g);
            } 
            finally 
            {
                g.popContext();
            }
        }
    }
    
    protected void paintBackground( Graphics g )
    {
        int oldColour = g.getColor();
        
        int B_COLOR = id == SHARE_HEADER? COLOR_BACKGROUND_HEADER : COLOR_BACKGROUND;

        int background = g.isDrawingStyleSet( Graphics.DRAWSTYLE_FOCUS ) ? COLOR_BACKGROUND_FOCUS : B_COLOR;
        try 
        {
            if( _drawPosition == 0 )
            {
                // Top
                g.setColor( background );
                g.fillRoundRect( 0, 0, getWidth(), getHeight() + CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS );
                g.setColor( COLOR_BORDER );
                g.drawRoundRect( 0, 0, getWidth(), getHeight() + CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS );
                g.drawLine( 0, getHeight() - 1, getWidth(), getHeight() - 1 );
            } 
            else if( _drawPosition == 1 ) 
            {
                // Bottom 
                g.setColor( background );
                g.fillRoundRect( 0, -CORNER_RADIUS, getWidth(), getHeight() + CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS );
                g.setColor( COLOR_BORDER );
                g.drawRoundRect( 0, -CORNER_RADIUS, getWidth(), getHeight() + CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS );
            } 
            else if( _drawPosition == 2 ) 
            {
                // Middle
                g.setColor( background );
                g.fillRoundRect( 0, -CORNER_RADIUS, getWidth(), getHeight() + 2 * CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS );
                g.setColor( COLOR_BORDER );
                g.drawRoundRect( 0, -CORNER_RADIUS, getWidth(), getHeight() + 2 * CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS );
                g.drawLine( 0, getHeight() - 1, getWidth(), getHeight() - 1 );
            } 
            else 
            {
                // Single
                g.setColor( background );
                g.fillRoundRect( 0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS );
                g.setColor( COLOR_BORDER );
                g.drawRoundRect( 0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS );
            }
        } 
        finally 
        {
            g.setColor( oldColour );
        }
    }
    
    protected void drawFocus( Graphics g, boolean on )
    { 
        boolean oldDrawStyleFocus = g.isDrawingStyleSet( Graphics.DRAWSTYLE_FOCUS );
        try 
        {
            if(on) 
            {
                g.setDrawingStyle( Graphics.DRAWSTYLE_FOCUS, true );
            }
            paintBackground( g );
            paint( g );
        } 
        finally 
        {
            g.setDrawingStyle( Graphics.DRAWSTYLE_FOCUS, oldDrawStyleFocus );
        }
    }
    
    public int getContext()
    {
        return id;
    }
    
    public void setContext(int _id)
    {
        id = _id;
    }

    protected boolean keyChar( char character, int status, int time ) 
    {
        if( character == Characters.ENTER ) {
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
        switch( action ) {
            case ACTION_INVOKE: {
                clickButton(); 
                return true;
            }
        }
        return super.invokeAction( action );
    }        
         
    public void clickButton() 
    {
        fieldChangeNotify(id);
    }
       
    public void setDirty( boolean dirty ) {}
    public void setMuddy( boolean muddy ) {}
    
    public boolean isFocusable()
    {
        return this.isEditable();
    }
    
    public void setDrawPosition( int drawPosition )
    {
        _drawPosition = drawPosition;
    }
}
