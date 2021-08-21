package il.ac.hit.handlers;

import il.ac.hit.handlers.exceptions.StopRequestException;
import il.ac.hit.handlers.exceptions.TaskNotFoundException;
import il.ac.hit.tasks.Task;

import java.io.*;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TaskResolverHandler implements Handler {
    private final Map<String, Task> tasks;

    public TaskResolverHandler(List<Task> tasks) {
        this.tasks = tasks
                .stream()
                .filter(t -> !t.getKey().equalsIgnoreCase("stop"))
                .collect(Collectors.toMap(t -> t.getKey().toLowerCase(Locale.getDefault()), Function.identity()));
    }

    @Override
    public void close() throws IOException {
        tasks
                .values()
                .stream()
                .filter(task -> task instanceof Closeable)
                .forEach(task -> closeTaskQuietly((Closeable) task));
    }

    @Override
    public void handle(InputStream fromClient, OutputStream toClient)
            throws IOException, ClassNotFoundException {

        // In order to read either objects or primitive types we can use ObjectInputStream
        ObjectInputStream objectInputStream = new ObjectInputStream(fromClient);
        // In order to write either objects or primitive types we can use ObjectOutputStream
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(toClient);

        boolean doWork = true;
        while (doWork) {
            String readObjectKey = objectInputStream.readObject().toString().toLowerCase(Locale.getDefault());
            if (!readObjectKey.equals("stop")) {
                if (tasks.containsKey(readObjectKey)) {
                    Task task = tasks.get(readObjectKey);
                    objectOutputStream.writeObject(task.run(objectInputStream));
                } else {
                    objectOutputStream.writeObject(new TaskNotFoundException());
                }
            } else {
                doWork = false;
            }
        }
        throw new StopRequestException();
    }

    private void closeTaskQuietly(Closeable task) {
        try {
            task.close();
        } catch (IOException e) {
            System.err.println("encountered an error while close a task: ");
            e.printStackTrace();
        }
    }
}