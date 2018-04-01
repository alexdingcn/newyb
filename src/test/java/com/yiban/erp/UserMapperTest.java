package com.yiban.erp;

import com.yiban.erp.dao.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testUserMapper() {
        userMapper.selectByPrimaryKey(1L);
    }
}
