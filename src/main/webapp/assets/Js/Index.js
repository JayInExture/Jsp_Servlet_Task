$(document).ready(function() {
    $("#add-address").click(function() {
        var newAddress = $(".address-field:first").clone();
        newAddress.find("input").val("");
        newAddress.insertAfter(".address-field:last");
    });

    // Event listener for dynamically added delete buttons
    $(document).on("click", ".delete-address", function() {
        // Check if there's only one address field left, don't delete it
        if ($(".address-field").length === 1) {
            alert("At least one address component must remain.");
            return;
        }
        $(this).closest(".address-field").remove();
    });
});
