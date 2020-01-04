package co.bbt.ref.infrastructure.persistence.doobie

import doobie.LogHandler
import doobie.util.log.{ExecFailure, ProcessingFailure, Success}
import org.slf4j.{Logger, LoggerFactory}

private[doobie] object LoggerHandler {
  val handler: LogHandler = {
    val logger: Logger = LoggerFactory.getLogger(LoggerHandler.getClass)
    LogHandler {
      case Success(s, a, e1, e2) =>
        logger.debug(s"""Successful Statement Execution:
                        |  $s
                        | arguments = [${a.mkString(", ")}]
                        |   elapsed = ${e1.toMillis} ms exec + ${e2.toMillis} ms processing (${(e1 + e2).toMillis} ms total)
      """.stripMargin)

      case ProcessingFailure(s, a, e1, e2, t) =>
        logger.error(s"""Failed Resultset Processing:
                        |  $s
                        | arguments = [${a.mkString(", ")}]
                        |   elapsed = ${e1.toMillis} ms exec + ${e2.toMillis} ms processing (failed) (${(e1 + e2).toMillis} ms total)
                        |   failure = ${t.getMessage}
      """.stripMargin)

      case ExecFailure(s, a, e1, t) =>
        logger.error(s"""Failed Statement Execution:
                        |  $s
                        | arguments = [${a.mkString(", ")}]
                        |   elapsed = ${e1.toMillis} ms exec (failed)
                        |   failure = ${t.getMessage}
      """.stripMargin)
    }
  }
}
