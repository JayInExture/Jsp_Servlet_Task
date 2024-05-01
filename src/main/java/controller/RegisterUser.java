package controller;

import dao.impl.UserDao;
import dao.UserDaoInterface;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Address;
import model.userData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.impl.UserValidationImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet("/register")
public class RegisterUser extends HttpServlet {
    private static final Logger Log = LogManager.getLogger(RegisterUser.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Retrieve user information from the request parameters
        String firstName = req.getParameter("First_name");
        String lastName = req.getParameter("Last_name");
        String email = req.getParameter("email");
        String password = req.getParameter("Password");
        String confirmPassword = req.getParameter("Confirm_Password");
        String userType = req.getParameter("userType"); // Retrieve user type from request parameters
        String dob = req.getParameter("dob");
        String country = req.getParameter("country");
        String[] interestsArray = req.getParameterValues("interests");
        List<String> interestsList = new ArrayList<>();
        if (interestsArray != null) {
            interestsList.addAll(Arrays.asList(interestsArray));
        }

        UserValidationImpl validation = new UserValidationImpl();

        // Perform validation checks
        if (!validation.isValidName(firstName)) {
            resp.sendRedirect("index.jsp?error=invalidFirstName");
            return;
        }
        if (!validation.isValidName(lastName)) {
            resp.sendRedirect("index.jsp?error=invalidLastName");
            return;
        }
        if (!validation.isValidEmail(email)) {
            resp.sendRedirect("index.jsp?error=invalidEmail");
            return;
        }
        if (!validation.isPasswordValid(password)) {
            resp.sendRedirect("index.jsp?error=invalidPassword");
            return;
        }
        if (!validation.doPasswordsMatch(password, confirmPassword)) {
            resp.sendRedirect("index.jsp?error=passwordMismatch");
            return;
        }
        if (!validation.isValidDateOfBirth(dob)) {
            resp.sendRedirect("index.jsp?error=SeletDob");
            return;
        }

        if (!validation.isValidCountry(country)){
            resp.sendRedirect("index.jsp?error=SelectCountry");
            return;
        }
        if (!validation.isValidInterests(interestsList)){
            resp.sendRedirect("index.jsp?error=SelectInterests");
            return;
        }


//        String alphaExp = "^[a-zA-Z ]*$";
//        if (!firstName.matches(alphaExp)) {
//            req.setAttribute("errorMessage", "First name must contain only alphabetic characters.");
//            resp.sendRedirect("index.jsp");
//        Log.info(req.getAttribute("errorMessage"));
////            resp.sendRedirect("index.jsp?errorMessage=First name must contain only alphabetic characters.");
//            return;
//        }
//
//        if (!lastName.matches(alphaExp)) {
//            req.setAttribute("errorMessage", "Last name must contain only alphabetic characters.");
//            resp.sendRedirect("index.jsp");
//            return;
//        }
//
//        if((password.length() <=7)){
//            resp.sendRedirect("index.jsp?error=passwordlengthTosort");
//            return;
//        }
//        // Check if passwords match
//        if (!password.equals(confirmPassword)) {
//            resp.sendRedirect("index.jsp?error=passwordMismatch");
//            return; // Stop further processing
//        }

        List<Address> addresses = new ArrayList<>();

        // Loop through each address index and retrieve its corresponding fields
        String[] streets = req.getParameterValues("street");
        String[] cities = req.getParameterValues("city");
        String[] zips = req.getParameterValues("zip");
        String[] states = req.getParameterValues("state");

        if (!validation.isValidAddress(streets,cities,zips,states)){
            resp.sendRedirect("index.jsp?error=invalidAddressFiled");
            return;
        }

        for (int i = 0; i < streets.length; i++) {
            String street = streets[i];
            String city = cities[i];
            String zip = zips[i];
            String state = states[i];

            // Create Address object and add it to the list
            Address address = new Address(0, street, city, zip, state);
            addresses.add(address);
        }

        // Create a user object and set its properties
        userData user = new userData();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        user.setUserType(userType);// Set user type
        user.setDateOfBirth(dob); // Set date of birth
        user.setCountry(country); // Set country
        user.setInterests(interestsList);
        user.setAddresses(addresses); // Set addresses

        // Save the user only if passwords match
        UserDaoInterface userDao = new UserDao();

        if (userDao.doesEmailExist(email)) {
            // If the email already exists, redirect back to registration page with an error message
            resp.sendRedirect("index.jsp?error=emailExists");
            return; // Stop further processing
        }
        userDao.saveUser(user);
//        userDao.closeConnection();

        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            userData loggedInUser = (userData) session.getAttribute("user");
            if (loggedInUser.getUserType().equals("admin")) {
                // If the logged-in user is an admin, redirect to the dashboard
                List<userData> users = userDao.getAllUsers();
                req.getSession().setAttribute("users", users);
                resp.sendRedirect("Dashboard.jsp");
                return; // Stop further processing
            }
        }


        resp.sendRedirect("Login.jsp");
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
