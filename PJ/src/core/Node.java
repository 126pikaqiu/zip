package core;

import java.io.Serializable;

class Node implements Serializable{
    private Node leftChild = null;
    private Node rightChild = null;
    private int weight = 0;
    private String key;
    Node(){ }
    Node(String key,int weight){
        this.key = key;
        this.weight = weight;
    }

    String getKey(){
        return key;
    }

    int getWeight(){
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

    void setWeight(int weight){
        this.weight = weight;
    }
}
