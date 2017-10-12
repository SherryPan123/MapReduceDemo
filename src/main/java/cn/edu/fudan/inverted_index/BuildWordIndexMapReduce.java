package cn.edu.fudan.inverted_index;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sherry on 17-10-11.
 */
public class BuildWordIndexMapReduce {

    private static final Logger logger = LoggerFactory.getLogger(BuildWordIndexMapReduce.class);

    public static class IndexMapper extends Mapper<Object, Text, Text, Text> {

        public void map(Object key, Text value, Context context) {
            String filename = ((FileSplit)context.getInputSplit()).getPath().getName();
            String line = value.toString();
            String[] val = line.split(" ");
            for (String aVal : val) {
                try {
                    context.write(new Text(aVal), new Text(filename));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static class IndexReducer extends Reducer<Text, Text, Text, Text> {
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            Map<String, Integer> myMap = new HashMap<String, Integer>();
            for (Text value : values) {
                String v = value.toString();
                int count = myMap.getOrDefault(v, 0);
                if (count == 0) {
                    myMap.put(v, 1);
                } else {
                    myMap.put(v, ++count);
                }
            }
            context.write(key, new Text(myMap.toString()));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        if (args.length != 2) {
            System.err.println("Usage: build index <in> <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf, "build index for documents");
        job.setJarByClass(BuildWordIndexMapReduce.class);
        job.setMapperClass(IndexMapper.class);
        job.setReducerClass(IndexReducer.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        Path outputPath = new Path(args[1]);
        FileOutputFormat.setOutputPath(job, outputPath);
        outputPath.getFileSystem(conf).delete(outputPath);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
