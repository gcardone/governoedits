appender("STDOUT",  ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  }
}


appender("EVENTS", RollingFileAppender) {

  rollingPolicy(TimeBasedRollingPolicy) {
    fileNamePattern = "logs/events/%d.gz"
  } 

  encoder(PatternLayoutEncoder) {
    pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  }
}

appender("EDITS", RollingFileAppender) {

  rollingPolicy(TimeBasedRollingPolicy) {
    fileNamePattern = "logs/edits/%d{yyyy-MM}.gz"
  } 

  encoder(PatternLayoutEncoder) {
    pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  }
}

root(DEBUG, ["STDOUT"])

logger("it.governoedits", DEBUG, ["STDOUT","EVENTS"])

logger("it.governoedits.handlers.SimpleLoggerHandler", INFO, ["EDITS"])

