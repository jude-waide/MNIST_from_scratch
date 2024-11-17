package matrix;

public interface FoldFunction {
    void fold(float[] vec1, final Vector vec2);

    FoldFunction SUM = (vec1, vec2) -> {
        for (int i = 0; i < vec1.length; i++) {
            vec1[i] += vec2.get(i);
        }
    };

    FoldFunction MAX = (vec1, vec2) -> {
        for (int i = 0; i < vec1.length; i++) {
            if (vec1[i] < vec2.get(i)) vec1[i] = vec2.get(i);
        }
    };

}
