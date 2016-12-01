package com.GrassInShell;


import java.io.IOException;

import java.io.PrintWriter;

import java.util.Scanner;
public class myTest {
public static void main(String[] args) throws IOException{
Process process=Runtime.getRuntime().exec("gnome-terminal");
PrintWriter out =new PrintWriter(process.getOutputStream());
(new myThread(process.getInputStream())).start();
(new myThread(process.getErrorStream())).start();
//String ml = JOptionPane.showInputDialog("请输入CMD命令(q - 退出):");  调用本地组件
String ml = (new Scanner(System.in)).nextLine();
while(ml!=null&&!ml.equals("q")){
out.println(ml);
out.flush();
//ml = JOptionPane.showInputDialog("请输入CMD命令(q - 退出):");
ml = (new Scanner(System.in)).nextLine();
// ml = sc.next();
}
System.out.println("退出。。。。。");
System.exit(0);
}
}
