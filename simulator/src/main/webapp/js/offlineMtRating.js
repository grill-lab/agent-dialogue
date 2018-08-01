var _maxTasksAssigned = 5;
var _listOfTasks = null;
var _tasksRating = {};
var _ratingScaleOffline = 5;
var _userId = null;
var _startTimeOfCurrentTask = (Date.now() / 1000) | 0; // UTC in seconds.
var _currentTask = null;

$(document).ready(function () {
    // When user presses "enter" in userId field, the request is being sent.
    $('#user').keypress(function (keyPressed) {
        if (keyPressed.which == 13) {
            keyPressed.preventDefault();
            redirectToUserPage();
        }
    });

    _userId = (new URL(document.location)).searchParams.get("user");
    if (_userId != null) {
        document.getElementById("user").value = _userId;
        $('#user-submit-button').text("Change username");
        validateUserAndStartExperiment()
    }
});

function redirectToUserPage() {
    let basicUrl = (new URL(document.location)).origin + (new URL(document.location)).pathname;
    userId = document.getElementById("user").value;
    window.location.replace(basicUrl + "?user=" + userId);
}

function validateUserAndStartExperiment() {
    $.ajax({
        url: "offline-mt-ranking-servlet",
        type: 'POST',
        headers: {"operation": "validateUserAndStartExperiment"},
        dataType: 'text',
        data: {
            userId: _userId
        },
        success: function (response) {
            if (response == "false") {
                alert("The User Id: " + _userId + " is invalid.");
                $('.user-details-form').append($("<div>").text("INVALID USER ID"));
            }
            else if (response == "true") {
                loadTasks(_userId);
            }
        },
        error: function (data, status, error) {
            alert("Error data: " + data + "\nStatus: " + status + "\nError message:" + error);
        },
    });
}


function loadTasks(_userId) {
    _tasksRating = {};
    $.ajax({
        url: "offline-mt-ranking-servlet",
        type: 'POST',
        headers: {"operation": "loadTasks"},
        dataType: 'json',
        data: {
            userId: _userId,
            maxTasksAssigned: _maxTasksAssigned
        },
        success: function (response) {
            $("#tasks-buttons").empty();
            _listOfTasks = JSON.parse(response.tasks);
            let $tasks_list = $('.tasks-list');
            $tasks_list.empty();
            for (let i = 0; i < Object.keys(_listOfTasks).length; i++) {
                let $task = $("<a class = 'task-a-element' id = \'" + i + "\' onclick=\'showTaskWithNumber(" + i + ")\'>" +
                    "Task " + i + " <img id='tasks-indicator' " +
                    "src='../resources/img/question-circle-solid.svg' /></a>");
                $tasks_list.append($task).append("<br>");
                _tasksRating[i] = 0;
            }
            $("#tasks-buttons").append("<button id = 'next-batch-button' class = 'submit-button' " +
                "type = 'button' onclick = \'loadTasks(\"" + _userId + "\")\'>Next Batch</button>");
            if (Object.keys(_listOfTasks).length > 0) {
                showTaskWithNumber(0);
            } else {
                $tasks_list.append("<a>The are no more available tasks in the database.</a>");
            }
        },
        error: function (data, status, error) {
            alert("Error data: " + data + "\nStatus: " + status + "\nError message:" + error);
        },
    });
}

function showTaskWithNumber(taskNumber) {
    _startTimeOfCurrentTask = getCurrentTime();
    _currentTask = JSON.parse(_listOfTasks[taskNumber]);
    let $current_task_details = $("#current-task-details");
    $current_task_details.empty();
    $current_task_details.append("<div>Client ID: " + _currentTask.clientId + "</div>");
    $current_task_details.append("<div>Device type: " + _currentTask.deviceType + "</div>");
    $current_task_details.append("<div>Language code: " + _currentTask.language_code + "</div>");
    let $rating_interface_block = $("#rating-interface-block");
    $rating_interface_block.empty();
    let turns = JSON.parse(_currentTask.turns);
    for (let i in Object.keys(turns)) {
        let turn = JSON.parse(turns[i]);
        if (turn.request != null) {
            $rating_interface_block.append($('<div id="request"/>')
                .append("<i class = 'date-utternace'>" + new Date(turn.requestTime_seconds * 1000).toLocaleString() + "</i>" + "<br>" + turn.request));
        }
        if (turn.response != null) {
            $rating_interface_block.append($('<div id="response"/>')
                .append("<i class = 'date-utternace'>" + new Date(turn.responseTime_seconds * 1000).toLocaleString() + "</i>" + "<br>" + turn.response));
        }
    }
    createMtRating(taskNumber);
    deselectStars(0, taskNumber)
}

function rateTask(taskNumber, starNumber) {
    let $ratingDiv = $("#current-rating");
    $.ajax({
        url: "offline-mt-ranking-servlet",
        type: 'POST',
        headers: {"operation": "rateTask"},
        dataType: 'text',
        data: {
            startTime_seconds: _startTimeOfCurrentTask,
            endTime_seconds: getCurrentTime(),
            ratingScore: starNumber +1,
            userId: _userId,
            taskId: _currentTask.taskId
        },
        success: function () {
            _tasksRating[taskNumber] = starNumber + 1;
            $(".tasks-list").find('a[id="' + taskNumber + '"]')
                .find('img[id="tasks-indicator"]')[0].src = '../resources/img/check-solid.svg';
            $ratingDiv.find('img[id="rating-indicator"]')[0].src = '../resources/img/check-solid.svg';
            deselectStars(starNumber + 1, taskNumber)
        },
        error: function (data, status, error) {
            alert("Error data: " + data + "\nStatus: " + status + "\nError message:" + error);
        },
    });
}

function createMtRating(taskNumber) {
    let rating = _tasksRating[taskNumber];
    let $rating = $('<div class = "rating" id = "current-rating">');
    for (let numberOfStars = 0; numberOfStars < _ratingScaleOffline; numberOfStars++) {
        $rating.append("<img id='star-rating' src='../resources/img/star-regular.svg' " +
            "onmouseover= \'selectStars(" + numberOfStars + ")\' " +
            "onmouseout = \'deselectStars(" + numberOfStars + ", " + taskNumber + ")\' " +
            "onclick=\'rateTask(" + taskNumber + ", " + numberOfStars + ")\' />");
    }
    $rating.append("<img id='rating-indicator' src='../resources/img/question-circle-solid.svg' />");
    $("#rating-interface-block").append($rating);
}


function selectStars(starNumber) {
    let $stars = $("#current-rating").find('img[id="star-rating"]');
    for (let i = 0; i < $stars.length; i++) {
        if (i <= starNumber) {
            $stars[i].src = '../resources/img/star-solid.svg';
        }
        else {
            $stars[i].src = '../resources/img/star-regular.svg';
        }
    }
}

function deselectStars(starNumber, taskNumber) {
    let $ratingDiv = $("#current-rating");
    let stars = $ratingDiv.find('img[id="star-rating"]');
    for (let i = 0; i < stars.length; i++) {
        if (i < _tasksRating[taskNumber]) {
            stars[i].src = '../resources/img/star-solid.svg';
        }
        else {
            stars[i].src = '../resources/img/star-regular.svg';
        }
    }
}

function getCurrentTime() {
    return ((Date.now() / 1000) | 0);
}