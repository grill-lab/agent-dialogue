function sendAjax() {
    var textInput = $('#message').val();

    $.ajax({
        url: "java-script-linker",
        type: 'POST',
        dataType: 'text',
        data: textInput,

        success: function (data) {
            $("#output").append($('<tr/>')
                .append(data)
            );
        },
        error: function (data, status, error) {
            alert("Error data: " + data + "\nStatus: " + status + "\nError message:" + error);
        }
    });
}