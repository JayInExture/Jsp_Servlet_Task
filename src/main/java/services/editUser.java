package services;
import dao.UserDaoInterface;
import dao.impl.UserDao;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.*;
import org.apache.logging.log4j.*;
import java.io.IOException;
import java.util.*;


@WebServlet("/editUser")
public class editUser extends HttpServlet {
    private static final Logger log = LogManager.getLogger(editUser.class);


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("id"));
        UserDaoInterface userDao = new UserDao();
        userData user = userDao.getUserById(userId);
        List<Address> addresses = userDao.getAllAddressesByUserId(userId); // Fetch all addresses associated with the user
        user.setAddresses(addresses); // Set all addresses to the user
        request.setAttribute("user", user);
        request.getRequestDispatcher("EditUser.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String firstName = req.getParameter("First_name");
        String lastName = req.getParameter("Last_name");
        String DateOfBirth = req.getParameter("dateOfBirth");
        String country = req.getParameter("country");
        String[] interestsArray = req.getParameterValues("interests");
        // Convert interests array to a list
        List<String> interests = interestsArray != null ? Arrays.asList(interestsArray) : new ArrayList<>();
        int userId = Integer.parseInt(req.getParameter("id"));
        List<Address> addresses = new ArrayList<>();

        // Retrieve new addresses from the form
        String[] addressIds = req.getParameterValues("addressId");
        String[] newStreet = req.getParameterValues("street");
        String[] newCity = req.getParameterValues("city");
        String[] newZip = req.getParameterValues("zip");
        String[] newState = req.getParameterValues("state");

        // Update or add new addresses
        if (newStreet != null && newCity != null && newZip != null && newState != null) {
            for (int i = 0; i < newStreet.length; i++) {
                Address address = new Address();
                if (addressIds != null && i < addressIds.length && !addressIds[i].isEmpty()) {
                    // Update existing address
                    address.setId(Integer.parseInt(addressIds[i]));
                }
                address.setStreet(newStreet[i]);
                address.setCity(newCity[i]);
                address.setZip(newZip[i]);
                address.setState(newState[i]);
                addresses.add(address);
            }
        }

        UserDaoInterface userDao = new UserDao();
        userData user = userDao.upDateInfo(userId, firstName, lastName, addresses,DateOfBirth,country,interests);
        HttpSession session = req.getSession(false);
        log.error(session.getAttribute("user"));
        if (session != null) {
            userData loggedInUser = (userData) session.getAttribute("user");
            if (loggedInUser.getUserType().equals("admin")){
                List<userData> users = userDao.getAllUsers();
                session.setAttribute("users", users);
            }else {
            userData updatedUser = userDao.getUserById(userId); // Fetch the updated user from the database
            session.setAttribute("user", updatedUser); }// Update the user object in the session
        }
        resp.sendRedirect("Dashboard.jsp?id=" + user.getId());
    }


}

