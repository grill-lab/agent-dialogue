let _ratingScale = 5;

function updateRating(starNumber, responseId) {
    let $ratingDiv = $("#" + responseId.toString());
    $.ajax({
        url: "java-script-linker",
        type: 'POST',
        headers: {"Operation": "updateRating"},
        dataType: 'text',
        data: {
            ratingScore: starNumber + 1,
            responseId: responseId,
            // TODO(Adam): Implement getting experimentId, utteranceID and requestId.
            experimentId: "to be implemented",
            utteranceId: "",
            requestId: ""
        },
        success: function () {
            $ratingDiv.attr("value", starNumber + 1);
            $ratingDiv.find('img[id="rating-indicator"]')[0].src = '../resources/img/check-solid.svg';
        },
        error: function (data, status, error) {
            alert("Error data: " + data + "\nStatus: " + status + "\nError message:" + error);
        },
    });
}

function createRating(responseId) {
    let $rating = $('<div class = "rating" id = \"' + responseId + '\"/>');
    for (let numberOfStars = 0; numberOfStars < _ratingScale; numberOfStars++) {
        $rating.append("<img id='star-rating' src='../resources/img/star-regular.svg' " +
            "onmouseover= \'selectStars(" + numberOfStars + ", \"" + responseId + "\")\' " +
            "onmouseout = \'deselectStars(\"" + responseId + "\")\' " +
            "onclick=\'updateRating(" + numberOfStars + ",\"" + responseId + "\")\'/>");
    }
    $rating.append("<img id='rating-indicator' src='../resources/img/question-circle-solid.svg' />");
    $("#output").append($rating);
}


function selectStars(starNumber, responseId) {
    let $stars = $("#" + responseId.toString()).find('img[id="star-rating"]');
    for (i = 0; i < $stars.length; i++) {
        if (i <= starNumber) {
            $stars[i].src = '../resources/img/star-solid.svg';
        }
        else {
            $stars[i].src = '../resources/img/star-regular.svg';
        }
    }
}

function deselectStars(responseId) {
    let $ratingDiv = $("#" + responseId.toString());
    let stars = $ratingDiv.find('img[id="star-rating"]');
    let ratingScore = 0;
    if ($($ratingDiv)[0].hasAttribute("value")) {
        ratingScore = $($ratingDiv)[0].getAttribute("value");
    }
    for (i = 0; i < stars.length; i++) {
        if (i < ratingScore) {
            stars[i].src = '../resources/img/star-solid.svg';
        }
        else {
            stars[i].src = '../resources/img/star-regular.svg';
        }
    }
}