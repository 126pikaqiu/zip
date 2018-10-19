package core;

//优先队列，最小堆维护
class MyQueue {
    private int number = 100;
    
    private int length = 0;
    
    private Node heap[] = new Node[number];

    private int parent(int i){//父节点索引
        return (i - 1) / 2;
    }

    private int left(int i){//左子节点索引
        return 2 * i + 1;
    }

    private int right(int i){//右子节点索引
        return 2 * i + 2;
    }

    private void build_min_heap(){
        for(int i = length / 2 + 1;i >= 0; i--){
            min_heapify(i);
        }
    }
    
    void insert(Node node){
        if(length == number){
            number *= 2;
            Node temp2[] = new Node[number];
            System.arraycopy(heap,0,temp2,0,heap.length);
            heap = temp2;
        }
        heap[length++] = node;
        int index = length - 1;
        while(index > 0 && heap[parent(index)].getWeight() > heap[index].getWeight()){
            Node temp = heap[index];
            heap[index] = heap[parent(index)];
            heap[parent(index)] = temp;
            index = parent(index);
        }
    }

    private void min_heapify(int i){
        int l = left(i);
        int r = right(i);
        int smallest = i;
        if(l < length && heap[l].getWeight() < heap[smallest].getWeight())
            smallest = l;
        if(r < length && heap[r].getWeight() < heap[smallest].getWeight())
            smallest = r;
        if(smallest != i){
            Node temp = heap[i];
            heap[i] = heap[smallest];
            heap[smallest] = temp;
            min_heapify(smallest);
        }
    }

    Node extract_min(){//取出最小的元素
        if(length < 1){
            throw new Error("heap underflow");
        }
        Node min = heap[0];
        heap[0] = heap[length - 1];
        heap[length - 1] = null;
        length--;
        if(length != 1)
            min_heapify(0);
        return min;
    }

}
