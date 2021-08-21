package il.ac.hit;

import il.ac.hit.objects.Index;
import il.ac.hit.tasks.GetOnesTask;
import il.ac.hit.tasks.LightestTracksTask;
import il.ac.hit.tasks.ShortestTracksTask;
import il.ac.hit.tasks.SubmarinesValidator;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import static il.ac.hit.logic.LogicRunnerImpl.SERVER_PORT;

public class Client {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("127.0.0.1", SERVER_PORT);
        System.out.println("client: Created Socket");

        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        ObjectOutputStream toServer = new ObjectOutputStream(outputStream);
        ObjectInputStream fromServer = new ObjectInputStream(inputStream);

        // sending #1 matrix
        int[][] primitiveMatrix = {
                {1, 0, 0},
                {1, 0, 1},
                {1, 0, 1}
        };

        toServer.writeObject(GetOnesTask.TASK_KEY);
        toServer.writeObject(primitiveMatrix);
        // should print [(1,2), (2,2), (0,0), (1,0), (2,0)]
        System.out.println(fromServer.readObject());

        int[][] primitiveMatrix2 = {
                {100, -200, 200},
                {100, -400, 100}
        };
        toServer.writeObject(LightestTracksTask.TASK_KEY);
        toServer.writeObject(primitiveMatrix2);
        toServer.writeObject(new Index(1, 1));
        toServer.writeObject(new Index(1, 2));
        // should print [[(1,1), (0,1), (0,2), (1,2)], [(1,1), (1,2)]]
        System.out.println(fromServer.readObject());

        toServer.writeObject(ShortestTracksTask.TASK_KEY);
        int[][] primitiveMatrix3 =  {
                { 1, 1, 0, 0, 0 },
                { 1, 1, 1, 0, 0 },
                { 0, 1, 1, 1, 0 },
                { 0, 0, 1, 1, 0 }
        };
        toServer.writeObject(primitiveMatrix3);
        toServer.writeObject(new Index(0, 1));
        toServer.writeObject(new Index(3,2));
        // should print [[(0,1), (1,2), (2,2), (3,2)], [(0,1), (1,1), (2,2), (3,2)], [(0,1), (1,0), (2,1), (3,2)]]
        System.out.println(fromServer.readObject());

        toServer.writeObject(SubmarinesValidator.TASK_KEY);
        int[][] primitiveMatrix4 = {
                { 0, 1, 1, 0, 1 },
                { 0, 1, 1, 0, 0 },
                { 0, 0, 0, 1, 1 },
                { 1, 1, 0, 1, 1 }
        };
        // should print 1
        toServer.writeObject(primitiveMatrix4);
        System.out.println(fromServer.readObject());

        toServer.writeObject("stop");
    }
}
