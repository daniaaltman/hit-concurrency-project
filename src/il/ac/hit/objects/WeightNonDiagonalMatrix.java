package il.ac.hit.objects;

import java.util.Collection;

public class WeightNonDiagonalMatrix extends NonDiagonalMatrix {
    public WeightNonDiagonalMatrix(int[][] oArray) {
        super(oArray);
    }

    @Override
    public Collection<Index> getReachables(Index index) {
        return this.getNeighbors(index, false);
    }
}
