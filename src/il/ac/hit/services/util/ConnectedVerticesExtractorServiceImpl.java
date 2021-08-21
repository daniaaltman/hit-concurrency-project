package il.ac.hit.services.util;

import il.ac.hit.objects.Index;
import il.ac.hit.objects.Matrix;
import il.ac.hit.services.distances.DistanceParentsAlgorithmService;
import il.ac.hit.services.objects.IndexData;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ConnectedVerticesExtractorServiceImpl implements ConnectedVerticesExtractorService {
    private final DistanceParentsAlgorithmService distanceParentsAlgorithmService;

    public ConnectedVerticesExtractorServiceImpl(DistanceParentsAlgorithmService distanceParentsAlgorithmService) {
        this.distanceParentsAlgorithmService = distanceParentsAlgorithmService;
    }

    @Override
    public List<Map<Index, IndexData>> getConnectedVertices(Matrix matrix) {
        List<Map<Index, IndexData>> connectedVertices = new LinkedList<>();
        List<Index> vertices = createInitialVerticesList(matrix.getPrimitiveMatrix());
        // while there are unconnected vertices
        while (!vertices.isEmpty()) {
            Map<Index, IndexData> data = distanceParentsAlgorithmService.runAlgo(matrix, vertices.get(0), (a, b) -> 1);
            connectedVertices.add(data);
            // remove whichever vertices are not in the current connected vertices
            data.entrySet().removeIf(entry -> entry.getValue().getDistance() == null);
            // remove whichever vertex is in any connected vertex
            vertices.removeIf(data::containsKey);
        }

        return connectedVertices;
    }

    @Override
    public Map<Index, Map<Index, IndexData>> convertToIndexToConnectedVertices(List<Map<Index, IndexData>> data) {
        Map<Index, Map<Index, IndexData>> indexToItsConnectedVertices = new HashMap<>();
        // get a map of a key to its map(or in other words - its connected vertices)
        data.forEach(v -> v.forEach((key, value) -> indexToItsConnectedVertices.put(key, v)));
        return indexToItsConnectedVertices;
    }

    private List<Index> createInitialVerticesList(int[][] primitiveMatrix) {
        List<Index> vertices = new LinkedList<>();
        for (int i = 0; i < primitiveMatrix.length; i++) {
            for (int j = 0; j < primitiveMatrix[i].length; j++) {
                if (primitiveMatrix[i][j] == 1) {
                    vertices.add(new Index(i, j));
                }
            }
        }

        return vertices;
    }
}
