var _maxTasksAssigned = 5;
var _listOfTasks = null;

$(document).ready(function () {
    // When user presses "enter" in userId field, the request is being sent.
    $('#user').keypress(function (keyPressed) {
        if (keyPressed.which == 13) {
            keyPressed.preventDefault();
            redirectToUserPage();
        }
    });

    userId = (new URL(document.location)).searchParams.get("user");
    if (userId != null) {
        document.getElementById("user").value = userId;
        $('#user-submit-button').text("Change username");
        validateUserAndStartExperiment(userId)
    }
});

function redirectToUserPage() {
    let basicUrl = (new URL(document.location)).origin + (new URL(document.location)).pathname;
    userId = document.getElementById("user").value;
    window.location.replace(basicUrl + "?user=" + userId);
}

function validateUserAndStartExperiment(userId) {
    $.ajax({
        url: "offline-mt-ranking-servlet",
        type: 'POST',
        headers: {"operation": "validateUserAndStartExperiment"},
        dataType: 'text',
        data: {
            userId: userId
        },
        success: function (response) {
            if (response == "false") {
                alert("The User Id: " + userId + " is invalid.");
                $('.user-details-form').append($("<div>").text("INVALID USER ID"));
            }
            else {
                loadTasks(userId);
            }
        },
        error: function (data, status, error) {
            alert("Error data: " + data + "\nStatus: " + status + "\nError message:" + error);
        },
    });
}

function loadTasks(userId) {
    $.ajax({
        url: "offline-mt-ranking-servlet",
        type: 'POST',
        headers: {"operation": "loadTasks"},
        dataType: 'json',
        data: {
            userId: userId,
            maxTasksAssigned: _maxTasksAssigned
        },
        success: function (response) {
            _listOfTasks = response.tasks;
            let $tasks_list = $('.tasks-list');
            $tasks_list.innerText = "";
            for (let i = 0; i < _listOfTasks.length; i++) {
                let $task = $("<span onclick=showTaskWithNumber(i)>").text("Task " + i + 1);
                $tasks_list.append($task).append("<br>");
            }
            $(".tasks-list-block").append("<button id = 'next-batch-button' class = 'submit-button' " +
                "type = 'button' onclick = \'loadTasks(" + userId + "\")\'>").text("Next batch");
            if (_listOfTasks.length > 0) {
                showTaskWithNumber(0);
            } else {
                $tasks_list.innerText = "The are no more available tasks in the database.";
            }
        },
        error: function (data, status, error) {
            alert("Error data: " + data + "\nStatus: " + status + "\nError message:" + error);
        },
    });


    // populate list of tasks:
    //     check if the user has any tasks assigned,
    //         not maxNum..:
    //             assign more tasks
    //     download assigned tasks
    //     populate the list of the right
    // show the user first task from the list
}

function showTaskWithNumber(taskNumber) {
    // Show all the metadata about a particular task
    // Display the conversation with times of each message
    // show rating starts for the conversation
}

function rateTask() {
    // send a rating to servlet:
    // Update user dabatase - remove task reference from user list
    // update rating database
    // update the view of rating starts
    // update the list of tasks on right

}


function nextTask() {
    let params = (new URL(document.location)).searchParams;
    let basicUrl = (new URL(document.location)).origin + (new URL(document.location)).pathname;
    let userIdInput = document.getElementById("user-id").value;
    let userId = params.get("user");
    let turnId = params.get("turn_id");
}
