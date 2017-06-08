/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.util.FloatMath
 */
package org.odk.collect.android.augmentedreality.sensorkit;

public class PSKVector3 {
    public float x;
    public float y;
    public float z;

    public PSKVector3() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
    }

    public PSKVector3(PSKVector3 other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public PSKVector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PSKVector3(double x, double y, double z) {
        this.x = (float)x;
        this.y = (float)y;
        this.z = (float)z;
    }

    public static PSKVector3 Zero() {
        return new PSKVector3();
    }

    public static PSKVector3 One() {
        return new PSKVector3(1.0f, 1.0f, 1.0f);
    }

    public void toOne() {
        this.x = 1.0f;
        this.y = 1.0f;
        this.z = 1.0f;
    }

    public void toZero() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
    }

    public float magnitude() {
        return Float.parseFloat(Double.toString(Math.sqrt((float)(this.x * this.x + this.y * this.y + this.z * this.z))));
    }

    public float sqrMagnitude() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public static float magnitude(float x, float y, float z) {
        return Float.parseFloat(Double.toString(Math.sqrt((float)(x * x + y * y + z * z))));
    }

    public PSKVector3 normalize() {
        float s = 1.0f / this.magnitude();
        return new PSKVector3(s * this.x, s * this.y, s * this.z);
    }

    public void normalize(float _x, float _y, float _z) {
        float s = 1.0f / PSKVector3.magnitude(_x, _y, _z);
        this.x = s * _x;
        this.y = s * _y;
        this.z = s * _z;
    }

    public void normalize(float _x, float _y, float _z, float _m) {
        this.x = _x / _m;
        this.y = _y / _m;
        this.z = _z / _m;
    }

    public float angleTowards(PSKVector3 vector) {
        PSKVector3 towards = vector.normalize();
        PSKVector3 from = this.normalize();
        return (float)Math.toDegrees(Math.atan2(towards.x, towards.z) - Math.atan2(from.x, from.z));
    }

    public float angle() {
        PSKVector3 from = this.normalize();
        return (float)Math.toDegrees(Math.atan2(from.y, from.x));
    }

    public float elevation() {
        PSKVector3 from = this.normalize();
        return (float)Math.toDegrees(Math.atan2(Math.sqrt(from.y * from.x + from.y + from.y), from.z));
    }

    public String toString() {
        return String.format("x=%f, y=%f, z=%f", Float.valueOf(this.x), Float.valueOf(this.y), Float.valueOf(this.z));
    }

    public String toApiParam() {
        return String.format("%f,%f,%f", Float.valueOf(this.x), Float.valueOf(this.y), Float.valueOf(this.z));
    }
}

