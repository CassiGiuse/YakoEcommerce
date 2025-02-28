<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="it" data-bs-theme="dark">
  <head>
    <%@ include file="../src/templates/headContent.jsp" %>
  </head>

  <body>
    <%@ include file="../src/templates/components/header.jsp" %>

    <main
      class="d-flex justify-content-center align-items-center min-vh-100 p-2"
    >
      <%
      String session_u_name = (String)session.getAttribute("username");
      out.print("Hi "+session_u_name);
      %>
    </main>

    <%@ include file="../src/templates/footerAndScripts.jsp" %>
  </body>
</html>
