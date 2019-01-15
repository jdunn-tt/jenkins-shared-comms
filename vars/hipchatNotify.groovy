/*
 * Handle sending out notifications for builds.
 *
 * @param fail Determines if a build failure (true) or success message (false) should be sent.
 * @param room HipChat room to send the notifications to.
 * @param errorMessage Message that contails detailed info about error which caused build failure.
 */

def call(success=false, room='TT Development') {
    //Build stage will be at the top level of the pipeline
    //stage("Build Notifications") {

    def status = success ? "SUCCESS" : "FAIL"
    def color = success ? "GREEN" : "RED"

    // Noting that build status is null while running
    def jobHeadline = """${env.JOB_NAME} - Build # ${env.BUILD_NUMBER} - ${status}"""
    def hipChatMessage = "<a href='${env.BUILD_URL}'>${jobHeadline}</a>"

    // Don't spam HipChat with pull request messages
    // Don't spam HipChat with release build where Gradle plugin commits new projects version (commit author = tt-build)
    //if (!BRANCH_NAME.startsWith('PR-') && getCommitAuthor() != 'tt-builds' && !JOB_NAME.startsWith('Tests')) {
        // Send a message to HipChat
        try {
            hipchatSend (
                color: color,
                message: hipChatMessage,
                server: 'api.hipchat.com',
                textFormat: false,
                sendAs: 'Jenkins TTOps',
                v2enabled: false,
                credentialId: 'hip-chat-global-api-token',
                room: room,
                notify: true,
                failOnError: false
            )
        } catch(e) {
            echo "WARNING: sending HipChat message FAILED: ${e.getMessage()}"
        }
}
