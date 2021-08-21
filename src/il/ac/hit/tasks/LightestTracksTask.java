package il.ac.hit.tasks;

import il.ac.hit.objects.Index;
import il.ac.hit.objects.Matrix;
import il.ac.hit.objects.WeightNonDiagonalMatrix;
import il.ac.hit.services.LightestPathsFinder;
import il.ac.hit.services.LightestPathsFinderImpl;
import il.ac.hit.services.distances.DistanceParentsAlgorithmService;
import il.ac.hit.services.distances.ModifiedBreadthFirstSearchAlgorithmService;
import il.ac.hit.services.objects.IndexData;

import java.io.IOException;
import java.io.ObjectInput;
import java.util.List;
import java.util.Map;

public class LightestTracksTask implements Task {
    public static final String TASK_KEY = "LightestTracks";
    private final DistanceParentsAlgorithmService distanceParentsAlgorithmService;
    private final LightestPathsFinder lightestPathsFinder;

    public LightestTracksTask(DistanceParentsAlgorithmService distanceParentsAlgorithmService, LightestPathsFinder lightestPathsFinder) {
        this.distanceParentsAlgorithmService = distanceParentsAlgorithmService;
        this.lightestPathsFinder = lightestPathsFinder;
    }

    @Override
    public String getKey() {
        return TASK_KEY;
    }

    @Override
    public Object run(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        int[][] primitiveMatrix = (int[][]) objectInput.readObject();
        Index source = (Index) objectInput.readObject();
        Index destination = (Index) objectInput.readObject();
        return getLightestPaths(primitiveMatrix, source, destination);
    }

    private List<List<Index>> getLightestPaths(int[][] primitiveMatrix, Index source, Index destination) {
        Matrix matrix = new WeightNonDiagonalMatrix(primitiveMatrix);
        Map<Index, IndexData> data = distanceParentsAlgorithmService.runAlgo(matrix, source, (a, b) -> matrix.getValue(b));
        return lightestPathsFinder.getLightestPaths(matrix, data, source, destination);
    }

    public static void main(String[] args) throws Exception {
        int[][] mat = {
                {-100, 100, 100, 100},
                {100, 500, 900, 300}
        };
        try (ModifiedBreadthFirstSearchAlgorithmService modifiedBreadthFirstSearchAlgorithmService = new ModifiedBreadthFirstSearchAlgorithmService()) {
            LightestPathsFinder lightestPathsFinder = new LightestPathsFinderImpl();
            LightestTracksTask lightestTracksTask = new LightestTracksTask(modifiedBreadthFirstSearchAlgorithmService, lightestPathsFinder);
            List<List<Index>> result = lightestTracksTask.getLightestPaths(mat, new Index(1, 1), new Index(1, 3));
            System.out.println(result);
            assert result.toString().equals("[[(1,1), (0,1), (0,2), (0,3), (1,3)], [(1,1), (1,0), (0,0), (0,1), (0,2), (0,3), (1,3)]]");

            int[][] newMat = {
                    {100, -200, 200},
                    {100, -400, 100}
            };
            result = lightestTracksTask.getLightestPaths(newMat, new Index(1, 1), new Index(1, 2));
            System.out.println(result);
        }
    }
}
