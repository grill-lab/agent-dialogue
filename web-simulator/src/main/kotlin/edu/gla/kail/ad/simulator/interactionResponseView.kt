package edu.gla.kail.ad.simulator

import edu.gla.kail.ad.Client.InteractionResponse

fun interactionResponseView(responses: List<InteractionResponse>) =
        """
<html>
    <head>
        <title>Responses</title>
    </head>
    <body>
        <div class="container">
            <div class="row">
                <div class="responses_all">
                    ${responses.map { interactionResponseTextExtractor(it) }.joinToString("\n")}
                </div>
            </div>
        </div>
    </body>
</html>
"""