package com.injoit.examplepush.containers;

import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.util.*;

public interface RatingsUpdateListener
{ 
    public abstract void RatingsUpdateError(String text);
    public abstract void RatingsUpdated(); 
} 
