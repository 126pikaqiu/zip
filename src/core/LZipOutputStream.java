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

    void putNextEntry(String nextEntry,int type) throws IOException {
        if(type == 1){
            tempFile = new File(sourceFile.getAbsolutePath().replace(sourceFile.getName(),"$" + sourceFile.getName()));//创建临时文件
            tempFileOutputStream = new FileOutputStream(tempFile);
            inflater.init();
            //写入了文件的内容，文件应该含有 文件名（含文件夹内部相对路径） +  huffman tree + 文件内容
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeByte(1);//写入类型1
            objectOutputStream.writeObject(pwdName(nextEntry));//写入加密后的文件名不含绝对路径,若为文件夹，含内部相对路径
            objectOutputStream.writeObject(inflater.getChars());//写入字符集
        }else{
            objectOutputStream = new ObjectOutputStream(fileOutputStream);//空文件夹只需要写入类型0  文件名
            objectOutputStream.writeByte(0);
            objectOutputStream.writeObject(pwdName(nextEntry));//写入加密后的文件名含绝对路径
        }
    }

    void scan(byte []bytes,int len){//扫描文件获得权值
        inflater.createWeight(bytes,len);
    }

    public void close() throws IOException {
        objectOutputStream.writeByte(2);//作为标记
        fileOutputStream.close();
        super.close();
    }

    void temp2file() throws IOException {
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
    }

    String pwdName(String name){//加密文件名，采用异或加密的方式
        int now;
        int start = now = name.charAt(0) + name.length();
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < name.length(); i++){
            now ^= name.charAt(i);
            result.append((char) now);
        }
        result.append((char)start);
        return result.toString();
    }


}
