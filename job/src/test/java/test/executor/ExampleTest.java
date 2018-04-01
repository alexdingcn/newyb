package test.executor;

import com.yiban.erp.executor.JobApp;
import com.yiban.erp.executor.jobHandler.example.SimpleJobHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = JobApp.class)
public class ExampleTest {

    @Autowired
    private SimpleJobHandler simpleJobHandler;


    @Test
    public void testSimpleJobHandler() throws Exception {
        simpleJobHandler.execute("ppp", "aaa");
    }



}
