package il.ac.hit.handlers;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Handler extends Closeable {
    void handle(InputStream fromClient,
                                OutputStream toClient) throws IOException, ClassNotFoundException;
}
