package com.AllFileInput;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.gdal.gdal.Band;
import org.gdal.gdalconst.gdalconstConstants;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
/**
 * 
 * @author Yayu Yao
 * @time July 30, 2014
 * @version 1.0
 * @filename splitImage.java  
 * @description
 * In this code, I use GDAL to split the images, especially for 16 parts.
 * Also you can change the parameters for more or less parts, see more detail in line 36 
 *  
 */
public class splitImage {
	/**
	 * @param args
	 * @throws IOException 
	 */
	
	public static void main(String args[]) throws IOException
	{
		splitImage t=new splitImage();
		//setting row and column for spliting the image
		t.splitImage(4, 4);
	}
	public void splitImage(int iX,int iY) throws IOException {
		//index file just for testing
		//actually this file is not necessary for the next procedure
		//we can ignore it
		File f=new File("/home/hadoop/result/index.txt");
		BufferedWriter output = new BufferedWriter(new FileWriter(f));
		String indexStr = ""; 
		int iXSplitNum = iX;
		int iYSplitNum = iY;
		//set the original image path and result folder path
		String fileName_tif = "/home/hadoop/test.tif";
		String outName_tif_pr = "/home/hadoop/result2/";
		gdal.AllRegister();
		Dataset hDataset = gdal.Open(fileName_tif,
				gdalconstConstants.GA_ReadOnly);
		Dataset[] hDatasetArr = new Dataset[iXSplitNum * iYSplitNum];
		if (hDataset == null) {
			System.err.println("GDALOpen failed - " + gdal.GetLastErrorNo());
			System.err.println(gdal.GetLastErrorMsg());
			System.exit(1);
		}
		Driver hDriver = hDataset.GetDriver();
		System.out.println("Driver: " + hDriver.getShortName() + "/"
				+ hDriver.getLongName());
		System.out.println("Size is " + hDataset.getRasterXSize() + ", "
				+ hDataset.getRasterYSize());
		int iXSize = hDataset.getRasterXSize();
		int iYSize = hDataset.getRasterYSize();
		int iXSizeSplit = iXSize / iXSplitNum;
		int iYSizeSplit = iYSize / iYSplitNum;
		String prj=hDataset.GetProjection();
		double [] argin=hDataset.GetGeoTransform();
		System.out.println("XSizeSplit="+iXSizeSplit+" YSizeSplit="+iYSizeSplit);
		int iBandCount = hDataset.getRasterCount();
		

		System.out.println(iBandCount);
		int arrayin[]= new int[iXSizeSplit * iYSizeSplit * iBandCount]; 
		//just get one band temporarily 
		int bandlist[] = new int[iBandCount];
		for(int i=0;i<iBandCount;i++)
		{
			bandlist[i]=i+1;
		}
//		for(int i=0;i<iBandCount;i++)
//		{
//			Band band=hDataset.GetRasterBand(i+1);
//			band.ReadRaster(iXSizeSplit, iYSizeSplit, iXSizeSplit, iYSizeSplit, iXSizeSplit, iYSizeSplit, gdalconstConstants.GDT_Int32, arrayin, 0, 0);
		hDataset.ReadRaster(iXSizeSplit, iYSizeSplit,iXSizeSplit, iYSizeSplit, iXSizeSplit,
				iYSizeSplit, gdalconstConstants.GDT_Int32, arrayin,
				bandlist, 0);
//		}
		//read and write splitted data
		for (int i = 0; i < iXSplitNum; i++) {
			for (int j = 0; j < iYSplitNum; j++) {
				
				int temp=i*iXSplitNum+j;
				System.out.println("i="+i+" j="+j+" temp="+temp);
				
				hDataset.ReadRaster(iXSizeSplit*i, iYSizeSplit*j,iXSizeSplit, iYSizeSplit, iXSizeSplit,
						iYSizeSplit, gdalconstConstants.GDT_Int32, arrayin,
						bandlist, 0);
				
				hDatasetArr[temp] = hDriver.Create(outName_tif_pr+i+"_"+j+".tif", iXSizeSplit, iYSizeSplit,iBandCount);
				indexStr+="result"+i+j+".tif ";
				hDatasetArr[temp].WriteRaster(0, 0, iXSizeSplit, iYSizeSplit, iXSizeSplit,
						iYSizeSplit, gdalconstConstants.GDT_Int32, arrayin,
						bandlist, 0);
				//set sequences for splitted images, it is important !!!
				hDatasetArr[temp].SetMetadata("num="+i+"_"+j);
				hDatasetArr[temp].SetProjection(prj);
				hDatasetArr[temp].SetGeoTransform(argin);
			}
		}
		for (int i = 0; i < hDatasetArr.length; i++) {
			hDatasetArr[i].delete();
			}
		output.write(indexStr);
		output.close();
		hDataset.delete();
		hDriver.delete();
		// optional
		gdal.GDALDestroyDriverManager();
	}
	public int[] binaryzate(int []arr)
	{
		for(int i=0;i<arr.length;i++)
		{
			if(arr[i]>200)
			{
				arr[i]=255;
			}
			else
			{
				arr[i]=0;
			}
		}
		return arr;
	}
}
