package dao.impl;

import connection.DatabaseConnection;
import dao.UserDaoInterface;
import jakarta.servlet.http.HttpSession;
import model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;


public class UserDao implements UserDaoInterface {
    private static final Logger log = LogManager.getLogger(UserDao.class);

    private Connection connection;
    private static final Logger Log = LogManager.getLogger(UserDao.class);
    public UserDao() {
        DatabaseConnection D = DatabaseConnection.getInstance();
        connection = D.getConnection();
    }
    public void saveUser(userData user) {
        try {
            String userQuery = "INSERT INTO Users (first_name, last_name, email, password, user_type, date_of_birth, country, interests) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement userStatement = connection.prepareStatement(userQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                userStatement.setString(1, user.getFirstName());
                userStatement.setString(2, user.getLastName());
                userStatement.setString(3, user.getEmail());
                userStatement.setString(4, user.getPassword());
                userStatement.setString(5, user.getUserType());
                userStatement.setString(6, user.getDateOfBirth()); // Insert date of birth
                userStatement.setString(7, user.getCountry()); // Insert country
                String interestsString = String.join(",", user.getInterests());
                userStatement.setString(8, interestsString); // Insert interests
                userStatement.executeUpdate();

                // Retrieve the auto-generated user ID
                int userId;
                try (ResultSet generatedKeys = userStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }

                // Insert addresses into the Addresses table
                String addressQuery = "INSERT INTO Addresses (user_id, street, city, zip, state) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement addressStatement = connection.prepareStatement(addressQuery)) {
                    for (Address address : user.getAddresses()) {
                        addressStatement.setInt(1, userId);
                        addressStatement.setString(2, address.getStreet());
                        addressStatement.setString(3, address.getCity());
                        addressStatement.setString(4, address.getZip());
                        addressStatement.setString(5, address.getState());
                        addressStatement.addBatch();
                    }
                    addressStatement.executeBatch();
                }

                // Insert images into the user_images table
                String imageQuery = "INSERT INTO user_images (user_id, image) VALUES (?, ?)";
                try (PreparedStatement imageStatement = connection.prepareStatement(imageQuery)) {
                    for (byte[] image : user.getImages()) {
                        imageStatement.setInt(1, userId);
                        imageStatement.setBytes(2, image);
                        imageStatement.addBatch();
                    }
                    int[] result = imageStatement.executeBatch();
                    log.info("Number of images inserted: " + Arrays.stream(result).sum()); // Log the number of images inserted
                }
            }
        } catch (SQLException e) {
          log.error(e);
            // Handle exception
        }
    }



