import java.util.Arrays;

public class Heap<T> {
    public static final int DEFAULT_SIZE = 32;
    HeapConstraint cons;
    T[] data;
    int hs;

    public Heap(HeapConstraint<T> constraint) {
        this(constraint, DEFAULT_SIZE);
    }

    public Heap(HeapConstraint<T> constraint, int size) {
        this(constraint, (T[])new Object[size], 0);
    }

    private Heap(HeapConstraint<T> constraint, T[] heapData, int size) {
        if (constraint == null) {
            throw new IllegalArgumentException();
        }
        
        cons = constraint;
        data = heapData;
        hs = size;
    }

    public void insert(T item) {
        if (item == null) return;

        // increase size if necessary
        if (hs >= data.length) {
            data = Arrays.copyOf(data, data.length * 2);
        }

        int itemIndex = hs++; // increment heap size
        data[itemIndex] = item;
        
        shiftUp(itemIndex);
    }

    public T extract() {
        if (hs <= 0) return null;

        T item = data[0];
        data[0] = data[--hs]; // decrease heap size

        shiftDown(0);

        return item;
    }

    public int size() {
        return hs;
    }

    private void shiftUp(int i) {
        if (!isPosValid(i) || i == 0) return;

        int parentIndex = (i - 1) / 2;

        boolean parentPosValid = isPosValid(parentIndex);
        boolean parentConsValid = !parentPosValid || cons.isConstraintValid(data[parentIndex], data[i]);

        if (!parentConsValid) {
            swap(parentIndex, i);
            shiftUp(parentIndex);
        }
    }

    private void shiftDown(int i) {
        if (!isPosValid(i)) return;

        int leftIndex = i * 2 + 1;
        int rightIndex = i * 2 + 2;
        
        boolean leftPosValid = isPosValid(leftIndex);
        boolean leftConsValid = !leftPosValid || cons.isConstraintValid(data[i], data[leftIndex]);

        boolean rightPosValid = isPosValid(rightIndex);        
        boolean rightConsValid = !rightPosValid || cons.isConstraintValid(data[i], data[rightIndex]);

        if (leftConsValid && rightConsValid) {
            // both constraints are valid, don't do anything
            return;
        } else if (!leftConsValid && !rightConsValid) {
            // both constraints are invalid, pick one of the two children

            boolean leftAsParentConsValid = cons.isConstraintValid(data[leftIndex], data[rightIndex]);
            if (leftAsParentConsValid) {
                // the left child is a valid parent for the right child
                // therefore, "validate" the right child, so it doesn't get swapped
                rightConsValid = true;
            } else {
                leftConsValid = true;
            }
        }

        if (!leftConsValid) {
            swap(i, leftIndex);
            shiftDown(leftIndex);
        } else if (!rightConsValid) {
            swap(i, rightIndex);
            shiftDown(rightIndex);
        }
    }

    private boolean isPosValid(int i) {
        return i >= 0 && i < hs;
    }

    private void swap(int i, int j) {
        T temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    public static <T> Heap<T> fromArray(HeapConstraint<T> constraint, T[] data) {
        if (constraint == null || data == null) {
            throw new IllegalArgumentException();
        }
        
        return new Heap(constraint, data, data.length);
    }
}