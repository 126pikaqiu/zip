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
    private int type;
    LZipInputStream(File sourceFile) throws IOException {
        this.sourceFile = sourceFile;
        fileInputStream = new FileInputStream(sourceFile);
    }

    private boolean pre_read() throws IOException, ClassNotFoundException { //预读文件头
        try {
            objectInputStream = new ObjectInputStream(fileInputStream);
        }catch (EOFException ignored){

        }
        Object object;
        try {
            if((type =  objectInputStream.readByte()) != 3){
                if(type == 1){ //文件
                    object = objectInputStream.readObject();
                    String nextEntry = (String) object;
                    int[] array = readWeightArray(objectInputStream);
                    inflater.setHuffmanTreeNodes(array);//读出字符集并创建huffman tree
                    int leftNumber = objectInputStream.readByte();//读出模8后的bit位
                    inflater.setLeftNumber(leftNumber);
                    len = (Long) objectInputStream.readObject();//读出字节数
                    inflater.setLength(len);
                    String name = getCurrentFilePath(unpwdName(nextEntry));
                    checkPath(name);//检查文件夹是否创建成功
                    File currentFile = new File(name);//解压到当前目录下
                    fileOutputStream = new FileOutputStream(currentFile);
                    inflater.emptyLeft();
                }else{ //空文件夹或者空文件
                    object = objectInputStream.readObject();
                    String nextEntry = (String) object;
                    String name = getCurrentFilePath(unpwdName(nextEntry));
                    if(type == 0 ){
                        checkPath(name + "\\");//检查文件夹是否创建成功
                    }else{
                        checkPath(name);
                        System.out.println(name);
                        File file = new File(name);
                        if(!file.exists()){
                            file.createNewFile();
                        }
                    }
                }
                return true;
            }else{
                return false;
            }
        }catch (EOFException e){
            objectInputStream.close();
            fileInputStream.close();
            return false;
        }
    }
    @Override
    public int read() throws IOException {
        try {
            while (pre_read()){
                if(type == 0 || type == 2){
                    continue;//文件夹或者空文件的话就跳过读出其他数据的操作
                }
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
        objectInputStream.close();
        super.close();
    }

    private String getCurrentFilePath(String oldPath){//解压到当前目录
        return sourceFile.getAbsolutePath().replaceAll(sourceFile.getName(),"") + oldPath;
    }

    private String unpwdName(String name){//解密文件名
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

    private void checkPath(String currentPath){
        String base = sourceFile.getAbsolutePath().replace(sourceFile.getName(),"");
        int baseLength = base.length();
        for(int j = baseLength;j < currentPath.length();j++){
            if(currentPath.charAt(j) == '\\'){
                File file = new File(currentPath.substring(0,j));
                if(!file.exists()){
                    file.mkdir();
                }
            }
        }
    }

    int[] readWeightArray(ObjectInputStream objectInputStream) throws IOException {//写入字节数组
        int array[] = new int[256];
        for(int i = 0; i < 256; i++){
            array[i] = objectInputStream.readInt();
        }
        return array;
    }
}
