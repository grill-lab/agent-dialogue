$(document).ready(function () {
    userId = (new URL(document.location)).searchParams.get("user");
    if (userId != null) {
        document.getElementById("user").value = userId;
        $('#user-submit-button').text("Change username");
    }

    // When user presses "enter" in userId field, the request is being sent.
    $('#user').keypress(function (keyPressed) {
        if (keyPressed.which == 13) {
            keyPressed.preventDefault();
            startExperiment();
        }
    });
});

function startExperiment() {
    let basicUrl = (new URL(document.location)).origin + (new URL(document.location)).pathname;
    userId = document.getElementById("user").value;

    $.ajax({
        url: "offline-mt-ranking-servlet",
        type: 'POST',
        headers: {"operation": "startExperiment"},
        dataType: 'text',
        data: {
            userId: userId
        },
        success: function (response) {
            if (response == "false") {
                alert("The User Id is invalid.")
            } else {
                window.location.replace(basicUrl + "?user=" + userId);
            }
        },
        error: function (data, status, error) {
            alert("Error data: " + data + "\nStatus: " + status + "\nError message:" + error);
        },
    });
}

function nextTask() {
    let params = (new URL(document.location)).searchParams;
    let basicUrl = (new URL(document.location)).origin + (new URL(document.location)).pathname;
    let userIdInput = document.getElementById("user-id").value;
    let userId = params.get("user");
    let turnId = params.get("turn_id");
}
