def call() {

    try {
        mattermostSend color: 'good', message: 'Message from Jenkins Pipeline', text: 'testing message from pipeline.'
    } catch(e) {
        echo "WARNING: sending HipChat message FAILED: ${e.getMessage()}"
    }

}