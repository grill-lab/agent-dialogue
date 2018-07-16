var awaitingResponses = 0;

function sendRequestToServlet() {
    var textInput = $('textarea#message').val();
    var language = $('input[name=language]:checked', '#language-form').val();
    $('textarea#message').val("");
    $("#output").append($('<div id="request"/>').append(textInput));
    awaitingResponses += 1;
    $('#awaiting-responses').text(awaitingResponses);
    $.ajax({
        url: "java-script-linker",
        type: 'POST',
        dataType: 'json',
        data: {
            textInput: textInput,
            language: language
        },
        success: function (response) {
            $("#output").append($('<div id="response">-  </div>')
                .append(response.message)
            );
            if ($('input[name=rating-enabled]:checked', '#rating-options-form').val()) {
                // TODO(Adam): Create a method to assigning rating id!
                createRating("ratingId1");
            }
            requestDetails = response.interactionRequest;
            responseDetails = response.interactionResponse;
        },
        error: function (data, status, error) {
            alert("Error data: " + data + "\nStatus: " + status + "\nError message:" + error);
        },
        complete: function () {
            awaitingResponses -= 1;
            $('#awaiting-responses').text(awaitingResponses);
        }
    });
}