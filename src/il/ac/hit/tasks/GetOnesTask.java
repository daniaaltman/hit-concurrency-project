package il.ac.hit.tasks;

import il.ac.hit.objects.Index;
import il.ac.hit.objects.Matrix;
import il.ac.hit.services.distances.ModifiedBreadthFirstSearchAlgorithmService;
import il.ac.hit.services.objects.IndexData;
import il.ac.hit.services.util.ConnectedVerticesExtractorService;
import il.ac.hit.services.util.ConnectedVerticesExtractorServiceImpl;

import java.io.IOException;
import java.io.ObjectInput;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A task that gets a matrix as input and in return retrieves all indexes that are equal to 1 sorted by connected components.
 */
public class GetOnesTask implements Task {
    public static final String TASK_KEY = "GetOnesTask";

    private final ConnectedVerticesExtractorService connectedVerticesExtractorService;

    public GetOnesTask(ConnectedVerticesExtractorService connectedVerticesExtractorService) {
        this.connectedVerticesExtractorService = connectedVerticesExtractorService;
    }

    @Override
    public String getKey() {
        return TASK_KEY;
    }

    public static void main(String[] args) {
        int[][] primitiveMatrix = {
                {1, 0, 0},
                {1, 0, 1},
                {1, 0, 1}
        };

        try (ModifiedBreadthFirstSearchAlgorithmService distanceParentsAlgorithmService = new ModifiedBreadthFirstSearchAlgorithmService()) {
            ConnectedVerticesExtractorServiceImpl connectedVerticesExtractorService = new ConnectedVerticesExtractorServiceImpl(distanceParentsAlgorithmService);
            GetOnesTask getOnesTask = new GetOnesTask(connectedVerticesExtractorService);
            Map<Index, Map<Index, IndexData>> indexToItsConnectedVertices = connectedVerticesExtractorService.getIndexToConnectedVertices(new Matrix(primitiveMatrix));
            LinkedHashSet<Index> result = getOnesTask.getSortedSetByComponentSize(indexToItsConnectedVertices);
            System.out.println(result);
            // should be [(1,2), (2,2), (0,0), (1,0), (2,0)]
            assert result.toString().equals("[(1,2), (2,2), (0,0), (1,0), (2,0)]");
        }
    }

    @Override
    public Object run(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        final int[][] primitiveMatrix = (int[][]) objectInput.readObject();
        Map<Index, Map<Index, IndexData>> indexToItsConnectedVertices = connectedVerticesExtractorService.getIndexToConnectedVertices(new Matrix(primitiveMatrix));
        return getSortedSetByComponentSize(indexToItsConnectedVertices);
    }

    private LinkedHashSet<Index> getSortedSetByComponentSize(Map<Index, Map<Index, IndexData>> indexToItsConnectedVertices) {
        return indexToItsConnectedVertices
                .keySet()
                .stream()
                .sorted((a, b) -> indexComparator(indexToItsConnectedVertices, a, b))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private int indexComparator(Map<Index, Map<Index, IndexData>> indexToItsConnectedVertices, Index a, Index b) {
        if (indexToItsConnectedVertices.get(a) == indexToItsConnectedVertices.get(b)) {
            int rowComparison = Integer.compare(a.getRow(), b.getRow());
            if (rowComparison == 0) {
                return Integer.compare(a.getColumn(), b.getColumn());
            } else {
                return rowComparison;
            }
        } else {
            return Integer.compare(indexToItsConnectedVertices.get(a).entrySet().size(),
                    indexToItsConnectedVertices.get(b).entrySet().size());
        }
    }
}
