import matrix.FoldFunction;
import matrix.Matrix;
import matrix.Vector;

import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
        testMatrix();
    }


    public static void testMatrix() {
        Matrix M = new Matrix(new float[][]{
                {1,2,3},
                {4,5,6}
        });

        // Access
        assert M.getRow(0).equals(Vector.rowVector(new float[]{1,2,3}));
        assert M.getColumn(1).equals(Vector.columnVector(new float[]{2,5}));

        // === UTIL ===
        // Matrix.copy()
        Matrix N = M.copy();
        assert N.equals(M);
        N.getColumn(0).set(0,7);
        assert !N.equals(M);

        // Matrix.toArray
        assert Arrays.equals(M.toArray(), new float[]{1, 2, 3, 4, 5, 6});

        // Matrix.reshape()
        N = M.reshape(3,2);
        assert N.getRow(0).equals(Vector.rowVector(new float[]{1,2}));
        assert N.getRow(1).equals(Vector.rowVector(new float[]{3,4}));
        assert N.getColumn(0).equals(Vector.columnVector(new float[]{1,3,5}));


        // Matrix.map()
        N = M.copy();
        N.map(aFloat -> aFloat + 1);
        assert N.equals(new Matrix(new float[][]{
                {2,3,4},
                {5,6,7}
        }));

        // Matrix.colMap()
        N = M.copy();
        N.colMap(vector -> {
            float sum = vector.get(0) + vector.get(1);
            vector.set(0, vector.get(0)/sum);
            vector.set(1, vector.get(1)/sum);
        });
        assert N.equals(new Matrix(new float[][]{
                {1f/5,2f/7,3f/9},
                {4f/5,5f/7,6f/9}
        }));

        // Matrix.rowFold()
        N = M.rowFoldd(FoldFunction.SUM::fold);
        assert N.equals(Vector.rowVector(new float[]{5,7,9}));

        // Matrix.colFold()
        N = M.colFoldl(FoldFunction.SUM::fold);
        assert N.equals(Vector.columnVector(new float[]{6,15}));

        // Matrix.slice
        N = M.slice(0, M.getRowCount(), 0, M.getColCount());
        assert N.equals(M);

        N = M.slice(1, 2, 1, 3);
        assert N.equals(new Matrix(new float[][]{{5,6}}));

        N = M.slice(0, 2, 1, 3);
        assert N.equals(new Matrix(new float[][]{
                {2,3},
                {5,6}}
        ));

        N.getRow(0).set(0,7);
        assert M.equals(new Matrix(new float[][]{
                {1,7,3},
                {4,5,6}
        }));

        assert Arrays.equals(N.toArray(), new float[]{7, 3, 5, 6});

        M = new Matrix(new float[][]{
                {1,2,3},
                {4,5,6}
        });

        // Matrix.getRowCount()
        assert M.getRowCount() == 2;
        assert M.getColCount() == 3;

        // Matrix.multiply
        N = M.slice(0,2,1,3);
        Matrix J = N.multiply(M);
        assert J.equals(new Matrix(new float[][]{
                {14, 19, 24},
                {29, 40, 51}
        }));

        M.getIterator().forEachRemaining(aFloat -> {
            System.out.println(aFloat);
        });

        N = M.transpose();
        N.getIterator().forEachRemaining(aFloat -> {
            System.out.println(aFloat);
        });

        N = N.reshape(2,3);
        System.out.println(N);
        N.getColumn(1).transpose().set(0, 7);
        System.out.println(M);

    }
}

