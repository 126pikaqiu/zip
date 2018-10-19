package core;

import java.io.*;

public class Zip {
    private String zipFileName;
    private String sourceFileName;
    private int READSIZE = 4096;
    public Zip(String zipFileName,String sourceFileName)
    {
        this.zipFileName = zipFileName;
        this.sourceFileName = sourceFileName;
    }

    public Zip(String zipFileName)
    {
        this.zipFileName = zipFileName;
    }

    public void zip() throws Exception {//压缩文件
        File sourcefile = new File(sourceFileName);//创建文件句柄
        LZipOutputStream out = new LZipOutputStream(new File(zipFileName));
        compress(out,sourcefile,sourceFileName);//压缩文件
    }

    public void unzip() throws IOException, ClassNotFoundException {//解压文件
        File zipFile = new File(zipFileName);//创建文件句柄
        LZipInputStream in = new LZipInputStream(zipFile);
        uncompress(in);//解压zip文件
        in.close();
    }

    private void compress(LZipOutputStream zos, File sourceFile, String base) throws Exception
    {
        //如果路径为目录（文件夹）
        if(sourceFile.isDirectory())
        {

            //取出文件夹中的文件（或子文件夹）
            File[] flist = sourceFile.listFiles();

            assert flist != null;
            if(flist.length==0)//如果文件夹为空，则只需在目的地zip文件中写入一个目录进入点
            {
                zos.putNextEntry(  base+"/" );
            }
            else//如果文件夹不为空，则递归调用compress，文件夹中的每一个文件（或文件夹）进行压缩
            {
                for (File aFlist : flist) {
                    compress(zos, aFlist, base + "/" + aFlist.getName());
                }
            }
        }
        else//如果不是目录（文件夹），即为文件，则先写入目录进入点，之后将文件写入zip文件中
        {
            FileInputStream fis1 = new FileInputStream(sourceFile);// 目录进入点的名字是文件在压缩文件中的路径

            int len = 0;
            byte[] buf = new byte[READSIZE];
            while ((len = fis1.read(buf)) != -1) {
                zos.scan(buf,  len);//扫描文件
            }
            zos.putNextEntry(base);// 建立一个目录进入点
            fis1.close();
            FileInputStream fis2 = new FileInputStream(sourceFile);
            while ((len = fis2.read(buf)) != -1) {
                zos.write(buf, 0, len);//写入文件
            }
            fis2.close();
            zos.close();

        }
    }

    private void uncompress(LZipInputStream in) throws IOException {
        in.read();
    }

}
