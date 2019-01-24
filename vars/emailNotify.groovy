 void call(success=false, errorMessage='') {
    //Build stage will be at the top level of the pipeline
    //stage("Build Notifications") {
 
    // Send an email to every developers's @timetrade.com address, who checked in code for the last build
    def CORPORATE_DOMAIN = '@timetrade.com'

    try {
        // Returns string containing recipients separated by whitespace
        def recipients = emailextrecipients([[$class: 'DevelopersRecipientProvider']])

        echo 'into try'
        echo recipients

        if (recipients != null && !recipients.isEmpty()) {
            def recipientsArray = recipients.split(' ')
            def timetradeRecipients = ""

            for (def recipient_index = 0; recipient_index < recipientsArray.length; recipient_index++) {
                if (recipientsArray[recipient_index].endsWith(CORPORATE_DOMAIN)) {
                    timetradeRecipients += recipientsArray[recipient_index] + ' '
                }
            }
            timetradeRecipients = timetradeRecipients.trim()


            def status = success ? "SUCCESS" : "FAIL"
            def jobHeadline = "${env.JOB_NAME} - Build # ${env.BUILD_NUMBER} - ${status}"

            if (!(timetradeRecipients.length() > 0) || getCommitAuthorsEmail().endsWith(CORPORATE_DOMAIN)) {
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
    } catch(e) {
        echo "WARNING: sending email message FAILED: ${e.getMessage()}"
    }
 }

def getCommitAuthorsEmail(gitCommitId = '') {
    if (gitCommitId == '') {
        gitCommitId = getCommitID()
    }
    def commitAuthorsEmail = sh(returnStdout: true, script: "git --no-pager show -s --format='%ae' $gitCommitId").trim()
    echo "Found Git commit author's email: ${commitAuthorsEmail}"
    return commitAuthorsEmail
}

def getCommitID() {
    gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
    echo "Found GIT commit ID $gitCommit"
    return gitCommit
}