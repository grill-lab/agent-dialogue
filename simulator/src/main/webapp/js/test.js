
function test() {
    let params = (new URL(document.location)).searchParams;
    let name = params.get("param");
    $.ajax({
        url: "offline-mt-ranking-servlet",
        type: 'POST',
        dataType: 'text',

        data: {
            param: name
        },

        success: function () {
            alert("success")
        },
        error: function (data, status, error) {
            alert("Error data: " + data + "\nStatus: " + status + "\nError message:" + error);
        },
    });
}