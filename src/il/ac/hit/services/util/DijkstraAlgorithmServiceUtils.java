package il.ac.hit.services.util;

import il.ac.hit.objects.Index;
import il.ac.hit.services.objects.IndexData;

import java.util.Map;

public final class DijkstraAlgorithmServiceUtils {

    private DijkstraAlgorithmServiceUtils() {

    }

    /**
     * Comparing to indexes by their distance, regarding null as infinity
     * @param map the map of index to data
     * @param a first index
     * @param b second index
     * @return comparison integer
     */
    public static int distanceComparator(Map<Index, IndexData> map, Index a, Index b) {
        if (map.get(a).getDistance() != null && map.get(a).getDistance().equals(map.get(b).getDistance())) {
            return 0;
        } else if (map.get(a).getDistance() == null) {
            return 1;
        } else if (map.get(b).getDistance() == null) {
            return -1;
        } else {
            return map.get(a).getDistance().compareTo(map.get(b).getDistance());
        }
    }
}
