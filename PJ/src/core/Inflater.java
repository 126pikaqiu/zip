package core;

import java.util.HashMap;

//压缩的核心程序类
class Inflater {
    private MyQueue myQueue = new MyQueue();
    private int n = 0;//字符的种类数
    private Node nodes[] = new Node[50];//叶子节点的个数
    private int[] chars = new int[100];
    private HashMap<String,String> mapchtobi=new HashMap<String,String>();//利用散列表储存字符及对应编码
    private Node root;
    private byte[] byte_array;
    private String con;
    private String left;
    Inflater(String con){
        this.con = con;
    }
    private void createWeight(String con){//获得叶子节点，通过计数获得其权值
        for(int i = 0; i < con.length();i++){
            if(con.charAt(i) >= chars.length){
                int length = con.charAt(i) < chars.length? chars.length * 2 : (con.charAt(i) + 10);
                int[] temp = new int[length];
                System.arraycopy(chars,0,temp,0,chars.length);
                chars = temp;
            }
            chars[con.charAt(i)]++;//计算每个字符的权值
        }
        int j = 0;
        for(int i = 0; i < chars.length; i++){
            if(chars[i] != 0){
                if(j >= nodes.length){
                    Node[] temp = new Node[nodes.length * 2];
                    System.arraycopy(nodes,0,temp,0,nodes.length);
                    nodes = temp;
                }
                nodes[j] = new Node((char)(i) + "",chars[i]);
                myQueue.insert(nodes[j]);
                n++;j++;
            }
        }
        Node[] temp = new Node[j + 1];
        System.arraycopy(nodes,0,temp,1,j);
        temp[0] = root;
        nodes = temp;
    }

    private void creatHuffman(){//构造huffman tree，获得根节点
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

    char[] uncompress(byte[] bytes){//解码
        Node now = root;
        StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            for (int j = 7; j > 0; j++) {
                if (aByte >> j == 0 && now.getLeftChild() != null) {
                    now = now.getLeftChild();
                } else if (aByte >> j == 1 && now.getRightChild() != null) {
                    now = now.getRightChild();
                } else {
                    result.append(now.getKey());
                    now = aByte >> j == 0 ? root.getLeftChild() : root.getRightChild();
                }
            }
        }

        return result.toString().toCharArray();
    }

    private void crtHuffmanNodeCode(Node node, String code){//根据huffman tree，编码叶子节点。
        if(node.getKey() != null){
            mapchtobi.put(node.getKey(),code);
            return;
        }
        if(node.getLeftChild() != null){
            crtHuffmanNodeCode(node.getLeftChild(),code + "0");
        }

        if(node.getRightChild() != null){
            crtHuffmanNodeCode(node.getRightChild(),code + "1");
        }
    }

    public void compress(){//编码所有字符
        createWeight(con);
        creatHuffman();
        crtHuffmanNodeCode(root,"");
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < con.length(); i++){
            result.append(mapchtobi.get(con.charAt(i) + ""));
        }
//        System.out.print(result);
        str2bytes(result.toString());
    }

    Node[] getHuffmanTreeNodes(){//返回huffman tree数组，第一个元素为根节点
        return nodes;
    }

    public void str2bytes(String str){//将二进制字符串转化为字节数组存于byte_array
        int foot=str.length();
        int n=foot/8;
        int m=foot%8;
        int c=0;
        if(m>0) c=1;
        byte[] nu=new byte[n+c];
        for(int i=0;i<n;i++){
            nu[i]=(byte)Integer.parseInt(str.substring(i * 8, i * 8 + 8), 2);
        }
        if(m>0){
            nu[n]=(byte)Integer.parseInt(str.substring(n * 8,foot),2);
        }
        byte_array = nu;
    }

    byte[] getByteArray(){
        return byte_array;
    }

    void setHuffmanTreeNodes(Object tree){
        nodes = (Node[])tree;
        root = nodes[0];//获得huffman tree的根节点
    }

}


