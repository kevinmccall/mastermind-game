package datastructures;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class KevLinkedDeque<E> implements Deque<E> {
    private class Node {
        private E data;
        private Node next;
        private Node previous;

        private Node(E data) {
            this.data = data;
            next = null;
            previous = null;
        }
    }

    private class KevLinkedDequeForwardsIterator implements Iterator<E> {

        Node current = head;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public E next() {
            Node temp = current;
            if (current == null) {
                throw new NoSuchElementException("No more elements");
            }
            current = current.next;
            return temp.data;
        }

    }

    private class KevLinkedDequeBackwardsIterator implements Iterator<E> {

        Node current = tail;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public E next() {
            Node temp = current;
            if (current == null) {
                throw new NoSuchElementException("No more elements");
            }
            current = current.previous;
            return temp.data;
        }

    }

    private Node head;
    private Node tail;
    private int numElements;

    @Override
    public Iterator<E> iterator() {
        return new KevLinkedDequeForwardsIterator();
    }

    @Override
    public int size() {
        return numElements;
    }

    @Override
    public boolean add(E e) {
        if (tail == null) {
            head = new Node(e);
            tail = head;
        } else {
            Node node = new Node(e);
            tail.next = node;
            node.previous = tail;
            tail = node;
        }
        numElements++;
        return true;
    }

    @Override
    public boolean isEmpty() {
        return numElements == 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E[] toArray() {
        E[] arr = (E[]) new Object[numElements];
        int index = 0;
        for (E e : this) {
            arr[index] = e;
            index++;
        }
        return arr;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        boolean result = true;
        for (E data : this) {
            if (!c.contains(data)) {
                result = false;
                break;
            }
        }
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for (Object object : c) {
            if (contains(object)) {
                remove(object);
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = false;
        for (Object object : c) {
            if (!contains(object)) {
                remove(object);
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public void clear() {
        numElements = 0;
        head = null;
        tail = null;
    }

    @Override
    public void addFirst(E e) {
        Node node = new Node(e);
        node.next = head;
        head.previous = node;
        head = node;
        numElements++;
    }

    @Override
    public void addLast(E e) {
        Node node = new Node(e);
        tail.next = node;
        node.previous = tail;
        tail = node;
        numElements++;
    }

    @Override
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    @Override
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    @Override
    public E removeFirst() {
        if (head == null) {
            throw new NoSuchElementException("Cannot remove -- Deque is empty.");
        }
        E data = head.data;
        head = head.next;
        head.previous = null;
        numElements--;
        return data;
    }

    @Override
    public E removeLast() {
        E data = tail.data;
        tail = tail.previous;
        tail.next = null;
        numElements--;
        return data;
    }

    @Override
    public E pollFirst() {
        if (isEmpty()) {
            return null;
        } else {
            return removeFirst();
        }
    }

    @Override
    public E pollLast() {
        if (isEmpty()) {
            return null;
        } else {
            return removeLast();
        }
    }

    @Override
    public E getFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("No first element -- Deque is empty.");
        } else {
            return head.data;
        }
    }

    @Override
    public E getLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("No last element -- Deque is empty.");
        } else {
            return tail.data;
        }
    }

    @Override
    public E peekFirst() {
        if (isEmpty()) {
            return null;
        } else {
            return head.data;
        }
    }

    @Override
    public E peekLast() {
        if (isEmpty()) {
            return null;
        } else {
            return tail.data;
        }
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        boolean removed = false;
        for (Node current = head; current != null && !removed; current = current.next) {
            if (current.data.equals(o)) {
                if (current == head) {
                    removeFirst();
                } else {
                    current.previous.next = current.next;
                    current.next.previous = null;
                    numElements--;
                }
                removed = true;
            }
        }
        return removed;
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        boolean removed = false;
        for (Node current = tail; current != null && !removed; current = current.previous) {
            if (current.data.equals(o)) {
                if (current == tail) {
                    removeLast();
                } else {
                    current.previous.next = current.next;
                    current.next.previous = null;
                    numElements--;
                }
                removed = true;
            }
        }
        return removed;
    }

    @Override
    public boolean offer(E e) {
        add(e);
        return true;
    }

    @Override
    public E remove() {
        return removeFirst();
    }

    @Override
    public E poll() {
        return pollFirst();
    }

    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E peek() {
        return peekFirst();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for (E element : c) {
            offer(element);
        }
        return true;
    }

    @Override
    public void push(E e) {
        addFirst(e);

    }

    @Override
    public E pop() {
        return removeFirst();
    }

    @Override
    public boolean remove(Object o) {
        return removeFirstOccurrence(o);
    }

    @Override
    public boolean contains(Object o) {
        boolean result = false;
        for (E data : this) {
            if (data.equals(o)) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new KevLinkedDequeBackwardsIterator();
    }
}
