package il.ac.hit.objects;

import java.util.Collection;
import java.util.stream.Collectors;

public class NonDiagonalMatrix extends Matrix {
    public NonDiagonalMatrix(int[][] oArray) {
        super(oArray);
    }

    @Override
    public Collection<Index> getReachables(Index index) {
        return this.getNeighbors(index, false)
                .stream()
                .filter(i -> getValue(i) == 1)
                .collect(Collectors.toList());
    }
}
