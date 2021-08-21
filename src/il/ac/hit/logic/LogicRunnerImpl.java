package il.ac.hit.logic;

import il.ac.hit.handlers.exceptions.StopRequestException;
import il.ac.hit.services.TcpServer;
import il.ac.hit.handlers.TaskResolverHandler;
import il.ac.hit.services.LightestPathsFinderImpl;
import il.ac.hit.services.distances.DijkstraAlgorithmService;
import il.ac.hit.services.distances.ModifiedBreadthFirstSearchAlgorithmService;
import il.ac.hit.services.util.ConnectedVerticesExtractorServiceImpl;
import il.ac.hit.tasks.GetOnesTask;
import il.ac.hit.tasks.LightestTracksTask;
import il.ac.hit.tasks.ShortestTracksTask;
import il.ac.hit.tasks.SubmarinesValidator;

import java.io.IOException;
import java.util.Arrays;

public class LogicRunnerImpl implements LogicRunner {
    public static final int SERVER_PORT = 8090;

    @Override
    public void run() {
        System.out.println("Starting server");
        TcpServer server = new TcpServer(SERVER_PORT);
        ModifiedBreadthFirstSearchAlgorithmService distanceParentsAlgorithmService = new ModifiedBreadthFirstSearchAlgorithmService();
        try {
            ConnectedVerticesExtractorServiceImpl connectedVerticesExtractorService = new ConnectedVerticesExtractorServiceImpl(distanceParentsAlgorithmService);
            LightestPathsFinderImpl lightestPathsFinder = createLightestPathsFinder();
            System.out.println("Running...");
            server.run(createTaskResolverHandler(distanceParentsAlgorithmService, connectedVerticesExtractorService, lightestPathsFinder));
        } catch (StopRequestException e) {
            System.out.println("Received stop signal");
            distanceParentsAlgorithmService.close();
            try {
                server.close();
            } catch (IOException ignored) {
            }


        } catch (Exception e) {
            System.err.println("Encountered an error!");
            e.printStackTrace();
        } finally {
            System.out.println("Server stopped");
        }
    }

    private TaskResolverHandler createTaskResolverHandler(ModifiedBreadthFirstSearchAlgorithmService distanceParentsAlgorithmService,
                                                          ConnectedVerticesExtractorServiceImpl connectedVerticesExtractorService,
                                                          LightestPathsFinderImpl lightestPathsFinder) {
        return new TaskResolverHandler(Arrays.asList(
                createGetOnesTask(connectedVerticesExtractorService),
                createShortestTracksTask(lightestPathsFinder),
                createLightestTracksTask(distanceParentsAlgorithmService, lightestPathsFinder),
                createSubmarinesValidator(connectedVerticesExtractorService)
        ));
    }

    private LightestPathsFinderImpl createLightestPathsFinder() {
        return new LightestPathsFinderImpl();
    }

    private SubmarinesValidator createSubmarinesValidator(ConnectedVerticesExtractorServiceImpl connectedVerticesExtractorService) {
        return new SubmarinesValidator(connectedVerticesExtractorService);
    }

    private LightestTracksTask createLightestTracksTask(ModifiedBreadthFirstSearchAlgorithmService distanceParentsAlgorithmService, LightestPathsFinderImpl lightestPathsFinder) {
        return new LightestTracksTask(distanceParentsAlgorithmService, lightestPathsFinder);
    }

    private ShortestTracksTask createShortestTracksTask(LightestPathsFinderImpl lightestPathsFinder) {
        return new ShortestTracksTask(new DijkstraAlgorithmService(), lightestPathsFinder);
    }

    private GetOnesTask createGetOnesTask(ConnectedVerticesExtractorServiceImpl connectedVerticesExtractorService) {
        return new GetOnesTask(connectedVerticesExtractorService);
    }
}
