package com.example.lab4.lab4android;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by varun on 4/9/2017.
 */


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG="firebaseinstanceIDserv";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedtoken= FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,"New token: "+refreshedtoken);

    }

}
