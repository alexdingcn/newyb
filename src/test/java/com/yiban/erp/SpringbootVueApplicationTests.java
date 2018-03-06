package com.yiban.erp;

import com.yiban.erp.controller.good.GoodCategoryController;
import com.yiban.erp.dao.GoodCategoryMapper;
import com.yiban.erp.entities.GoodCategory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



/**
 * @author Boyle Gu
 * @version 0.0.1
 * @GitHub https://github.com/boylegu
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MockServletContext.class)
@WebAppConfiguration
public class SpringbootVueApplicationTests {

    private MockMvc mvc;

    @Autowired
    private GoodCategoryMapper goodCategoryMapper;

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.standaloneSetup(
                new GoodCategoryController()).build();

        GoodCategory goodCategory = new GoodCategory();
        goodCategory.setName("test");
        goodCategory.setCreatedTime(new Date());
        goodCategory.setCreatedBy("admin");
        goodCategoryMapper.insert(goodCategory);

    }

    @Test
    public void testUserController() throws Exception {
//  	Test MainController
        RequestBuilder request = null;

        request = get("/api/persons/sex");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[]")));

        request = get("/api/persons/");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[]")));

        request = get("/api/persons/detail/1");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[{\"id\":1,\"username\":\"test\",\"zone\":20}]")));

        request = put("/api/persons/detail/1")
                .param("phone", "111111")
                .param("email", "test@qq.com");
        mvc.perform(request)
                .andExpect(content().string(equalTo("success")));

        request = get("/api/persons");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[]")));

    }

}



        /*
        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

                Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

                Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

                Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);
        /*
        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

                Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

                Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

                Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

                /*
        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

                Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

                Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

                Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

                /*
        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

                Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

                Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

                Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

                /*
        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

                Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

                Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

                Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

                /*
        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

                Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

                Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

                Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

                /*
        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

                Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

                Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

                Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);



        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);


        Persons persons2 = new Persons();
        persons2.setZone("t1 zone");
        persons2.setPhone("11111");
        persons2.setEmail("t1@qq.com");
        persons2.setUsername("t1");
        personsRepository.save(persons2);

        */
