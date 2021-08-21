package il.ac.hit.services;

import il.ac.hit.objects.Index;
import il.ac.hit.objects.Matrix;
import il.ac.hit.services.objects.IndexData;
import il.ac.hit.services.exceptions.UnreachableDestinationException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An implementation for {@link LightestPathsFinder}
 */
public class LightestPathsFinderImpl implements LightestPathsFinder {

    @Override
    public List<List<Index>> getLightestPaths(Matrix matrix, Map<Index, IndexData> data, Index source, Index destination) {
        data.values().forEach(IndexData::resetVisit);
        return getCurrentShortestPaths(matrix, data, destination);
    }

    private List<List<Index>> getCurrentShortestPaths(Matrix matrix, Map<Index, IndexData> data, Index destination) {
        IndexData destinationData = data.get(destination);
        if (data.get(destination).isSource()) { // source finally reached
            List<List<Index>> paths = new LinkedList<>();
            List<Index> currentPath = new LinkedList<>();
            currentPath.add(0, destination);
            paths.add(currentPath);
            return paths;
        } else if (destinationData.getDistance() == null) {
            throw new UnreachableDestinationException();
        } else { // retrieve shortest paths from here
            Index parent = destinationData.getParent();
            IndexData parentData = data.get(parent);
            int parentDistance = parentData.getDistance();
            List<Index> sameDistanceReachables = matrix
                    .getReachables(destination)
                    .stream()
                    // get unvisited to not cause endless recursion and get other paths with the same distance
                    .filter(i -> (!data.get(i).isVisited() || data.get(i).isSource()) && data.get(i).getDistance() == parentDistance)
                    .collect(Collectors.toList());
            List<List<Index>> shortestPathsFromHere = new LinkedList<>();
            destinationData.visit();
            for (Index reachable : sameDistanceReachables) {
                List<List<Index>> shortestPathFromHere = getCurrentShortestPaths(matrix, data, reachable);
                shortestPathFromHere.forEach(path -> path.add(destination));
                shortestPathsFromHere.addAll(shortestPathFromHere);
            }

            return shortestPathsFromHere;
        }
    }
}
