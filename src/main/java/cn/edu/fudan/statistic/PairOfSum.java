package cn.edu.fudan.statistic;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by sherry on 17-9-30.
 */
public class PairOfSum implements Writable {

    DoubleWritable avg, squareAvg;

    public PairOfSum() {
        this.avg = new DoubleWritable(0);
        this.squareAvg = new DoubleWritable(0);
    }

    public PairOfSum(DoubleWritable avg, DoubleWritable squareAvg) {
        this.avg = avg;
        this.squareAvg = squareAvg;
    }

    public void write(DataOutput dataOutput) throws IOException {
        avg.write(dataOutput);
        squareAvg.write(dataOutput);
    }

    public void readFields(DataInput dataInput) throws IOException {
        avg.readFields(dataInput);
        squareAvg.readFields(dataInput);
    }

    @Override
    public String toString() {
        return avg.toString() + "\t" + squareAvg.toString();
    }
}
