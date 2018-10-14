package core;

import java.io.*;

public class LZipOutputStream extends OutputStream {
    private String nextEntry;//目录进入点
    private FileOutputStream fileOutputStream;
    private StringBuilder con = new StringBuilder();
    private File sourceFile;
    LZipOutputStream(File file) throws FileNotFoundException {
        fileOutputStream = new FileOutputStream(file);
    }
    void setSourceFile(File sourceFile){
        this.sourceFile = sourceFile;
    }
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        char[] chars = new char[len];
        for(int j = 0; j < len; j++){
            chars[j] = (char)b[j];
        }
        con.append(new String(chars));
        if(con.toString().length() == sourceFile.length()){
            Inflater inflater = new Inflater(con.toString());
            inflater.compress();//压缩文件
            byte[] byte_array = inflater.getByteArray();//获得压缩后的字节数组
            //写入了文件的内容，文件应该含有 文件名（含文件夹内部相对路径） +  huffman tree + 文件内容
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(nextEntry);//写入文件名
            objectOutputStream.writeObject(inflater.getHuffmanTreeNodes());//写入编码树
            fileOutputStream.write(byte_array.length);//写入字节的大小
            fileOutputStream.write(byte_array);
            objectOutputStream.writeObject(null);//作为标记
            fileOutputStream.close();
            objectOutputStream.close();
        }
    }

    @Override
    public void write(int b) throws IOException {
        fileOutputStream.write(b);
    }

    void putNextEntry(String nextEntry){
        this.nextEntry = nextEntry;
    }
}
