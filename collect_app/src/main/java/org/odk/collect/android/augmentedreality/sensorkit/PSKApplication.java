/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.app.Application
 *  android.content.Context
 */
package org.odk.collect.android.augmentedreality.sensorkit;

import android.app.Application;
import android.content.Context;

public class  PSKApplication
extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
    }

    public static Context getAppContext() {
        return context;
    }
}

