$(document).ready(function() {
  Dropzone.autoDiscover = false;
        var myDropzone = new Dropzone(".dropzone", {
            url: " ",
             paramName: "file",
            autoProcessQueue: true,
            uploadMultiple: true,
            parallelUploads: 20,
            maxFiles: 50,
            maxThumbnailFilesize:50,
            addRemoveLinks: true,
            acceptedFiles: ".jpg, .jpeg, .png",
            dictDefaultMessage: "Drop your files here!",
            thumbnailWidth: null,
            thumbnailHeight:null,
            resizeWidth: null,
            resizeHeight:null,

        });

        document.getElementById("submit_button").addEventListener("click", function() {
            var images = [];
            $(".dropzone .dz-preview .dz-image img").each(function() {
                var imageSrc = $(this).attr("src");
                images.push(imageSrc);
            });

            var form = $("#registration_form"); // Assuming your form has an ID of "registration_form"
            $.each(images, function(index, imageSrc) {
                var hiddenInput = $('<input type="hidden" name="images[]" value="' + imageSrc + '">');
                form.append(hiddenInput);
            });
        });





    $("#add-address").click(function() {
        var newAddress = $(".address-field:first").clone();
        newAddress.find("input").val("");
        newAddress.insertAfter(".address-field:last");
        updateDeleteButtons();
    });

    // Event listener for dynamically added delete buttons
    $(document).on("click", ".delete-address", function() {
        // Check if there's only one address field left, don't delete it
        if ($(".address-field").length === 1) {
            alert("At least one address component must remain.");
            return;
        }
        $(this).closest(".address-field").remove();
        updateDeleteButtons(); // Call the function to update delete buttons
    });

    function updateDeleteButtons() {
        $(".delete-address").toggle($(".address-field").length > 1);
    }
    updateDeleteButtons();


       $("#add-image").click(function() {
               var newImageField = $(".image-field:first").clone();
               newImageField.find("input").val(""); // Clear the input value
               newImageField.find(".preview-image").attr("src", ""); // Clear the preview image
               newImageField.insertAfter(".image-field:last");
               updateImgDeleteButtons();
           });

           // Event listener for file input change
           $(document).on("change", "input[type=file]", function() {
               var input = this; // Store a reference to the input element
               if (input.files && input.files[0]) {
                   var reader = new FileReader();
                   reader.onload = function(e) {
                       $(input).siblings(".preview-image").attr("src", e.target.result);
                   }
                   reader.readAsDataURL(input.files[0]);
               }
           });

           // Event listener for dynamically added delete buttons
           $(document).on("click", ".delete-image", function() {
               // Check if there's only one image field left, don't delete it
               if ($(".image-field").length === 1) {
                   alert("At least one image must remain.");
                   return;
               }
               $(this).closest(".image-field").remove();
               updateImgDeleteButtons(); // Call the function to update delete buttons
           });

           function updateImgDeleteButtons() {
               $(".delete-image").toggle($(".image-field").length > 1);
           }
           updateImgDeleteButtons();
});
