package com.petter.web;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author hongxf
 * @since 2017-03-08 9:29
 */
@Controller
public class HelloController {

    @RequestMapping(value = { "/", "/welcome" }, method = RequestMethod.GET)
    public ModelAndView welcomePage() {

        ModelAndView model = new ModelAndView();
        model.addObject("title", "Spring Security Hello World");
        model.addObject("message", "This is welcome page!");
        model.setViewName("hello");
        return model;

    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public ModelAndView adminPage() {

        ModelAndView model = new ModelAndView();
        model.addObject("title", "Spring Security Hello World");
        model.addObject("message", "This is protected page - Admin Page!");
        model.setViewName("admin");

        return model;
    }

    @RequestMapping(value = "/dba", method = RequestMethod.GET)
    public ModelAndView dbaPage() {

        ModelAndView model = new ModelAndView();
        model.addObject("title", "Spring Security Hello World");
        model.addObject("message", "This is protected page - Database Page!");
        model.setViewName("admin");

        return model;
    }

    /**
     * 登录页面只允许使用密码登录
     * 如果用户通过remember me的cookie登录则跳转到登录页面输入密码
     * 为了避免盗用remember me cookie 来更新信息
     */
    @RequestMapping(value = "/admin/update", method = RequestMethod.GET)
    public ModelAndView updatePage(HttpServletRequest request) {

        ModelAndView model = new ModelAndView();

        if (isRememberMeAuthenticated()) {

            //把targetUrl放入session中，登录页面使用${session.targetUrl}获取
            setRememberMeTargetUrlToSession(request);
            //跳转到登录页面
            model.addObject("loginUpdate", true);
            model.setViewName("login");
        } else {
            model.setViewName("update");
        }

        return model;
    }

    /**
     * 判断用户是不是通过remember me方式登录，参考
     * org.springframework.security.authentication.AuthenticationTrustResolverImpl
     */
    private boolean isRememberMeAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && RememberMeAuthenticationToken.class.isAssignableFrom(authentication.getClass());
    }

    /**
     * 保存请求的页面targetUrl到session中
     */
    private void setRememberMeTargetUrlToSession(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if(session != null){
            session.setAttribute("targetUrl", request.getRequestURI());
        }
    }


    //获取session存储的SPRING_SECURITY_LAST_EXCEPTION的值，自定义错误信息
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            HttpServletRequest request) {

        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("error", getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION"));
            //在update的登录页面上，判断targetUrl是否有值，没有则显示记住我，有则不显示
            String targetUrl = getRememberMeTargetUrlFromSession(request);
            System.out.println(targetUrl);
            if(StringUtils.hasText(targetUrl)){
                model.addObject("loginUpdate", true);
            }
        }

        if (logout != null) {
            model.addObject("msg", "你已经成功退出");
        }
        model.setViewName("login");
        return model;
    }

    /**
     * 从session中获取targetUrl
     */
    private String getRememberMeTargetUrlFromSession(HttpServletRequest request){
        String targetUrl = "";
        HttpSession session = request.getSession(false);
        if(session != null){
            targetUrl = session.getAttribute("targetUrl") == null ? "" :session.getAttribute("targetUrl").toString();
        }
        return targetUrl;
    }

    //自定义错误类型
    private String getErrorMessage(HttpServletRequest request, String key){

        Exception exception = (Exception) request.getSession().getAttribute(key);

        String error;
        if (exception instanceof BadCredentialsException) {
            error = "不正确的用户名或密码";
        }else if(exception instanceof LockedException) {
            error = exception.getMessage();
        }else{
            error = "不正确的用户名或密码";
        }

        return error;
    }

    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public ModelAndView accessDenied() {

        ModelAndView model = new ModelAndView();

        //检查用户是否已经登录
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetail = (UserDetails) auth.getPrincipal();
            model.addObject("username", userDetail.getUsername());
        }

        model.setViewName("403");
        return model;

    }
}
