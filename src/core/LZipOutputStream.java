package core;

import java.io.*;

public class LZipOutputStream extends OutputStream {
    private FileOutputStream fileOutputStream;
    private FileOutputStream tempFileOutputStream;//临时文件
    private ObjectOutputStream objectOutputStream;
    private File sourceFile;
    private File tempFile;
    private Inflater inflater = new Inflater();
    LZipOutputStream(File file) throws FileNotFoundException {
        sourceFile = file;
        fileOutputStream = new FileOutputStream(file);
        tempFile = new File(file.getAbsolutePath().replace(file.getName(),"$" + file.getName()));
        tempFileOutputStream = new FileOutputStream(tempFile);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        byte[] bytes = inflater.compress(b,len);
        tempFileOutputStream.write(bytes);
    }

    @Override
    public void write(int b) throws IOException {
        fileOutputStream.write(b);
    }

    void putNextEntry(String nextEntry) throws IOException {
        inflater.init();
        //写入了文件的内容，文件应该含有 文件名（含文件夹内部相对路径） +  huffman tree + 文件内容
        objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(nextEntry);//写入文件名含绝对路径
        objectOutputStream.writeObject(inflater.getChars());//写入字符集
    }

    void scan(byte []bytes,int len){//扫描文件获得权值
        inflater.createWeight(bytes,len);
    }

    public void close() throws IOException {
        tempFileOutputStream.close();
        objectOutputStream.writeObject(inflater.getLeftNumber());//写入模8后的bit位
        objectOutputStream.writeObject(inflater.getLength());//写入字节数
        byte[] buf = new byte[2048];
        FileInputStream fileInputStream = new FileInputStream(new File(sourceFile.getAbsolutePath().replace(sourceFile.getName(),"$" + sourceFile.getName())));
        int len;
        while((len = fileInputStream.read(buf)) != -1){
            fileOutputStream.write(buf,0,len);
        }
        fileInputStream.close();
        tempFile.delete();
        objectOutputStream.writeObject(null);//作为标记
        fileOutputStream.close();
        super.close();
    }

}
