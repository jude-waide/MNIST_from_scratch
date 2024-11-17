import matrix.Matrix;
import matrix.Vector;

public class MLP {

    private final Layer[] layers;
    private final CostFunction costFunction = CostFunction.L2;

    public MLP(int[] layers) {
        // Neural Network needs an input and output size
        assert layers.length >= 2;

        this.layers = new Layer[layers.length-1];
        for (int i = 0; i < layers.length-1; i++) {
            this.layers[i] = new Layer(layers[i], layers[i+1], ActivationFunction.SIGMOID);
        }
    }

    public MLP(Layer[] layers) {
        // Neural Network needs at least 1 layer
        assert layers.length > 0;

        this.layers = layers;
        for (int i = 0; i < layers.length - 1; i++) {
            if (layers[i].getOutputSize() != layers[i+1].getInputSize()) {
                throw new IllegalArgumentException();
            }
        }
    }

    public int getInputSize() {
        return layers[0].getInputSize();
    }

    public int getOutputSize() {
        return layers[layers.length-1].getOutputSize();
    }

    public Matrix predict(Matrix input) {
        // Number of feature vectors must match the input size of the Neural Network
        assert input.getColCount() != getInputSize();

        for (Layer layer : layers) {
            input = layer.predict(input);
        }
        return input;
    }

    /**
     * Trains the Neural Network using supervised learning and back propagation
     * @param x_train The training data, rows are feature vectors
     * @param y_train The labels, rows are feature vectors
     * @param epoch Number of times the training data is processed
     * @param batchSize Weights are updated after processing every batchSize number of inputs
     * @param alpha The learning rate
     */
    public void train(Matrix x_train, Matrix y_train, int epoch, int batchSize, float alpha) {
        // Number of features in input matrix must match input size of the Neural Network
        // Number of features in output matrix must match output size of the Neural Network
        // Number of samples must match number of labels
        assert x_train.getRowCount() == getInputSize();
        assert y_train.getRowCount() == getOutputSize();
        assert x_train.getColCount() == y_train.getColCount();

        // TODO: Randomise train

        // Iterate over the date epoch number of times
        for (int e = 0; e < epoch; e++) {
            System.out.println(e);
            // Iterate over the data
            for (int i = 0; i < x_train.getColCount(); i+=batchSize) {
                // Upper bound, cap to size of dataset
                int j = i + batchSize;
                if (j > x_train.getColCount()) j = x_train.getColCount();

                // Get a batch sized slice
                Matrix trainChunk = x_train.slice(0, x_train.getRowCount(), i, j).copy();
                Matrix labelsChunk = y_train.slice(0, y_train.getRowCount(), i, j).copy();

                // Calculate error
                Matrix error = predict(trainChunk);
                costFunction.derivative(error, labelsChunk);

                // Back propagate
                for (int k = layers.length-1; k >= 0; k--) {
                    error = layers[k].backPropagate(error, alpha);
                }
            }
        }
    }

    public float accuracy(Matrix input, Matrix labels) {
        float right = 0;
        Matrix predictions = predict(input);
        for (int x = 0; x < predictions.getColCount(); x++) {
            Vector inputCol = predictions.getColumn(x);
            Vector labelCol = labels.getColumn(x);
            int prediction = 0;
            int label = 0;
            float predictionValue = inputCol.get(0);
            float labelValue = inputCol.get(0);
            for (int y = 1; y < predictions.getRowCount(); y++) {
                if (inputCol.get(y) > predictionValue) {
                    predictionValue = inputCol.get(y);
                    prediction = y;
                }
                if (labelCol.get(y) > labelValue) {
                    labelValue = labelCol.get(y);
                    label = y;
                }
                if (prediction == label) right++;
            }

        }
        return right/input.getColCount();
    }

}
