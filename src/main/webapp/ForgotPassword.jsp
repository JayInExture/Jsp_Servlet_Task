<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="./assets/css/bootstrap.min.css" />
<link rel="stylesheet" href="./assets/css/Forgot_Password.css" />
<html>
<head>
    <title>Forgot Password</title>
</head>
<body>
<jsp:include page="Header.jsp"></jsp:include>
<section class="forgot">
    <h2>Forgot Password</h2>
    <form action="forgotPassword" method="post">
        <div>
            <label>Email:</label>
            <input type="email" name="email" required>
        </div>
        <div>
            <label>New Password:</label>
            <input type="password" name="newPassword" required>
        </div>

        <div>
            <button class="reset_btn" type="submit">Reset Password</button>
        </div>
    </form>
            <a href="Login.jsp">Login</a>
            ||
            <a href="index.jsp">Register</a>

    </section>
   <jsp:include page="Footer.jsp"></jsp:include>
</body>
</html>
