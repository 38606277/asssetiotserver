package root.configure;

import com.alibaba.druid.support.http.StatViewServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DruidManagerController {

//    @Bean
//    public FilterRegistrationBean getFilterRegistrationBean(){
//        FilterRegistrationBean filter = new FilterRegistrationBean();
//        filter.setFilter(new WebStatFilter());
//        filter.setName("druidWebStatFilter");
//        filter.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*");
//        filter.addUrlPatterns("/*");
//        return filter;
//    }

//    @Bean    //监控
//    public FilterRegistrationBean filterRegistrationBean(){
//        FilterRegistrationBean filterRegistrationBean=new FilterRegistrationBean();
////        filterRegistrationBean.setFilter(new WebStatFilter());
////        filterRegistrationBean.addUrlPatterns("/*");//所有请求进行监控处理
////        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.css,/druid/*");//排除
//        return filterRegistrationBean;
//    }

    //just use database user for easy
    @Value("${durid.username}")
    private String loginUsername;
    @Value("${durid.password}")
    private String loginPassword;

    @Bean
    public ServletRegistrationBean getServletRegistrationBean(){
        ServletRegistrationBean servlet = new ServletRegistrationBean(new StatViewServlet(){

            @Override
            public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                String contextPath = request.getContextPath();
                String servletPath = request.getServletPath();
                String requestURI = request.getRequestURI();

                response.setCharacterEncoding("utf-8");

                if (contextPath == null) { // root context
                    contextPath = "";
                }
                String uri = contextPath + servletPath;
                String path = requestURI.substring(contextPath.length() + servletPath.length());

                if (!isPermittedRequest(request)) {
                    path = "/nopermit.html";
                    returnResourceFile(path, uri, response);
                    return;
                }

                if ("/submitLogin".equals(path)) {
                    String usernameParam = request.getParameter(PARAM_NAME_USERNAME);
                    String passwordParam = request.getParameter(PARAM_NAME_PASSWORD);

                    if(usernameParam ==null  && passwordParam ==null){
                        if("POST".equals(request.getMethod())){

                            BufferedReader reader = request.getReader();
                            StringBuilder builder = new StringBuilder();
                            String line = reader.readLine();
                            while(line != null){
                                builder.append(line);
                                line = reader.readLine();
                            }
                            reader.close();

                            String reqBody = builder.toString();
                            String[] params = reqBody.split("&");
                            Map<String,String> paramsMap = new HashMap<>();
                            for(String p : params){
                                String[] map = p.split("=");
                                paramsMap.put(map[0],map[1]);
                            }
                            usernameParam = paramsMap.get(PARAM_NAME_USERNAME);
                            passwordParam = paramsMap.get(PARAM_NAME_PASSWORD);
                        }
                    }

                    if (username.equals(usernameParam) && password.equals(passwordParam)) {
                        request.getSession().setAttribute(SESSION_USER_KEY, username);
                        response.getWriter().print("success");
                    } else {
                        response.getWriter().print("error");
                    }
                    return;
                }

                if (isRequireAuth() //
                        && !ContainsUser(request)//
                        && !("/login.html".equals(path) //
                        || path.startsWith("/css")//
                        || path.startsWith("/js") //
                        || path.startsWith("/img"))) {
                    if (contextPath.equals("") || contextPath.equals("/")) {
                        response.sendRedirect("/druid/login.html");
                    } else {
                        if ("".equals(path)) {
                            response.sendRedirect("druid/login.html");
                        } else {
                            response.sendRedirect("login.html");
                        }
                    }
                    return;
                }

                if ("".equals(path)) {
                    if (contextPath.equals("") || contextPath.equals("/")) {
                        response.sendRedirect("/druid/index.html");
                    } else {
                        response.sendRedirect("druid/index.html");
                    }
                    return;
                }

                if ("/".equals(path)) {
                    response.sendRedirect("index.html");
                    return;
                }

                if (path.contains(".json")) {
                    String fullUrl = path;
                    if (request.getQueryString() != null && request.getQueryString().length() > 0) {
                        fullUrl += "?" + request.getQueryString();
                    }
                    response.getWriter().print(process(fullUrl));
                    return;
                }

                // find file in resources path
                returnResourceFile(path, uri, response);
            }
        },"/druid/*");
        servlet.setName("druidStatViewServlet");
        //servlet.addInitParameter("resetEnable", "false");
        servlet.addInitParameter("loginUsername",loginUsername);
        servlet.addInitParameter("loginPassword",loginPassword);

        return servlet;
    }


}
