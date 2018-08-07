$(document).ready(function () {
        // When user presses "enter" in userId field, the request is being sent.
        $('#user').keypress(function (keyPressed) {
            if (keyPressed.which == 13) {
                keyPressed.preventDefault();
                redirectToUserPage();
            }
        });

        _userId = (new URL(document.location)).searchParams.get("user");
        _experimentId = (new URL(document.location)).searchParams.get("experiment");

        if (_userId != null) {
            document.getElementById("user").value = _userId;
            $('#user-submit-button').text("Change username");
            if (_experimentId == null || _experimentId == "null") {
                if (validateUser() == true) {
                    loadTasks(_userId);
                }
            } else {
                $(".epxeriment-id").append("Experiment ID: " + _experimentId);
            }
        }
    }
);
