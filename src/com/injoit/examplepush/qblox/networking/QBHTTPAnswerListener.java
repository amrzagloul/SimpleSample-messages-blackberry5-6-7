package com.injoit.examplepush.qblox.networking;

public abstract interface QBHTTPAnswerListener
{
    public abstract void ProcessAnswer(int handler, String result);
    public abstract void ProcessError(int handler, String message);
}
