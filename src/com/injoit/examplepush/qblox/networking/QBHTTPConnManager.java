package com.injoit.examplepush.qblox.networking;

import javax.microedition.io.*;

import net.rim.device.api.io.transport.*;
import net.rim.device.api.io.transport.options.*;
import net.rim.device.api.util.*;
import net.rim.device.api.io.transport.options.*;
import net.rim.device.api.collection.util.*;

public class QBHTTPConnManager
{
    ConnectionFactory _factory = new ConnectionFactory();
    private QBHTTPAnswerListener listener;
    private BigVector postData;

    public QBHTTPConnManager(final int type, final String http, BigVector _postData, final int handler, final QBHTTPAnswerListener _listener) 
    {
        listener = _listener;
        postData = _postData;
        // Create a preference-ordered list of transports.
        int[] _intTransports = { TransportInfo.TRANSPORT_TCP_WIFI,
                                 TransportInfo.TRANSPORT_BIS_B, 
                                 TransportInfo.TRANSPORT_MDS,
                                 TransportInfo.TRANSPORT_WAP2,
                                 TransportInfo.TRANSPORT_TCP_CELLULAR };

        // Remove any transports that are not currently available.
        for(int i = 0; i < _intTransports.length; i++) 
        {
            int transport = _intTransports[i];
            if (!TransportInfo.isTransportTypeAvailable(transport) || !TransportInfo.hasSufficientCoverage(transport)) 
            {
                Arrays.removeAt(_intTransports, i);
            }
        }

        // Set ConnectionFactory options.
        if (_intTransports.length > 0) 
        {
            _factory.setPreferredTransportTypes(_intTransports);
        }

        _factory.setAttemptsLimit(5); 

        // Open a connection on a new thread.
        Thread t = new Thread(new Runnable() 
        {
            public void run() 
            {
                ConnectionDescriptor cd = _factory.getConnection(http);
                if (cd != null)
                {
                    Connection c = cd.getConnection();
                    new QBHTTPOut(type, postData, c, handler, listener);
                }
                else
                {
                    System.out.println("HTTPConnManager: UNABLE TO CREATE CONNECTION!");
                }
            }
        });
        t.start();
    }
}
