package core;

import java.io.*;

public class LZipInputStream extends InputStream {
    private FileInputStream fileInputStream;
    private long len = 0;
    private int k;
    private FileOutputStream fileOutputStream;
    private Inflater inflater = new Inflater();
    private ObjectInputStream objectOutputStream;
    LZipInputStream(File sourceFile) throws IOException {
        fileInputStream = new FileInputStream(sourceFile);
        objectOutputStream = new ObjectInputStream(fileInputStream);
    }

    private boolean pre_read() throws IOException, ClassNotFoundException { //预读文件头
        Object object;
        if((object =  objectOutputStream.readObject()) != null){
            String nextEntry = (String) object;
            object = objectOutputStream.readObject();
            inflater.setHuffmanTreeNodes(object);//读出字符集并创建huffman tree
            int leftNumber = (Integer)objectOutputStream.readObject();//读出模8后的bit位
            inflater.setLeftNumber(leftNumber);
            len = (Long) objectOutputStream.readObject();//读出字节数
            inflater.setLength(len);
            File currentFile = new File(nextEntry);
            fileOutputStream = new FileOutputStream(currentFile);
            inflater.emptyLeft();
            return true;
        }else{
            return false;
        }
    }
    @Override
    public int read() throws IOException {
        try {
            while (pre_read()){
                while (len > 0){
                    byte bytes[];
                    if(len > 2048) {
                        bytes = new byte[2048];
                    }else{
                        bytes = new byte[(int)len];
                    }
                    int leng = fileInputStream.read(bytes);
                    Object cons[] = inflater.uncompress(bytes,leng);
                    fileOutputStream.write((byte[])cons[0],0,(int)cons[1]);
                    len = len - bytes.length;
                }
                fileOutputStream.close();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int read(byte[] bytes) throws IOException {
        return super.read(bytes);
    }

    public void close() throws IOException {
        fileInputStream.close();
        super.close();
    }
}
