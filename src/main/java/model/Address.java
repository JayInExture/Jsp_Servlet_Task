package model;

public class Address {
    private int id;
    private int userId;
    private String street;
    private String city;
    private String zip;
    private String state;
    private int addressId;
    // Other properties and methods

    public int getAddressId() {
        return addressId;
    }


    public Address() {
    }
    public Address(int userId, String street, String city, String zip, String state) {
        this.userId = userId;
        this.street = street;
        this.city = city;
        this.zip = zip;
        this.state = state;
    }
    public Address(int id, int userId, String street, String city, String zip, String state) {
        this.id = id;
        this.userId = userId;
        this.street = street;
        this.city = city;
        this.zip = zip;
        this.state = state;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }
}
