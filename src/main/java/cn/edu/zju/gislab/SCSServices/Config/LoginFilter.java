package cn.edu.zju.gislab.SCSServices.Config;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = "/*", filterName = "LoginFilter")
public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession();

        // 暂时不过滤，意图：用于用户登录验证
//        String uri = req.getRequestURI();
//        boolean isLoginReq = uri.matches("/user/.*\\.do");
//
//        boolean isLogin = session.getAttribute("isLogin") != null
//                && (boolean) session.getAttribute("isLogin");
//        if (isLoginReq || isLogin) {
//            chain.doFilter(request, response);
//            return;
//        }
//        //跳转至登录页面
//        res.sendRedirect("/user/login.do");

        chain.doFilter(request, response);
    }
}