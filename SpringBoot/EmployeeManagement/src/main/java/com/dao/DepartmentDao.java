package com.dao;

import com.pojo.Department;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class DepartmentDao {
    //模拟数据库中的数据
    private static Map<Integer, Department> departmentMap = null;

    static {
        departmentMap = new HashMap<>();
        departmentMap.put(101, new Department(101, "研发部"));
        departmentMap.put(102, new Department(102, "运营部"));
        departmentMap.put(103, new Department(103, "市场部"));
        departmentMap.put(104, new Department(104, "行政"));
    }

    //获取所有的部门信息
    public Collection<Department> getDepartments() {
        return departmentMap.values();
    }

    //通过id获取部门
    public Department getDepartmentById(Integer id) {
        return departmentMap.get(id);
    }
}