package com.GrassInShell;

import java.io.InputStream;


public class myThread extends Thread{
	private InputStream is;
	 public volatile boolean exit = false; 
	public myThread(InputStream is){
	this.is=is;
	}
@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		byte[] buf=new byte[1024];
		int size;
		while(!exit){
		try{
			while((size = is.read(buf))!=-1){
				System.out.println(new String(buf,0,size,"utf-8"));
				}
			}catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}
}