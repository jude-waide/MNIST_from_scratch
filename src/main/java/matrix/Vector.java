package matrix;

public class Vector extends Matrix {
    private Vector(int rows, int columns) {
        super(new ArrayWrapper(new float[rows*columns]), rows, columns);
    }

    private Vector(float[] mat, int rows, int columns) {
        super(mat, rows, columns);
    }

    private Vector(ArrayView values, int rows, int columns) {
        super(values, rows, columns);
    }

    public float get(int i) {
        return values.get(i);
    }

    public void set(int i, float value) {
        values.set(i, value);
    }

    public int length() {
        return values.size();
    }

    public static Vector columnVector(int length) {
        return new Vector(length, 1);
    }

    public static Vector rowVector(int length) {
        return new Vector(1, length);
    }

    public static Vector columnVector(float[] values) {
        return new Vector(values, values.length, 1);
    }

    public static Vector rowVector(float[] values) {
        return new Vector(values, 1, values.length);
    }

    public static Vector columnVector(ArrayView values) {
        return new Vector(values, values.size(), 1);
    }

    public static Vector rowVector(ArrayView values) {
        return new Vector(values, 1, values.size());
    }

    public static Vector cast(Matrix matrix) {
        if (matrix.getRowCount() != 1 && matrix.getColCount() != 1) throw new IllegalArgumentException();
        return new Vector(matrix.values, matrix.getRowCount(), matrix.getColCount());
    }

    public void setAll(float[] values) {
        this.values.setAll(values);
    }

    @Override
    public Vector transpose() {
        Matrix transpose = super.transpose();
        return cast(transpose);
    }
}
