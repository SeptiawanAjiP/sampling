//package org.odk.collect.android.augmentedreality.scan;
//
//import com.adobe.creativesdk.foundation.AdobeCSDKFoundation;
//import com.adobe.creativesdk.foundation.auth.IAdobeAuthClientCredentials;
//import com.example.septiawanajipradan.skripsikode.ARKit.PARApplication;
//import com.example.septiawanajipradan.skripsikode.kamera.Keys;
//
//import org.odk.collect.android.augmentedreality.arkit.PARApplication;
//
///**
// * Created by doPanic on 26.02.14.
// */
//
//    public class SkripsiApp extends PARApplication implements IAdobeAuthClientCredentials {
//
//    private static final String CREATIVE_SDK_CLIENT_ID = Keys.CSDK_CLIENT_ID;
//    private static final String CREATIVE_SDK_CLIENT_SECRET = Keys.CSDK_CLIENT_SECRET;
//    private static final String CREATIVE_SDK_REDIRECT_URI = Keys.CSDK_REDIRECT_URI;
//    private static final String[] CREATIVE_SDK_SCOPES = Keys.CSDK_SCOPES;
//
//        @Override
//        public void onCreate() {
//            super.onCreate();
//            AdobeCSDKFoundation.initializeCSDKFoundation(getApplicationContext());
//        }
//
//        @Override
//        public String setApiKey() {
//            return "";
//        }
//
//    @Override
//    public String getClientID() {
//        return CREATIVE_SDK_CLIENT_ID;
//    }
//
//    @Override
//    public String getClientSecret() {
//        return CREATIVE_SDK_CLIENT_SECRET;
//    }
//
//    @Override
//    public String[] getAdditionalScopesList() {
//        return CREATIVE_SDK_SCOPES;
//    }
//
//    @Override
//    public String getRedirectURI() {
//        return CREATIVE_SDK_REDIRECT_URI;
//    }
//    }
