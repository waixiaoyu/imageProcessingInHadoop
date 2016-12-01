package com.AllFileInput;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

class AllFileInputformat extends FileInputFormat<Object, Object>
{

	@Override
	public RecordReader createRecordReader(
			InputSplit arg0, TaskAttemptContext arg1) throws IOException,
			InterruptedException {
		// TODO Auto-generated method stub
		return new AllFileRecordReader();
	}
	@Override  
	protected boolean isSplitable(JobContext context, Path filename) {  
	    // TODO Auto-generated method stub  
	    return false;  
	} 
}
