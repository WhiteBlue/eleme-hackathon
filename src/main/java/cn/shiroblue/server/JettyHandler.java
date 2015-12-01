package cn.shiroblue.server;

import cn.shiroblue.core.TinyHandler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Description:
 * <p>
 * ======================
 * by WhiteBlue
 * on 15/11/8
 */
public class JettyHandler extends AbstractHandler {
    private TinyHandler tinyHandler;

    public JettyHandler(TinyHandler tinyHandler) {
        this.tinyHandler = tinyHandler;
    }

    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        tinyHandler.handle(httpServletRequest, httpServletResponse);

        request.setHandled(true);
    }
}