//    public void saveUser(userData user) {
//        try {
//            String userQuery = "INSERT INTO Users (first_name, last_name, email, password, user_type, date_of_birth, country, interests) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
//            try (PreparedStatement userStatement = connection.prepareStatement(userQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
//                userStatement.setString(1, user.getFirstName());
//                userStatement.setString(2, user.getLastName());
//                userStatement.setString(3, user.getEmail());
//                userStatement.setString(4, user.getPassword());
//                userStatement.setString(5, user.getUserType());
//                userStatement.setString(6, user.getDateOfBirth()); // Insert date of birth
//                userStatement.setString(7, user.getCountry()); // Insert country
//                String interestsString = String.join(",", user.getInterests());
//                userStatement.setString(8, interestsString); // Insert interests
//                userStatement.executeUpdate();
//
//                // Retrieve the auto-generated user ID
//                int userId;
//                try (ResultSet generatedKeys = userStatement.getGeneratedKeys()) {
//                    if (generatedKeys.next()) {
//                        userId = generatedKeys.getInt(1);
//                    } else {
//                        throw new SQLException("Creating user failed, no ID obtained.");
//                    }
//                }
//
//                // Insert addresses into the Addresses table
//                String addressQuery = "INSERT INTO Addresses (user_id, street, city, zip, state) VALUES (?, ?, ?, ?, ?)";
//                try (PreparedStatement addressStatement = connection.prepareStatement(addressQuery)) {
//                    for (Address address : user.getAddresses()) {
//                        addressStatement.setInt(1, userId);
//                        addressStatement.setString(2, address.getStreet());
//                        addressStatement.setString(3, address.getCity());
//                        addressStatement.setString(4, address.getZip());
//                        addressStatement.setString(5, address.getState());
//                        addressStatement.addBatch();
//                    }
//                    addressStatement.executeBatch();
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            // Handle exception
//        }
//    }

    public userData getUserByEmailAndPassword(HttpSession session, String email, String password) {
        try {
            String query = "SELECT u.id, u.first_name, u.last_name, u.email, u.password, u.user_type, u.date_of_birth, u.country, " +
                    "a.id AS address_id, a.street, a.city, a.zip, a.state " +
                    "FROM Users u " +
                    "LEFT JOIN Addresses a ON u.id = a.user_id " +
                    "WHERE u.email=? AND u.password=?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, email);
                statement.setString(2, password);
                try (ResultSet resultSet = statement.executeQuery()) {
                    userData user = null;
                    List<Address> addresses = new ArrayList<>();
                    List<byte[]> images = new ArrayList<>();  // List to store image data

                    while (resultSet.next()) {
                        if (user == null) {
                            int id = resultSet.getInt("id");
                            String firstName = resultSet.getString("first_name");
                            String lastName = resultSet.getString("last_name");
                            String fetchedEmail = resultSet.getString("email");
                            String fetchedPassword = resultSet.getString("password");
                            String fetchedDob = resultSet.getString("date_of_birth");
                            String fetchedcountry = resultSet.getString("country");
                            String fetchedUserType = resultSet.getString("user_type");
                            user = new userData(id, firstName, lastName, fetchedEmail, fetchedPassword, fetchedDob, fetchedcountry, fetchedUserType);
                        }
                        int addressId = resultSet.getInt("address_id"); // Fetch address ID
                        String street = resultSet.getString("street");
                        String city = resultSet.getString("city");
                        String zip = resultSet.getString("zip");
                        String state = resultSet.getString("state");

                        Address address = new Address();
                        address.setAddressId(addressId); // Set the addressId
                        address.setStreet(street);
                        address.setCity(city);
                        address.setZip(zip);
                        address.setState(state);

                        addresses.add(address);
                    }

                    if (user != null) {
                        // Fetch images separately
                        String imageQuery = "SELECT image FROM user_images WHERE user_id=?";
                        try (PreparedStatement imageStatement = connection.prepareStatement(imageQuery)) {
                            imageStatement.setInt(1, user.getId());
                            try (ResultSet imageResultSet = imageStatement.executeQuery()) {
                                while (imageResultSet.next()) {
                                    byte[] imageData = imageResultSet.getBytes("image");
                                    images.add(imageData);  // Add the byte array directly to the list
                                }
                            }
                        }

                        user.setAddresses(addresses);
                        user.setImages(images);  // Set the list of images in the userData object
                        session.setAttribute("user", user); // Set user in session
                    }
                    return user;
                }
            }
        } catch (SQLException e) {
            log.error("An error occurred while fetching user by email and password", e);
        }
        return null;
    }




    public boolean updatePasswordByEmailAndType(String email, String newPassword) {
        try {
            // Prepare SQL statement
            String sql = "UPDATE users SET password = ? WHERE email = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, newPassword);
                stmt.setString(2, email);
//                stmt.setString(3, userType);

                // Execute update
                int rowsAffected = stmt.executeUpdate();
                Log.info("Password Updated!!");

                // Check if password is updated successfully
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQL exception
            return false;
        }
    }
    public List<userData> getAllUsers() {
        List<userData> users = new ArrayList<>();
        String query = "SELECT u.id, u.first_name, u.last_name, u.email, u.password, u.user_type, " +
                "u.date_of_birth, u.country, " +
                "a.id as address_id, a.street, a.city, a.zip, a.state " +
                "FROM Users u LEFT JOIN Addresses a ON u.id = a.user_id";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            // Map to store user data and their addresses temporarily
            Map<Integer, userData> userMap = new HashMap<>();

            while (resultSet.next()) {
                int userId = resultSet.getInt("id");

                // Check if the user already exists in the map
                userData user = userMap.get(userId);
                if (user == null) {
                    // Create a new user if not found
                    user = new userData();
                    user.setId(userId);
                    user.setFirstName(resultSet.getString("first_name"));
                    user.setLastName(resultSet.getString("last_name"));
                    user.setEmail(resultSet.getString("email"));
                    user.setPassword(resultSet.getString("password"));
                    user.setUserType(resultSet.getString("user_type"));
                    // Retrieve additional fields
                    user.setDateOfBirth(String.valueOf(resultSet.getDate("date_of_birth")));
                    user.setCountry(resultSet.getString("country"));
//                    user.setInterests(resultSet.getString("interests"));

                    // Initialize addresses list
                    user.setAddresses(new ArrayList<>());

                    // Add user to the map
                    userMap.put(userId, user);
                }

                // Add address if available
                int addressId = resultSet.getInt("address_id");
                if (addressId != 0) {
                    Address address = new Address();
                    address.setId(addressId);
                    address.setStreet(resultSet.getString("street"));
                    address.setCity(resultSet.getString("city"));
                    address.setZip(resultSet.getString("zip"));
                    address.setState(resultSet.getString("state"));

                    // Add address to the user's addresses list
                    user.getAddresses().add(address);
                }
            }

            // Add all users from the map to the list
            users.addAll(userMap.values());

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception
        }

        return users;
    }


    public userData getUserById(int userId) {
        userData user = null;

        try {
            String query = "SELECT u.id, u.first_name, u.last_name, u.email, u.password, u.user_type, " +
                    "u.date_of_birth, u.country,u.interests, " +
                    "a.id as address_id, a.street, a.city, a.zip, a.state " +
                    "FROM Users u LEFT JOIN Addresses a ON u.id = a.user_id " +
                    "WHERE u.id=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            List<Address> addresses = new ArrayList<>();
            while (resultSet.next()) {
                if (user == null) {
                    int id = resultSet.getInt("id");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");
                    String userType = resultSet.getString("user_type");
                    // Retrieve additional fields
                    Date dateOfBirth = resultSet.getDate("date_of_birth");
                    String country = resultSet.getString("country");
                    String interests = resultSet.getString("interests");
                    List<String> interestsList = Arrays.asList(interests.split(","));

                    log.info(String.valueOf(dateOfBirth),country,interests);
                    // Create user object with additional fields
                    user = new userData(id, firstName, lastName, email, password, dateOfBirth, country,interestsList,userType);
                }


                int addressId = resultSet.getInt("address_id");
                log.info("Address ID: " + addressId);
                String street = resultSet.getString("street");
                String city = resultSet.getString("city");
                String zip = resultSet.getString("zip");
                String state = resultSet.getString("state");
                if (addressId != 0) {
                    Address address = new Address(addressId, street, city, zip, state);
                    addresses.add(address);
                }
            }

            if (user != null) {
                user.setAddresses(addresses);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception
        }

        return user;
    }

    public userData upDateInfo(int userId, String firstName, String lastName, List<Address> addresses,String DateOfBirth,String country,  List<String> interests,  List<byte[]> images,List<String> removedImageIds) {
        userData user = getUserById(userId);

        if (user != null) {
            try {
                log.info("Updating user information for user ID: " + userId);

                // Update user's name if changed
                if (!user.getFirstName().equals(firstName) || !user.getLastName().equals(lastName) || !user.getDateOfBirth().equals(DateOfBirth) || !user.getCountry().equals(country) || !user.getInterests().equals(interests)) {
                    log.info("Updating user's name from " + user.getFirstName() + " " + user.getLastName() + " to " + firstName + " " + lastName, country, DateOfBirth, interests);
                    String updateUserQuery = "UPDATE Users SET first_name = ?, last_name = ?, date_of_birth = ?, country = ?, interests = ? WHERE id = ?";
                    try (PreparedStatement updateUserStatement = connection.prepareStatement(updateUserQuery)) {
                        updateUserStatement.setString(1, firstName);
                        updateUserStatement.setString(2, lastName);
                        updateUserStatement.setString(3, DateOfBirth);
                        updateUserStatement.setString(4, country);
                        String interestsString = String.join(",", interests);
                        updateUserStatement.setString(5, interestsString);

                        updateUserStatement.setInt(6, userId);
                        updateUserStatement.executeUpdate();
                    }
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setDateOfBirth(DateOfBirth);
                    user.setCountry(country);
                    user.setInterests(interests);
                }

                // Update existing addresses and add new ones
                for (Address address : addresses) {
                    if (address.getId() != 0) {
                        log.info("Updating existing address with ID: " + address.getId());
                        // Update existing address
                        String updateQuery = "UPDATE Addresses SET street=?, city=?, zip=?, state=? WHERE id=?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setString(1, address.getStreet());
                            updateStatement.setString(2, address.getCity());
                            updateStatement.setString(3, address.getZip());
                            updateStatement.setString(4, address.getState());
                            updateStatement.setInt(5, address.getId());
                            updateStatement.executeUpdate();
                        }
                    } else {
                        log.info("Adding new address");
                        // Add new address
                        String insertQuery = "INSERT INTO Addresses (user_id, street, city, zip, state) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                            insertStatement.setInt(1, userId);
                            insertStatement.setString(2, address.getStreet());
                            insertStatement.setString(3, address.getCity());
                            insertStatement.setString(4, address.getZip());
                            insertStatement.setString(5, address.getState());
                            insertStatement.executeUpdate();

                            ResultSet generatedKeys = insertStatement.getGeneratedKeys();
                            if (generatedKeys.next()) {
                                int generatedId = generatedKeys.getInt(1);
                                address.setId(generatedId);
                            }
                        }
                    }
                }

                // Delete addresses that are not present in the submitted form
                List<Integer> submittedAddressIds = addresses.stream().map(Address::getId).collect(Collectors.toList());
                List<Integer> currentAddressIds = getAllAddressesByUserId(userId).stream().map(Address::getId).collect(Collectors.toList());
                currentAddressIds.removeAll(submittedAddressIds); // Remove all submitted address IDs from current address IDs
                for (int addressId : currentAddressIds) {
                    log.info("Deleting address with ID: " + addressId);
                    deleteAddress(addressId);
                }

                log.info("size...:-"+images.size());
//                log.info("img idss...:-"+imageIds);
//                 Insert new images into user_images table
                for (int i = 0; i < images.size(); i++) {
                    byte[] imageData = images.get(i);
                    String insertImageQuery = "INSERT INTO user_images (user_id, image) VALUES (?, ?)";
                    try (PreparedStatement insertImageStatement = connection.prepareStatement(insertImageQuery)) {
                        insertImageStatement.setInt(1, userId);
                        insertImageStatement.setBytes(2, imageData);
                        insertImageStatement.executeUpdate();
                    }
                }

log.info("removed:-"+removedImageIds);
                for (String removedImageId : removedImageIds) {
                    log.info("Deleting image with ID: " + removedImageId);
                    String deleteImageQuery = "DELETE FROM user_images WHERE id = ?";
                    try (PreparedStatement deleteImageStatement = connection.prepareStatement(deleteImageQuery)) {
                        int imageId = Integer.parseInt(removedImageId);
                        deleteImageStatement.setInt(1, imageId);
                        deleteImageStatement.executeUpdate();
                    } catch (NumberFormatException e) {
                        // Handle invalid image ID format
                        log.error("Invalid image ID format: " + removedImageId, e);
                    } catch (SQLException e) {
                        // Handle SQL exception
                        log.error("Error deleting image with ID: " + removedImageId, e);
                    }
                }



            } catch (SQLException e) {
                log.error("SQL error occurred while updating user info for user " + userId, e);
            }
        } else {
            log.warn("User not found with ID: " + userId);
        }
        return user;
    }

    public boolean deleteUser(int userId) {
        try {
            // Delete user's addresses first
            String deleteAddressesQuery = "DELETE FROM Addresses WHERE user_id = ?";
            try (PreparedStatement deleteAddressesStatement = connection.prepareStatement(deleteAddressesQuery)) {
                deleteAddressesStatement.setInt(1, userId);
                deleteAddressesStatement.executeUpdate();
            }
            String deleteImgQuery = "DELETE FROM user_images WHERE user_id = ?";
            try (PreparedStatement deleteImgStatement = connection.prepareStatement(deleteImgQuery)) {
                deleteImgStatement.setInt(1, userId);
                deleteImgStatement.executeUpdate();
            }

            // Then delete the user
            String deleteUserQuery = "DELETE FROM Users WHERE id = ?";
            try (PreparedStatement deleteUserStatement = connection.prepareStatement(deleteUserQuery)) {
                deleteUserStatement.setInt(1, userId);
                int rowsAffected = deleteUserStatement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception
            return false;
        }
    }
    public boolean doesEmailExist(String email) {
        String query = "SELECT COUNT(*) AS count FROM Users WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt("count");
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception
        }
        return false;
    }





    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle exception
            }
        }
    }

    @Override
    public List<Address> getAllAddressesByUserId(int userId) {
        List<Address> addresses = new ArrayList<>();
        try {
            String query = "SELECT * FROM Addresses WHERE user_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Address address = new Address();
                        address.setId(resultSet.getInt("id"));
                        address.setStreet(resultSet.getString("street"));
                        address.setCity(resultSet.getString("city"));
                        address.setZip(resultSet.getString("zip"));
                        address.setState(resultSet.getString("state"));
                        address.setUserId(resultSet.getInt("user_id"));
                        addresses.add(address);
                    }
                }
            }
        } catch (SQLException e) {
            log.error(e);
        }
        return addresses;
    }
    @Override
    public boolean deleteAddress(int addressId) {
        try {
            String deleteQuery = "DELETE FROM Addresses WHERE id = ?";
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                deleteStatement.setInt(1, addressId);
                int rowsAffected = deleteStatement.executeUpdate();
                if (rowsAffected > 0) {
                    log.info("Address with ID " + addressId + " deleted successfully.");
                } else {
                    log.warn("No address found with ID " + addressId + " to delete.");
                }
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            log.error("Error occurred while deleting address with ID " + addressId, e);
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<UserImage> getImagesByUserId(int userId) {
        List<UserImage> images = new ArrayList<>();
        String query = "SELECT id, image FROM user_images WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int img_id = resultSet.getInt("id");
                    log.info("Image id " + img_id);
                    byte[] imageBytes = resultSet.getBytes("image");
                    images.add(new UserImage(img_id, imageBytes)); // Create a new UserImage object with ID and byte data and add it to the list
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving images for user ID " + userId + ": " + e.getMessage());
        }
        return images;
    }
}


