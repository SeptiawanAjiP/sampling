/*
 * Decompiled with CFR 0_118.
 */
package org.odk.collect.android.augmentedreality.sensorkit;

public class PSKVector3Double {
    public double x;
    public double y;
    public double z;

    public PSKVector3Double() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
    }

    public PSKVector3Double(PSKVector3Double other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public PSKVector3Double(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static PSKVector3Double Zero() {
        return new PSKVector3Double();
    }

    public static PSKVector3Double One() {
        return new PSKVector3Double(1.0, 1.0, 1.0);
    }

    public void toOne() {
        this.x = 1.0;
        this.y = 1.0;
        this.z = 1.0;
    }

    public void toZero() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
    }

    public double magnitude() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public double sqrMagnitude() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public static double magnitude(double x, double y, double z) {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public PSKVector3Double normalize() {
        double s = 1.0 / this.magnitude();
        return new PSKVector3Double(s * this.x, s * this.y, s * this.z);
    }

    public void normalize(double _x, double _y, double _z) {
        double s = 1.0 / PSKVector3Double.magnitude(_x, _y, _z);
        this.x = s * _x;
        this.y = s * _y;
        this.z = s * _z;
    }

    public void normalize(double _x, double _y, double _z, double _m) {
        this.x = _x / _m;
        this.y = _y / _m;
        this.z = _z / _m;
    }

    public double angleTowards(PSKVector3Double vector) {
        PSKVector3Double towards = vector.normalize();
        PSKVector3Double from = this.normalize();
        return Math.toDegrees(Math.atan2(towards.x, towards.z) - Math.atan2(from.x, from.z));
    }

    public double angle() {
        PSKVector3Double from = this.normalize();
        return Math.toDegrees(Math.atan2(from.y, from.x));
    }

    public double elevation() {
        PSKVector3Double from = this.normalize();
        return Math.toDegrees(Math.atan2(Math.sqrt(from.y * from.x + from.y + from.y), from.z));
    }

    public String toString() {
        return String.format("x=%f, y=%f, z=%f", this.x, this.y, this.z);
    }
}

