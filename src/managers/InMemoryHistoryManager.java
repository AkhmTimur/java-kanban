package managers;

import dataClasses.TaskData;
import interfaces.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager<TaskData> {
    private static CustomLinkedList<TaskData> history = new CustomLinkedList<>();
    private HashMap<Integer, Node<TaskData>> historyMap = new HashMap<>();

    public void add(TaskData data) {
        if (history.size() >= 10) {
            removeNode(history.getLast());
        }
        addToMap(history.linkLast(data));
    }

    void addToMap(Node<TaskData> data) {
        ArrayList<TaskData> a = new ArrayList<>();
        a.add(data.item);
        System.out.println(a);
        if(!historyMap.containsKey(data.item.getId())) {
            historyMap.put(data.item.getId(), history.getLast());
        }
    }

    public ArrayList<TaskData> getHistory() {
        ArrayList<TaskData> result = new ArrayList<>();

        Node<TaskData> node = history.getFirst();
        for(int i = 0; i < history.size(); i++) {
            if(!result.contains(node.item)) {
                result.add(node.item);
                node = node.next;
            }
        }

        return result;
    }

    @Override
    public void removeNode(Node<TaskData> node) {
        history.removeNode(node);
    }
}

class CustomLinkedList<T>{

    private int size = 0;
    private Node<T> first;
    private Node<T> last;

    Node<T> linkLast(T element) {
        final Node<T> oldlast = last;
        final Node<T> newNode = new Node<>(null, element, oldlast);
        last = newNode;

        if(oldlast == null) {
            first = newNode;
            size++;
            return first;
        }
        else {
            oldlast.next = newNode;
            size++;
            return newNode;
        }


    }

     int size() {
        return size;
    }

    public ArrayList<T> getTasks() {
        ArrayList<T> tasks = new ArrayList<>();

        T curElement = first.item;
        for(int i = 0; i < this.size(); i++) {
            tasks.add(curElement);
            Node<T> nextElement = first.next;
            curElement = nextElement.item;
        }

        return tasks;
    }

    Node<T> getFirst() {
        return first;
    }
    Node<T> getLast() {
        return last;
    }

    void removeNode(Node<T> node) {
        final Node<T> next = node.next;
        final Node<T> prev = node.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }

        node.item = null;
        size--;
    }

}

