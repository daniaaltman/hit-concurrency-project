package il.ac.hit.services.distances;

import il.ac.hit.objects.Index;
import il.ac.hit.objects.Matrix;
import il.ac.hit.services.objects.ConcurrentIndexData;
import il.ac.hit.services.objects.IndexData;

import java.io.Closeable;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Runs a modified breadth first search algorithm(fit for weight matrices with negative weights).
 * It is a modified version of BFS that is fit for light paths searching.
 * It go over each neighbor(vertex) from source and checks whether or not it is suitable for a weight relaxing.
 */
public class ModifiedBreadthFirstSearchAlgorithmService implements DistanceParentsAlgorithmService, Closeable {
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public Map<Index, IndexData> runAlgo(final Matrix matrix,
                                         final Index source,
                                         BiFunction<Index, Index, Integer> weight) {
        Map<Index, IndexData> map = new ConcurrentHashMap<>();
        setupIndexMap(matrix, map);
        map.get(source).setParentAndDistance(null, 0);
        map.get(source).setSource(true);
        Queue<Index> queue = new LinkedBlockingQueue<>();
        queue.add(source);
        List<Future<?>> tasks = new LinkedList<>();
        // using CompletionService to avoid busy-waiting 
        final CompletionService<Void> completionService = new ExecutorCompletionService<>(executorService);
        Index current;
        // continue so long as there are more tasks to go
        while ((current = queue.poll()) != null || tasks.stream().anyMatch(t -> !t.isDone())) {
            Index finalCurrent = current;
            if (current != null && !map.get(current).isVisited()) {
                tasks.add(completionService.submit(() -> goOverVertex(matrix, weight, map, queue, finalCurrent), null));
                // filter out done tasks for shorter future searches
                tasks = tasks.stream().filter(t -> !t.isDone()).collect(Collectors.toList());
            }

            Optional<Future<?>> unfinishedTask = tasks.stream().filter(t -> !t.isDone()).findFirst();
            if (unfinishedTask.isPresent()) {
                try {
                    completionService.take(); // wait for tasks to finish
                } catch (InterruptedException ignored) {
                }
            }
        }
        return map;
    }

    @Override
    public void close() {
        executorService.shutdownNow();
    }

    private void goOverVertex(Matrix matrix,
                              BiFunction<Index, Index, Integer> weight,
                              Map<Index, IndexData> map,
                              Queue<Index> queue,
                              Index current) {
        matrix.getReachables(current)
                .stream()
                .filter(i -> !map.get(i).isSource() && !map.get(i).isVisited())
                .forEach(i -> relax(current, map.get(current), map.get(i), i, queue, weight.apply(current, i)));
        map.get(current).visit();
    }

    private void relax(Index source,
                       IndexData sourceIndexData,
                       IndexData destinationIndexData,
                       Index destination,
                       Queue<Index> queue,
                       int weight) {
        int distanceCandidate = sourceIndexData.getDistance() + weight;
        if (destinationIndexData.getDistance() == null || distanceCandidate < destinationIndexData.getDistance()) {
            destinationIndexData.setParentAndDistance(source, distanceCandidate);
        }

        if (!destinationIndexData.isVisited()) {
            queue.add(destination);
        }
    }

    private void setupIndexMap(Matrix matrix, Map<Index, IndexData> map) {
        int[][] primitiveMatrix = matrix.getPrimitiveMatrix();
        for (int i = 0; i < primitiveMatrix.length; i++) {
            for (int j = 0; j < primitiveMatrix[i].length; j++) {
                Index index = new Index(i, j);
                map.put(index, new ConcurrentIndexData(null, null));
            }
        }
    }

}
