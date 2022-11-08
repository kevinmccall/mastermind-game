package datastructures;

import java.util.NoSuchElementException;

public class KevLinkedList<Q> {
    private Node<Q> head;
    private int length;

    public static void main(String[] args) {
        KevLinkedList<String> list = new KevLinkedList<>();
        list.append("one fish");
        list.append("two fish");
        list.append("orange fish");
        list.append("blue fish");
        System.out.println(list);
        list.remove(2);
        list.insert("red fish", 2);
        System.out.println(list);
        list.reverse();
        System.out.println(list);

    }

    public KevLinkedList() {
    }

    private class Node<T> {
        T data;
        Node<T> next;

        private Node(T data, Node<T> next) {
            this.data = data;
            this.next = next;
        }
    }

    private Node<Q> getNodeAt(int pos) {
        if (pos < 0 || pos >= length) {
            throw new IndexOutOfBoundsException();
        }

        Node<Q> current = head;
        for (int i = 0; i < pos; i++) {
            current = current.next;
        }

        return current;
    }

    public void insert(Q data, int index) {
        if (index < 0 || index > length) {
            throw new IndexOutOfBoundsException();
        }

        Node<Q> newNode = new Node<Q>(data, null);
        if (index == 0) {
            head = newNode;
        } else {
            Node<Q> before = getNodeAt(index - 1);
            newNode.next = before.next;
            before.next = newNode;
        }
        length++;
    }

    public void append(Q data) {
        insert(data, length);
    }

    public Q remove(int index) {
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException();
        }
        if (length == 0) {
            throw new NoSuchElementException();
        }
        Q data = null;
        if (index == 0) {
            data = head.data;
            head = head.next;
        } else {
            Node<Q> before = getNodeAt(index - 1);
            data = before.next.data;
            before.next = before.next.next;
        }
        length--;
        return data;
    }

    public Q getData(int index) {
        return getNodeAt(index).data;
    }

    public void reverse() {
        Node<Q> cur = head;
        Node<Q> end = null;
        Node<Q> temp = null;

        while (cur != null) {
            temp = cur.next;
            cur.next = end;
            end = cur;
            cur = temp;
        }
        head = end;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Head: ");
        Node<Q> current = head;
        while (current != null) {
            sb.append(current.data.toString());
            sb.append(" --> ");
            current = current.next;
        }
        sb.append("null");

        return sb.toString();
    }
}
