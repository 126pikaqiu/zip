package core;

import java.io.*;

class MultiThreadScan implements Runnable{
    private static FileInputStream fi = null;
    private static LZipOutputStream lZipOutputStream = null;
    private byte[] buf = new byte[8192];

    MultiThreadScan(FileInputStream fileInputStream1, LZipOutputStream lZipOutputStream1) throws FileNotFoundException, InterruptedException {
        fi = fileInputStream1;
        lZipOutputStream = lZipOutputStream1;
    }

    MultiThreadScan() throws InterruptedException {}

    public void run() {
                int len;
                try {
                    while((len = fi.read(buf)) != -1) {
                        synchronized(lZipOutputStream) {
                            lZipOutputStream.scan(buf, len);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
    }

}
