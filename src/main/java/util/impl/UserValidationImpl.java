package util.impl;

//import util.UserValidation;

import java.util.List;
import java.util.regex.Pattern;

public class UserValidationImpl  {
    private static final String NAME_REGEX = "^[a-zA-Z ]*$";
    private static final String PASSWORD_REGEX = ".{8,}"; // Minimum 8 characters
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";


    public boolean isValidName(String name) {
        return name != null && name.matches(NAME_REGEX);
    }


    public boolean isPasswordValid(String password) {
        return password != null && password.matches(PASSWORD_REGEX);
    }


    public boolean doPasswordsMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }


    public boolean isValidEmail(String email) {
        return email != null && Pattern.matches(EMAIL_REGEX, email);
    }

    public boolean isValidDateOfBirth(String dob) {
        return dob != null && !dob.isEmpty();
    }


     public boolean isValidCountry(String country) {
         return country != null && !country.isEmpty();
     }
     public boolean isValidInterests(List<String> interests) {
         return interests != null && !interests.isEmpty();
     }

    public boolean isValidAddress(String[] streets, String[] cities, String[] zips, String[] states) {
        if (streets == null || cities == null || zips == null || states == null) {
            return false;
        }
        if (streets.length != cities.length || streets.length != zips.length || streets.length != states.length) {
            return false;
        }
        for (String street : streets) {
            if (street == null || street.isEmpty()) {
                return false;
            }
        }
        for (String city : cities) {
            if (city == null || city.isEmpty()) {
                return false;
            }
        }
        for (String zip : zips) {
            if (zip == null || zip.isEmpty()) {
                return false;
            }
        }
        for (String state : states) {
            if (state == null || state.isEmpty()) {
                return false;
            }
        }

        return true;
    }


}
