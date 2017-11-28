
public interface HeapConstraint<T> {
    public static HeapConstraint<Integer> INT_MIN = new IntMinConstraint();
    public static HeapConstraint<Integer> INT_MAX = new IntMaxConstraint();

    boolean isConstraintValid(T parent, T child);

    static class IntMinConstraint implements HeapConstraint<Integer> {
        @Override
        public boolean isConstraintValid(Integer parent, Integer child) {
            return parent <= child;
        }
    }
    static class IntMaxConstraint implements HeapConstraint<Integer> {
        @Override
        public boolean isConstraintValid(Integer parent, Integer child) {
            return parent >= child;
        }
    }
}