package com.cleanmaster.notificationclean.view;


/**
 * Copyright (c) 2012 Oscar Blasco Maestro and Contributors

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 associated documentation files (the "Software"), to deal in the Software without restriction,
 including without limitation the rights to use, copy, modify, merge, publish, distribute,
 sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or
 substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 https://github.com/TraxNet/ShadingZen
 */

public class NCVector3 {

    // Global ShadingZen's axis vectors
    public static final NCVector3 vectorRight = new NCVector3(1.f, 0.f, 0.f);
    public static final NCVector3 vectorUp = new NCVector3(0.f, 1.f, 0.f);
    public static final NCVector3 vectorFront = new NCVector3(0.f, 0.f, 1.f);
    public static final float[] vectorRightArray = {1.f, 0.f, 0.f, 0.f};
    public static final float[] vectorUpArray = {0.f, 1.f, 0.f, 0.f};
    public static final float[] vectorFrontArray = {0.f, 0.f, 1.f, 0.f};
    public static final NCVector3 zero = new NCVector3();

    public float x, y, z;
    public NCVector3(){
        x = y = z = 0.f;
    }
    public NCVector3(float x, float y, float z){
        this.x = x; this.y = y; this.z = z;
    }
    public NCVector3(NCVector3 v){
        x = v.x; y = v.y; z = v.z;
    }
    public NCVector3(float v[]){
        x = v[0];
        y = v[1];
        z = v[2];
    }

    public float dot(NCVector3 v){
        return x *v.x + y *v.y + z *v.z;
    }

    public NCVector3 cross(NCVector3 v){
        return new NCVector3(
                y *v.z - z *v.y,
                z *v.x - x *v.z,
                x *v.y - y *v.x
        );
    }

    public void crossNoCopy(NCVector3 v){

        float _x = y *v.z - z *v.y;
        float _y = z *v.x - x *v.z;
        z = x *v.y - y *v.x;
        x = _x;
        y = _y;

    }

    /*** Return a float [3]
     *
     * @return an array of float with the components
     */
    public float [] getAsArray(){
        float array[] = new float[3];
        array[0] = x;
        array[1] = y;
        array[2] = z;

        return array;
    }

    public void toArray(float [] array){
        array[0] = x;
        array[1] = y;
        array[2] = z;
    }

    /*** Lenght of this vector */
    public float length(){
        return x * x + y * y + z * z;
    }

    /*** sqrt(Lenght) of this vector */
    public float lengthSqrt(){
        return (float)Math.sqrt(length());
    }
    /*** Perform a normalization
     * If sqrt(len) of this vector is greater than an EPSILON value (0,0000001)
     * this methods perform a normalization of this vector.
     * Original vector is untouched, a new one is returned.
     * @return Returns a new normalized vector.
     */
    public NCVector3 normalize(){
        float sqr_length =  (float)Math.sqrt(length());
        if(sqr_length >= 0.0000001f){
            float inv = 1/sqr_length;
            return new NCVector3(x *inv, y *inv, z *inv);
        }
        return new NCVector3(0.f, 0.f, 0.f);
    }

    /***
     * Normalizes this vector without creating a new one
     */
    public float normalizeNoCopy(){
        float sqr_length =  (float)Math.sqrt(length());
        if(sqr_length >= 0.0000001f){
            float inv = 1/sqr_length;
            x *= inv;
            y *= inv;
            z *= inv;
        } else {
            x = 0.f;
            y = 0.f;
            z = 0.f;
        }

        return sqr_length;
    }

    public float getX(){
        return x;
    }
    public float getY(){
        return y;
    }
    public float getZ(){
        return z;
    }

    public void setX(float x){
        this.x = x;
    }
    public void setY(float y){
        this.y = y;
    }
    public void setZ(float z){
        this.z = z;
    }

    public void set(float x, float y, float z) {
        setX(x);
        setY(y);
        setZ(z);
    }

    public void set(NCVector3 v){
        setX(v.x);
        setY(v.y);
        setZ(v.z);
    }

    public void set(float [] vec){
        x = vec[0];
        y = vec[1];
        z = vec[2];
    }

    public NCVector3 sub(NCVector3 b){
        return new NCVector3(
                x - b.x,
                y - b.y,
                z - b.z
        );
    }
    public void subNoCopy(NCVector3 b){
        x -= b.x;
        y -= b.y;
        z -= b.z;
    }

    public NCVector3 add(NCVector3 b){
        return new NCVector3(
                x + b.x,
                y + b.y,
                z + b.z
        );
    }

    public void addNoCopy(NCVector3 b){
        x += b.x;
        y += b.y;
        z += b.z;
    }

    public NCVector3 mul(float f){
        return new NCVector3(
                x *f,
                y *f,
                z *f
        );
    }

    public void mulInplace(float f){
        x *= f;
        y *= f;
        z *= f;
    }

    public NCVector3 negate(){
        return new NCVector3(
                -x,
                -y,
                -z
        );
    }

    public void negateNoCopy(){
        x = -x;
        y = -y;
        z = -z;
    }

    public String getDebugString(){
        return "("+Float.toString(x)+", "+Float.toString(y)+", "+Float.toString(z)+")";
    }
}