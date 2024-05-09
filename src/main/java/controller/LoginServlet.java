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

                // Encode only the images of the logged-in user to base64
                List<byte[]> userImages = user.getImages();
                List<String> base64Images = new ArrayList<>();
                for (byte[] imageData : userImages) {
                    String base64Image = Base64.getEncoder().encodeToString(imageData);
                    base64Images.add(base64Image);
                }
                request.setAttribute("base64Images", base64Images);

                // Check if the logged-in user is an admin
                if (user.getUserType().equals("admin")) {
                    List<userData> users = userDao.getAllUsers();
                    request.setAttribute("users", users);
                }
//                response.sendRedirect("Dashboard.jsp");
                request.getRequestDispatcher("Dashboard.jsp").forward(request, response);
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
