package core;

import java.util.HashMap;

//压缩的核心程序类
class Inflater {
    private int READSIZE = 4096;
    private MyQueue myQueue = new MyQueue();
    private int n = 0;//字符的种类数
    private long[] chars = new long[256];
    private HashMap<Integer,String> mapchtobi=new HashMap<Integer, String>();//利用散列表储存字符及对应编码
    private Node root;
    private long lengthzips = 0;
    private int leftNumber = 0;
    private Node left;
    private String leftStr = "";

    void createWeight(byte []bytes,int len){//获得叶子节点，通过计数获得其权值
        for (int i = 0; i < len; i++) {
            chars[bytes[i] + 128]++;//计算每个字符的权值，加128保证非负
        }
    }

    private void dequeue(){ //入队
        n = 0;
        myQueue = new MyQueue();
        for(int i = 0; i < chars.length; i++){
            if(chars[i] != 0){
                myQueue.insert(new Node((char)i,chars[i]));
                n++;
            }
        }
    }

    private void creatHuffman(){//构造huffman tree，获得根节点
        dequeue();
        for(int i = 0; i < n - 1; i++){
            Node temp = new Node();
            Node node_l = myQueue.extract_min();
            temp.setLeftChild(node_l);
            Node node_r = myQueue.extract_min();
            temp.setRightChild(node_r);
            temp.setWeight(node_l.getWeight() + node_r.getWeight());
            myQueue.insert(temp);
        }
        root =  myQueue.extract_min();
    }

    Object[] uncompress(byte[] bytes,long len){//解码
        System.out.println(lengthzips);
        lengthzips = lengthzips - len;
        Node now = root;
        if(left != null){
            now = left;
        }
        byte []bytes1 = new byte[2048];
        int k = 0;
        for (int i = 0; i < len; i++) {
            byte aByte = bytes[i];
                if(lengthzips == 0 && i == len - 1 && leftNumber != 0){//lengthzips等于零表示读到最后一个字节
                    for (int j = 9 - leftNumber; j <= 8 ; j++) {
                        if (gitBit(j, aByte) == 0 && now.getLeftChild() != null) {
                            now = now.getLeftChild();
                        } else if (gitBit(j, aByte) == 1 && now.getRightChild() != null) {
                            now = now.getRightChild();
                        } else {
                            if(k == bytes1.length){
                                byte [] temp = new byte[bytes1.length * 2];
                                System.arraycopy(bytes1,0,temp,0,bytes1.length);
                                bytes1 = temp;
                            }
                            bytes1[k++] = (byte)(now.getKey() - 128);
                            left = null;
                            now = gitBit(j, aByte) == 0 ? root.getLeftChild() : root.getRightChild();
                        }
                        if(j == 8){
                            bytes1[k++] = (byte)(now.getKey() - 128);
                            left = null;
                        }
                    }
                }else{
                    for (int j = 1; j <= 8 ; j++) {
                        if (gitBit(j, aByte) == 0 && now.getLeftChild() != null) {
                            now = now.getLeftChild();
                        } else if (gitBit(j, aByte) == 1 && now.getRightChild() != null) {
                            now = now.getRightChild();
                        }else{
                            if(k == bytes1.length){
                                byte [] temp = new byte[bytes1.length * 2];
                                System.arraycopy(bytes1,0,temp,0,bytes1.length);
                                bytes1 = temp;
                            }
                            bytes1[k++] = (byte)(now.getKey() - 128);
                            now = gitBit(j, aByte) == 0 ? root.getLeftChild() : root.getRightChild();
                        }
                        if(i == len - 1 && now.getKey() == 'ā' && j == 8){
                            left = now;
                        }else if(i == len - 1 && j == 8){
                            bytes1[k++] = (byte)(now.getKey() - 128);
                            left = null;
                        }
                    }
                }

        }

        return new Object[]{bytes1,k};
    }

    private int gitBit(int index, byte b){
        return (b & (1 << 8 - index)) >> (8 - index);
    }

    private void crtHuffmanNodeCode(Node node, String code){//根据huffman tree，编码叶子节点。
        if(node.getKey() != 'ā'){
            mapchtobi.put((int)node.getKey(),code);
            return;
        }
        if(node.getLeftChild() != null){
            crtHuffmanNodeCode(node.getLeftChild(),code + "0");
        }

        if(node.getRightChild() != null){
            crtHuffmanNodeCode(node.getRightChild(),code + "1");
        }
    }

    byte[] compress(byte[] bytes, int len){//编码字节数组
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < len; i++){
            result.append(mapchtobi.get(bytes[i] + 128));
        }
        return str2bytes(result.toString(),len);
    }

    private byte[] str2bytes(String str,int len){//将二进制字符串转化为字节数组存于nu
        str = leftStr + str;
        int foot=str.length();
        int n=foot/8;
        int m=foot%8;
        int c=0;
        if(m>0 && len < READSIZE) c=1;
        byte[] nu=new byte[n+c];
        for(int i=0;i<n;i++){
            nu[i]=(byte)Integer.parseInt(str.substring(i * 8, i * 8 + 8), 2);
        }
        leftStr = str.substring(n * 8,foot);
        if(m>0 && len < READSIZE){
            leftNumber = foot - n * 8;
            nu[n]=(byte)Integer.parseInt(str.substring(n * 8,foot),2);
        }
        lengthzips += n + c;
        return nu;
    }


    void setHuffmanTreeNodes(Object tree){//获得字符集
        chars = (long [])tree;
//        for(int i = 0; i < chars.length; i++){
//            if(chars[i] != 0){
//                System.out.print(i + " ");
//            }
//        }
//        System.out.println();
        dequeue();
        creatHuffman();
    }

    void setLength(long length){
        this.lengthzips = length;
    }

    int getLeftNumber(){
        return leftNumber;
    }

    void setLeftNumber(int leftNumber){
        this.leftNumber = leftNumber;
    }

    long getLength(){
        return lengthzips;
    }

    long[] getChars(){
        return chars;
    }

    void init(){
        lengthzips = 0;
        leftNumber = 0;
        creatHuffman();
        crtHuffmanNodeCode(root,"");
    }

    void emptyLeft(){//清空left
        left = null;
    }
}


