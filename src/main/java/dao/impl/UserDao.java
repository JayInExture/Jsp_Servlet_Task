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
                userStatement.setString(8, user.getInterests()); // Insert interests
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception
        }
    }

    public userData getUserByEmailAndPassword(HttpSession session, String email, String password) {
        try {
            String query = "SELECT u.id, u.first_name, u.last_name, u.email, u.password, u.user_type, u.date_of_birth, u.country, " +
                    "a.id AS address_id, a.street, a.city, a.zip, a.state " +
                    "FROM Users u LEFT JOIN Addresses a ON u.id = a.user_id " +
                    "WHERE u.email=? AND u.password=?";

//            String query = "SELECT u.id, u.first_name, u.last_name, u.email, u.password, u.user_type, u.date_of_birth, u.country " +
//                    "a.id as address_id, a.street, a.city, a.zip, a.state " +
//                    "FROM Users u LEFT JOIN Addresses a ON u.id = a.user_id " +
//                    "WHERE u.email=? AND u.password=?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, email);
                statement.setString(2, password);
                try (ResultSet resultSet = statement.executeQuery()) {
                    userData user = null;
                    List<Address> addresses = new ArrayList<>();
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
                            user = new userData(id, firstName, lastName, fetchedEmail, fetchedPassword,fetchedDob,fetchedcountry, fetchedUserType);
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
                        log.info(addressId);
                        log.info(addresses);
                    }

                    if (user != null) {
                        user.setAddresses(addresses);
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
                    user = new userData(id, firstName, lastName, email, password, userType);
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

    public userData upDateInfo(int userId, String firstName, String lastName, List<Address> addresses) {
        userData user = getUserById(userId);

        if (user != null) {
            try {
                log.info("Updating user information for user ID: " + userId);

                // Update user's name if changed
                if (!user.getFirstName().equals(firstName) || !user.getLastName().equals(lastName)) {
                    log.info("Updating user's name from " + user.getFirstName() + " " + user.getLastName() + " to " + firstName + " " + lastName);
                    String updateUserQuery = "UPDATE Users SET first_name = ?, last_name = ? WHERE id = ?";
                    try (PreparedStatement updateUserStatement = connection.prepareStatement(updateUserQuery)) {
                        updateUserStatement.setString(1, firstName);
                        updateUserStatement.setString(2, lastName);
                        updateUserStatement.setInt(3, userId);
                        updateUserStatement.executeUpdate();
                    }
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
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
            } catch (SQLException e) {
                e.printStackTrace();
                // Log the SQL exception
                log.error("SQL error occurred while updating user info for user " + userId, e);
            }
        } else {
            log.warn("User not found with ID: " + userId);
        }
        return user;
    }




//    public userData upDateInfo(int userId, String firstName, String lastName, List<Address> addresses) {
//        userData user = getUserById(userId);
//
//        if (user != null) {
//            try {
//                // Update user's name if changed
//                // Update existing addresses and add new ones
//                for (Address address : addresses) {
//                    if (address.getId() != 0) {
//                        // Update existing address
//                        String updateQuery = "UPDATE Addresses SET street=?, city=?, zip=?, state=? WHERE id=?";
//                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
//                            updateStatement.setString(1, address.getStreet());
//                            updateStatement.setString(2, address.getCity());
//                            updateStatement.setString(3, address.getZip());
//                            updateStatement.setString(4, address.getState());
//                            updateStatement.setInt(5, address.getId());
//                            updateStatement.executeUpdate();
//                        }
//                    } else {
//                        // Add new address
//                        String insertQuery = "INSERT INTO Addresses (user_id, street, city, zip, state) VALUES (?, ?, ?, ?, ?)";
//                        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
//                            insertStatement.setInt(1, userId);
//                            insertStatement.setString(2, address.getStreet());
//                            insertStatement.setString(3, address.getCity());
//                            insertStatement.setString(4, address.getZip());
//                            insertStatement.setString(5, address.getState());
//                            insertStatement.executeUpdate();
//
//                            ResultSet generatedKeys = insertStatement.getGeneratedKeys();
//                            if (generatedKeys.next()) {
//                                int generatedId = generatedKeys.getInt(1);
//                                address.setId(generatedId);
//                            }
//                        }
//                    }
//                }
//
//                // Delete removed addresses
//                String deleteQuery = "DELETE FROM Addresses WHERE user_id=? AND id NOT IN (?)";
//                String addressIds = addresses.stream().map(a -> String.valueOf(a.getId())).collect(Collectors.joining(","));
//                try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
//                    deleteStatement.setInt(1, userId);
//                    deleteStatement.setString(2, addressIds);
//                    deleteStatement.executeUpdate();
//                }
//
//                // Update user's name if changed
//                if (!user.getFirstName().equals(firstName) || !user.getLastName().equals(lastName)) {
//                    String updateUserQuery = "UPDATE Users SET first_name = ?, last_name = ? WHERE id = ?";
//                    try (PreparedStatement updateUserStatement = connection.prepareStatement(updateUserQuery)) {
//                        updateUserStatement.setString(1, firstName);
//                        updateUserStatement.setString(2, lastName);
//                        updateUserStatement.setInt(3, userId);
//                        updateUserStatement.executeUpdate();
//                    }
//                    user.setFirstName(firstName);
//                    user.setLastName(lastName);
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//                // Log the SQL exception
//                log.error("SQL error occurred while updating user info for user " + userId, e);
//            }
//        }
//        return user;
//    }





    public boolean deleteUser(int userId) {
        try {
            // Delete user's addresses first
            String deleteAddressesQuery = "DELETE FROM Addresses WHERE user_id = ?";
            try (PreparedStatement deleteAddressesStatement = connection.prepareStatement(deleteAddressesQuery)) {
                deleteAddressesStatement.setInt(1, userId);
                deleteAddressesStatement.executeUpdate();
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
            e.printStackTrace();
            // Handle exception
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




}