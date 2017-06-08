/*
 * Decompiled with CFR 0_118.
 */
package org.odk.collect.android.augmentedreality.sensorkit;

public class PSKUtils {
    public static String matrix16ToString(float[] matrix) {
        if (matrix.length == 16) {
            return "" + matrix[0] + "\t" + matrix[1] + "\t" + matrix[2] + "\t" + matrix[3] + "\n" + matrix[4] + "\t" + matrix[5] + "\t" + matrix[6] + "\t" + matrix[7] + "\n" + matrix[8] + "\t" + matrix[9] + "\t" + matrix[10] + "\t" + matrix[11] + "\n" + matrix[12] + "\t" + matrix[13] + "\t" + matrix[14] + "\t" + matrix[15];
        }
        return "Matrix is not 4x4";
    }

    public static String floatArrayToString(float[] array) {
        StringBuilder builder = new StringBuilder();
        for (float v : array) {
            builder.append("" + v + " ");
        }
        builder.append("\n");
        return builder.toString();
    }

    public static String floatArrayToStringWithRadToDeg(float[] array) {
        StringBuilder builder = new StringBuilder();
        for (float v : array) {
            builder.append("" + 57.295776f * v + " ");
        }
        builder.append("\n");
        return builder.toString();
    }
}

