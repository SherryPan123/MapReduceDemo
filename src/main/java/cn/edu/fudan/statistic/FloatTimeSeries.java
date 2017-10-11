package cn.edu.fudan.statistic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sherry on 17-9-28.
 */
public class FloatTimeSeries {

    List<List<Float> > vals;
    double[] squareAvg = new double[1000000000 / 1000 + 10];
    double[] avg = new double[1000000000 / 1000 + 10];
    double average;
    double standard;

    public FloatTimeSeries() {
        vals = new ArrayList<List<Float>>();
        average = 0d;
        standard = 0d;
    }


    public void add(int id, FloatTimeSeriesNode floatTimeSeriesNode) {
        List<Float> data = floatTimeSeriesNode.getData();
        avg[id] = 0d;
        squareAvg[id] = 0d;
        for (Float d : data) {
            avg[id] += (double)(d) / 1000;
            squareAvg[id] += (double)(d)*(double)(d) / 1000;
        }
        average += avg[id] / (1000000000 / 1000);
        standard += squareAvg[id] / (1000000000 / 1000);
    }

    public double getAverage() {
        return average;
    }

    public double getStandard() {
        standard -= average*average;
        return standard;
    }
}
