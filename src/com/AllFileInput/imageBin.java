package com.AllFileInput;
/**
 * 
 * @author Yayu Yao
 * @time July 30, 2014
 * @version 1.0
 * @filename imageBin.java  
 * @description
 * This code is the core of whole task.
 * In the function of createFileCache, we can get the data from Reduce, 
 * and then create a new image in RAM preparing for the next step.
 * 
 * "vsimem" is another point that you should notice.
 * It is a part of GDAL, not from Linux, it just offer a space to store and get byte streams,
 * for the sake of strict requirements of data format in Hadoop.
 * 
 * 
 * 
 * In this part, we also implement to call the function of JavaShellUtil.
 * It is same to output of Hadoop, 
 * the file folder which named "tm" in the root directory of grass should be deleted,
 * before a new task.
 *  
 */
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;

import com.GrassInShell.JavaShellUtil;
import com.GrassInShell.deleteFolder;

public class imageBin {
	

	String numvalue;
	String createFileCache(String fileName,byte [] nBytes,int num)
	{
		String name=fileName.split("\\.")[0];
		gdal.AllRegister();
		gdal.FileFromMemBuffer("/vsimem/"+name+num+".dat", nBytes);
		Dataset hDataset = gdal.Open("/vsimem/"+name+num+".dat",gdalconstConstants.GA_ReadOnly);
	    gdal.Unlink("/vsimem/"+name+".dat");
		if (hDataset == null) {
			System.err.println("GDALOpen failed - " + gdal.GetLastErrorNo());
			System.err.println(gdal.GetLastErrorMsg());
			System.exit(1);
		}
		Driver hDriver = hDataset.GetDriver();		
		int iXsize=hDataset.getRasterXSize();
		int iYsize=hDataset.getRasterYSize();
		String prj=hDataset.GetProjection();
		double [] argin=hDataset.GetGeoTransform();
		int iBandCount=hDataset.getRasterCount();
		System.out.println("bandcount="+iBandCount);
		int []arrayin = new int[iXsize*iYsize*iBandCount];
		int bandlist[] = new int[iBandCount];
		for(int i=0;i<iBandCount;i++)
		{
			bandlist[i]=i+1;
		}
		
		
		hDataset.ReadRaster(0, 0,iXsize, iYsize, iXsize,iYsize, gdalconstConstants.GDT_Int32, arrayin,
				bandlist, 0);
		numvalue=hDataset.GetMetadataItem("num");
		
		Dataset hDatasetResult=hDriver.Create("/ram/"+name+"_"+num+".tif", iXsize, iYsize,iBandCount);

		hDatasetResult.WriteRaster(0, 0, iXsize, iYsize, iXsize,
				iYsize, gdalconstConstants.GDT_Int32, arrayin,
				bandlist, 0);	
		hDatasetResult.SetMetadata("num="+numvalue);
		hDatasetResult.SetProjection(prj);
		hDatasetResult.SetGeoTransform(argin);
		hDatasetResult.delete();
		
		
		
		
		
//	    hDriver.CreateCopy("/ram/"+name+"_"+num+".tif", hDataset);
//		hDataset.delete();
//		hDriver.delete();
//		gdal.GDALDestroyDriverManager();
		
		
		return "/ram/"+name+"_"+num+".tif";
	}
	
	void classingrass(String filepath)
	{
		//use grass to processing the file
		System.out.println("Start grass path="+filepath);
		new deleteFolder().DeleteFolder("/home/hadoop/grassdata/tm");
		new JavaShellUtil().process(filepath);
	}
	 
	void rewritenumvalue(String filepath)
	{
		gdal.AllRegister();
		Dataset hDataset = gdal.Open(filepath,gdalconstConstants.GA_ReadOnly);	
		hDataset.SetMetadata("num="+numvalue);
		hDataset.delete();
	}

}
