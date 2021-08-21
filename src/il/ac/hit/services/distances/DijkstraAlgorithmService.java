package il.ac.hit.services.distances;

import il.ac.hit.objects.Index;
import il.ac.hit.objects.Matrix;
import il.ac.hit.services.objects.IndexData;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static il.ac.hit.services.util.DijkstraAlgorithmServiceUtils.distanceComparator;

/**
 * A service that run Dijkstra algorithm(not fit for negative weight matrices)
 */
public class DijkstraAlgorithmService implements DistanceParentsAlgorithmService {

    @Override
    public Map<Index, IndexData> runAlgo(final Matrix matrix,
                                         final Index source,
                                         BiFunction<Index, Index, Integer> weight) {
        Map<Index, IndexData> map = new HashMap<>();
        setupIndexMap(matrix, map);
        matrix.getReachables(source).forEach(i -> map.get(i).setParentAndDistance(source, weight.apply(source, i)));
        map.get(source).setParentAndDistance(null, 0);
        map.get(source).setSource(true);
        // prioritize by distance
        Queue<Index> queue = new PriorityQueue<>((a, b) -> distanceComparator(map, a, b));
        Index current = source;
        queue.add(current);

        while (!queue.isEmpty()) {
            final IndexData currentData = map.get(current);
            // add next unvisited neighbors to list
            Collection<Index> reachables = matrix
                    .getReachables(current)
                    .stream()
                    .filter(index -> !map.get(index).isVisited())
                    .collect(Collectors.toList());

            int currentDataDistance = currentData.getDistance();
            for (Index currentIndexNeighbor : reachables) {
                queue.add(currentIndexNeighbor);
                IndexData currentIndexNeighborData = map.get(currentIndexNeighbor);
                Integer distance = currentIndexNeighborData.getDistance();
                if (distance == null || currentDataDistance + weight.apply(current, currentIndexNeighbor) < distance) { // relax distance if needed
                    currentIndexNeighborData.setParentAndDistance(current, currentDataDistance + weight.apply(current, currentIndexNeighbor));
                }
            }

            currentData.visit();
            current = queue.poll(); // get next lowest distance neighbor
        }

        return map;
    }

    protected void setupIndexMap(Matrix matrix, Map<Index, IndexData> map) {
        int[][] primitiveMatrix = matrix.getPrimitiveMatrix();
        for (int i = 0; i < primitiveMatrix.length; i++) {
            for (int j = 0; j < primitiveMatrix[i].length; j++) {
                if (primitiveMatrix[i][j] == 1) {
                    Index index = new Index(i, j);
                    map.put(index, new IndexData(null, null));
                }
            }
        }
    }
}
