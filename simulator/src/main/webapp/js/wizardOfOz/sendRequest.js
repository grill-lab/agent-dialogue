var _awaitingResponses = 0;

function addUserToUrl() {
    let basicUrl = (new URL(document.location)).origin + (new URL(document.location)).pathname;
    userId = document.getElementById("userId").value;
    window.location.replace(basicUrl + "?user=" + userId + "&conversation=" + _conversationId);
}

function chooseConversation() {
    let basicUrl = (new URL(document.location)).origin + (new URL(document.location)).pathname;
    conversationId = document.getElementById("conversationId").value;
    window.location.replace(basicUrl + "?user=" + _userId + "&conversation=" + conversationId);
}

function loadConversation(_userId, _conversationId) {

}


/**
 * Send message request to servlet when the Submit button is pressed.
 * Use AJAX.
 */
function sendRequest() {
    // TODO: only if the userid and conversation id are specified


    let textInput = $('textarea#message').val();
    let language = $('input[name=language]:checked', '#language-form').val();
    let createRatingBool = $('input[name=rating-enabled]:checked', '#rating-options-form').val();
    $('textarea#message').val("");
    $("#conversation-panel").append($('<div id="request"/>').append(textInput));
    _awaitingResponses += 1;
    $('#awaiting-responses').text(_awaitingResponses);
    $.ajax({
        url: "ad-client-service-servlet",
        type: 'POST',
        headers: {"Operation": "sendRequestToAgents"},
        dataType: 'json',
        data: {
            textInput: textInput,
            language: language,
            chosen_agents: "WizardOfOz",
            agent_request_parameters: ""
        },
        success: function (response) {
            $("#conversation-panel").append($('<div id="response">-  </div>')
                .append(response.message)
            );
            if (createRatingBool == "true") {
                createRating(response.responseId);
            }
            requestDetails = response.interactionRequest;
            responseDetails = response.interactionResponse;
        },
        error: function (data, status, error) {
            alert("Error data: " + data + "\nStatus: " + status + "\nError message:" + error);
        },
        complete: function () {
            _awaitingResponses -= 1;
            $('#awaiting-responses').text(_awaitingResponses);
        }
    });
}