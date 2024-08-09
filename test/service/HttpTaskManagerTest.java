package service;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

public abstract class HttpTaskManagerTest {

    protected TaskManager manager = new InMemoryTaskManager();
    protected HttpTaskServer taskServer;
    protected Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        taskServer = new HttpTaskServer(manager);
        gson = taskServer.getGson();
        manager.removeAllTasks();
        manager.removeAllSubtasks();
        manager.removeAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }
}
