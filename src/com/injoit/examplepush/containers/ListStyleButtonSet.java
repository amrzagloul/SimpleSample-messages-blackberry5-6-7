package com.injoit.examplepush.containers;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.system.*;
import net.rim.device.api.util.*;

public class ListStyleButtonSet extends VerticalFieldManager 
{
    public static final int MARGIN = 5;
    
    public ListStyleButtonSet()
    {
        super( NO_VERTICAL_SCROLL );
        //setPadding( MARGIN , MARGIN, MARGIN, MARGIN );
    }
    
    protected void sublayout( int maxWidth, int maxHeight )
    {
        super.sublayout( maxWidth, maxHeight );
        
        int numChildren = this.getFieldCount();
        if( numChildren > 0 ) 
        {
            if( numChildren == 1 ) 
            {
                Field child = getField( 0 );
                if( child instanceof ListStyleAbstract ) 
                {
                    ( (ListStyleAbstract) child ).setDrawPosition( ListStyleAbstract.DRAWPOSITION_SINGLE );
                }
            } 
            else 
            {
                int index = 0;
                Field child = getField( index );
                if( child instanceof ListStyleAbstract ) 
                {
                    ( (ListStyleAbstract) child ).setDrawPosition( ListStyleAbstract.DRAWPOSITION_TOP );
                }

                for( index = 1; index < numChildren - 1 ; index++ ) 
                {
                    child = getField( index );
                    if( child instanceof ListStyleAbstract ) 
                    {
                        ( (ListStyleAbstract) child ).setDrawPosition( ListStyleAbstract.DRAWPOSITION_MIDDLE );
                    }
                }
                child = getField( index );
                if( child instanceof ListStyleAbstract ) 
                {
                    ( (ListStyleAbstract) child ).setDrawPosition( ListStyleAbstract.DRAWPOSITION_BOTTOM );
                }
            }
        }
    }
}

