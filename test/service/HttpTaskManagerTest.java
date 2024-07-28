package service;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

public abstract class HttpTaskManagerTest {

    protected TaskManager manager = new InMemoryTaskManager();
    protected HttpTaskServer taskServer = new HttpTaskServer(manager);
    protected Gson gson = taskServer.getGson();

    public HttpTaskManagerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
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
