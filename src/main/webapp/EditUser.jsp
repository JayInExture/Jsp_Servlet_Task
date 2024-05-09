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
  <link href="https://unpkg.com/dropzone@6.0.0-beta.1/dist/dropzone.css" rel="stylesheet" type="text/css" />
</head>
<body>
  <jsp:include page="Header.jsp"></jsp:include>
   <form class="log_out" action="logout" method="post">
          <input class="log_out_btn" type="submit" value="Logout">
      </form>
  <section class="Edit_common">
  <div class="Edit_common_form" >
    <h3>Edit User</h3>
    <form id="edit_user_form" action="editUser" method="post" enctype="multipart/form-data">
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
        <div>
            <label class="form-label" for="dateOfBirth">Date of Birth:-</label>
            <input class="form-control" type="date" id="dateOfBirth" name="dateOfBirth" value="${user.dateOfBirth}">
        </div>
        <label class="form-label">Country:-</label>
        <div class="countries">
            <label><input type="radio" name="country" value="india" ${user.country eq 'india' ? 'checked' : ''}>India</label>
            <label><input type="radio" name="country" value="USA" ${user.country eq 'USA' ? 'checked' : ''}>USA</label>
            <label><input type="radio" name="country" value="Canada" ${user.country eq 'Canada' ? 'checked' : ''}>Canada</label>
            <label><input type="radio" name="country" value="other" ${user.country eq 'other' ? 'checked' : ''}>Other..</label>
        </div>
        <label class="form-label">Interests:-</label>
        <div class="interests">
            <label><input type="checkbox" name="interests" value="sports" ${user.interests.contains('sports') ? 'checked' : ''}>Sports</label>
            <label><input type="checkbox" name="interests" value="music" ${user.interests.contains('music') ? 'checked' : ''}>Music</label>
            <label><input type="checkbox" name="interests" value="travel" ${user.interests.contains('travel') ? 'checked' : ''}>Travel</label>
            <label><input type="checkbox" name="interests" value="other" ${user.interests.contains('other') ? 'checked' : ''}>Other..</label>
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
<h2>Images</h2>
<c:forEach var="image" items="${user.userImages}">
    <input  type="hidden" name="existingImageId" value="${image.id}">
</c:forEach>
<div id="image-dropzone" class="dropzone">

</div>

<input  type="hidden" id="removedImageIds" name="removedImageIds">

      <button id="submit_form" class="submit_form" type="submit">
        Submit
      </button>
    </form>
    </div>
  </section>
  <jsp:include page="Footer.jsp"></jsp:include>
 <script src="https://unpkg.com/dropzone@6.0.0-beta.1/dist/dropzone-min.js"></script>
  <script src="./assets/Js/EditUser.js" type="module" defer></script>

  <script>
  $(document).ready(function() {

    var removedImageIds = [];
      Dropzone.autoDiscover = false;
      var myDropzone = new Dropzone("#image-dropzone", {

          url: " ", // Your server endpoint to handle image upload
          maxFiles: 5, // Maximum number of files allowed
          addRemoveLinks: true, // Show remove links on each image
          acceptedFiles: 'image/*', // Accepted file types
          dictDefaultMessage: "Drop or click to upload images", // Default message
           thumbnailWidth: null,
           thumbnailHeight:null,
           resizeWidth: null,
           resizeHeight:null,
          // Additional Dropzone configurations or callbacks can be added here

            init: function() {
                    this.on("removedfile", function(file) {
                    var removedImageId = file.customId;
                    removedImageIds.push(removedImageId);
                    $("#removedImageIds").val(removedImageIds.join(","));
                    console.log("Removed image ID:", removedImageId);
                    });
                  }

      });
           <c:forEach var="image" items="${user.userImages}" varStatus="loop">
               var imageId = ${image.id}; // Get the image ID from the userImages list
               var base64Image = "${user.base64Images[loop.index]}";
               var imageUrl = "data:image/jpeg;base64," + base64Image;
               var mockFile = { name: "image", size: 12345, customId: imageId };
               myDropzone.displayExistingFile(mockFile, imageUrl, "custom_" + imageId);
               console.log("ids:_-", mockFile.customId);
           </c:forEach>

            document.getElementById("submit_form").addEventListener("click", function() {
                        var new_images = [];
                  $(".dropzone .dz-preview.new .dz-image img").each(function() {
                      var imageSrc = $(this).attr("src");
                      new_images.push(imageSrc);
                  });

                  // Add new images to hidden input fields
                  var form = $("#edit_user_form");
                  $.each(new_images, function(index, imageSrc) {
                      var hiddenInput = $('<input type="hidden" name="images_new[]" value="' + imageSrc + '">');
                      form.append(hiddenInput);
                  });
                  // Now, submit the form
                  form.submit();
              });

              // Event listener for adding files to Dropzone
              myDropzone.on("addedfile", function(file) {
                  // Add a class to newly added files
                  $(file.previewElement).addClass("new");
              });

                   });

  </script>

</body>
</html>
