package com.AllFileInput;


/**
 * 
 * @author Yayu Yao
 * @time July 30, 2014
 * @version 1.0
 * @filename uoloadToHdfs.java  
 * @description
 * 
 * The function of this code is to upload file to HDFS.
 * 
 * You need to pay attention to the way of data transmission mode.
 * 
 * The file system and path mentioned below is the part of Hadoop API not from JAVA native API.   
 *  
 */

import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;


public class uploadToHdfs {

	 void uploadFileToHdfs(String filename) throws IOException {       
	        Configuration conf = new Configuration();  
	        FileSystem fs = FileSystem.get(conf);  
	        Path src = new Path(filename);  
	        Path dst = new Path("hdfs://localhost:9000/tmp/hadoop/image");  
	        fs=dst.getFileSystem(conf);
	        fs.copyFromLocalFile(src, dst);  
	    }  
	 void deleteLocalFile(String filename)
	 {
	      //delete the file after upload  
	   	 File f= new File(filename);
		 if(f.exists())
		 {
			 f.delete();
		 }
	 }
	
}
