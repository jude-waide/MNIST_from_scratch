import matrix.Vector;

public interface ActivationFunction {

    void apply(Vector v);

    void derivative(Vector v);

    ActivationFunction SIGMOID = new ActivationFunction() {
        @Override
        public void apply(Vector v) {
            v.map(aFloat -> (float) (1/ (1 + Math.exp(-aFloat))));
        }

        @Override
        public void derivative(Vector v) {
            apply(v);
            v.map(aFloat -> aFloat*(1-aFloat));
        }
    };

    ActivationFunction TANH = new ActivationFunction() {
        @Override
        public void apply(Vector v) {
            v.map(aFloat -> (float) Math.tanh(aFloat));
        }

        @Override
        public void derivative(Vector v) {
            apply(v);
            v.map(aFloat -> (float) (1 - Math.pow(aFloat, 2)));
        }
    };

    ActivationFunction RELU = new ActivationFunction() {
        @Override
        public void apply(Vector v) {
            v.map(aFloat -> aFloat > 0 ? aFloat : 0);
        }

        @Override
        public void derivative(Vector v) {
            v.map(aFloat -> aFloat > 0 ? 1f : 0);
        }
    };

    ActivationFunction SOFTMAX = new ActivationFunction() {
        @Override
        public void apply(Vector v) {
            v.map(aFloat -> (float) Math.exp(aFloat));
            float total = v.sum();
            v.map(aFloat -> aFloat/total);
        }

        @Override
        public void derivative(Vector v) {

        }
    };
}
