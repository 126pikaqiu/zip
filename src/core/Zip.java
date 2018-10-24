/*
* 压缩文件中有以下内容：
* 1 文件名（如果为文件夹，含内部相对路径)
* 2 文件的字节权数组（以byte + 128 作为索引，以出现次数作为元素内容） ！！读出的字节可能为负，不能作为数组索引
* 3 压缩后的字节数
* 4 最后一个字节实际有效的位数
* 5 压缩产生的字节
*
* 压缩有以下过程：
* 1 扫描文件，获得字节权值数组
* 2 根据字节权值数组创建优先队列，创建huffman tree,编码所有字节
* 3 再次读入文件，编码字节并存入文件中（每次最多读入4096个字节）
*
* 解压过程：
* 1 预读文件头，创建huffman数，并获得文件字节数，最后一个字节实际有效的位数
* 2 读入文件，解码文件（每次读入最多4096个字节），写入新文件中
* 说明：
* 1 压缩过程中会把压缩后的字节存入一个临时文件中 $filename.zip
*   目的是我必须要等到压缩完之后才能获得部分文件头内容 如 压缩后的字节数，最后一个字节实际有效的位数
* 2 文件头部分都是以写入对象的形式写入,压缩后文件的末尾写入了对象null作为作为标记
*
* 3 文件名字做适当加密
* */
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
        long time1 = System.currentTimeMillis();
        compress(out,sourcefile,sourcefile.getName());//压缩文件
        out.close();
        long time2 = System.currentTimeMillis();
        System.out.println( "压缩时间" + (time2 - time1));
    }

    public void unzip() throws IOException, ClassNotFoundException {//解压文件
        File zipFile = new File(zipFileName);//创建文件句柄
        LZipInputStream in = new LZipInputStream(zipFile);
        long time1 = System.currentTimeMillis();
        uncompress(in);//解压zip文件
        long time2 = System.currentTimeMillis();
        System.out.println("解压时间" + (time2 - time1));
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
                zos.putNextEntry(  base+"\\",0 );//0表示空文件夹
            }
            else//如果文件夹不为空，则递归调用compress，文件夹中的每一个文件（或文件夹）进行压缩
            {
                for (File aFlist : flist) {
                    compress(zos, aFlist, base + "\\" + aFlist.getName());
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
            zos.putNextEntry(base,1);// 建立一个目录进入点,1表示文件
            fis1.close();
            FileInputStream fis2 = new FileInputStream(sourceFile);
            while ((len = fis2.read(buf)) != -1) {
                zos.write(buf, 0, len);//写入文件
            }
            zos.temp2file();//写入文件头，临时文件到zip里面
            fis2.close();
        }
    }

    private void uncompress(LZipInputStream in) throws IOException {
        in.read();
    }

}
