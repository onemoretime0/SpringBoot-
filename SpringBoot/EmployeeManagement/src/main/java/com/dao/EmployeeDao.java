package com.dao;

import com.pojo.Department;
import com.pojo.Employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class EmployeeDao {
    private static Map<Integer, Employee> employeeMap;
    @Autowired
    private DepartmentDao departmentDao;

    static {
        employeeMap = new HashMap<>();
        employeeMap.put(1001, new Employee(1001, "张三", "12345@gmial.com", 1, new Department(101, "研发部")));
        employeeMap.put(1002, new Employee(1002, "王司徒", "12asd5@gmial.com", 1, new Department(102, "运行部")));
        employeeMap.put(1003, new Employee(1003, "李四", "1234asd5as@gmial.com", 0, new Department(103, "市场部")));
        employeeMap.put(1004, new Employee(1004, "王五", "12shgj5@gmial.com", 0, new Department(104, "行政")));
    }

    //增加一个员工，主键自增
    private static Integer initId = 1005;

    public void addEmployee(Employee employee) {
        if (employee.getId() == null) {
            employee.setId(initId++);
        }
        employee.setDepartment(departmentDao.getDepartmentById(employee.getDepartment().getId()));
        employeeMap.put(employee.getId(), employee);
    }

    //查询全部员工
    public Collection<Employee> getEmployeeAll() {
        return employeeMap.values();
    }

    //通过id查询员工
    public Employee getEmployeeById(Integer id) {
        return employeeMap.get(id);
    }

    //删除一个员工
    public void removeEmployee(Integer id) {
        employeeMap.remove(id);
    }
}

