package com.GrassInShell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;



public class JavaShellUtil {
	// 基本路径
	private static final String basePath = "/home/hadoop/grassdata/";

	// 记录Shell执行状况的日志文件的位置(绝对路径)
	private static final String executeShellLogFile = basePath
			+ "executeShell.log";

	// 发送文件到Kondor系统的Shell的文件名(绝对路径)
	private static final String ShellName = basePath
			+ "startgrass.sh";

	public int executeShell(String shellCommand,String [] cmdarr) throws IOException {
		System.out.println("shellCommand:"+shellCommand);
		int success = 0;
		StringBuffer stringBuffer = new StringBuffer();
		BufferedReader bufferedReader = null;
		// 格式化日期时间，记录日志时使用
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS ");

		try {
			stringBuffer.append(dateFormat.format(new Date()))
					.append("准备执行Shell命令 ").append(shellCommand)
					.append(" \r\n");
			Process pid = null;

			String[] cmd = { "/bin/sh", "-c", shellCommand };
			// 执行Shell命令
			pid = Runtime.getRuntime().exec(cmd);
			//start thread which is to detect the inputstream from terminal
			myThread is=new myThread(pid.getInputStream());
			is.start();
			myThread es=new myThread(pid.getErrorStream());
			es.start();
			
			PrintWriter out =new PrintWriter(pid.getOutputStream());
			//process the cmds
			for(int i=0;i<cmdarr.length;i++)
			{
			out.println(cmdarr[i]);
			out.flush();
			}	
			stringBuffer.append("进程号：").append(pid.toString())
					.append("\r\n");
			// bufferedReader用于读取Shell的输出内容
			bufferedReader = new BufferedReader(new InputStreamReader(pid.getInputStream()), 1024);
			pid.waitFor();
			out.close();
			//stop the thread
			is.exit=true;
			es.exit=true;
			is.join();
			es.join();
			stringBuffer.append(dateFormat.format(new Date())).append(
					"Shell命令执行完毕\r\n执行结果为：\r\n");
			
			String line = null;
			// 读取Shell的输出内容，并添加到stringBuffer中
			while (bufferedReader != null
					&& (line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line).append("\r\n");
			}
			System.out.println("stringBuffer:"+stringBuffer);
		} catch (Exception ioe) {
			stringBuffer.append("执行Shell命令时发生异常：\r\n").append(ioe.getMessage())
					.append("\r\n");
		} finally {
			success = 1;
		}
		
		return success;
	}

	public static void process(String name) {
		try {
			
			String[] cmdarr=new String[8];	
				cmdarr[0]="g.mapset -c mapset=YYY";
				cmdarr[1] = "r.in.gdal -o input="+name+"  output=test";
				cmdarr[2]= "i.group group=tm subgroup=subtm input=test.1,test.2,test.3,test.4,test.5,test.6";
				cmdarr[3] = "cp -ri /home/hadoop/grassdata/sig /home/hadoop/grassdata/tm/YYY/group/tm/subgroup/subtm/sig";
				cmdarr[4] = "i.maxlik group=tm subgroup=subtm signaturefile=tmsig output=tmres";
				cmdarr[5] = "r.out.gdal --o input=tmres output="+name+" format=GTiff";
				cmdarr[6] = "exit";

			System.out.println(new JavaShellUtil().executeShell(ShellName, cmdarr));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String args[]) throws Exception
	{
		new deleteFolder().DeleteFolder("/home/hadoop/grassdata/tm");
		process("/home/hadoop/grassdata/2_1.tif");
	}
}

