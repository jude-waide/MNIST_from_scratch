package matrix;

public interface ArrayView extends Iterable<Float> {
    int size();
    float get(int i);
    void set(int i, float value);

    default void setAll(float[] arr) {
        assert arr.length == size();
        for (int i = 0; i < size(); i++) {
            set(i, arr[i]);
        }
    }

    default float[] toArray() {
        float[] ret = new float[size()];
        for (int i = 0; i < size(); i++) {
            ret[i] = get(i);
        }
        return ret;
    }

    default void swap(ArrayView arrayView) {
        assert size() == arrayView.size();
        float temp;
        for (int i = 0; i < size(); i++) {
            temp = get(i);
            set(i,arrayView.get(i));
            arrayView.set(i,temp);
        }
    }

    default ArrayView copy() {
        ArrayView ret = new ArrayWrapper(size());
        for (int i = 0; i < size(); i++) {
            ret.set(i, get(i));
        }
        return ret;
    }
}
