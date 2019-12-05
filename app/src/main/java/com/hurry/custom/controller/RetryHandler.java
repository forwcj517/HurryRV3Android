package com.hurry.custom.controller;

/**
 * Created by Administrator on 6/23/2018.
 */


import android.os.SystemClock;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Iterator;
import javax.net.ssl.SSLException;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

class RetryHandler implements HttpRequestRetryHandler {
    private static final HashSet<Class<?>> exceptionWhitelist = new HashSet();
    private static final HashSet<Class<?>> exceptionBlacklist = new HashSet();
    private final int maxRetries;
    private final int retrySleepTimeMS;

    public RetryHandler(int maxRetries, int retrySleepTimeMS) {
        this.maxRetries = maxRetries;
        this.retrySleepTimeMS = retrySleepTimeMS;
    }

    public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
        boolean retry = true;
        Boolean b = (Boolean)context.getAttribute("http.request_sent");
        boolean sent = b != null && b.booleanValue();
        if(executionCount > this.maxRetries) {
            retry = false;
        } else if(this.isInList(exceptionWhitelist, exception)) {
            retry = true;
        } else if(this.isInList(exceptionBlacklist, exception)) {
            retry = false;
        } else if(!sent) {
            retry = true;
        }

        if(retry) {
            HttpUriRequest currentReq = (HttpUriRequest)context.getAttribute("http.request");
            if(currentReq == null) {
                return false;
            }
        }

        if(retry) {
            SystemClock.sleep((long)this.retrySleepTimeMS);
        } else {
            exception.printStackTrace();
        }

        return retry;
    }

    static void addClassToWhitelist(Class<?> cls) {
        exceptionWhitelist.add(cls);
    }

    static void addClassToBlacklist(Class<?> cls) {
        exceptionBlacklist.add(cls);
    }

    protected boolean isInList(HashSet<Class<?>> list, Throwable error) {
        Iterator i$ = list.iterator();

        Class aList;
        do {
            if(!i$.hasNext()) {
                return false;
            }

            aList = (Class)i$.next();
        } while(!aList.isInstance(error));

        return true;
    }

    static {
        exceptionWhitelist.add(NoHttpResponseException.class);
        exceptionWhitelist.add(UnknownHostException.class);
        exceptionWhitelist.add(SocketException.class);
        exceptionBlacklist.add(InterruptedIOException.class);
        exceptionBlacklist.add(SSLException.class);
    }
}
