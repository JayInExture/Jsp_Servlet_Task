<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Edit User</title>
  <link rel="stylesheet" type="text/css" href="./assets/css/EditInfo.css">
  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
  <jsp:include page="Header.jsp"></jsp:include>
   <form class="log_out" action="logout" method="post">
          <input class="log_out_btn" type="submit" value="Logout">
      </form>
  <section class="Edit_common">
  <div class="Edit_common_form" >
    <h3>Edit User</h3>
    <form action="editUser" method="post">
     <input type="hidden" name="id" value="${user.id}">
      <div>
        <label for="First_name" class="form-label">First Name:-</label>
        <input type="text" id="First_name" name="First_name" class="form-control " value="${user.firstName}" required>
      </div>
      <div>
        <label for="Last_name" class="form-label">Last Name:-</label>
        <input type="text" id="Last_name" name="Last_name" class="form-control " value="${user.lastName}" required>
      </div>
      <div>
        <label class="form-label" for="email">Email:-</label>
        <input class="form-control" type="email" id="email" name="email" value="${user.email}" readonly>
      </div>

      <h4>Addresses:</h4>
      <div id="address-fields">
        <c:forEach var="address" items="${user.addresses}">
          <div class="address-field">
         <%-- <label for="addressId">Address ID:</label> --%>
         <input type="hidden" type="text" name="addressId" value="${address.id}" readonly>

            <label for="street">Street:</label>
            <input type="text" name="street" placeholder="Street" value="${address.street}" required>
            <label for="city">City:</label>
            <input type="text" name="city" placeholder="City" value="${address.city}" required>
            <label for="zip">ZIP:</label>
            <input type="text" name="zip" placeholder="Zip" value="${address.zip}" required>
            <label for="state">State:</label>
            <input type="text" name="state" placeholder="State" value="${address.state}" required>
          <button type="button" class="delete-address" id="delete-address">Delete Address</button>
                  <br>
          </div>
        </c:forEach>
      </div>

      <button type="button" id="add-address">Add Address</button>
      <br>
      <button class="submit_form" type="submit">
        Submit
      </button>
    </form>
    </div>
  </section>
  <script>
    $(document).ready(function() {
      let addressCount = $('#address-fields .address-field').length;

      $('#add-address').click(function() {
        addressCount++;
        const newAddressField = `
          <div class="address-field">
            <label for="street">Street:</label>
            <input type="text" name="street" placeholder="Street" required>
            <label for="city">City:</label>
            <input type="text" name="city" placeholder="City" required>
            <label for="zip">ZIP:</label>
            <input type="text" name="zip" placeholder="Zip" required>
            <label for="state">State:</label>
            <input type="text" name="state" placeholder="State" required>
            <button type="button" class="delete-address" id="delete-address">Delete Address</button>
                    <br>
          </div>`;
        $('#address-fields').append(newAddressField);
      });
       $(document).on("click", ".delete-address", function() {
          if ($(".address-field").length === 1) {
                      alert("At least one address component must remain.");
                      return;
                  }
             $(this).closest('.address-field').remove();
           });
    });
  </script>
  <jsp:include page="Footer.jsp"></jsp:include>
</body>
</html>
