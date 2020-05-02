package com;

import com.pojo.User;
import com.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

@SpringBootTest
class SpringbootShiroApplicationTests {

    @Autowired
    UserServiceImpl userService;
    @Autowired
    DataSource dataSource;

    @Test
    void contextLoads() {
        User zhangsan = userService.queryUserByName("法外狂徒张三");
        System.out.println(zhangsan);
    }
    @Test
    void ConnectionTest(){
        System.out.println(dataSource);
    }

}
