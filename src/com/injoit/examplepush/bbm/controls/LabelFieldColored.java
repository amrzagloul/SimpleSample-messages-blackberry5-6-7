package com.injoit.examplepush.bbm.controls;

import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.collection.util.*;
import net.rim.device.api.ui.container.*;
import java.util.*;
import net.rim.device.api.util.*;
import net.rim.device.api.i18n.SimpleDateFormat;

public class LabelFieldColored extends LabelField
{
    private Vector v = new Vector();
    private String s = "";
    
    public LabelFieldColored(Object text)
    {
        super(text);
    }
    
    public LabelFieldColored(Object text, int _color, Font _font)
    {
        super(text);
        color = _color;
        if(_font !=null)
        {
            setFont(_font);
        }
    }
    
    public LabelFieldColored(Object text, int offset, int lenght, long style)
    {
        super(text, offset, lenght, style);
    }
    
    public LabelFieldColored(Object text, long style)
    {
        super(text, style);
    }
    
    public LabelFieldColored()
    {
        super();
    }
    
    public void invalidate()
    {
        super.invalidate();
    }
    
    private int color = 0x000000; 
    public void paint(Graphics g)
    {
         g.setFont(getFont());
         g.setColor(color);
         super.paint(g);
    }
    
    public void paint2(Graphics g)
    {
        if(v.size()==0)
        {
            v = wrap(getText(), g.getClippingRect().width, getFont());
        }
        else
        {
            if(v.elementAt(0) != null)
            {
                String word = (String) v.elementAt(0);
                if(word.length() == 0 && getText().length()>0)
                {
                    v = wrap(getText(), g.getClippingRect().width, getFont());
                }
            }
        }
        
        g.setColor(color);
        int yOff = 0;
        for(int j=0; j<v.size(); j++)
        {
            g.drawText((String) v.elementAt(j), 0, yOff); 
            yOff+=getFont().getHeight();
        } 
    }
    
    public void paint3(Graphics g)
    {
        if(v.size()==0)
        {
            v = wrap(getText(), g.getClippingRect().width, getFont());
        }
        g.setColor(color);
        int yOff = 0;
        int lim = v.size();
        if(lim >2) lim = 2;
        for(int j=0; j<lim; j++)
        {
            g.drawText((String) v.elementAt(j), 0, yOff); 
            yOff+=getFont().getHeight();
        } 
    }
    
    public void paint4(Graphics g, int w)
    {
        if(s.length()==0)
        {
            if(getFont().getAdvance(getText()) > w)
            {
                for(int l=0; l<getText().length(); l++)
                {
                    String temp = getText().substring(0, l);
                    if(getFont().getAdvance(temp) >= w)
                    {
                        s = getText().substring(0, l - 3) + "...";
                        break;
                    } 
                }
                
                if(s.length()==0)
                {
                    s = getText();
                }
            }
            else
            {
                s = getText();
            }
        }
        g.setColor(color);
        g.drawText(s , 0, 0); 
    }
    
    public void setColor(int _value)
    {
        this.color = _value;
    }
    
    public int getColor()
    {
        return this.color;
    }
    
    public void layout( int width, int height )
    { 
        v = wrap(getText(), width, getFont());
        int h = this.getFont().getHeight();
        super.setExtent( width, v.size() * h );
    } 
    
    public Vector wrap (String text, int width, Font font) 
    {
        Vector result = new Vector ();
        String remaining = text;
        while (remaining.length()>=0)
        {
            int index = getSplitIndex(remaining, width, font);
            if (index == -1)  break;
            result.addElement(remaining.substring(0,index));
            remaining = remaining.substring(index);
            if (index == 0) break;
            if(remaining.length()==0) break;
        }
        return result;
    }  
      
    private int getSplitIndex(String bigString, int width, Font font)
    {
        int index = -1;
        int lastSpace = -1;
        String smallString="";
        boolean spaceEncountered = false;
        boolean maxWidthFound = false;
    
        for (int i=0; i<bigString.length(); i++)
        {
            char current = bigString.charAt(i);
            smallString += current; 
            if (current == ' ')
            {
                lastSpace = i;
                    spaceEncountered = true;
            }
            int linewidth = font.getAdvance(smallString,0,  smallString.length()); 
            if(linewidth>width)
            {
                if (spaceEncountered) 
                {
                    index = lastSpace+1;
                }
                else 
                {
                    index = i;
                }
                maxWidthFound = true;
                break;
            }    
        }
        if (!maxWidthFound) index = bigString.length();
        return index;
    }
} 
