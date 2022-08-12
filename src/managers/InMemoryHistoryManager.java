package managers;

import dataClasses.TaskData;
import interfaces.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager<TaskData> {
    private static CustomLinkedList<TaskData> history = new CustomLinkedList<>();
    private HashMap<Integer, Node<TaskData>> historyMap = new HashMap<>();

    @Override
    public void add(TaskData data) {
        if(historyMap.containsKey(data.getId())) {
            historyMap.remove(data.getId());
        }
        addToMap(history.linkLast(data));
    }

    private void addToMap(Node<TaskData> data) {
        if (!historyMap.containsKey(data.item.getId())) {
            historyMap.put(data.item.getId(), history.getLast());
        }
    }

    @Override
    public ArrayList<TaskData> getHistory() {
        ArrayList<TaskData> result = new ArrayList<>();

        Node<TaskData> node = history.getFirst();
        for (int i = 0; i < history.size(); i++) {
            if (!result.contains(node.item)) {
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

class CustomLinkedList<T> {

    private int size = 0;
    private Node<T> first;
    private Node<T> last;

    Node<T> linkLast(T element) {
        final Node<T> oldLast = last;
        final Node<T> newNode = new Node<>(null, element, oldLast);
        last = newNode;

        if (oldLast == null) {
            first = newNode;
        } else {
            oldLast.next = newNode;
        }
        size++;
        return newNode;

    }

    int size() {
        return size;
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

