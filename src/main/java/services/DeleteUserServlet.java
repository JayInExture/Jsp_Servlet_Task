package services;

import dao.UserDaoInterface;
import dao.impl.UserDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.userData;

import java.io.IOException;
import java.util.List;

@WebServlet("/deleteUser")
public class DeleteUserServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("id"));

        // Call DAO method to delete user and associated addresses
        UserDaoInterface userDao = new UserDao();
        boolean deleted = userDao.deleteUser(userId);

        if (deleted) {
            // Refresh the list of users in the session
            List<userData> users = userDao.getAllUsers();
            request.getSession().setAttribute("users", users);

            // Redirect to the dashboard
            response.sendRedirect("Dashboard.jsp");
        }  else {
            // Redirect to some error page or display an error message

        }
    }
}
