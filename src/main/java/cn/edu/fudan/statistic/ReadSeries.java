package cn.edu.fudan.statistic;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 * Created by sherry on 17-9-27.
 */
public class ReadSeries {

    public static void main(String[] args) throws IOException {
        Configuration conf = HBaseConfiguration.create();
        Connection connection = ConnectionFactory.createConnection(conf);
        TableName tableName = TableName.valueOf("HTSI:data-1000000000-1");
        Table table = connection.getTable(tableName);
        Scan scan = new Scan();
        //scan.addColumn(Bytes.toBytes(""), Bytes.toBytes(""));
        ResultScanner scanner1 = table.getScanner(scan);
        //Series series = new Series();
        FloatTimeSeries floatTimeSeries = new FloatTimeSeries();
        int rowId = 0;
        for (Result res : scanner1) {
            /*ByteBuffer byteBuffer = res.getValueAsByteBuffer(Bytes.toBytes("info"), Bytes.toBytes("d"));
            int id = 0, pt = 0;
            Float[] vals = new Float[1000];
            while(id < 1000){
                vals[id++] = byteBuffer.getFloat(pt);
                pt += Bytes.SIZEOF_FLOAT;
            }
            series.addArrayToList(rowId++, vals);*/
            FloatTimeSeriesNode floatTimeSeriesNode =  new FloatTimeSeriesNode();
            floatTimeSeriesNode.parseBytes(res.value());
            //series.addListToList(rowId++, floatTimeSeriesNode.getData());
            floatTimeSeries.add(rowId++, floatTimeSeriesNode);
            if(rowId%1000==0) System.out.println(Bytes.toString(res.getRow()));
        }
        //System.out.println("avg: " + series.getAverage() + "  standard:" + series.getStandard());
        System.out.println("avg: " + floatTimeSeries.getAverage() + "  standard:" + floatTimeSeries.getStandard());
    }
}
