package matrix;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class Matrix {

    protected final ArrayView values;
    private final Shape shape;
    private static final MatrixKernel KERNEL = new MatrixKernel();

    // CONSTRUCTORS
    private Matrix(int rows, int columns) {
        assert rows > 0 && columns > 0;
        values = new ArrayWrapper(rows*columns);
        this.shape = new Shape(rows, columns);
    }

    protected Matrix(ArrayView arrayView, int rows, int columns) {
        assert arrayView != null;
        assert arrayView.size() == rows*columns;
        assert rows > 0 && columns > 0;

        values = arrayView;
        shape = new Shape(rows, columns);
    }

    protected Matrix(ArrayView arrayView, Shape shape) {
        assert arrayView != null;
        assert arrayView.size() == shape.length();

        values = arrayView;
        this.shape = shape;
    }

    private Matrix(int rows, int columns, float min, float max) {
        this(rows, columns);
        assert min <= max;
        Random random = new Random();
        for (int i = 0; i < values.size(); i++) {
            values.set(i, random.nextFloat(min, max));
        }
    }

    private Matrix(int rows, int columns, int min, int max) {
        this(rows, columns);
        assert min <= max;
        Random random = new Random();
        for (int i = 0; i < values.size(); i++) {
            values.set(i, (float) random.nextInt(min, max));
        }
    }

    public Matrix(float[] mat, int rows, int columns) {
        assert mat != null;
        assert rows*columns == mat.length;

        shape = new Shape(rows, columns);
        values = new ArrayWrapper(Arrays.copyOf(mat, mat.length));
    }

    public Matrix(float[][] mat) {
        assert mat != null;
        for (float[] f: mat) {
            assert f != null;
            assert f.length == mat[0].length;
        }

        shape = new Shape(mat.length, mat[0].length);
        values = new ArrayWrapper(shape.length());
        for (int i = 0; i < values.size(); i++) {
            values.set(i, mat[i/shape.columns()][i%shape.columns()]);
        }
    }


    @Contract("_, _ -> new")
    public static @NotNull Matrix zeros(int rows, int columns) {
        assert rows > 0 && columns > 0;
        return new Matrix(new ArrayWrapper(rows*columns), rows, columns);
    }

    @Contract("_, _ -> new")
    public static @NotNull Matrix ones(int rows, int columns) {
        assert rows > 0 && columns > 0;
        return new Matrix(new ArrayWrapper(rows*columns, 1), rows, columns);
    }

    @Contract("_, _, _, _ -> new")
    public static @NotNull Matrix random(int rows, int columns, float min, float max) {
        assert rows > 0 && columns > 0;
        assert min <= max;
        return new Matrix(rows, columns, min, max);
    }

    @Contract("_, _, _, _ -> new")
    public static @NotNull Matrix random(int rows, int columns, int min, int max) {
        assert rows > 0 && columns > 0;
        assert min <= max;
        return new Matrix(rows, columns, min, max);
    }


    // UTIL
    public Matrix copy() {
        return new Matrix(values.copy(), shape.rows(), shape.columns());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;

        final Matrix other = (Matrix) obj;
        if (getRowCount() != other.getRowCount() || getColCount() != other.getColCount()) return false;
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) != other.values.get(i)) return false;
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("matrix " + shape.rows() + "x" + shape.columns() + ":\n");
        int i = 0;
        while (i < values.size()) {
            ret.append(values.get(i)).append(" ");
            i++;
            if (i % shape.columns() == 0 && i != values.size()) ret.append("\n");
        }
        return ret.toString();
    }

    public float[] toArray() {
        return values.toArray();
    }

    public Matrix reshape(int rowCount, int colCount) {
        assert rowCount * colCount == values.size();
        return new Matrix(values, rowCount, colCount);
    }

    public void map(Function<Float, Float> func) {
        for (int i = 0; i < values.size(); i++) {
            values.set(i, func.apply(values.get(i)));
        }
    }

    public void colMap(Consumer<Vector> func) {
        for (int i = 0; i < getColCount(); i++) {
            func.accept(getColumn(i));
        }
    }

    public Vector rowFoldd(BiConsumer<float[], Vector> func) {
        float[] result = new float[getColCount()];
        for (int i = 0; i < result.length; i++) {
            result[i] = getRow(0).get(i);
        }
        for (int i = 1; i < getRowCount(); i++) {
            func.accept(result, getRow(i));
        }
        return Vector.rowVector(new ArrayWrapper(result));
    }

    public Vector colFoldl(BiConsumer<float[], Vector> func) {
        float[] result = new float[getRowCount()];
        for (int i = 0; i < result.length; i++) {
            result[i] = getColumn(0).get(i);
        }
        for (int i = 1; i < getColCount(); i++) {
            func.accept(result, getColumn(i));
        }

        return Vector.columnVector(new ArrayWrapper(result));
    }


    // ACCESS
    public Vector getRow(int i) {
        assert i < getRowCount();
        return Vector.rowVector(new ArrayView() {
            @NotNull
            @Override
            public Iterator<Float> iterator() {
                return new Iterator<>() {
                    int i =0;
                    @Override
                    public boolean hasNext() {
                        return i < size()-1;
                    }

                    @Override
                    public Float next() {
                        return get(i++);
                    }
                };
            }

            @Override
            public int size() {
                return shape.columns();
            }

            @Override
            public float get(int j) {
                return values.get(i*shape.columns() + j);
            }

            @Override
            public void set(int j, float value) {
                values.set(i*shape.columns() + j, value);
            }

        });
    }

    public Vector getColumn(int j) {
        assert j < getColCount();
        return Vector.columnVector(new ArrayView() {
            @NotNull
            @Override
            public Iterator<Float> iterator() {
                return new Iterator<>() {
                    int i = 0;
                    @Override
                    public boolean hasNext() {
                        return i < size()-1;
                    }

                    @Override
                    public Float next() {
                        return get(i++);
                    }
                };
            }

            @Override
            public int size() {
                return shape.rows();
            }

            @Override
            public float get(int i) {
                return values.get(i*shape.columns() + j);
            }

            @Override
            public void set(int i, float value) {
                values.set(i*shape.columns() + j, value);
            }
        });
    }

    public Matrix slice(int minRow, int maxRow, int minCol, int maxCol) {
        int n = maxRow-minRow;
        int m = maxCol-minCol;
        ArrayView sliceView = new SliceView(values, shape.columns(), minRow, maxRow, minCol, maxCol);
        return new Matrix(sliceView,n,m);
    }

    public int getRowCount() {
        return shape.rows();
    }

    public int getColCount() {
        return shape.columns();
    }


    // MATH
    public Matrix multiply(Matrix M) {
        return multiplyCPU(M);
    }

    private Matrix multiplyCPU(Matrix M) {
        if (this.getColCount() != M.getRowCount()) throw new IllegalArgumentException("Matricies are incompatibles sizes");

        float[] out = new float[this.getRowCount() * M.getColCount()];
        for (int y = 0; y < this.getRowCount(); y++) {
            for (int x = 0; x < this.getColCount(); x++) {
                for (int z = 0; z < M.getColCount(); z++) {
                    out[z + y * M.getColCount()] += values.get(x + y * getColCount()) * M.values.get(z + x * M.getColCount());
                }
            }
        }
        return new Matrix(out, this.getRowCount(), M.getColCount());
    }

    private Matrix multiplyGPU(Matrix M) {
        if (this.getColCount() != M.getRowCount()) throw new IllegalArgumentException("Matricies are incompatibles sizes");
        float[] inA;
        if (values instanceof ArrayWrapper) {
            inA = ((ArrayWrapper) values).getArray();
        }
        else {
            inA = values.toArray();
        }

        float[] inB;
        if (values instanceof ArrayWrapper) {
            inB = ((ArrayWrapper) M.values).getArray();
        }
        else {
            inB = M.values.toArray();
        }

        return new Matrix(KERNEL.multiply(inA, inB, this.getRowCount(), this.getColCount(), M.getColCount()), this.getRowCount(), M.getColCount());
    }

    public void add(Matrix M) {
        addCPU(M);
    }

    private Matrix addGPU(Matrix M) {
        float[] inA;
        if (values instanceof ArrayWrapper) {
            inA = ((ArrayWrapper) values).getArray();
        }
        else {
            inA = values.toArray();
        }

        float[] inB;
        if (values instanceof ArrayWrapper) {
            inB = ((ArrayWrapper) M.values).getArray();
        }
        else {
            inB = M.values.toArray();
        }
        float[] out = KERNEL.add(inA, inB);
        return new Matrix(out, M.getRowCount(), M.getColCount());
    }

    private void addCPU(Matrix M) {
        float[] out = new float[getRowCount()*getColCount()];
        for (int i = 0; i < values.size(); i++) {
            values.set(i, values.get(i) + M.values.get(i*M.getColCount()/getColCount()));
        }
    }

    public float sum() {
        float total = 0;
        for (int i = 0; i < values.size(); i++) {
            total += values.get(i);
        }
        return total;
    }

    public void subtract(Matrix M) {
        if (M.getColCount() != getColCount() || M.getRowCount() != getRowCount()) throw new IllegalArgumentException();
        for (int i = 0; i < values.size(); i++) {
            values.set(i, values.get(i) - M.values.get(i));
        }
    }

    public void scale(float s) {
        for (int i = 0; i < values.size(); i++) {
            values.set(i, values.get(i) * s);
        }
    }

    public void entrywiseProduct(Matrix M) {
        // Matricies must be the same size
        assert getRowCount() == M.getRowCount();
        assert getColCount() == M.getColCount();
        for (int i = 0; i < values.size(); i++) {
            values.set(i, values.get(i) * M.values.get(i));
        }
    }

    public Matrix transpose() {
        Shape shape = new Shape(getColCount(), getRowCount());
        ArrayView arrayView;

        if (values instanceof TranposeView) arrayView = ((TranposeView) values).getArrayView();
        else arrayView = new TranposeView(values, shape);

        return new Matrix(arrayView, shape);
    }

    public Matrix transpose2() {
        Matrix transpose = Matrix.zeros(getColCount(), getRowCount());
        for (int i = 0; i < getRowCount(); i++) {
            transpose.getColumn(i).setAll(getRow(i).toArray());
        }
        return transpose;
    }

    public Iterator<Float> getIterator() {
        return values.iterator();
    }
}
