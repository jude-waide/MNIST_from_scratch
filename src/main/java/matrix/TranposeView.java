package matrix;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class TranposeView implements ArrayView{

    private final ArrayView arrayView;
    private final Shape shape;

    public TranposeView(ArrayView arrayView, Shape shape) {
        assert arrayView.size() == shape.length();
        this.arrayView = arrayView;
        this.shape = shape;
    }

    public ArrayView getArrayView() {
        return arrayView;
    }

    @Override
    public int size() {
        return arrayView.size();
    }

    public int transform(int i) {
        int row = (i % shape.columns());
        int col = (i / shape.columns());
        i = row * shape.rows() + col;
        return i;
    }

    @Override
    public float get(int i) {
        return arrayView.get(transform(i));
    }

    @Override
    public void set(int i, float value) {
        arrayView.set(transform(i), value);
    }

    @NotNull
    @Override
    public Iterator<Float> iterator() {
        return new Iterator<>() {
            int i = 0;
            int j = 0;

            @Override
            public boolean hasNext() {
                return j < shape.columns();
            }

            @Override
            public Float next() {
                float v = arrayView.get(i);
                i+=shape.columns();
                if (i >= size()) {
                    j++;
                    i = j;
                }
                return v;
            }
        };
    }
}
