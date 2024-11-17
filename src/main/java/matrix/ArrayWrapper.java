package matrix;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;

public class ArrayWrapper implements ArrayView {

    private final float[] array;

    ArrayWrapper(int size) {
        array = new float[size];
    }

    ArrayWrapper(int size, int fill) {
        array = new float[size];
        Arrays.fill(array, fill);
    }

    public ArrayWrapper(float[] arr) {
        array = arr;
    }

    @Override
    public int size() {
        return array.length;
    }

    @Override
    public float get(int i) {
        assert i >= 0;
        assert i < size();
        return array[i];
    }

    @Override
    public void set(int i, float value) {
        assert i >= 0;
        assert i < size();
        array[i] = value;
    }

    @Override
    public void setAll(float[] arr) {
        assert arr.length == size();
        System.arraycopy(arr, 0, array, 0, arr.length);
    }

    public float[] toArray() {
        return ArrayView.super.toArray();
    }

    protected float[] getArray() {
        return array;
    }

    @NotNull
    @Override
    public Iterator<Float> iterator() {
        return new Iterator<>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < size();
            }

            @Override
            public Float next() {
                return get(i++);
            }
        };
    }
}
