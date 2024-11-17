import matrix.FoldFunction;
import matrix.Matrix;
import matrix.Vector;

public class Layer {
    private final Matrix weights;
    private final Vector biases;
    private final ActivationFunction activationFunction;
    private Matrix z;
    private Matrix input;

    Layer(final int inputSize, final int outputSize, final ActivationFunction activationFunction) {
        // Neural Network must have a positive input size
        // Neural Network must have a positive output size
        assert inputSize > 0;
        assert outputSize > 0;

        weights = Matrix.random(outputSize, inputSize, -2f, 2f);
        biases = Vector.cast(Matrix.zeros(outputSize, 1));
        this.activationFunction = activationFunction;
    }

    Layer(final Matrix weights, final Vector biases, final ActivationFunction activationFunction) {
        // Weights and biases must have the same input size
        // Biases must be a column vector
        assert weights.getColCount() == biases.getColCount();
        assert biases.getColCount() == 1;

        this.weights = weights;
        this.biases = biases;
        this.activationFunction = activationFunction;
    }

    public int getInputSize() {
        return weights.getColCount();
    }

    public int getOutputSize() {
        return weights.getRowCount();
    }

    public Matrix predict(final Matrix input) {
        // Number of feature vectors must equal the input size
        assert input.getRowCount() == getInputSize();

        // Store input for later in backpropagation
        this.input = input.copy();

        // z = Wx + b
        z = weights.multiply(input);
        z.add(biases);

        // a = f(z)
        Matrix a = z.copy();
        a.colMap(activationFunction::apply);
        return a;
    }

    public Matrix backPropagate(final Matrix error, final float alpha) {

        //System.out.println("error: " + error.getRowCount() + ", " + error.getColCount());
        //System.out.println("z: " + z.getRowCount() + ", " + z.getColCount());

        // Derivative of z with respect to error
        z.colMap(activationFunction::derivative);
        z.entrywiseProduct(error);

        // Calculate bias delta
        Vector biasDelta = z.colFoldl(FoldFunction.SUM::fold);
        biasDelta.scale(alpha/input.getColCount());
        biases.subtract(biasDelta);


        Matrix inputTranpose = input.transpose2();
        Matrix weightsDelta = Matrix.zeros(z.getRowCount(), inputTranpose.getColCount());
        //Matrix out = Matrix.zeros(weights.getColCount(), input.getColCount());
        Matrix out = weights.transpose2().multiply(z);

        for (int i = 0; i < error.getColCount(); i++) {
            Matrix dOutrW = z.getColumn(i).multiply(inputTranpose.getRow(i));
            weightsDelta.add(dOutrW);
            //out.getColumn(i).setAll(dOutrW.rowFoldd(FoldFunction.SUM::fold).toArray());
        }
        weightsDelta.scale(alpha/inputTranpose.getRowCount());
        weights.subtract(weightsDelta);

        return out;
    }


}
