var _maxTasksAssigned = 5;
var _listOfTasks = null;
var _tasksRating = {};
var _ratingScaleOffline = 5;

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
    _tasksRating = {};
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
                _tasksRating[i] = 0;
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
}

function showTaskWithNumber(taskNumber) {
    let task = _listOfTasks[taskNumber];
    let $current_task_details = $("#current-task-details");
    $current_task_details.innerText = "";
    $current_task_details.append("<div>").text("Client ID: " + task.clientId)
        .append("<div>").text("Device type: " + task.deviceType)
        .append("<div>").text("Language code: " + task.language_code);
    let $rating_interface_block = $(".rating-interface-block");
    $rating_interface_block.innerText = "";
    let turns = task.turns;
    for (let turn in turns) {
        if (turn.request != null) {
            $rating_interface_block.append($('<div id="request"/>')
                .append(turn.requestTime_seconds + "<br>" + turn.request));
        }
        if (turn.response != null) {
            $rating_interface_block.append($('<div id="response"/>')
                .append(turn.responseTime_seconds + "<br>" + turn.response));
        }
    }
    createMtRating(taskNumber);
}

function rateTask(taskNumber, rating) {
    _tasksRating[taskNumber] = rating;
    
    // send a rating to servlet:
    // Update user dabatase - remove task reference from user list
    // update rating database
    // update the view of rating starts
    // update the list of tasks on right
    // update rating reference list of a task
}

function createMtRating(taskNumber) {
    let rating = _tasksRating[taskNumber];
    let $rating = $('<div class = "rating" id = "current-rating">');
    for (let numberOfStars = 0; numberOfStars < _ratingScaleOffline; numberOfStars++) {
        $rating.append("<img id='star-rating' src='../resources/img/star-regular.svg' " +
            "onmouseover= \'selectStars(" + numberOfStars + "\")\' " +
            "onmouseout = \'deselectStars(" + numberOfStars + "\")\' " +
            "onclick=\'rateTask(" + taskNumber + ",\"" + numberOfStars + "\")\'/>");
    }
    $rating.append("<img id='rating-indicator' src='../resources/img/question-circle-solid.svg' />");
    $(".rating-interface-block").append($rating);
}


function selectStars(starNumber) {
    let $stars = $("#current-rating").find('img[id="star-rating"]');
    for (i = 0; i < $stars.length; i++) {
        if (i <= starNumber) {
            $stars[i].src = '../resources/img/star-solid.svg';
        }
        else {
            $stars[i].src = '../resources/img/star-regular.svg';
        }
    }
}

function deselectStars(starNumber) {
    let $ratingDiv = $("#current-rating");
    let stars = $ratingDiv.find('img[id="star-rating"]');
    for (i = 0; i < stars.length; i++) {
        if (i < _tasksRating[starNumber]) {
            stars[i].src = '../resources/img/star-solid.svg';
        }
        else {
            stars[i].src = '../resources/img/star-regular.svg';
        }
    }
}