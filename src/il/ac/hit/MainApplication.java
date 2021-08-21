package il.ac.hit;

import il.ac.hit.logic.LogicRunner;
import il.ac.hit.logic.LogicRunnerImpl;

public class MainApplication {

    public static void main(String[] args) {
        LogicRunner logicRunner = new LogicRunnerImpl();
        logicRunner.run();
    }
}
