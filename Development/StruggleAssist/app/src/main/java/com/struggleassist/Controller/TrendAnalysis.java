package com.struggleassist.Controller;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import com.struggleassist.Model.ViewContext;
import com.struggleassist.View.Notifications.NotificationController;

import java.util.ArrayList;
import java.util.Iterator;

public class TrendAnalysis {

    static Tuple<Double,Double> goodInterval;
    static double goodStdDev;
    static Tuple<Double,Double> badInterval;
    static double badStdDev;
    static double difference;
    static double stdDev;
    static float newThreshold;

    private static class Tuple<L, R>{
        private final L lowerBound;
        private final R upperBound;

        public Tuple(L lowerBound, R upperBound) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }

        public L getLowerBound() { return lowerBound; }
        public R getUpperBound() { return upperBound; }
    }

    private static Tuple<Double,Double> getInterval(double[] samples, double interval){
        double mean = getMean(samples);
        double stdDev = getStdDev(samples);

        double confidenceLevel = 1.96;

        double temp = confidenceLevel * stdDev / Math.sqrt(samples.length);
        Tuple<Double,Double> tuple = new Tuple<>(mean-temp,mean+temp);
        return tuple;
    }

    public static void updateValue() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ViewContext.getContext());
        SharedPreferences.Editor editor = pref.edit();

        ArrayList<Double> badData = new ArrayList<>();
        ArrayList<Double> goodData = new ArrayList<>();

        DatabaseController db = new DatabaseController(ViewContext.getContext());
        db.open();
        Cursor cursor = db.getAllRecords();
        if(cursor.moveToFirst()){
            do{
                String response = cursor.getString(cursor.getColumnIndex("userResponse"));
                double temp = (double) cursor.getFloat(cursor.getColumnIndex("incidentScore"));

                if(NotificationController.CONFIRM_ACTION.equals(response)){
                    goodData.add(temp);
                }
                if(NotificationController.CANCEL_ACTION.equals(response)){
                    badData.add(temp);
                }
            }while(cursor.moveToNext());
        }

        double[] goodDataArray = new double[goodData.size()];
        double[] badDataArray = new double[badData.size()];

        Iterator goodIterator = goodData.iterator();
        Iterator badIterator = badData.iterator();

        int i = 0;
        int j = 0;

        while(goodIterator.hasNext()){
            goodDataArray[i]=(double)goodIterator.next();
            i++;
        }

        while(badIterator.hasNext()){
            badDataArray[j]=(double)badIterator.next();
            j++;
        }

        goodInterval = getInterval(goodDataArray,0.95);
        goodStdDev = getStdDev(goodDataArray);
        badInterval = getInterval(badDataArray, 0.95);
        badStdDev = getStdDev(badDataArray);
        newThreshold = pref.getFloat("trend_analysis_value",newThreshold);

        difference = goodInterval.lowerBound - badInterval.upperBound;

        if(difference>0){
            stdDev = (goodStdDev + badStdDev)/2;
            if(difference>stdDev){
                //we have problems and should make extra adjustments
            }else{
                double temp = badInterval.upperBound + (stdDev/4);
                newThreshold = (float) temp;
            }
        } else{
            double temp = badInterval.upperBound;
            newThreshold = (float)temp;
        }

        editor.putFloat("trend_analysis_value",newThreshold);
        editor.apply();
    }

    private static double getMean(double[] m){
        double sum = 0;
        for(double num: m){
            sum += num;
        }
        return sum / m.length;
    }

    private static double getStdDev(double[] s){
        double squaredDifferenceSum = 0.0;
        double mean = getMean(s);

        for(double num: s){
            squaredDifferenceSum += Math.pow(num - mean, 2);
        }

        return Math.sqrt(squaredDifferenceSum/s.length);
    }
}
