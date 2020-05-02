package com.controller;

import com.dao.DepartmentDao;
import com.dao.EmployeeDao;
import com.pojo.Department;
import com.pojo.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.Collection;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeDao employeeDao;
    @Autowired
    private DepartmentDao departmentDao;
    @RequestMapping("/emps")
    public String list(Model model) {
        Collection<Employee> employeeAll = employeeDao.getEmployeeAll();
        model.addAttribute("emps",employeeAll);
        return "emp/list";
    }
    @GetMapping("/emp")
    public String toAddpage(Model model) {
        //查询所有部门的信息
        Collection<Department> departments = departmentDao.getDepartments();
        System.out.println(departments);
        model.addAttribute("departments", departments);
        return "emp/add";
    }
    @PostMapping("/emp")
    public String addEmp(Employee employee) {
        //添加操作
        System.out.println("save employee:" + employee);
        employeeDao.addEmployee(employee);
        return "redirect:/emps";
    }

    //去员工的修改页面
    @GetMapping("/emp/{id}")
    public String toUpdateEmp(@PathVariable("id") Integer id, Model model) {
        //查出原来的数据
        Employee employee = employeeDao.getEmployeeById(id);
        System.out.println(employee);
        model.addAttribute("emp", employee);
        //查询所有部门信息
        Collection<Department> departments = departmentDao.getDepartments();
        System.out.println(departments);
        model.addAttribute("departments", departments);
        return "emp/update";
    }

    @PostMapping("/updateEmp")
    public String updateEmp(Employee employee) {
        employeeDao.addEmployee(employee);
        return "redirect:/emps";
    }

    @GetMapping("/delemp/{id}")
    public String toDeleteEmp(@PathVariable("id") Integer id) {
        employeeDao.removeEmployee(id);
        return "redirect:/emps";
    }

    @RequestMapping("/user/logout")
    public String UserLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/index.html";
    }
}
