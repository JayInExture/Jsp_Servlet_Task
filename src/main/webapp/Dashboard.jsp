<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
<script type="text/javascript">
        // Prevent caching of this page
        function preventBack() {
            window.history.forward();
        }
        setTimeout("preventBack()", 0);
        window.onunload = function() { null };
    </script>
    <meta charset="UTF-8">
    <title>User Dashboard</title>
    <link rel="stylesheet" type="text/css" href="./assets/css/Dashboard.css">
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.25/css/jquery.dataTables.css">
</head>
<body>
<jsp:include page="Header.jsp"></jsp:include>
<nav class="navbar">
        <div class="navbar-brand">Dashboard</div>
        <ul class="navbar-menu">
            <li class="navbar-item"><a href="#" class="navbar-link">Home</a></li>
            <li class="navbar-item"><a href="#" class="navbar-link">Profile</a></li>
            <c:if test="${sessionScope.user.userType eq 'admin'}"> <li>
            <a href="index.jsp?fromAdmin=true" class="add-user-link">Add User</a>
            </li></c:if>
            <li> <form action="logout" method="post">
             <input type="submit" value="Logout">
             </form> </li>
        </ul>
    </nav>

    <c:choose>
        <c:when test="${sessionScope.user.userType eq 'admin'}">
       <section class="admin_dash">
            <h2>Welcome! ${user.firstName} ${user.lastName}</h2>
            <table id="adminTable" border="1">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>First Name</th>
                        <th>Last Name</th>
                        <th>Email</th>
                        <th>Date of Birth</th>
                        <th>Country</th>
                        <th>User Type</th>
                        <th>Addresses</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="user" items="${users}">
                        <tr>
                            <td>${user.id}</td>
                            <td>${user.firstName}</td>
                            <td>${user.lastName}</td>
                            <td>${user.email}</td>
                            <td>${user.dateOfBirth}</td> <!-- Include Date of Birth -->
                            <td>${user.country}</td>
                            <td>${user.userType}</td>
                            <td>
                                <ol>
                                    <c:forEach var="address" items="${user.addresses}">
                                        <li>${address.street}, ${address.city}, ${address.zip}, ${address.state}</li>
                                    </c:forEach>
                                </ol>
                            </td>
                             <td>
                             <a href="editUser?id=${user.id}">Edit</a>
                             <form class="Del_form" action="deleteUser" method="post">
                             <input type="hidden" name="id" value="${user.id}">
                             <button type="submit" onclick="return confirm('Are you sure you want to delete this user?')">Delete</button>
                             </form>
                             </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <!-- Include jQuery -->
            <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
            <!-- Include DataTables JS -->
            <script type="text/javascript" charset="utf8" src="https://cdn.datatables.net/1.10.25/js/jquery.dataTables.js"></script>
            <script>
                $(document).ready(function() {
                    $('#adminTable').DataTable();
                });
            </script>
        </section>
        </c:when>
        <c:otherwise>
           <section class="user_d">
            <h3>Welcome! ${sessionScope.user.firstName} ${sessionScope.user.lastName}</h3>
            <table border="1">
                <tr>
                   <%-- <th>ID</th> --%>
                    <th>First Name</th>
                    <th>Last Name</th>
                    <th>Email</th>
                    <th>Date of Birth</th>
                    <th>Country</th>
                </tr>
                <tr>
                   <%-- <td>${sessionScope.user.id}</td>  --%>
                    <td>${sessionScope.user.firstName}</td>
                    <td>${sessionScope.user.lastName}</td>
                    <td>${sessionScope.user.email}</td>
                    <td>${sessionScope.user.dateOfBirth}</td> <!-- Include Date of Birth -->
                    <td>${sessionScope.user.country}</td> <!-- Include Country -->

                </tr>
            </table>

            <h2>Addresses</h2>
            <table border="1">
                <tr>
                    <th>Street</th>
                    <th>City</th>
                    <th>ZIP</th>
                    <th>State</th>
                </tr>

                <c:forEach var="address" items="${sessionScope.user.addresses}">
                    <tr>
                        <td>${address.street}</td>
                        <td>${address.city}</td>
                        <td>${address.zip}</td>
                        <td>${address.state}</td>
                     <%--   <td>${address.addressId}</td> <!-- Corrected: Display the address ID --> --%>
                        <td> <a href="editUser?id=${sessionScope.user.id}&addressId=${address.addressId}">Edit</a></td>
                    </tr>
                </c:forEach>

            </table>
            <h2>Images</h2>
            <div class="image-boxes">
          <c:forEach var="base64Image" items="${base64Images}">
              <img class="user-image" src="data:image/jpeg;base64,${base64Image}">
          </c:forEach>
            </div>


            </section>
        </c:otherwise>
    </c:choose>
<jsp:include page="Footer.jsp"></jsp:include>
</body>
</html>


