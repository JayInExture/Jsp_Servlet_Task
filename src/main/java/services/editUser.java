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
        userData user = userDao.upDateInfo(userId, firstName, lastName, addresses);

        // Redirect to a confirmation page or user details page
        List<userData> users = userDao.getAllUsers();
        req.getSession().setAttribute("users", users);
        resp.sendRedirect("Dashboard.jsp?id=" + user.getId());
    }


}


// Retrieve existing addresses from the form
//        String[] existingStreet = req.getParameterValues("existingStreet");
//        String[] existingCity = req.getParameterValues("existingCity");
//        String[] existingZip = req.getParameterValues("existingZip");
//        String[] existingState = req.getParameterValues("existingState");
//
//        if (existingStreet != null && existingCity != null && existingZip != null && existingState != null) {
//            for (int i = 0; i < existingStreet.length; i++) {
//                Address address = new Address();
//                address.setStreet(existingStreet[i]);
//                address.setCity(existingCity[i]);
//                address.setZip(existingZip[i]);
//                address.setState(existingState[i]);
//                addresses.add(address);
//            }
//        }
