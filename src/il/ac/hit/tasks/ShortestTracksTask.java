package il.ac.hit.tasks;

import il.ac.hit.objects.Index;
import il.ac.hit.objects.Matrix;
import il.ac.hit.services.*;
import il.ac.hit.services.distances.DijkstraAlgorithmService;
import il.ac.hit.services.distances.DistanceParentsAlgorithmService;

import java.io.IOException;
import java.io.ObjectInput;
import java.util.List;

public class ShortestTracksTask implements Task {
    public static final String TASK_KEY = "ShortestTracks";
    private final DistanceParentsAlgorithmService dijkstraService;
    private final LightestPathsFinder lightestPathsFinder;

    public ShortestTracksTask(DistanceParentsAlgorithmService dijkstraService, LightestPathsFinder lightestPathsFinder) {
        this.dijkstraService = dijkstraService;
        this.lightestPathsFinder = lightestPathsFinder;
    }

    @Override
    public String getKey() {
        return TASK_KEY;
    }

    // the unit test(since there's no unit test framework)
    @Override
    public Object run(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        int[][] primitiveMatrix = (int[][]) objectInput.readObject();
        Index source = (Index) objectInput.readObject();
        Index destination = (Index) objectInput.readObject();
        return getShortestTrack(source, destination, primitiveMatrix);
    }

    private List<List<Index>> getShortestTrack(Index source, Index destination, int[][] primitiveMatrix) {
        Matrix matrix = new Matrix(primitiveMatrix);
        return lightestPathsFinder.getLightestPaths(matrix, dijkstraService.runAlgo(matrix, source, (a, b) -> 1), source, destination);
    }

    public static void main(String[] args) throws Exception {
        int[][] mat =  {
                { 1, 1, 0, 0, 0 },
                { 1, 1, 1, 0, 0 },
                { 0, 1, 1, 1, 0 },
                { 0, 0, 1, 1, 0 },
        };
        DistanceParentsAlgorithmService dijkstraAlgorithmService = new DijkstraAlgorithmService();
        LightestPathsFinder lightestPathsFinder = new LightestPathsFinderImpl();
        ShortestTracksTask shortestTracksTask = new ShortestTracksTask(dijkstraAlgorithmService, lightestPathsFinder);
        List<List<Index>> result = shortestTracksTask.getShortestTrack(new Index(0, 1), new Index(3,2), mat);
        // should output [[(0,1), (1,2), (2,2), (3,2)], [(0,1), (1,1), (2,2), (3,2)], [(0,1), (1,0), (2,1), (3,2)]]
        System.out.println(result);
        result = shortestTracksTask.getShortestTrack(new Index(0, 1), new Index(3,2), mat);
        System.out.println(result);
        assert result.toString().equals("[[(0,1), (1,2), (2,2), (3,2)], [(0,1), (1,1), (2,2), (3,2)], [(0,1), (1,0), (2,1), (3,2)]]");
    }




}
