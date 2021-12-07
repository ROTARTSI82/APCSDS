import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A doubly-linked list container
 * @author Grant Yang
 * @version 2021.10.15
 * @param <E> The type of the element to be stored in the container
 */
public class MyLinkedList<E>
{
    private DoubleNode first;
    private DoubleNode last;
    private int size;
    private byte modId = 0;

    /**
     * Constructs a new empty linked list
     */
    public MyLinkedList()
    {
        first = null;
        last = null;
        size = 0;
    }

    /**
     * Constructs a human-readable string
     * @return A human-readable representation of this list.
     */
    @Override
    public String toString()
    {
        DoubleNode node = first;
        if (node == null)
            return "[]";
        String s = "[";
        while (node.getNext() != null)
        {
            s += node.getValue() + ", ";
            node = node.getNext();
        }
        return s + node.getValue() + "]";
    }

    /**
     * @precondition  0 <= index <= size / 2
     * @param index The index of the element to get (0-indexed)
     * @return Starting from first, returns the node
     *         with given index (where index 0
     *         returns first)
     */
    private DoubleNode getNodeFromFirst(int index)
    {
        DoubleNode ret = first;
        for (int i = 0; i < index; i++)
            ret = ret.getNext();
        return ret;
    }

    /**
     * @precondition  size / 2 <= index < size
     * @param index The index of the element to get (0-indexed)
     * @return Starting from last, returns the node
     *         with given index (where index size-1
     *         returns last)
     */
    private DoubleNode getNodeFromLast(int index)
    {
        DoubleNode ret = last;
        for (int i = size() - 1; i > index; i--)
            ret = ret.getPrevious();
        return ret;
    }

    /**
     * @precondition  0 <= index < size
     * @param index The index of the element to get (0-indexed)
     * @throws IndexOutOfBoundsException If the precondition is not met
     * @return starting from first or last (whichever
     *         is closer), returns the node with given index
     */
    private DoubleNode getNode(int index)
    {
        if (index < 0 || index >= size())
            throw new IndexOutOfBoundsException("Cannot get node " + index + " of " + size());
        return (index > (size() / 2)) ? getNodeFromLast(index) : getNodeFromFirst(index);
    }

    /**
     * Queries the size of the list
     * @return The number of elements in this list
     */
    public int size()
    {
        return size;
    }

    /**
     * Gets the value of the element at an index
     * @precondition 0 <= index < size()
     * @throws IndexOutOfBoundsException If the precondition is not met
     * @param index 0-indexed index of the element to get
     * @return The value of the element
     */
    public E get(int index)
    {
        return (E) getNode(index).getValue();
    }

    /**
     * Sets the value of a specific element
     * @precondition 0 <= index < size()
     * @throws IndexOutOfBoundsException If the precondition is not met
     * @postcondition replaces the element at position index with obj
     * @param index The index of the element to modify (0-indexed)
     * @param obj The new value for the element at index
     * @return The element formerly at the specified position
     */
    public E set(int index, E obj)
    {
        E ret = get(index);
        getNode(index).setValue(obj);
        modId++;
        return ret;
    }

    /**
     * Adds an element to the end of the list
     * @postcondition appends obj to end of list;
     * @param obj The value to be added at the end of the list
     * @return Always true
     */
    public boolean add(E obj)
    {
        addLast(obj);
        modId++;
        return true;
    }

    private E removeNode(DoubleNode node)
    {
        if (node.getPrevious() != null)
            node.getPrevious().setNext(node.getNext());
        if (node.getNext() != null)
            node.getNext().setPrevious(node.getPrevious());

        size--;
        modId++;
        return (E) node.getValue();
    }

    private void addNode(DoubleNode addBefore, DoubleNode toAdd)
    {
        size++;
        toAdd.setPrevious(addBefore.getPrevious());
        toAdd.setNext(addBefore);

        if (addBefore.getPrevious() != null)
            addBefore.getPrevious().setNext(toAdd);
        addBefore.setPrevious(toAdd);
        modId++;
    }

    /**
     * Removes a specfic element from the list
     * @precondition 0 <= index < size()
     * @throws IndexOutOfBoundsException If the precondition is not met
     * @postcondition removes element from position index, moving elements
     *               at position index + 1 and higher to the left
     *               (subtracts 1 from their indices) and adjusts size
     * @param index The index of the element to remove (0-indexed)
     * @return The element formerly at the specified position
     */
    public E remove(int index)
    {
        return index == 0 ? removeFirst() : index == size() - 1 ? removeLast() :
                removeNode(getNode(index));
    }

    /**
     * Adds an element at an index
     * @precondition  0 <= index <= size
     * @throws IndexOutOfBoundsException If the precondition is not met
     * @postcondition inserts obj at position index,
     *                moving elements at position index and higher
     *                to the right (adds 1 to their indices) and adjusts size
     * @param index The index to add the element at (0-indexed)
     * @param obj The value to add
     */
    public void add(int index, E obj)
    {
        if (index == 0) addFirst(obj);
        else if (index == size()) addLast(obj);
        else addNode(getNode(index), new DoubleNode(obj));
    }

