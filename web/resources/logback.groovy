def logDir = System.getProperty("log.dir") ?: "/var/log";
def logFile = "${logDir}/mgmt.log"

println "Using log file: ${logFile}"

appender("FILE", RollingFileAppender) {
    append = true
    file = "${logFile}"

    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${logDir}/mgmt.log.%d{yyyy-MM-dd}.zip"
        maxHistory = 30
        timeBasedFileNamingAndTriggeringPolicy(SizeAndTimeBasedFNATP) {
            maxFileSize = "100MB"
        }
    }
    encoder(PatternLayoutEncoder) {
        pattern = "%date{ISO8601} %level %logger:%line - %msg%n"
    }
}

root(INFO, ["FILE"])
