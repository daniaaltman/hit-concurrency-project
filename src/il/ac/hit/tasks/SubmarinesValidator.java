package il.ac.hit.tasks;

import il.ac.hit.objects.Index;
import il.ac.hit.objects.Matrix;
import il.ac.hit.objects.NonDiagonalMatrix;
import il.ac.hit.services.distances.ModifiedBreadthFirstSearchAlgorithmService;
import il.ac.hit.services.objects.IndexData;
import il.ac.hit.services.util.ConnectedVerticesExtractorService;
import il.ac.hit.services.util.ConnectedVerticesExtractorServiceImpl;

import java.io.IOException;
import java.io.ObjectInput;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SubmarinesValidator implements Task {
    public static final String TASK_KEY = "SubmarinesValidator";
    private final ConnectedVerticesExtractorService connectedVerticesExtractorService;

    public SubmarinesValidator(ConnectedVerticesExtractorService connectedVerticesExtractorService) {
        this.connectedVerticesExtractorService = connectedVerticesExtractorService;
    }

    @Override
    public String getKey() {
        return TASK_KEY;
    }

    @Override
    public Object run(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        int[][] primitiveMatrix = (int[][]) objectInput.readObject();
        return retrieveValidSubmarinesNumber(primitiveMatrix);
    }

    private long retrieveValidSubmarinesNumber(int[][] primitiveMatrix) {
        // connected vertices when diagonal vertices are reachable
        List<Map<Index, IndexData>> allConnectedVertices = connectedVerticesExtractorService.getConnectedVertices(new Matrix(primitiveMatrix));
        // connected vertices when diagonal vertices are not reachable
        Map<Index, Map<Index, IndexData>> nonDiagonalConnectedVertices = connectedVerticesExtractorService.getIndexToConnectedVertices(new NonDiagonalMatrix(primitiveMatrix));
        return allConnectedVertices
                .stream()
                .filter(connectedVertices -> isNonDiagonalVertices(nonDiagonalConnectedVertices, connectedVertices))
                .filter(this::isConnectedVerticesValidSubmarine)
                .count();
    }

    /**
     * Validates that a submarine that is considered one connected vertices component is still
     * considered one vertices component even if diagonal vertices cannot be reached.
     * @param nonDiagonalConnectedVertices
     * @param connectedVertices
     * @return
     */
    private boolean isNonDiagonalVertices(Map<Index, Map<Index, IndexData>> nonDiagonalConnectedVertices, Map<Index, IndexData> connectedVertices) {
        Optional<Index> index = connectedVertices.keySet().stream().findFirst();
        if (index.isPresent()) {
            Map<Index, IndexData> indexMap = nonDiagonalConnectedVertices.get(index.get());
            return connectedVertices
                    .keySet()
                    .stream()
                    .allMatch(i -> nonDiagonalConnectedVertices.get(i).equals(indexMap));
        }
        return false;
    }

    /**
     * Validates that all row counters and column counters are the same
     * @param currentConnectedVertices the current connected vertices component
     * @return whether or not it is a valid submarine
     */
    private boolean isConnectedVerticesValidSubmarine(Map<Index, IndexData> currentConnectedVertices) {
        Long rowCounters = getCounterIfCountersAreAllTheSame(currentConnectedVertices, Index::getRow);
        Long columnCounters = getCounterIfCountersAreAllTheSame(currentConnectedVertices, Index::getColumn);
        return (rowCounters > 1 || columnCounters > 1) && rowCounters != -1 && columnCounters != -1;
    }

    /**
     * Returns the counter if counter for all values are the same, else returning -1
     * @param currentConnectedVertices the current connected vertices
     * @param getIdentifier a function that retrieves an index counter
     * @return the counter or -1 if not all the counters are the same
     */
    private Long getCounterIfCountersAreAllTheSame(Map<Index, IndexData> currentConnectedVertices, Function<Index, Integer> getIdentifier) {
        Map<Integer, Long> identifierToCounter = currentConnectedVertices
                .keySet()
                .stream()
                .collect(Collectors.groupingBy(getIdentifier, Collectors.counting()));

        Optional<Long> counter = identifierToCounter
                .values()
                .stream()
                .findFirst();
        if (counter.isPresent()) {
            if (identifierToCounter
                    .values()
                    .stream()
                    .allMatch(c -> c.equals(counter.get()))) {
                return counter.get();
            } else {
                return -1L;
            }
        }

        return 0L;
    }

    public static void main(String[] args) {
        int[][] primitiveMatrix =  {
                { 0, 1, 1, 0, 1 },
                { 0, 1, 1, 0, 0 },
                { 0, 0, 0, 1, 1 },
                { 1, 1, 0, 1, 1 },
        };
        try (ModifiedBreadthFirstSearchAlgorithmService distanceParentsAlgorithmService = new ModifiedBreadthFirstSearchAlgorithmService()) {
            ConnectedVerticesExtractorService connectedVerticesExtractorService = new ConnectedVerticesExtractorServiceImpl(distanceParentsAlgorithmService);

            SubmarinesValidator submarinesValidator = new SubmarinesValidator(connectedVerticesExtractorService);
            long result = submarinesValidator.retrieveValidSubmarinesNumber(primitiveMatrix);
            System.out.println(result);
            assert result == 1;
        }
    }
}
