let ratingScale = 5;

function updateRating(starNumber, ratingId) {
    let $ratingDiv = $("#" + ratingId.toString());
    $.ajax({
        url: "java-script-linker",
        type: 'POST',
        headers: {"Operation": "updateRating"},
        dataType: 'json',
        data: {
            ratingScore: starNumber + 1,
            ratingId: ratingId
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

function createRating(ratingId) {
    let $rating = $('<div class = "rating" id = \"' + ratingId + '\"/>');
    for (let numberOfStars = 0; numberOfStars < ratingScale; numberOfStars++) {
        $rating.append("<img id='star-rating' src='../resources/img/star-regular.svg' " +
            "onmouseover= \'selectStars(" + numberOfStars + ", \"" + ratingId + "\")\' " +
            "onmouseout = \'deselectStars(\"" + ratingId + "\")\' " +
            "onclick=\'updateRating(" + numberOfStars + ",\"" + ratingId + "\")\'/>");
    }
    $rating.append("<img id='rating-indicator' src='../resources/img/question-circle-solid.svg' />");
    $("#output").append($rating);
}


function selectStars(starNumber, ratingId) {
    let $stars = $("#" + ratingId.toString()).find('img[id="star-rating"]');
    for (i = 0; i < $stars.length; i++) {
        if (i <= starNumber) {
            $stars[i].src = '../resources/img/star-solid.svg';
        }
        else {
            $stars[i].src = '../resources/img/star-regular.svg';
        }
    }
}

function deselectStars(ratingId) {
    let $ratingDiv = $("#" + ratingId.toString());
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