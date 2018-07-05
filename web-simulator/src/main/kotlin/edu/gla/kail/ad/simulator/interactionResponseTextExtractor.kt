package edu.gla.kail.ad.simulator

import edu.gla.kail.ad.Client.InteractionResponse

fun interactionResponseTextExtractor(interactionResponse: InteractionResponse) =
        """
<div class="agentResponse">
    <div class="rowOfTableResponses">
        <table class="table">
            <tbody>
                <tr> <td>Response ID: </td> <td>${interactionResponse.responseId}</td> </tr>
                <tr> <td>Time: </td> <td>${interactionResponse.time}</td> </tr>
                <tr> <td>Client ID: </td> <td>${interactionResponse.clientId}</td> </tr>
                <tr> <td>User ID: </td> <td>${interactionResponse.userID}</td> </tr>
                <tr> <td>Message Status: </td> <td>${interactionResponse.messageStatus}</td> </tr>
                <tr> <td>Optional Error message: </td> <td>${interactionResponse.errorMessage}</td> </tr>
                <tr> <td>Response: </td> <td>${interactionResponse.interactionList}</td> </tr>
            </tbody>
        </table>
    </div>
</div>
"""