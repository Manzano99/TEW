package es.tew.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(
        dispatcherTypes = {DispatcherType.REQUEST},
        urlPatterns = {"/restricted/*"},
        initParams = {
                @WebInitParam(name = "LoginPath", value = "/login.xhtml")
        }
)
public class LoginFilter implements Filter {

    private FilterConfig config = null;

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
        config = fConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession();

        if (session.getAttribute("LOGGEDIN_USER") == null) {
            String loginForm = config.getInitParameter("LoginPath");
            httpResponse.sendRedirect(httpRequest.getContextPath() + loginForm);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
