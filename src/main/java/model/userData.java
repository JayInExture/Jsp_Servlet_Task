package model;

import java.sql.Blob;
import java.util.Date;
import java.util.List;

public class userData {


    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String dateOfBirth;
    private String country;
    private List<String> interests;

    private List<Address> addresses;
    private String userType;
    private List<byte[]> images;

    private List<UserImage> userImages;
    private List<String> base64Images;
    public List<UserImage> getUserImages() {
        return userImages;
    }

    public void setUserImages(List<UserImage> userImages) {
        this.userImages = userImages;
    }

    public List<String> getBase64Images() {
        return base64Images;
    }

    public void setBase64Images(List<String> base64Images) {
        this.base64Images = base64Images;
    }

//    private int addressId;


    public userData(int id, String firstName, String lastName, String email, String password, String userType) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }


    public userData(int id, String firstName, String lastName, String email, String password, String dateOfBirth, String country, String userType) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.country = country;
        this.userType = userType;
    }





    public userData(int id, String firstName, String lastName, String email, String password, Date dateOfBirth, String country, List<String> interests, String userType) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.password = password;
            this.dateOfBirth = String.valueOf(dateOfBirth);
            this.country = country;
            this.interests = interests;
            this.userType = userType;
        }

    public userData() {

    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
        // Getters and Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public List<byte[]> getImages() {
        return images;
    }

    public void setImages(List<byte[]> images) {
        this.images = images;
    }
}

//    public String getInterests() {
//        return interests;
//    }
//
//    public void setInterests(String interests) {
//        this.interests = interests;
//    }

//    public int getAddressId() {
//        return addressId;
//    }
//
//    public void setAddressId(int addressId) {
//        this.addressId = addressId;
//    }








