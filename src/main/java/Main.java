import matrix.Matrix;
import matrix.Vector;
import util.Pair;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // Load the training data
        Pair<Matrix, Matrix> ret;
        try {
            ret = readData("./././archive/train-images.idx3-ubyte", "./././archive/train-labels.idx1-ubyte");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Matrix x_train = ret.left();
        Matrix y_train = ret.right();
        x_train.scale(1f/255);  // Scale the data

        // Load the test data
        try {
            ret = readData("./././archive/t10k-images.idx3-ubyte", "./././archive/t10k-labels.idx1-ubyte");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Matrix x_test = ret.left();
        Matrix y_test = ret.right();
        x_test.scale(1f/255);  // Scale the data

        // Create MLP and train
        MLP perceptron = new MLP(new int[]{784,64,32,10});
        perceptron.train(x_train, y_train, 5, 64, 1f);  // 5 epochs with a batch size of 64

        // Test on test data
        Matrix p = perceptron.predict(x_test);
        System.out.println(accuracy(getLabels(p), getLabels(y_test)));
    }

    public static Pair<Matrix, Matrix> readData(String dataFilePath, String labelFilePath) throws IOException {

        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(dataFilePath)));
        int magicNumber = dataInputStream.readInt();
        int numberOfItems = dataInputStream.readInt();
        int nRows = dataInputStream.readInt();
        int nCols = dataInputStream.readInt();

        System.out.println("magic number is " + magicNumber);
        System.out.println("number of items is " + numberOfItems);
        System.out.println("number of rows is: " + nRows);
        System.out.println("number of cols is: " + nCols);

        DataInputStream labelInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(labelFilePath)));
        int labelMagicNumber = labelInputStream.readInt();
        int numberOfLabels = labelInputStream.readInt();

        System.out.println("labels magic number is: " + labelMagicNumber);
        System.out.println("number of labels is: " + numberOfLabels);

        Matrix data = Matrix.zeros(nRows * nCols, numberOfItems);
        Matrix labels = Matrix.zeros(10, numberOfLabels);

        assert numberOfItems == numberOfLabels;

        for(int i = 0; i < numberOfItems; i++) {
            Vector col = data.getColumn(i);
            int label = labelInputStream.readUnsignedByte();
            labels.getColumn(i).set(label, 1);
            for (int j = 0; j < nRows*nCols; j++) {
                col.set(j, dataInputStream.readUnsignedByte());
            }
        }
        dataInputStream.close();
        return new Pair<>(data, labels);
    }

    public static Vector getLabels(Matrix m) {
        Vector l = Vector.rowVector(m.getColCount());
        Vector p = Vector.rowVector(m.getColCount());
        for (int i = 0; i < m.getRowCount(); i++) {
            Vector v = m.getRow(i);
            for (int j = 0; j < v.length(); j++) {
                if (v.get(j) > p.get(j)) {
                    p.set(j, v.get(j));
                    l.set(j, i);
                }
            }
        }
        return l;
    }

    public static float accuracy(Vector v1, Vector v2) {
        float right = 0;
        for (int i = 0; i < v1.length(); i++) {
            if (v1.get(i) == v2.get(i)) right++;
        }
        return right/v1.getColCount();
    }
}
