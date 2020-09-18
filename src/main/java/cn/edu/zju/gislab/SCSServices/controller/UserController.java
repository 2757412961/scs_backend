package cn.edu.zju.gislab.SCSServices.controller;

import cn.edu.zju.gislab.SCSServices.po.ScsUsers;
import cn.edu.zju.gislab.SCSServices.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    //登录验证
    @RequestMapping("/login")
    public @ResponseBody int login(String username, String password) {
        // 登录验证
        int loginResult = userService.checkLogin(username, password);
        return loginResult;
    }
    //查询所有用户
    @RequestMapping("/AllUsers")
    public @ResponseBody List<ScsUsers> queryAllUser() {
        List<ScsUsers> result = userService.getAllUsers();
        return result;
    }
    //
    @RequestMapping("/updateUser")
    public @ResponseBody int updateUser(String username,String password,int category) {
        int result = userService.updateUser(username,password,category);
        return result;
    }
    //
    @RequestMapping("/deleteUser")
    public @ResponseBody int deleteUser(String username) {
        int result = userService.deleteUser(username);
        return result;
    }
    //
    @RequestMapping("/addUser")
    public @ResponseBody int addUser(String username,String password,int groupId) {
        int result = userService.addUser(username,password,groupId);
        return result;
    }

}
