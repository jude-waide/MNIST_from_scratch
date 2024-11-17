package matrix;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class SliceView implements ArrayView {

    private final ArrayView arrayView;
    private final int originalCols;
    private final int minRow;
    private final int maxRow;
    private final int minCol;
    private final int maxCol;
    private final int rowLength;
    private final int colLength;
    private final int length;

    public SliceView(ArrayView arrayView, int originalCols, int minRow, int maxRow, int minCol, int maxCol) {
        assert arrayView.size() % originalCols == 0;
        assert minRow <= maxRow;
        assert maxRow <= originalCols;

        this.arrayView = arrayView;
        this.originalCols = originalCols;
        this.minRow = minRow;
        this.maxRow = maxRow;
        this.minCol = minCol;
        this.maxCol = maxCol;
        this.rowLength = maxRow - minRow;
        this.colLength = maxCol - minCol;
        this.length = rowLength * (maxCol-minCol);

        assert size() <= arrayView.size();
    }

    @Override
    public int size() {
        return length;
    }

    private int transform(int i) {
        assert i >= 0;
        assert i < size();
        int j = i;

        int row = (i / colLength) + minRow;
        int col = (i % colLength) + minCol;
        i = row * originalCols + col;

        assert i < arrayView.size();
        return i;
    }

    @Override
    public float get(int i) {
        assert i >= 0;
        assert i < size();
        return arrayView.get(transform(i));
    }

    @Override
    public void set(int i, float value) {
        assert i >= 0;
        assert i < size();
        arrayView.set(transform(i), value);
    }

    @NotNull
    @Override
    public Iterator<Float> iterator() {
        return new Iterator<>() {
            int i = minCol + minRow * originalCols;
            @Override
            public boolean hasNext() {
                return i < size()-1;
            }

            @Override
            public Float next() {
                int j = i++;
                if (i >= maxCol) {
                    i += originalCols;
                    i -= rowLength;
                }
                return get(j);
            }
        };
    }
}
