package controller;

import dao.UserDaoInterface;
import dao.impl.UserDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.userData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.*;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(LoginServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            log.info("Invalidating existing session.");
            session.invalidate();
        }
        try {
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            UserDaoInterface userDao = new UserDao();
            userData user = userDao.getUserByEmailAndPassword(request.getSession(), email, password);

            if (user != null) {
                log.info("User logged in successfully: {}", user.getEmail());
                // Add this line to log the user object
                log.info("User details: {}", user);
                if (user.getUserType().equals("admin")) {
                    List<userData> users = userDao.getAllUsers();
                    request.setAttribute("users", users);
                }
                // Forward the request to Dashboard.jsp
                response.sendRedirect("Dashboard.jsp");
//                request.getRequestDispatcher("Dashboard.jsp").forward(request, response);
            }
            else {
                log.info("Invalid credentials for email: {}", email);
                response.sendRedirect("Login.jsp?error=invalidCredentials");
            }
        } finally {
            // No need to invalidate session here
        }
    }
}
