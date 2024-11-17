import matrix.Matrix;

public interface CostFunction {

    void cost(Matrix predictions, Matrix actual);

    void derivative(Matrix predictedLabels, Matrix labels);

    CostFunction L2 = new CostFunction() {
        @Override
        public void cost(Matrix predictedLabels, Matrix labels) {
            predictedLabels.subtract(labels);
            predictedLabels.map(aFloat -> (float) Math.pow(aFloat, 2));
        }

        @Override
        public void derivative(Matrix predictedLabels, Matrix labels) {
            predictedLabels.subtract(labels);
            predictedLabels.scale(2);
        }
    };
}
