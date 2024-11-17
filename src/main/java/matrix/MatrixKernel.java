package matrix;

import com.aparapi.Kernel;
import com.aparapi.Range;

public class MatrixKernel extends Kernel {
    private float[] inA;
    private float[] inB;
    private float[] out;
    private int[] extra = new int[1];

    private int function = 0;
    private final static int MAT_MULT = 0;
    private final static int MAT_ADD = 1;

    @Override
    public void run() {
        if (function == MAT_MULT) {
            int i = getGlobalId(0);
            int p = extra[1];
            int n = extra[2];
            int a = i/n;
            int b = i%n;

            float value = 0;
            for (int j = 0; j < p; j++) {
                value += inA[j+a*p] * inB[j*n+b];
            }
            out[i] = value;
        }

        if (function == MAT_ADD) {
            int i = getGlobalId(0);
            out[i] = inA[i] + inB[i];
        }

    }

    public float[] multiply(float[] A, float[] B, int m, int p, int n) {
        setExplicit(true);
        inA = A;
        inB = B;
        out = new float[m * n];
        extra = new int[]{m,p,n};
        function = MAT_MULT;

        put(inA).put(inB).put(extra);
        execute(Range.create(m*n));
        get(out);
        return out;
    }

    public float[] add(float[] A, float[] B) {
        setExplicit(true);
        inA = A;
        inB = B;
        out = new float[A.length];
        function = MAT_MULT;
        put(inA).put(inB);
        execute(Range.create(A.length));
        get(out);
        return out;
    }
}
