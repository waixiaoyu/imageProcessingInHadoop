package com.AllFileInput;
/**
 * 
 * @author Yayu Yao
 * @time July 30, 2014
 * @version 1.0
 * @filename AllFileRecordReader.java
 * @description
 * In this file, we override the InputFormat of Hadoop, so the Hadoop can load an entire file once a time, not just a byte or a line of text. 
 * You can get more details in line 73 .
 */
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

class AllFileRecordReader extends RecordReader<Object, Object>
{
	private FileSplit fileSplit;
	private FSDataInputStream fis;

	private Text key=null;
	private BytesWritable value = null;

	private boolean processed = false;

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public Text getCurrentKey() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return this.key;
	}

	@Override
	public BytesWritable getCurrentValue() throws IOException,
			InterruptedException {
		// TODO Auto-generated method stub
		return this.value;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return processed? fileSplit.getLength():0;
	}

	@Override
	public void initialize(InputSplit inputSplit, TaskAttemptContext tacontext)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		fileSplit = (FileSplit)inputSplit;
		Configuration job = tacontext.getConfiguration();
		Path file = fileSplit.getPath();
		FileSystem fs = file.getFileSystem(job);
		fis = fs.open(file);
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		if(key==null)
		{
			key = new Text();
		}
		if(value==null)
		{
			value = new BytesWritable();
		}
		if(!processed)
		{
			byte[] content = new byte[(int)fileSplit.getLength()];
			Path file = fileSplit.getPath();
			System.out.println(file.getName());
			key.set(file.getName());
			try{
				IOUtils.readFully(fis, content, 0, content.length);
				value.set(new BytesWritable(content));
			}catch(IOException e)
			{
				e.printStackTrace();
			}finally{
				IOUtils.closeStream(fis);
			}
			processed = true;
			return true;               //return true表示这次inputformat还没有结束，会有下一对keyvalue产生
		}
		return false;                      //return false表示这次inputformat结束了
	}
}