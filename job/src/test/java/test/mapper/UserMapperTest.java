package test.mapper;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.executor.JobApp;
import com.yiban.erp.executor.dao.UserMapper;
import com.yiban.erp.executor.entities.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = JobApp.class)
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testUserMapper() {
        User user = userMapper.getUser("ppp");
        System.out.println(JSON.toJSONString(user));
    }
}
