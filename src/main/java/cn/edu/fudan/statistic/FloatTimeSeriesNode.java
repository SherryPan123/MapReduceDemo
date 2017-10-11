package cn.edu.fudan.statistic;

import org.apache.hadoop.hbase.util.Bytes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sherry on 17-9-28.
 */
public class FloatTimeSeriesNode {

    public static int ROW_LENGTH = 1000;

    private List<Float> data;

    public FloatTimeSeriesNode() {
        this.data = new ArrayList<Float>();
    }

    public FloatTimeSeriesNode(List<Float> data) {
        this.data = data;
    }

    public byte[] toBytes() {
        byte[] result = new byte[Bytes.SIZEOF_FLOAT * data.size()];
        for (int i = 0; i < data.size(); i++) {
            System.arraycopy(Bytes.toBytes(data.get(i)), 0, result, Bytes.SIZEOF_FLOAT * i, Bytes.SIZEOF_FLOAT);
        }
        return result;
    }

    public void parseBytes(byte[] concatData) {
        for (int i = 0; i < concatData.length / Bytes.SIZEOF_FLOAT; i++) {
            byte[] temp = new byte[Bytes.SIZEOF_FLOAT];
            System.arraycopy(concatData, Bytes.SIZEOF_FLOAT * i, temp, 0, Bytes.SIZEOF_FLOAT);
            data.add(Bytes.toFloat(temp));
        }
    }

    @Override
    public String toString() {
        return "TimeSeriesNode{" + "data=" + data + '}';
    }

    public List<Float> getData() {
        return data;
    }

    public void setData(List<Float> data) {
        this.data = data;
    }

}