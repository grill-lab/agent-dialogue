let ratingScale = 5;
var ratingTestingVar = 0;

function createRating(ratingId) {
    let $rating = $('<div class = "rating" id = \"' + ratingId + ratingTestingVar + '\"/>');
    for (let numberOfStars = 0; numberOfStars < ratingScale; numberOfStars++) {
        $rating.append("<img id='star-rating' src='../resources/img/star-regular.svg' " +
            "onmouseover= \'selectStars(" + numberOfStars + ", \"" + ratingId + ratingTestingVar + "\")\' " +
            "onmouseout = \'deselectStars(\"" + ratingId + ratingTestingVar + "\")\' />");
    }
    $rating.append("<img id='rating-indicator' src='../resources/img/question-circle-solid.svg' />")
    $("#output").append($rating);
    ratingTestingVar += 1;
}


function selectStars(starNumber, ratingId) {
    let stars = $("#" + ratingId.toString()).find('img[id="star-rating"]');
    for (i = 0; i < stars.length; i++) {
        if (i <= starNumber) {
            stars[i].src = '../resources/img/star-solid.svg';
        }
        else {
            stars[i].src = '../resources/img/star-regular.svg';
        }
    }
}

function deselectStars(ratingId) {
    let stars = $("#" + ratingId).find('img[id="star-rating"]');
    for (i = 0; i < stars.length; i++) {
        // TODO(Adam): .hasAttribute doesn't exist!
        if ($(stars[i]).hasAttribute("value")) {
            stars[i].src = '../resources/img/star-regular.svg';
        }
        else {
            stars[i].src = '../resources/img/star-solid.svg';
        }
    }
}