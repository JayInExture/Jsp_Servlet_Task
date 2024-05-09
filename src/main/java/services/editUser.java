package services;
import dao.UserDaoInterface;
import dao.impl.UserDao;
import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.*;
import org.apache.logging.log4j.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@WebServlet("/editUser")
@MultipartConfig(
        maxFileSize = 20848820, // 20 MB in bytes
        maxRequestSize = 418018841, // 400 MB in bytes
        fileSizeThreshold = 1048576 // 1 MB in bytes
)
public class editUser extends HttpServlet {
    private static final Logger log = LogManager.getLogger(editUser.class);
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("id"));
        UserDaoInterface userDao = new UserDao();
        userData user = userDao.getUserById(userId);
        List<Address> addresses = userDao.getAllAddressesByUserId(userId); // Fetch all addresses associated with the user
        user.setAddresses(addresses); // Set all addresses to the user

        List<UserImage> userImages = userDao.getImagesByUserId(userId); // Using the modified method
        List<String> base64Images = new ArrayList<>(); // Change the list type to String
        for (UserImage userImage : userImages) {
            byte[] imageBytes = userImage.getImageData();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            base64Images.add(base64Image); // Add the base64 encoded image directly
        }
        user.setUserImages(userImages);
        user.setBase64Images(base64Images);

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
        for (Part part : req.getParts()) {
            log.info("Part name: " + part.getName());
        }

        List<Part> imageParts = req.getParts().stream()
                .filter(part -> "images_new[]".equals(part.getName()) && part.getSize() > 0)
                .toList();
        List<byte[]> images = new ArrayList<>();
        for (Part part : imageParts) {
            if (part.getSize() > 0) {
                try {
                    String base64Image = new String(part.getInputStream().readAllBytes());
                    if (base64Image != null && !base64Image.isEmpty()) { // Add null check
                        // Decode the Base64 string to byte array
                        byte[] imageBytes = Base64.getDecoder().decode(base64Image.split(",")[1]);
                        images.add(imageBytes);
                    }
                } catch (IOException e) {
                    log.error("Error processing image: " + e.getMessage());
                }
            }
        }


        log.info("Parts"+imageParts.size());
        List<String> removedImageIds;
        String removedImageIdsString = req.getParameter("removedImageIds");
        if (removedImageIdsString != null) {
            removedImageIds = Arrays.asList(removedImageIdsString.split(","));
        } else {
            removedImageIds = new ArrayList<>(); // Initialize an empty list if no image IDs were removed
        }

// Convert the list of removed image IDs to a string
        String removedImageIdsStringFormatted = String.join(",", removedImageIds);

// Log the string representation of removed image IDs
        log.info("Removed image IDs: " + removedImageIdsStringFormatted);

//        List<Part> fileParts = req.getParts().stream().filter(part -> "images_new[]".equals(part.getName())).collect(Collectors.toList());
//        List<byte[]> images = new ArrayList<>();
//        for (Part filePart : fileParts) {
//            byte[] imageData = filePart.getInputStream().readAllBytes();
//            images.add(imageData);
//        }

        UserDaoInterface userDao = new UserDao();
        userData user = userDao.upDateInfo(userId, firstName, lastName, addresses,DateOfBirth,country,interests,images,removedImageIds);
        HttpSession session = req.getSession(false);
//        log.error(session.getAttribute("user"));
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



//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        int userId = Integer.parseInt(request.getParameter("id"));
//        UserDaoInterface userDao = new UserDao();
//        userData user = userDao.getUserById(userId);
//        List<Address> addresses = userDao.getAllAddressesByUserId(userId); // Fetch all addresses associated with the user
//        user.setAddresses(addresses); // Set all addresses to the user
//        request.setAttribute("user", user);
//        List<UserImage> userImages = userDao.getImagesByUserId(userId); // Using the modified method
//        request.setAttribute("userImages", userImages); // Set user images attribute
//
//        List<String> base64Images = new ArrayList<>(); // Change the list type to String
//        for (UserImage userImage : userImages) {
//            byte[] imageBytes = userImage.getImageData();
//            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
//            base64Images.add(base64Image); // Add the base64 encoded image directly
//        }
//        request.setAttribute("base64Images", base64Images);
//        request.getRequestDispatcher("EditUser.jsp").forward(request, response);
//    }