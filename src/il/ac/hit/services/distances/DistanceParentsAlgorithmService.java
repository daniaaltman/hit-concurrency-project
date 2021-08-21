package il.ac.hit.services.distances;

import il.ac.hit.objects.Index;
import il.ac.hit.objects.Matrix;
import il.ac.hit.services.objects.IndexData;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * Algorithm that retrieve lowest distance & its appropriate parent(for calculating lightest paths).
 * It uses {@link IndexData} to store distance & parent.
 */
public interface DistanceParentsAlgorithmService {
    /**
     * Runs the actual algorithm
     * @param matrix the matrix to run on
     * @param source the source to run from
     * @param weight the weight function of two indexes
     * @return a map of index to its data(distance & parents)
     */
    Map<Index, IndexData> runAlgo(final Matrix matrix,
                                  final Index source,
                                  BiFunction<Index, Index, Integer> weight);
}
