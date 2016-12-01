package com.AllFileInput;
/**
 * 
 * @author Yayu Yao
 * @time July 30, 2014
 * @version 1.0
 * @filename AllFileInput.java  
 * @description
 * This is a function to start the processing of hadoop. 
 * 
 * You can change the configuration of hadoop in the main function.
 * And then, you should override the function of map and reduce to implement what you want it do.
 * 
 * Notably, there is a important part that in order to get the same key to match the process of reduce, 
 * the number of Integer divided by two.
 * 
 * You can see more detail in line 45.
 *  
 */

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;


public class AllFileInput {
	
	public static class mapper extends Mapper<Text, BytesWritable, Text, Text>
	{
		protected void map(Text key, BytesWritable value, Context context) throws IOException, InterruptedException
		{
			String[] temp1=key.toString().split("\\.");
			String[] temp2=temp1[0].toString().split("_");
			int i=Integer.parseInt(temp2[0])/2;
			int j=Integer.parseInt(temp2[1])/2;
			String mykey=i+"_"+j+"."+temp1[1];
//			value.setSize(value.getLength()+1);
//			String str="1";
//			value.set(str.getBytes(), value.getLength()-1, str.getBytes().length);
			context.write(new Text(mykey), new Text(value.getBytes()));
		}	
		
	}
	public static class reducer extends Reducer<Text, Text, Text, Text>
	{
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException
		{
			//System.out.println("reduce key="+key.toString());
			gdal.AllRegister();
			Dataset[] hDatasetArr = new Dataset[4];
			int i=0;
			for(Text t : values)
			{
			
	             imageBin myimageBin = new imageBin();
	           //create the file cache in the mem
	             String resultpath=myimageBin.createFileCache(key.toString(),t.getBytes(),i);
	             myimageBin.classingrass(resultpath);
	             myimageBin.rewritenumvalue(resultpath);
	           //hDatasetArr[i]=myimageBin.binaryzate(key.toString(),t.getBytes(),i);
	             i++;
			   //context.write(key, t);
			}
			combineImage com=new combineImage();
			//combine image in the mem
			com.combineImageInCache(key.toString());
		//	com.combineImage(key.toString(), hDatasetArr);
		}
	}
	public static void main(String[] args) throws Exception
	{
		//clear old files
		File file = new File("/ram/");
		deleteFile.delete(file);
		deleteFile.deleteOutput();
		//configure hadoop
		Configuration conf = new Configuration();
		Job job = new Job(conf,"wholeFile");
		job.setJarByClass(AllFileInput.class);
		job.setMapperClass(mapper.class);
		job.setReducerClass(reducer.class);
		job.setInputFormatClass(AllFileInputformat.class);
	//	job.setMapOutputValueClass(BytesWritable.class);
		job.setOutputKeyClass(Text.class);		
		job.setOutputValueClass(Text.class);
		
		//set input path
		FileInputFormat.addInputPath(job, new Path("hdfs://localhost:9000/home/hadoop/result2"));
		//set output path
		FileOutputFormat.setOutputPath(job, new Path("hdfs://localhost:9000/home/hadoop/output"));
		
		System.out.println(job.waitForCompletion(true)?0:1);
	}
}




