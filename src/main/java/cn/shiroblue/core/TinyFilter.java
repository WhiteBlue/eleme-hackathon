package cn.shiroblue.core;

import cn.shiroblue.TinyApplication;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Description:
 * <p>
 * ======================
 * by WhiteBlue
 * on 15/10/25
 */
public class TinyFilter implements Filter {
    // web.xml 配置参数
    private static final String APPLICATION_CLASS_PARAM = "applicationClass";

    private TinyApplication tinyApplication;

    private TinyHandler tinyHandler;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.tinyApplication = getApplication(filterConfig);
        this.tinyHandler = TinyHandler.get();
        this.tinyApplication.init();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        this.handle(servletRequest, servletResponse, filterChain);
    }

    @Override
    public void destroy() {
        this.tinyApplication.destroy();
    }


    private TinyApplication getApplication(FilterConfig filterConfig) throws ServletException {
        try {
            String applicationClassName = filterConfig.getInitParameter(APPLICATION_CLASS_PARAM);

            System.out.println("Server : launch a Server with TinyService -> FastService");

            Class<?> applicationClass = Class.forName(applicationClassName);
            return (TinyApplication) applicationClass.newInstance();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }


    private void handle(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        tinyHandler.handle(httpRequest, httpResponse);
    }


}
