package com.AllFileInput;

/**
 * 
 * @author Yayu Yao
 * @time July 30, 2014
 * @version 1.0
 * @filename combineImage.java  
 * @description
 * In this code, I use GDAL to combine the images, especially from 16 parts to 4 parts.
 * 
 * In the function of combineImageInStream in line 40, it is used to match the byte stream from hdfs, 
 * however it is obsoleted, you can ignore this part.
 * 
 * In the function of combineImageInCache in line 119 it is used to get byte from the reduce processing, 
 * and the data is from RDFS in Linux, you should pay more attention to this part.
 * 
 * On the other hand, I also offer you a class of interviewing HDFS which is named uploadToHdfs. 
 * I hope that will help all of you a little.
 *  
 */


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;

public class combineImage {

	public static void main(String args[]) throws IOException, InterruptedException
	{		
		//test code

		combineImage con=new combineImage();
		con.combineImageInCache("0_0.tif");	
	}	
	//the old function which is to combine image from the last datasets
	 void combineImageInStream(String fileName,Dataset[] hDatasetArr) throws IOException {
		
		HashMap<Integer, Integer> map= new HashMap<Integer, Integer>();
		int[] arr=new int[4];
		//get the sequence of every image
		for(int i=0;i<4;i++)
		{
			System.out.println("num="+hDatasetArr[i].GetMetadataItem("num"));
			int x=Integer.parseInt(hDatasetArr[i].GetMetadataItem("num").split("_")[0]);
			int y=Integer.parseInt(hDatasetArr[i].GetMetadataItem("num").split("_")[1]);
			map.put(x*10+y,i);
			arr[i]=x*10+y;
		}
		//sort the images
		Arrays.sort(arr);
				
		Driver hDriver = hDatasetArr[0].GetDriver();
		Dataset hDatasetResult = null;
					
		int iXsize=hDatasetArr[0].getRasterXSize();
		int iYsize=hDatasetArr[0].getRasterYSize();
		int iBandCount=hDatasetArr[0].getRasterCount();
		hDatasetResult=hDriver.Create("/ram/"+fileName, iXsize*2, iYsize*2,iBandCount);
		//reset the sequence of images 
		String[] temp=fileName.split("\\.")[0].split("_");
		hDatasetResult.SetMetadata("num="+temp[0]+"_"+temp[1]);
		
		int []arrayin = new int[iXsize*iYsize];
		int bandlist[] = new int[iBandCount];
		for(int i=0;i<iBandCount;i++)
		{
			bandlist[i]=i+1;
		}
		//writing the data of new image from small images
			hDatasetArr[map.get(arr[0])].ReadRaster(0, 0,iXsize, iYsize, iXsize,
					iYsize, gdalconstConstants.GDT_Int32, arrayin,
					bandlist, 0);
			hDatasetResult.WriteRaster(0, 0, iXsize, iYsize, iXsize,
					iYsize, gdalconstConstants.GDT_Int32, arrayin,
					bandlist, 0);
			
			hDatasetArr[map.get(arr[1])].ReadRaster(0, 0,iXsize, iYsize, iXsize,
					iYsize, gdalconstConstants.GDT_Int32, arrayin,
					bandlist, 0);
			hDatasetResult.WriteRaster(0, iYsize, iXsize, iYsize, iXsize,
					iYsize, gdalconstConstants.GDT_Int32, arrayin,
					bandlist, 0);
			
			hDatasetArr[map.get(arr[2])].ReadRaster(0, 0,iXsize, iYsize, iXsize,
					iYsize, gdalconstConstants.GDT_Int32, arrayin,
					bandlist, 0);
			hDatasetResult.WriteRaster(iXsize, 0, iXsize, iYsize, iXsize,
					iYsize, gdalconstConstants.GDT_Int32, arrayin,
					bandlist, 0);
			
			hDatasetArr[map.get(arr[3])].ReadRaster(0, 0,iXsize, iYsize, iXsize,
					iYsize, gdalconstConstants.GDT_Int32, arrayin,
					bandlist, 0);
			
			hDatasetResult.WriteRaster(iXsize, iYsize, iXsize, iYsize, iXsize,
					iYsize, gdalconstConstants.GDT_Int32, arrayin,
					bandlist, 0);
		
			
		//the dataset must be delete, otherwise the file can not be uploaded to the hdfs completely	
		hDatasetResult.delete();
		
		//get the file from the mfs to hdfs
		uploadToHdfs uth= new uploadToHdfs();
		uth.uploadFileToHdfs("/ram/"+fileName);
		//delete the dataset
		for(int i=0;i<4;i++)
		{
			hDatasetArr[i].delete();
		}
		
		
	}
    //combine image from a cache
	 void combineImageInCache(String filename) throws IOException, InterruptedException
	 {
	
		 //System.out.print("reduce path:"+filepath);

		 ArrayList<String> filelist=new ArrayList<String>();
		 String[] keys=filename.split("\\.")[0].split("_");
		 //get all file name in the ram
		 File file=new File("/ram");
		  String test[];
		  test=file.list();
		  for(int i=0;i<test.length;i++)
		  {
		   String[] targetkeys=test[i].split("\\.")[0].split("_");
		   if(targetkeys[0].equals(keys[0])&&targetkeys[1].equals(keys[1]))
		   {
			   filelist.add(test[i]);
		   }
		  }
		  

			 gdal.AllRegister(); 
		  
		  //read file name in mem based on key word
		  Dataset[] hDatasetArr = new Dataset[4];
		  int k=0;
		  for(int i=0;i<filelist.size();i++)
		  {
			  if(filelist.get(i).length()<11)
			  {
			  System.out.println(filelist.get(i));
			  hDatasetArr[k]=gdal.Open("/ram/"+filelist.get(i),gdalconstConstants.GA_ReadOnly);
			  k++;
			  }
			  
			 
		  }		  
			HashMap<Integer, Integer> map= new HashMap<Integer, Integer>();
			int[] arr=new int[4];
			//get the sequence of every image
			for(int i=0;i<4;i++)
			{
				System.out.println("num="+hDatasetArr[i].GetMetadataItem("num"));
				int x=Integer.parseInt(hDatasetArr[i].GetMetadataItem("num").split("_")[0]);
				int y=Integer.parseInt(hDatasetArr[i].GetMetadataItem("num").split("_")[1]);
				map.put(x*10+y,i);
				arr[i]=x*10+y;
			}
			//sort the images
			Arrays.sort(arr);
					
			Driver hDriver = hDatasetArr[0].GetDriver();
			Dataset hDatasetResult = null;
						
			int iXsize=hDatasetArr[0].getRasterXSize();
			int iYsize=hDatasetArr[0].getRasterYSize();
			int iBandCount=hDatasetArr[0].getRasterCount();
			//set band number
			hDatasetResult=hDriver.Create("/ram/"+filename, iXsize*2, iYsize*2,iBandCount);
			System.out.println("create: "+"/ram/"+filename);
			//reset the sequence of images 
			String[] temp=filename.split("\\.")[0].split("_");
			hDatasetResult.SetMetadata("num="+temp[0]+"_"+temp[1]);
			
			int []arrayin = new int[iXsize*iYsize*iBandCount];
			int bandlist[] = new int[iBandCount];
			for(int i=0;i<iBandCount;i++)
			{
				bandlist[i]=i+1;
			}
			//writing the data of new image from small images
				hDatasetArr[map.get(arr[0])].ReadRaster(0, 0,iXsize, iYsize, iXsize,
						iYsize, gdalconstConstants.GDT_Int32, arrayin,
						bandlist, 0);
				hDatasetResult.WriteRaster(0, 0, iXsize, iYsize, iXsize,
						iYsize, gdalconstConstants.GDT_Int32, arrayin,
						bandlist, 0);
				
				hDatasetArr[map.get(arr[1])].ReadRaster(0, 0,iXsize, iYsize, iXsize,
						iYsize, gdalconstConstants.GDT_Int32, arrayin,
						bandlist, 0);
				hDatasetResult.WriteRaster(0, iYsize, iXsize, iYsize, iXsize,
						iYsize, gdalconstConstants.GDT_Int32, arrayin,
						bandlist, 0);
				
				hDatasetArr[map.get(arr[2])].ReadRaster(0, 0,iXsize, iYsize, iXsize,
						iYsize, gdalconstConstants.GDT_Int32, arrayin,
						bandlist, 0);
				hDatasetResult.WriteRaster(iXsize, 0, iXsize, iYsize, iXsize,
						iYsize, gdalconstConstants.GDT_Int32, arrayin,
						bandlist, 0);
				
				hDatasetArr[map.get(arr[3])].ReadRaster(0, 0,iXsize, iYsize, iXsize,
						iYsize, gdalconstConstants.GDT_Int32, arrayin,
						bandlist, 0);
				
				hDatasetResult.WriteRaster(iXsize, iYsize, iXsize, iYsize, iXsize,
						iYsize, gdalconstConstants.GDT_Int32, arrayin,
						bandlist, 0);
			
				
			//the dataset must be delete, otherwise the file can not be uploaded to the hdfs completely	
			hDatasetResult.delete();
			System.out.println("delete hdatasetresult");
			hDriver.delete();
//			hDriver.delete();
			for(int i=0;i<k;i++)
			{
				hDatasetArr[i].delete();
			}

			//get the file from the mfs to hdfs
			uploadToHdfs uth= new uploadToHdfs();
		//	uth.uploadFileToHdfs("/ram/"+filename);
		  
		  
		  
		  
	 }

}
