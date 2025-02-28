package com.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/pages/*")
public class AuthFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    final HttpServletRequest httpRequest = (HttpServletRequest) request;
    final HttpServletResponse httpResponse = (HttpServletResponse) response;
    final HttpSession session = httpRequest.getSession(false);

    if (session == null || session.getAttribute("username") == null) {
      httpResponse.sendRedirect(httpRequest.getContextPath() + "/index.jsp?error=notAuthenticated");
      return;
    }

    chain.doFilter(request, response);

  }

  @Override
  public void destroy() {
  }
}
