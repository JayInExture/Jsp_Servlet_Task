package controller;

import dao.UserDaoInterface;
import dao.impl.UserDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@WebServlet("/forgotPassword")
public class ForgotPasswordServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(ForgotPasswordServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String newPassword = request.getParameter("newPassword");
//        String userType = request.getParameter("userType");

        UserDaoInterface userDao = new UserDao();
        boolean passwordUpdated = userDao.updatePasswordByEmailAndType(email, newPassword);

        if (passwordUpdated) {
            log.info("Password updated successfully for email: {}", email);
            response.sendRedirect("Login.jsp?passwordResetSuccess=true");
        } else {
            log.info("Failed to update password for email: {}", email);
            response.sendRedirect("ForgotPassword.jsp?error=passwordResetFailed");
        }
    }
}
