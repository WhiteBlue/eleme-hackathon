package cn.shiroblue.http;

import cn.shiroblue.route.RouteMatch;
import cn.shiroblue.utils.TinyUtils;
import cn.shiroblue.utils.UrlUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

/**
 * Description:
 * 从Spark抄来的Request封装
 * ======================
 * by WhiteBlue
 * on 15/10/29
 */
public class Request {

    private static final String USER_AGENT = "user-agent";

    private Map<String, String> params;

    public HttpServletRequest servletRequest;

    /* Lazy loaded stuff */
    private String body = null;
    private byte[] bodyAsBytes = null;

    private Set<String> headers = null;

    public Request(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    public Request(RouteMatch match, HttpServletRequest request) {
        this.servletRequest = request;
        changeMatch(match);
    }

    private static Map<String, String> getParams(List<String> request, List<String> matched) {
        Map<String, String> params = new HashMap<>();

        for (int i = 0; (i < request.size()) && (i < matched.size()); i++) {
            String matchedPart = matched.get(i);
            if (TinyUtils.isParam(matchedPart)) {
                params.put(matchedPart.toLowerCase(), request.get(i));
            }
        }
        return params;
    }

    /**
     * 更改匹配路径
     *
     * @param match RouteMatch
     */
    private void changeMatch(RouteMatch match) {
        //接受路径和匹配路径转为数组
        List<String> requestList = UrlUtils.convertRouteToList(match.getUrl());
        List<String> matchedList = UrlUtils.convertRouteToList(match.getMatchPath());

        params = getParams(requestList, matchedList);
    }


    public void bind(RouteMatch match) {
        this.clearParam();
        this.changeMatch(match);
    }

    /**
     * 得到路径参数的Map
     *
     * @return a map containing all route params
     */
    public Map<String, String> pathParams() {
        return Collections.unmodifiableMap(params);
    }

    /**
     * 取得路径参数(空返回null)
     * Example: parameter 'name' from the following pattern: (get '/hello/:name')
     *
     * @param param the param
     * @return null if the given param is null or not found
     */
    public String pathParam(String param) {
        if (param.startsWith(":")) {
            return params.get(param.toLowerCase()); // NOSONAR
        } else {
            return params.get(":" + param.toLowerCase()); // NOSONAR
        }
    }

    /**
     * 请求方法
     *
     * @return request method e.g. GET, POST, PUT, ...
     */
    public String requestMethod() {
        return servletRequest.getMethod();
    }

    /**
     * @return the scheme
     */
    public String scheme() {
        return servletRequest.getScheme();
    }

    /**
     * @return the host
     */
    public String host() {
        return servletRequest.getHeader("host");
    }

    /**
     * @return the user-agent
     */
    public String userAgent() {
        return servletRequest.getHeader(USER_AGENT);
    }

    /**
     * @return the server port
     */
    public int port() {
        return servletRequest.getServerPort();
    }

    /**
     * @return the path info
     * Example return: "/example/foo"
     */
    public String pathInfo() {
        return servletRequest.getPathInfo();
    }

    /**
     * @return the servlet path
     */
    public String servletPath() {
        return servletRequest.getServletPath();
    }

    /**
     * @return the context path
     */
    public String contextPath() {
        return servletRequest.getContextPath();
    }

    /**
     * @return the URL string
     */
    public String url() {
        return servletRequest.getRequestURL().toString();
    }

    /**
     * @return the content type of the body
     */
    public String contentType() {
        return servletRequest.getContentType();
    }

    /**
     * @return the client's IP address
     */
    public String ip() {
        return servletRequest.getRemoteAddr();
    }

    /**
     * @return the request body sent by the client
     */
    public String body() {
        if (body == null) {
            body = new String(bodyAsBytes());
        }
        return body;
    }

    public byte[] bodyAsBytes() {
        if (bodyAsBytes == null) {
            readBodyAsBytes();
        }
        return bodyAsBytes;
    }

    private void readBodyAsBytes() {
        try {
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            InputStream inputStream = this.servletRequest.getInputStream();
            while (-1 != inputStream.read(buffer)) {
                byteArrayOutputStream.write(buffer);
            }
            bodyAsBytes = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return 请求本体length
     */
    public int contentLength() {
        return servletRequest.getContentLength();
    }

    /**
     * 请求参数
     *
     * @param queryParam the query parameter
     * @return the value of the provided queryParam
     * Example: query parameter 'id' from the following request URI: /hello?id=foo
     */
    public String queryParam(String queryParam) {
        return servletRequest.getParameter(queryParam);
    }

    /**
     * 得到某一相同参数的所有值
     * Example: query parameter 'id' from the following request URI: /hello?id=foo&id=bar
     *
     * @param queryParam the query parameter
     * @return the values of the provided queryParam, null if it doesn't exists
     */
    public String[] queryParams(String queryParam) {
        return servletRequest.getParameterValues(queryParam);
    }

    /**
     * 取得header值
     *
     * @param header the header
     * @return the value of the provided header
     */
    public String headers(String header) {
        return servletRequest.getHeader(header);
    }

    /**
     * 得到所有http参数键值
     *
     * @return all query parameters
     */
    public Set<String> queryParams() {
        return servletRequest.getParameterMap().keySet();
    }

    /**
     * 返回所有header名
     *
     * @return all headers
     */
    public Set<String> headers() {
        if (headers == null) {
            headers = new TreeSet<>();
            Enumeration<String> enumeration = servletRequest.getHeaderNames();
            while (enumeration.hasMoreElements()) {
                headers.add(enumeration.nextElement());
            }
        }
        return headers;
    }

    /**
     * @return the query string
     */
    public String queryString() {
        return servletRequest.getQueryString();
    }

    /**
     * Sets an attribute on the request (can be fetched in filters/routes later in the chain)
     *
     * @param attribute The attribute
     * @param value     The attribute value
     */
    public void attribute(String attribute, Object value) {
        servletRequest.setAttribute(attribute, value);
    }

    /**
     * Gets the value of the provided attribute
     *
     * @param attribute The attribute value or null if not present
     * @param <T>       the type parameter.
     * @return the value for the provided attribute
     */
    public <T> T attribute(String attribute) {
        return (T) servletRequest.getAttribute(attribute);
    }

    /**
     * @return all attributes
     */
    public Set<String> attributes() {
        Set<String> attrList = new HashSet<String>();
        Enumeration<String> attributes = (Enumeration<String>) servletRequest.getAttributeNames();
        while (attributes.hasMoreElements()) {
            attrList.add(attributes.nextElement());
        }
        return attrList;
    }

    /**
     * @return the raw HttpServletRequest object handed in by Jetty
     */
    public HttpServletRequest raw() {
        return servletRequest;
    }


    /**
     * @return the part of this request's URL from the protocol name up to the query string in the first line of the HTTP request.
     */
    public String uri() {
        return servletRequest.getRequestURI();
    }

    /**
     * @return Returns the name and version of the protocol the request uses
     */
    public String protocol() {
        return servletRequest.getProtocol();
    }


    public void clearParam() {
        if (this.params != null) {
            this.params.clear();
        }
    }


}

