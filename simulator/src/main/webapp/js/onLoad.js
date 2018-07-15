/**
 * Do on loading HTML.
 */
$(document).ready(function () {
    populateLanguages();
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
        var $input = $('<input type="radio">').attr({
            id: parameters.value,
            name: parameters.name,
            value: parameters.value
        });

        $languageFieldset.append($label)
            .append($input)

        $('.options').append($languageFieldset);
    }
}

