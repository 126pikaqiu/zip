package core;

import java.io.*;

public class LZipInputStream extends InputStream {
    private FileInputStream fileInputStream;

    LZipInputStream(File sourceFile) throws FileNotFoundException {
        fileInputStream = new FileInputStream(sourceFile);
    }


    @Override
    public int read() throws IOException {
        ObjectInputStream objectOutputStream = new ObjectInputStream(fileInputStream);
        Object object;
        try {
            while ((object =  objectOutputStream.readObject()) != null){
                Inflater inflater = new Inflater("");
                String nextEntry = (String) object;
                inflater.setHuffmanTreeNodes(objectOutputStream.readObject());
                int len = new DataInputStream(fileInputStream).readInt();//读入字节数
                File newFile = new File(nextEntry);
                byte bytes[] = new byte[len];
                fileInputStream.read(bytes);
                inflater.uncompress(bytes);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        objectOutputStream.close();
        return fileInputStream.read();
    }

    @Override
    public int read(byte[] bytes) throws IOException {

        return super.read(bytes);
    }
}
