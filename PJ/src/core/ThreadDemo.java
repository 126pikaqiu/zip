package core;


        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 多线程下写文件
 *
 * @author Administrator
 *
 */
public class ThreadDemo {

    public static void main(String[] args) {
        File file=new File("C:/Users/asus/Desktop/test1.txt");
        try {
            FileOutputStream out=new FileOutputStream(file, true);
            ConcurrentLinkedQueue<String> queue=new ConcurrentLinkedQueue<String>();
            for(int i=0;i<10;i++){
                new Thread(new MyThread(queue,"线程"+i+",")).start();//多线程往队列中写入数据
            }
            long time1 = System.nanoTime();
            new Thread(new DealFile(out,queue)).start();//监听线程，不断从queue中读数据写入到文件中
//            new Thread(new DealFile(out,queue)).start();//监听线程，不断从queue中读数据写入到文件中
            long time2 = System.nanoTime();
            try {
                Thread.sleep(3000);
                if(!Thread.currentThread().isAlive()){
                    System.out.println(time2 - time1);
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}

/**
 * 将要写入文件的数据存入队列中
 *
 * @author Administrator
 *
 */
class MyThread implements Runnable {
    private ConcurrentLinkedQueue<String> queue;
    private String contents;

    public MyThread() {
    }

    public MyThread(ConcurrentLinkedQueue<String> queue, String contents) {
        this.queue = queue;
        this.contents = contents;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        queue.add(contents);
    }
}
/**
 * 将队列中的数据写入到文件
 * @author Administrator
 *
 */
class DealFile implements Runnable {
    private FileOutputStream out;
    private ConcurrentLinkedQueue<String> queue;

    public DealFile() {
    }

    public DealFile(FileOutputStream out, ConcurrentLinkedQueue<String> queue) {
        this.out = out;
        this.queue = queue;
    }

    @Override
    public void run() {
        synchronized (queue) {
            while (true) {
                if (!queue.isEmpty()) {
                    try {
                        out.write(queue.poll().getBytes("UTF-8"));
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}

