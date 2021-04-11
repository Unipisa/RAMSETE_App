package com.example.ramsete;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

// Defines a generic receiver used to pass data to Activity from a Service
public class EventReceiver extends ResultReceiver {
    private Receiver receiver;

    // Constructor takes a handler
    public EventReceiver(Handler handler) {
        super(handler);
    }

    // Setter for assigning the receiver
    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    // Defines our event interface for communication
    public interface Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }

    // Delegate method which passes the result to the receiver if the receiver has been assigned
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (receiver != null) {
            receiver.onReceiveResult(resultCode, resultData);
        }
    }
}