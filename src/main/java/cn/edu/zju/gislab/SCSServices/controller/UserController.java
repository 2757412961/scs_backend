package cn.edu.zju.gislab.SCSServices.controller;

import cn.edu.zju.gislab.SCSServices.po.ScsUsers;
import cn.edu.zju.gislab.SCSServices.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.*;
import java.util.List;

@RestController
public class UserController {
    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    // 是否登录
    @GetMapping("/isLogin")
    public Boolean isLogin(HttpServletResponse response) {


        return false;
    }

    // 登录验证
    @RequestMapping("/login")
    public int login(String username, String password,
                     HttpSession session,
                     HttpServletRequest request, HttpServletResponse response) {
        // 登录验证
        int loginResult = userService.checkLogin(username, password);

        if (loginResult != -1) {
            Cookie cookie = new Cookie("cookieUseName", username);
            cookie.setMaxAge(20); // 单位：秒
            cookie.setPath("/");
            response.addCookie(cookie);
        }

        // 返回用户权限
        return loginResult;
    }


    @RequestMapping("/logout")
    public Boolean logout(HttpServletRequest request, HttpServletResponse response) {

        return false;
    }

    // 查询所有用户
    @RequestMapping("/AllUsers")
    public List<ScsUsers> queryAllUser() {
        List<ScsUsers> result = userService.getAllUsers();
        return result;
    }

    @RequestMapping("/updateUser")
    public int updateUser(String username, String password, int category) {
        int result = userService.updateUser(username, password, category);
        return result;
    }

    @RequestMapping("/deleteUser")
    public int deleteUser(String username) {
        int result = userService.deleteUser(username);
        return result;
    }

    @RequestMapping("/addUser")
    public int addUser(String username, String password, int groupId) {
        int result = userService.addUser(username, password, groupId);
        return result;
    }

    // 曲终人散
    @RequestMapping("/userLogin")
    public Cookie[] userLogin(String username, String password,
                              HttpSession session,
                              HttpServletRequest request, HttpServletResponse response) {

//        session.setAttribute("username", username);
//        session.setAttribute("password", password);

        logger.info("Hello world!");
        // 默认情况下debug不会显示
        logger.info("info日志");
        logger.debug("debug日志");
        logger.error("error日志");
        logger.warn("warn日志");

        Cookie[] cookies = request.getCookies();

        return cookies;
    }

}
