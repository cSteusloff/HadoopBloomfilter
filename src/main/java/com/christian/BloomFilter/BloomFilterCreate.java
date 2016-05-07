package com.christian.BloomFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.KeyValueTextInputFormat;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.NullOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class BloomFilterCreate extends Configured implements Tool
{
	
	public static class MapClass extends MapReduceBase implements Mapper<Text, Text, Text, BloomFilter<String>>
	{
		private static final String DELIMITER_REGEX = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
		BloomFilter<String> bf = new BloomFilter<String>();
		
		OutputCollector<Text, BloomFilter<String>> oc = null;
		
		public void map(Text key, Text value, OutputCollector<Text, BloomFilter<String>> output, Reporter reporter) throws IOException
		{
			// fetch output collector. Don't change!
			if(oc == null)
				oc = output;
				
			// insert your map code here!
			List<String> elementList = Arrays.asList(value.toString().split(DELIMITER_REGEX,-1));
			String locationName = elementList.get(19);
			bf.add(locationName);
			String currentValue = bf.ToString();
			System.out.println(currentValue);
		}
		
		public void close() throws IOException
		{
			oc.collect(new Text("testkey"), bf);
		}
	}
	
	public static class Reduce extends MapReduceBase implements Reducer<Text, BloomFilter<String>, Text, Text>
	{
		JobConf job = null;		
		BloomFilter<String> bf = new BloomFilter<String>();
		
		public void configure(JobConf job)
		{
			this.job = job;
		}
		
		public void reduce(Text key, Iterator<BloomFilter<String>> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
		{
			// insert your reduce code here!
			while (values.hasNext()) {
				bf.union(values.next());
			}
		}
		
		public void close() throws IOException
		{
			Path file = new Path(job.get("mapred.output.dir") + "/bloomfilter");
			String hallo = bf.ToString();
			
			FSDataOutputStream out = file.getFileSystem(job).create(file);
			
			bf.write(out);
		
			out.close();
		}
	}
	
	public int run(String[] args) throws IOException
	{
		Configuration conf = getConf();
		JobConf job = new JobConf(conf, BloomFilterCreate.class);
		
		Path in = new Path(args[0]);
		Path out = new Path(args[1]);
		
		FileInputFormat.setInputPaths(job, in);
		FileOutputFormat.setOutputPath(job, out);
		
		job.setJobName("Bloom Filter");	
		job.setMapperClass(MapClass.class);
		job.setReducerClass(Reduce.class);
		job.setNumReduceTasks(1);
		
		job.setInputFormat(KeyValueTextInputFormat.class);
		job.setOutputFormat(NullOutputFormat.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(BloomFilter.class);
		job.set("key.value.separator.in.input.line", ",");
		
		JobClient.runJob(job);
		
		return 0;
	}
	
	public static void main(String[] args) throws Exception
	{
		if (args.length != 2) {
		     System.err.println("Usage: BloomFilter <input path> <output path>");
		     System.exit(-1);
		}
		int res = ToolRunner.run(new Configuration(), new BloomFilterCreate(), args);		
		System.exit(res);
	}
}
