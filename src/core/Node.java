package core;

import java.io.Serializable;

class Node implements Serializable{
    private Node leftChild = null;
    private Node rightChild = null;
    private long weight = 0;
    private char key = 'ā';//标记字符
    Node(){ }
    Node(char key,long weight){
        this.key = key;
        this.weight = weight;
    }

    char getKey(){
        return key;
    }

    long getWeight(){
        return weight;
    }

    Node getLeftChild(){
        return leftChild;
    }

    Node getRightChild(){
        return rightChild;
    }

    void setLeftChild(Node node){
        this.leftChild = node;
    }

    void setRightChild(Node node){
        this.rightChild = node;
    }

    void setWeight(long weight){
        this.weight = weight;
    }
}
