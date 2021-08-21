package il.ac.hit.services.exceptions;

/**
 * An exception representing a destination that cannot be reached from the mentioned source
 */
public class UnreachableDestinationException extends RuntimeException {

    public UnreachableDestinationException() {
        super("destination cannot be reached from this source!");
    }
}
