package il.ac.hit.services;

import il.ac.hit.objects.Index;
import il.ac.hit.objects.Matrix;
import il.ac.hit.services.distances.DistanceParentsAlgorithmService;
import il.ac.hit.services.objects.IndexData;

import java.util.List;
import java.util.Map;

/**
 * Uses data from {@link DistanceParentsAlgorithmService} to find all lightest paths.
 */
public interface LightestPathsFinder {

    /**
     * Retrieve all lightest paths according to the data
     * @param matrix a matrix
     * @param data an already processed(dijkstra or similar) data on the matrix and its indexes
     * @param source source from which the path starts
     * @param destination the destination to reach to
     * @return a list of lightest paths
     */
    List<List<Index>> getLightestPaths(Matrix matrix, Map<Index, IndexData> data, Index source, Index destination);
}
