function sendRequestToServlet() {
    var textInput = $('textarea#message').val();
    var language = $('input[name=language]:checked', '#language-fieldset').val();
    $('textarea#message').val("");
    $("#output").append($('<div id="request"/>').append(textInput));

    $.ajax({
        url: "java-script-linker",
        type: 'POST',
        dataType: 'text',
        data: {
            textInput: textInput,
            language: language
        },
        success: function (data) {
            $("#output").append($('<div id="response">-  </div>')
                .append(data)
            );
        },
        error: function (data, status, error) {
            alert("Error data: " + data + "\nStatus: " + status + "\nError message:" + error);
        }
    });
}