    /**
     * Adds an element to the beginning of the list
     * @param obj The value to add
     */
    public void addFirst(E obj)
    {
        size++;
        modId++;
        DoubleNode toAdd = new DoubleNode(obj);
        if (first == null)
        {
            first = toAdd;
            last = toAdd;
            return;
        }

        // cannot rely on first.getNext() existing here
        first.setPrevious(toAdd);
        toAdd.setNext(first);
        first = toAdd;
    }

    /**
     * Adds an element to the end of the list
     * @param obj The value to add
     */
    public void addLast(E obj)
    {
        size++;
        modId++;
        DoubleNode toAdd = new DoubleNode(obj);
        if (last == null)
        {
            first = toAdd;
            last = toAdd;
            return;
        }

        last.setNext(toAdd);
        toAdd.setPrevious(last);
        last = toAdd;
    }

    /**
     * Gets the first element
     * @precondition The list is not empty
     * @throws NoSuchElementException if the precondition is not met
     * @return The value of the head
     */
    public E getFirst()
    {
        if (first == null) throw new NoSuchElementException("List empty");
        return (E) first.getValue();
    }

    /**
     * Gets the last element
     * @precondition The list is not empty
     * @throws NoSuchElementException if the precondition is not met
     * @return The value of the tail
     */
    public E getLast()
    {
        if (last == null) throw new NoSuchElementException("List empty");
        return (E) last.getValue();
    }

    /**
     * Removes the first element from the list
     * @precondition The list is not empty
     * @throws NoSuchElementException if the precondition is not met
     * @return The value of the element removed
     */
    public E removeFirst()
    {
        if (first == null) throw new NoSuchElementException("List is empty");

        size--;
        modId++;
        E ret = getFirst();
        if (first == last)
        {
            first = null;
            last = null;
        }
        else
        {
            first = first.getNext();
            first.setPrevious(null);
        }
        return ret;
    }

    /**
     * Removes the last element from the list
     * @precondition The list is not empty
     * @throws NoSuchElementException if the precondition is not met
     * @return The value of the element removed
     */
    public E removeLast()
    {
        if (last == null) throw new NoSuchElementException("List is empty");

        size--;
        modId++;
        E ret = getLast();
        if (first == last)
        {
            first = null;
            last = null;
        }
        else
        {
            last = last.getPrevious();
            last.setNext(null);
        }
        return ret;
    }

    /**
     * Constructs a new iterator over this list
     * @return An iterator implementing java.util.Iterator.
     */
    public Iterator<E> iterator()
    {
        return new MyLinkedListIterator(modId);
    }

    private class MyLinkedListIterator implements Iterator<E>
    {
        private DoubleNode cursorNode = new DoubleNode(null);
        private boolean isReady = true;
        private byte expectedId;

        private MyLinkedListIterator(byte expectedId)
        {
            cursorNode.setNext(first);
            this.expectedId = expectedId;
        }

        private void checkConcurrentMod()
        {
            if (expectedId != modId)
                throw new ConcurrentModificationException("List modified through " + 
                                    " non-iterator method");
        }

        /**
         * Queries if more values may be retrieved with {@code next()}
         * @throws ConcurrentModificationException If the list was modified through
         *                                         a method that the iterator is not aware of.
         * @return True if next() may be called
         */
        public boolean hasNext()
        {
            checkConcurrentMod();
            return cursorNode != null && cursorNode.getNext() != null;
        }

        /**
         * Gets the next value and increments the iterator
         * @throws NoSuchElementException if the precondition was not met
         * @throws ConcurrentModificationException If the list was modified through
         *                                         a method that the iterator is not aware of.
         * @precondition hasNext()
         * @postcondition The iterator has been incremented and now
         *                points to the element that comes after.
         * @return The value of the next element
         */
        public E next()
        {
            checkConcurrentMod();
            if (cursorNode == null || cursorNode.getNext() == null)
                throw new NoSuchElementException("No next element");
            cursorNode = cursorNode.getNext();
            isReady = true;
            return (E) cursorNode.getValue();
        }

        /**
         * Removes the element returned by next()
         * @throws IllegalStateException If next() was not called immediately before
         * @throws ConcurrentModificationException If the list was modified through
         *                                         a method that the iterator is not aware of.
         * @precondition next() was just called successfully
         *               and remove() had not been called after
         *               next() yet.
         * @postcondition The element just returned by next() is removed
         *                from the list
         */
        public void remove()
        {
            checkConcurrentMod();
            if (cursorNode == null || !isReady)
                throw new IllegalStateException("remove() does not immediately follow next()");
            DoubleNode newValue = cursorNode.getNext();
            if (cursorNode == last)
            {
                removeLast();
                cursorNode = newValue;
            }
            else if (cursorNode == first)
            {
                assert newValue == null;
                removeFirst();
                cursorNode = new DoubleNode(null);
                cursorNode.setNext(first);
            }
            else
            {
                removeNode(cursorNode);
                cursorNode = newValue;
            }
            expectedId = modId;
            isReady = false;
        }
    }
}
