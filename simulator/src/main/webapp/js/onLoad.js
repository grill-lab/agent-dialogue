/**
 * Do on loading HTML.
 */
$(document).ready(function () {
    populateLanguages();
    $('#awaiting-responses').text(0);
});


// Populate languages from LANGUAGES dictionary.
let LANGUAGES = {
    AMERICAN_ENGLISH: {value: "en-US", name: "American English"},
    BRITISH_ENGLISH: {value: "en-UK", name: "British English"},
};

function populateLanguages() {
    var $languageFieldset = $('<form id = "language-fieldset">').append("<h5><legend>Language:</legend></h5>");

    for (var language in LANGUAGES) {
        var parameters = LANGUAGES[language];
        var $label = $("<label for=" + parameters.value + ">").text(parameters.name);
        var $input = $('<input type="radio" name="language">').attr({
            id: parameters.value,
            value: parameters.value
        });

        $languageFieldset.append($label)
            .append($input)

        $('.options').append($languageFieldset);
    }
    $("#" + LANGUAGES.AMERICAN_ENGLISH.value).prop("checked", true);
}

