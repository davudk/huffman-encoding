
public class BTree<T> {
    BTree<T> left, right;
    T value;

    public BTree() { }
    public BTree(T value) {
        setValue(value);
    }
    public BTree(T value, BTree<T> left, BTree<T> right) {
        setValue(value);
        setLeft(left);
        setRight(right);
    }
    
    public boolean isLeaf() { return left == null && right == null; }
    
    public T getValue() { return value; }
    public void setValue(T value) { this.value = value; }
    
    public BTree<T> getLeft() { return left; }
    public void setLeft(BTree<T> left) { this.left = left; }
    
    public BTree<T> getRight() { return right; }
    public void setRight(BTree<T> right) { this.right = right; }
}