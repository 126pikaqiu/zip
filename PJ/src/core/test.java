package core;

import java.io.File;

public class test {
    public static void main(String []args){
//        File srcFile = new File("D:\\BaiduNetdiskDownload\\YY杀手.mp4");
//        File tarFile = new File("D:\\BaiduNetdiskDownload\\YY杀手1.mp4");
//        multiThreadCopy(srcFile,tarFile);
        System.out.println((char)(-1));

    }
    //使用三个线程同时拷贝文件
    static void multiThreadCopy(File srcFile, File tarFile){
        int n = 5;
        for (int i = 0; i < n; i++) {// 每一部分的编号
            new Thread(new MultiThreadFileCopy(srcFile, tarFile, n, i)).start();
        }
    }
}
