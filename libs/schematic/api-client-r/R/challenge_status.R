#' @docType class
#' @title ChallengeStatus
#' @description ChallengeStatus Class
#' @format An \code{R6Class} generator object
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
ChallengeStatus <- R6::R6Class(
  "ChallengeStatus",
  public = list(
    #' Initialize a new ChallengeStatus class.
    #'
    #' @description
    #' Initialize a new ChallengeStatus class.
    #'
    #' @param ... Optional arguments.
    #' @export
    initialize = function(...) {
      local.optional.var <- list(...)
      val <- unlist(local.optional.var)
      enumvec <- .parse_ChallengeStatus()

      stopifnot(length(val) == 1L)

      if (!val %in% enumvec)
          stop("Use one of the valid values: ",
              paste0(enumvec, collapse = ", "))
      private$value <- val
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ChallengeStatus in JSON format
    #' @export
    toJSON = function() {
        jsonlite::toJSON(private$value, auto_unbox = TRUE)
    },
    #' Deserialize JSON string into an instance of ChallengeStatus
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengeStatus
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengeStatus
    #' @export
    fromJSON = function(input_json) {
      private$value <- jsonlite::fromJSON(input_json,
          simplifyVector = FALSE)
      self
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ChallengeStatus in JSON format
    #' @export
    toJSONString = function() {
      as.character(jsonlite::toJSON(private$value,
          auto_unbox = TRUE))
    },
    #' Deserialize JSON string into an instance of ChallengeStatus
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengeStatus
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengeStatus
    #' @export
    fromJSONString = function(input_json) {
      private$value <- jsonlite::fromJSON(input_json,
          simplifyVector = FALSE)
      self
    }
  ),
  private = list(
    value = NULL
  )
)

# add to utils.R
.parse_ChallengeStatus <- function(vals) {
  res <- gsub("^\\[|\\]$", "", "[upcoming, active, completed]")
  unlist(strsplit(res, ", "))
}

