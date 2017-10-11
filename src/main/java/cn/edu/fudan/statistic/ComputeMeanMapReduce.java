package cn.edu.fudan.statistic;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created by sherry on 17-9-30.
 */
public class ComputeMeanMapReduce {

    private static final Logger logger = LoggerFactory.getLogger(ComputeMeanMapReduce.class);

    static double average, variance;

    static class ComputeMeanMapper extends TableMapper<IntWritable, PairOfSum> {
        @Override
        public void map(ImmutableBytesWritable row, Result columns, Context context) throws IOException, InterruptedException {
            logger.info("Into map phase.");
            // parse data
            //long first = Long.parseLong(Bytes.toString(row.get()));
            FloatTimeSeriesNode floatTimeSeriesNode =  new FloatTimeSeriesNode();
            floatTimeSeriesNode.parseBytes(columns.getValue(Bytes.toBytes("info"), Bytes.toBytes("d")));
            // start computing
            List<Float> data = floatTimeSeriesNode.getData();
            double avg = 0d;
            double squareAvg = 0d;
            for (Float d : data) {
                avg += (double)d / 1000;
                squareAvg += (double)(d)*(double)(d) / 1000;
            }
            final IntWritable ONETHOUSAND = new IntWritable(1000);
            PairOfSum tuple = new PairOfSum(new DoubleWritable(avg), new DoubleWritable(squareAvg));
            context.write(ONETHOUSAND, tuple);
        }
    }

    static class ComputeMeanReducer extends TableReducer<IntWritable, PairOfSum, DoubleWritable> {
        @Override
        public void reduce(IntWritable key, Iterable<PairOfSum> values, Context context) {
            logger.info("Into reduce phase.");
            double avg = 0d, squareAvg = 0d;
            for (PairOfSum val : values) {
                avg += val.avg.get();
                squareAvg += val.squareAvg.get();
            }
            avg = avg / (1000000000 / key.get());
            squareAvg = squareAvg / (1000000000 / key.get());
            average = avg;
            variance = squareAvg - average*average;
        }

        @Override
        public void cleanup(Context context) {
            logger.info("avg: " + average + "  standard:" + variance);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = HBaseConfiguration.create();
        // Connection connection = ConnectionFactory.createConnection(conf);
        TableName tableName = TableName.valueOf("HTSI:data-1000000000-1");
        Scan scan = new Scan();
        scan.addColumn(Bytes.toBytes("info"), Bytes.toBytes("d"));
        Job job = Job.getInstance(conf);
        job.setJobName("compute mean and variance of data: " + tableName);
        job.setJarByClass(ComputeMeanMapReduce.class);
        TableMapReduceUtil.initTableMapperJob(tableName, scan, ComputeMeanMapper.class, IntWritable.class, PairOfSum.class, job);
        job.setReducerClass(ComputeMeanReducer.class);
        job.setNumReduceTasks(1);
        job.setOutputFormatClass(NullOutputFormat.class);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
