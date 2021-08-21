package il.ac.hit.tasks;

import java.io.IOException;
import java.io.ObjectInput;

public interface Task {
    /**
     * Key to identify task.
     * @return the key
     */
    String getKey();

    /**
     * Runs the logic of the task
     * @param objectInput the objectInput object to to read input
     * @return the object which would be returned to client
     */
    Object run(ObjectInput objectInput) throws IOException, ClassNotFoundException;

}
