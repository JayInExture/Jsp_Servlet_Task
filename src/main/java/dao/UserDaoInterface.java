package dao;

import com.sun.tools.javac.util.Pair;
import jakarta.servlet.http.HttpSession;
import model.Address;
import model.UserImage;
import model.userData;

import java.util.List;

public interface UserDaoInterface {

    void saveUser(userData user);


    userData getUserByEmailAndPassword(HttpSession session, String email, String password);

    boolean updatePasswordByEmailAndType(String email, String newPassword);

    List<userData> getAllUsers();

    userData getUserById(int userId);

    userData upDateInfo(int userId, String firstName, String lastName, List<Address> addresses,String DateOfBirth,String country,  List<String> interests,  List<byte[]> images,List<String> removedImageIds);


    boolean deleteUser(int userId);

    boolean doesEmailExist(String email);
    List<Address> getAllAddressesByUserId(int userId);



    boolean deleteAddress(int addressId);

    List<UserImage> getImagesByUserId(int userId);
}


