<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<html>
<head>
<link rel="stylesheet" href="./assets/css/bootstrap.min.css" />
<link rel="stylesheet" href="./assets/css/style.css" />

<title>Registration Form</title>
</head>
<body>
    <header class="container">
        <div>
            Registration Form
        </div>
    </header>
    <section class="container ">
        <div class="row">
            <div class="col-12">
            </div>
            <form class="col-12" action="register" method="post">
                <p>Note:- all <span>*</span> fields are mandatory</p>
                <div>
                    <label class="form-label">User Type:-</label>
                    <select class="form-select" name="userType">
                        <option value="user">User</option>
                        <option value="admin">Admin</option>
                    </select>
                </div>

                <div>
                    <label for="First_name" class="form-label">First Name:-</label>
                    <input type="text" id="First_name" name="First_name" class="form-control " required>
                </div>
                 <div>
                    <label for="Last_name" class="form-label">Last Name:-</label>
                    <input type="text" id="Last_name" name="Last_name" class="form-control " required>
                </div>

                <div>
                    <label class="form-label" for="email">Email:-</label>
                    <input class="form-control" type="email" id="email" name="email" required>
                </div>

                <div>
                    <label for="Password" class="form-label">Password:-</label>
                    <input type="text" id="Password" name="Password" class="form-control " required>
                </div>
                <div>
                    <label for="Confirm_Password" class="form-label">Confirm Password:-</label>
                    <input type="text" id="Confirm_Password" name="Confirm_Password" class="form-control " required>
                </div>
                  <div id="dob_container">
                  <label class="form-label" for="dob">Date of Birth:-</label>
                  <input class="form-control" type="date" id="dob" name="dob" required>
                  <span id="dob_error"></span>
                  </div>

                 <div>
                 <label class="form-label">Country:-</label>
                 <div class="countries">
                 <label><input type="radio" name="country" value="india">India</label>
                 <label><input type="radio" name="country" value="USA">USA</label>
                 <label><input type="radio" name="country" value="Canada">Canada</label>
                 <label><input type="radio" name="country" value="other">Other..</label>
                 </div>
                 <span id="country_error"></span>
                 </div>
                 <div>
                 <label class="form-label">Interests:-</label><br>
                 <div class="interests">
                 <label><input type="checkbox" name="interests" value="sports">Sports</label>
                 <label><input type="checkbox" name="interests" value="music">Music</label>
                 <label><input type="checkbox" name="interests" value="travel">Travel</label>
                 <label><input type="checkbox" name="interests" value="other">Other..</label>
                 </div>
                 <span id="interests_error"></span>
                 </div>

                <div>
                    <label  class="form-label"  for="name" class="form-label">Address:-</label>
                    <div class="address-field">
                        <input class="form-control" type="text" name="street" placeholder="Street">
                        <input class="form-control" type="text" name="city" placeholder="City">
                        <input class="form-control" type="text" name="zip" placeholder="Zip">
                        <input class="form-control" type="text" name="state" placeholder="State">
                        <button class="btn btn-danger delete-address" type="button">Delete</button>
                    </div>

                    <div class="add-address-button">
                    <button class="btn btn-info"  type="button" id="add-address">Add Address</button>
                    </div>
                </div>
                <div>
                    <button type="submit" id="submit_button" class="btn btn-success">
                        Submit
                    </button>
                </div>
            </form>
    <a class="a" href="Login.jsp">Login</a>
        </div>
    </section>
<jsp:include page="Footer.jsp"></jsp:include>
</body>
 <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
 <script src="./assets/Js/Index.js" type="module" defer></script>
</html>