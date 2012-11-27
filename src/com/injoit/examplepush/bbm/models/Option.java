package com.injoit.examplepush.bbm.models;

import net.rim.device.api.util.Persistable;

public class Option implements Persistable
{
    private String name;
    private String[] values;
    private int defaultOption;
    private boolean changed;
    private int currentOption;
    private String description;
    
    public  Option(String name, String[] values, int defaultOption)
    {
        this.name=name;
        this.values = values;
        this.defaultOption = defaultOption;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String[] getValues()
    {
        return values;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String _val)
    {
        description = _val;
    }

    public int getDefaultValueIndex()
    {
        return defaultOption;
    }
    
    public int getCurrentValueIndex()
    {
        return currentOption;
    }
    
    public void setCurrentValueIndex(int index)
    {
        currentOption = index;
    }
    
    public String getCurrentOptionValue()
    {
        return values[currentOption];
    }
    
    public boolean getChanged()
    {
        return changed;
    }
    
    public void setValue(int index, String str) 
    {
        values[index] = str;
    }
    
    public String getValue()
    {
        return values[currentOption];
    }
    
    public boolean setChanged(boolean changed)
    {
        boolean prev = this.changed;
        this.changed = changed;
        return prev;
    }
    
    public String toString()
    {
        return this.getName();
    }
}
