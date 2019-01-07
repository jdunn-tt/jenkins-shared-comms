 def call(errorMessage='') {
    //Build stage will be at the top level of the pipeline
    //stage("Build Notifications") {
 
    // Send an email to every developers's @timetrade.com address, who checked in code for the last build
    def CORPORATE_DOMAIN = '@timetrade.com'

    try {
        // Returns string containing recipients separated by whitespace
        def recipients = emailextrecipients([[$class: 'DevelopersRecipientProvider']])
        //Testing
        echo recipients

        if (recipients != null && !recipients.isEmpty()) {
            def recipientsArray = recipients.split(' ')
            // timetradeRecipients = Arrays.asList(recipientsArray).contains(CORPORATE_DOMAIN)
            def timetradeRecipients = ""

            for (def recipient_index = 0; recipient_index < recipientsArray.length; recipient_index++) {
                if (recipientsArray[recipient_index].endsWith(CORPORATE_DOMAIN)) {
                    timetradeRecipients += recipientsArray[recipient_index] + ' '
                }
            }
            timetradeRecipients = timetradeRecipients.trim()

            if (!(timetradeRecipients.length() > 0) || getCommitAuthorsEmail.endsWith(CORPORATE_DOMAIN)) {
                emailext (
                    to: (timetradeRecipients.length() > 0) ? getCommitAuthorsEmail() : timetradeRecipients,
                    subject: jobHeadline + "!",
                    body: """<p>${jobHeadline}:</p>
                        <p>Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a> to view the results.</p>
                        <p>${errorMessage}</p>"""
                )
            } else {
                echo "WARNING: email notification was NOT sent: any of contributors emails doesn't belong to TimeTrade!"
            }
        }
        // } else {
        //     def recipient = getCommitAuthorsEmail()

        //     if (recipient.endsWith(CORPORATE_DOMAIN)) {
        //         emailext (
        //             to: recipient,
        //             subject: jobHeadline + "!",
        //             body: """<p>${jobHeadline}:</p>
        //                 <p>Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a> to view the results.</p>
        //                 <p>${errorMessage}</p>"""
        //         )
        //     } else {
        //         echo "WARNING: email notification was NOT sent: commit author's email doesn't belong to TimeTrade!"
        //     }
        // }
    } catch(e) {
        echo "WARNING: sending email message FAILED: ${e.getMessage()}"
    }
 }

 /*
 * Retrieves the Git commit author's email for the given commit ID.
 *
 * @param gitCommitId String Git commit ID.
 *
 * @return String Git commit author's email.
 */

def getCommitAuthorsEmail(gitCommitId = '') {
    if (gitCommitId == '') {
        gitCommitId = getCommitID()
    }
    def commitAuthorsEmail = sh(returnStdout: true, script: "git --no-pager show -s --format='%ae' $gitCommitId").trim()
    echo "Found Git commit author's email: ${commitAuthorsEmail}"
    return commitAuthorsEmail
}