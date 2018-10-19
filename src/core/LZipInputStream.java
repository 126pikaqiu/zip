package core;

import java.io.*;

public class LZipInputStream extends InputStream {
    private FileInputStream fileInputStream;
    private long len = 0;
    private int READSIZE = 4096;
    private FileOutputStream fileOutputStream;
    private Inflater inflater = new Inflater();
    private ObjectInputStream objectInputStream;
    private File sourceFile;
    LZipInputStream(File sourceFile) throws IOException {
        this.sourceFile = sourceFile;
        fileInputStream = new FileInputStream(sourceFile);
        objectInputStream = new ObjectInputStream(fileInputStream);
    }

    private boolean pre_read() throws IOException, ClassNotFoundException { //预读文件头
        Object object;
        try {
            if((object =  objectInputStream.readObject()) != null){
                String nextEntry = (String) object;
                object = objectInputStream.readObject();
                inflater.setHuffmanTreeNodes(object);//读出字符集并创建huffman tree
                int leftNumber = (Integer)objectInputStream.readObject();//读出模8后的bit位
                inflater.setLeftNumber(leftNumber);
                len = (Long) objectInputStream.readObject();//读出字节数
                inflater.setLength(len);
                File currentFile = new File(getCurrentFilePath(nextEntry));//解压到当前目录下
                fileOutputStream = new FileOutputStream(currentFile);
                inflater.emptyLeft();
                return true;
            }else{
                return false;
            }
        }catch (EOFException e){
            e.printStackTrace();
            objectInputStream.close();
            fileInputStream.close();
            System.exit(0);
            return false;
        }
    }
    @Override
    public int read() throws IOException {
        try {
            while (pre_read()){
                while (len > 0){
                    byte bytes[];
                    if(len > READSIZE) {
                        bytes = new byte[READSIZE];
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

    String getCurrentFilePath(String oldPath){//解压到当前目录
        oldPath = unpwdName(oldPath);
        int start = 0;
        for(int i = 0; i < oldPath.length();i++){
            if(oldPath.charAt(i) == '\\'){
                start = i;
            }
        }
        String name = oldPath.substring(start + 1,oldPath.length());
        return sourceFile.getAbsolutePath().replaceAll(sourceFile.getName(),name);
    }

    String unpwdName(String name){//解密文件名
        int now;
        int start = now = name.charAt(name.length() - 1);
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < name.length() - 1; i++){
            now ^= name.charAt(i);
            result.append((char) now);
            now = name.charAt(i);
        }
        return result.toString();
    }
}
