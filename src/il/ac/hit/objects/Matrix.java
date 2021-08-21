package il.ac.hit.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Matrix implements Serializable {

    int[][] primitiveMatrix;

    public Matrix(int[][] oArray) {
        List<int[]> list = new ArrayList<>();
        for (int[] row : oArray) {
            int[] clone = row.clone();
            list.add(clone);
        }
        primitiveMatrix = list.toArray(new int[0][]);
    }

    public void printMatrix() {
        for (int[] row : primitiveMatrix) {
            String s = Arrays.toString(row);
            System.out.println(s);
        }
    }

    public final int[][] getPrimitiveMatrix() {
        return primitiveMatrix;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int[] row : primitiveMatrix) {
            stringBuilder.append(Arrays.toString(row));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public Collection<Index> getNeighbors(final Index index) {
        return getNeighbors(index, true);
    }

    public Collection<Index> getNeighbors(final Index index, boolean includeDiagonal) {
        Collection<Index> list = new ArrayList<>();
        boolean rowGreaterThanZero = false;
        boolean rowNotAtEnd = false;
        if (index.row > 0) {
            list.add(new Index(index.row - 1, index.column));
            rowGreaterThanZero = true;
        }
        if (index.row < primitiveMatrix.length - 1) {
            list.add(new Index(index.row + 1, index.column));
            rowNotAtEnd = true;
        }

        if (index.column > 0) {
            list.add(new Index(index.row, index.column - 1));

            if (includeDiagonal) {
                if (rowGreaterThanZero) {
                    list.add(new Index(index.row - 1, index.column - 1));
                }

                if (rowNotAtEnd) {
                    list.add(new Index(index.row + 1, index.column - 1));
                }
            }
        }

        if (index.column < primitiveMatrix[index.row].length - 1) {
            list.add(new Index(index.row, index.column + 1));

            if (includeDiagonal) {
                if (rowNotAtEnd) {
                    list.add(new Index(index.row + 1, index.column + 1));
                }

                if (rowGreaterThanZero) {
                    list.add(new Index(index.row - 1, index.column + 1));
                }
            }
        }

        return list;
    }

    public int getValue(Index index) {
        return primitiveMatrix[index.row][index.column];
    }

    public Collection<Index> getReachables(Index index) {
        return this.getNeighbors(index)
                .stream()
                .filter(i -> getValue(i) == 1)
                .collect(Collectors.toList());
    }



    public static void main(String[] args) {
        int[][] source = {
                {0, 1, 0},
                {1, 0, 1},
                {1, 0, 1}
        };
        Matrix matrix = new Matrix(source);
        matrix.printMatrix();
        System.out.println(matrix.getNeighbors(new Index(1, 1)));
        System.out.println(matrix.getReachables(new Index(1, 1)));
    }
}