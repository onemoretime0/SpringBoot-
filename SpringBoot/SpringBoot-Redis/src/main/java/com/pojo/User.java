package com.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
//在企业级开发中，所有的pojo类都需要序列化
public class User implements Serializable {
    private String name;
    private int age;
}
