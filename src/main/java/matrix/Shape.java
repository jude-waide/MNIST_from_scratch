package matrix;

public class Shape {
    private int rows;
    private int columns;

    public Shape(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
    }

    public void reshape(int rows, int columns) {
        assert this.rows * this.columns == rows*columns;
        this.rows = rows;
        this.columns = columns;
    }

    public int length() {
        return rows*columns;
    }

    public int rows() {
        return this.rows;
    }

    public int columns() {
        return this.columns;
    }

    @Override
    public String toString() {
      return "(" + rows + "," + columns + ")";
    }
}
