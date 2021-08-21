package il.ac.hit.services.util;

import il.ac.hit.objects.Index;
import il.ac.hit.objects.Matrix;
import il.ac.hit.services.objects.IndexData;

import java.util.List;
import java.util.Map;

/**
 * This represents any classes that help extracting all connected vertices components
 */
public interface ConnectedVerticesExtractorService {
    /**
     * Retrieves a list of all connected vertices
     * @param matrix a matrix
     * @return a list of maps which represent connected vertices
     */
    List<Map<Index, IndexData>> getConnectedVertices(Matrix matrix);

    /**
     * Retrieves a map of index to its connected vertices component
     * @param matrix a matrix
     * @return a map of index to connected vertices map
     */
    default Map<Index, Map<Index, IndexData>> getIndexToConnectedVertices(Matrix matrix) {
        return convertToIndexToConnectedVertices(getConnectedVertices(matrix));
    }

    /**
     * Converts result from {@link ConnectedVerticesExtractorService#getConnectedVertices(Matrix)}
     * to a map of an {@link Index} to its matching connected vertices component
     * @param data the data from {@link ConnectedVerticesExtractorService#getConnectedVertices(Matrix)}
     * @return a map of an {@link Index} to its matching connected vertices component
     */
    Map<Index, Map<Index, IndexData>> convertToIndexToConnectedVertices(List<Map<Index, IndexData>> data);
}
