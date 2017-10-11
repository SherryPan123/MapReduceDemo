package cn.edu.fudan.statistic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sherry on 17-9-27.
 */
public class Series {

    List<Float[]> vals;
    double[] squareAvg = new double[1000000000 / 1000];
    double[] avg = new double[1000000000 / 1000];
    double average;
    double standard;

    public Series() {
        vals = new ArrayList<Float[]>();
    }

    void addArrayToList(int id, Float[] v) {
        vals.add(v);
        double a = 0, sa = 0;
        for (int i = 0; i < 1000; i++) {
            a += (double)v[i] / 1000;
            sa += (double)(v[i])*(double)(v[i]);
        }
        avg[id] = a;
        squareAvg[id] = sa / 1000;
    }

    public double getAverage() {
        average = 0f;
        for (int i = 0; i < 1000000000 / 1000; i++) {
            average += avg[i] / (1000000000 / 1000);
        }
        return average;
    }

    public double getStandard() {
        standard = 0f;
        for (int i = 0; i < 1000000000 / 1000; i++) {
            standard += squareAvg[i] / (1000000000 / 1000);
        }
        standard -= average*average;
        return 0;
    }

}
