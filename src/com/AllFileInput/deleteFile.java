package com.AllFileInput;
/**
 * 
 * @author Yayu Yao
 * @time July 30, 2014
 * @version 1.0
 * @filename deleteFile.java  
 * @description
 * In this code, there is a easy funtion of delete files.
 * Remember, you must clear the output files before the next processing.
 * Otherwise, a new task will not be executed.
 *  
 */
import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class deleteFile {

	static public void delete(File oldPath) {
        if (oldPath.isDirectory()) {
         System.out.println("clear "+oldPath.getName());
         File[] files = oldPath.listFiles();
         for (File file : files) {
        	 delete(file);
         }
        }else{
          oldPath.delete();
        }
      }


	 public static void main(String[] args) throws IOException
	 {
	  File file = new File("/ram/");
	  deleteFile.deleteOutput();
	 }
	static public void deleteOutput() throws IOException {
		 String dst = "hdfs://localhost:9000/home/hadoop/output/";  
		  Configuration conf = new Configuration();  
		  FileSystem fs = FileSystem.get(URI.create(dst), conf);  
		  fs.deleteOnExit(new Path(dst));  
		  fs.close(); 
		  System.out.println("delete output");
	    }
}
