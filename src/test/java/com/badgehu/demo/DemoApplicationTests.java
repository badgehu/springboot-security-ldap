package com.badgehu.demo;

import com.badgehu.demo.dao.UserDao;
import com.badgehu.demo.domain.Person;
import com.badgehu.demo.domain.SysUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    @Autowired
    Person person;
    @Autowired
    private Environment environment;
    @Autowired
    private UserDao userDao;
    @Test
    public void contextLoads() {
        //System.out.println(person);
        SysUser admin = userDao.findByUserName("admin");
        System.out.println(admin);
    }

}

