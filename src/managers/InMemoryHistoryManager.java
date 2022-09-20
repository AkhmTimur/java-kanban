package managers;

import dataClasses.TaskData;
import interfaces.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class InMemoryHistoryManager implements HistoryManager<TaskData> {
    private final CustomLinkedList<TaskData> history = new CustomLinkedList<>();
    private final HashMap<Integer, Node<TaskData>> historyMap = new HashMap<>();

    @Override
    public void add(TaskData data) {
        if (data != null) {
            int id = data.getId();
            removeNode(historyMap.get(id));
            historyMap.put(id, history.linkLast(data));
        }
    }

    public void addAll(TaskData data) {
        if (data != null) {
            int id = data.getId();
            removeNode(historyMap.get(id));
            historyMap.put(id, history.linkLast(data));
        }
    }

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

    private void removeNode(Node<TaskData> node) {
        history.removeNode(node);
    }

    @Override
    public void remove(int id) {
        history.removeNode(historyMap.get(id));
        historyMap.remove(id);
    }

    @Override
    public void removeDataType(Set<Integer> dataId) {
        if (dataId.size() > 0) {
            for (Integer id : dataId) {
                remove(id);
            }
        }
    }
}

class CustomLinkedList<T> {

    private int size = 0;
    private Node<T> first;
    private Node<T> last;

    Node<T> linkLast(T element) {
        final Node<T> oldLast = last;
        final Node<T> newNode = new Node<>(oldLast, element, null);
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
        if (node != null) {
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

}

