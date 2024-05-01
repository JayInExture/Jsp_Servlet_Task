<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="./assets/css/Login.css" />
<html>
<head>
    <title>Login</title>
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate" />
    <meta http-equiv="Pragma" content="no-cache" />
    <meta http-equiv="Expires" content="0" />
</head>
<body>
<jsp:include page="Header.jsp"></jsp:include>
<section class="login_back">
<h3>Login</h3>
    <form action="login" method="post">
        <label for="email">Email:</label>
        <input type="email" id="email" name="email" autocomplete="off" required><br>
        <label for="password">Password:</label>
        <input type="password" id="password" name="password" autocomplete="off" required><br>


        <button class="login_btn" type="submit">Login</button>
    </form>
        <a href="ForgotPassword.jsp">Forgot Password</a>
        ||
        <a href="index.jsp">Register</a>
        </section>

<jsp:include page="Footer.jsp"></jsp:include>
</body>
</html>
