
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MyDoubleLinkedList<E> implements List<E> {

    // Represents one node in the doubly linked list.
    private class Node {
        private E data;
        private Node next;
        private Node prev;

        public Node(E data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }

        @Override
        public String toString() {
            return "Node(" + data + ")";
        }
    }

    // Tracks the number of elements and both ends of the list.
    private int size;
    private Node head;
    private Node tail;

    public MyDoubleLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    // Adds an element to the end of the list.
    @Override
    public boolean add(E element) {
        Node newNode = new Node(element);

        if (head == null) {
            head = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
        }

        tail = newNode;
        size++;
        return true;
    }

    // Inserts an element at the specified index.
    @Override
    public void add(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }

        if (index == size) {
            add(element);
            return;
        }

        Node newNode = new Node(element);

        if (index == 0) {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        } else {
            Node current = getNode(index);

            newNode.prev = current.prev;
            newNode.next = current;

            current.prev.next = newNode;
            current.prev = newNode;
        }

        size++;
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        boolean changed = false;

        for (E element : collection) {
            add(element);
            changed = true;
        }

        return changed;
    }

    @Override
    public boolean addAll(int index,
                          Collection<? extends E> collection) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }

        boolean changed = false;

        // Copy first so adding this list to itself is safe.
        Object[] elements = collection.toArray();

        for (Object element : elements) {
            @SuppressWarnings("unchecked")
            E value = (E) element;

            add(index++, value);
            changed = true;
        }

        return changed;
    }

    // Removes all elements from the list.
    @Override
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public boolean contains(Object obj) {
        return indexOf(obj) != -1;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        for (Object obj : collection) {
            if (!contains(obj)) {
                return false;
            }
        }

        return true;
    }

    // Returns the element at the specified index.
    @Override
    public E get(int index) {
        return getNode(index).data;
    }

    // Finds the node at the specified index.
    private Node getNode(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        Node current;

        // Search from whichever end is closer.
        if (index < size / 2) {
            current = head;

            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            current = tail;

            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }
        }

        return current;
    }

    @Override
    public int indexOf(Object target) {
        Node current = head;
        int index = 0;

        while (current != null) {
            if (elementsEqual(target, current.data)) {
                return index;
            }

            current = current.next;
            index++;
        }

        return -1;
    }

    // Compares elements while safely handling null values.
    private boolean elementsEqual(Object target, Object element) {
        if (target == null) {
            return element == null;
        }

        return target.equals(element);
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // Returns an iterator for traversing the playlist.
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Node current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public E next() {
                if (current == null) {
                    throw new java.util.NoSuchElementException();
                }

                E element = current.data;
                current = current.next;
                return element;
            }
        };
    }

    @Override
    public int lastIndexOf(Object target) {
        Node current = tail;
        int index = size - 1;

        while (current != null) {
            if (elementsEqual(target, current.data)) {
                return index;
            }

            current = current.prev;
            index--;
        }

        return -1;
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }

        return new ListIterator<E>() {
            private int cursor = index;
            private int lastReturned = -1;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }

                lastReturned = cursor;
                return get(cursor++);
            }

            @Override
            public boolean hasPrevious() {
                return cursor > 0;
            }

            @Override
            public E previous() {
                if (!hasPrevious()) {
                    throw new java.util.NoSuchElementException();
                }

                cursor--;
                lastReturned = cursor;
                return get(cursor);
            }

            @Override
            public int nextIndex() {
                return cursor;
            }

            @Override
            public int previousIndex() {
                return cursor - 1;
            }

            @Override
            public void remove() {
                if (lastReturned == -1) {
                    throw new IllegalStateException();
                }

                MyDoubleLinkedList.this.remove(lastReturned);

                if (lastReturned < cursor) {
                    cursor--;
                }

                lastReturned = -1;
            }

            @Override
            public void set(E element) {
                if (lastReturned == -1) {
                    throw new IllegalStateException();
                }

                MyDoubleLinkedList.this.set(lastReturned, element);
            }

            @Override
            public void add(E element) {
                MyDoubleLinkedList.this.add(cursor, element);
                cursor++;
                lastReturned = -1;
            }
        };
    }

    // Removes the first matching element from the list.
    @Override
    public boolean remove(Object obj) {
        Node current = head;

        while (current != null) {
            if (elementsEqual(obj, current.data)) {
                unlink(current);
                return true;
            }

            current = current.next;
        }

        return false;
    }

    // Removes and returns the element at the specified index.
    @Override
    public E remove(int index) {
        Node current = getNode(index);
        E element = current.data;

        unlink(current);

        return element;
    }

    // Disconnects a node and reconnects its neighbors.
    private void unlink(Node node) {
        if (node.prev == null) {
            head = node.next;
        } else {
            node.prev.next = node.next;
        }

        if (node.next == null) {
            tail = node.prev;
        } else {
            node.next.prev = node.prev;
        }

        node.next = null;
        node.prev = null;
        size--;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean changed = false;
        Node current = head;

        while (current != null) {
            Node next = current.next;

            if (collection.contains(current.data)) {
                unlink(current);
                changed = true;
            }

            current = next;
        }

        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean changed = false;
        Node current = head;

        while (current != null) {
            Node next = current.next;

            if (!collection.contains(current.data)) {
                unlink(current);
                changed = true;
            }

            current = next;
        }

        return changed;
    }

    // Replaces the element at an index.
    @Override
    public E set(int index, E element) {
        Node current = getNode(index);
        E oldElement = current.data;

        current.data = element;

        return oldElement;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size
                || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException();
        }

        MyDoubleLinkedList<E> list = new MyDoubleLinkedList<>();

        for (int i = fromIndex; i < toIndex; i++) {
            list.add(get(i));
        }

        return list;
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        Node current = head;
        int index = 0;

        while (current != null) {
            array[index++] = current.data;
            current = current.next;
        }

        return array;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] array) {
        T[] result = array;

        if (array.length < size) {
            result = Arrays.copyOf(array, size);
        }

        Node current = head;
        int index = 0;

        while (current != null) {
            result[index++] = (T) current.data;
            current = current.next;
        }

        if (result.length > size) {
            result[size] = null;
        }

        return result;
    }

    // Counts the nodes by traversing the entire list.
    // Required for the Playlist Manager assignment.
    public int count() {
        int total = 0;
        Node current = head;

        while (current != null) {
            total++;
            current = current.next;
        }

        return total;
    }

    // Physically reverses the order of the linked list.
    // Both next and prev pointers are updated.
    public void reverse() {
        Node current = head;

        while (current != null) {
            Node next = current.next;

            current.next = current.prev;
            current.prev = next;

            current = next;
        }

        // Exchange the first and last nodes.
        Node temp = head;
        head = tail;
        tail = temp;
    }
}