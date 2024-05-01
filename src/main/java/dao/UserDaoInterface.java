package dao;

import jakarta.servlet.http.HttpSession;
import model.Address;
import model.userData;

import java.util.List;

public interface UserDaoInterface {

    void saveUser(userData user);


    userData getUserByEmailAndPassword(HttpSession session, String email, String password);

    boolean updatePasswordByEmailAndType(String email, String newPassword);

    List<userData> getAllUsers();

    userData getUserById(int userId);

    userData upDateInfo(int userId, String firstName, String lastName, List<Address> addresses, String DateOfBirth,String country,  List<String> interests);


    boolean deleteUser(int userId);

    boolean doesEmailExist(String email);
    List<Address> getAllAddressesByUserId(int userId);



    boolean deleteAddress(int addressId);
}


