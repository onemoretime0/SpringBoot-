package com;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
class SpringbootJdbcApplicationTests {

    @Autowired
    private DataSource dataSource;
    @Test
    void contextLoads() {
    }

    @Test
    public void DataSourceTest() throws SQLException {
        //查看默认的数据源
        System.out.println(dataSource.getClass());
        Connection connection = dataSource.getConnection();
        System.out.println(connection);
        connection.close();
    }
}
