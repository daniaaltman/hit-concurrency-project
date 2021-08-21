package il.ac.hit.handlers.exceptions;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException() {
        super("could not find the specified task");
    }
}
