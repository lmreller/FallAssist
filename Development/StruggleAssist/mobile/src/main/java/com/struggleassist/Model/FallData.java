package com.struggleassist.Model;

/**
 * Created by Lucas on 9/17/2017.
 */

public class FallData {

    private float min;
    private float Q1;
    private float avg;
    private float Q2;
    private float max;

    public FallData(float min, float Q1, float avg, float Q2, float max) {
        this.min = min;
        this.Q1 = Q1;
        this.avg = avg;
        this.Q2 = Q2;
        this.max = max;
    }

    public float getMin() {
        return min;
    }

    public float getQ1() {
        return Q1;
    }

    public float getAvg() {
        return avg;
    }

    public float getQ2() {
        return Q2;
    }

    public float getMax() {
        return max;
    }
}
