package managers;

import com.yandex.app.managers.FileBackedTaskManager;

import org.junit.jupiter.api.BeforeEach;

import java.io.File;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {


    @BeforeEach
    public void setUp() {

        File testFilePath = new File("test_tasks.csv");
        manager = new FileBackedTaskManager(testFilePath);

        super.setUp();
    }
}
