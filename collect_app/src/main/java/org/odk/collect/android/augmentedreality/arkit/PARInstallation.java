/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.content.Context
 */
package org.odk.collect.android.augmentedreality.arkit;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

public class PARInstallation {
    private static final String INSTALLATION = "PAR-android";
    private static String sID = null;

    public static synchronized String id(Context context) {
        if (sID == null) {
            File installation = new File(context.getFilesDir(), "PAR-android");
            try {
                if (!installation.exists()) {
                    PARInstallation.writeInstallationFile(installation);
                }
                sID = PARInstallation.readInstallationFile(installation);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sID;
    }

    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int)f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }
}

