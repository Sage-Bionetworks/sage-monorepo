#' @docType class
#' @title ChallengeSubmissionType
#' @description ChallengeSubmissionType Class
#' @format An \code{R6Class} generator object
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
ChallengeSubmissionType <- R6::R6Class(
  "ChallengeSubmissionType",
  public = list(
    #' Initialize a new ChallengeSubmissionType class.
    #'
    #' @description
    #' Initialize a new ChallengeSubmissionType class.
    #'
    #' @param ... Optional arguments.
    #' @export
    initialize = function(...) {
      local.optional.var <- list(...)
      val <- unlist(local.optional.var)
      enumvec <- .parse_ChallengeSubmissionType()

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
    #' @return ChallengeSubmissionType in JSON format
    #' @export
    toJSON = function() {
        jsonlite::toJSON(private$value, auto_unbox = TRUE)
    },
    #' Deserialize JSON string into an instance of ChallengeSubmissionType
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengeSubmissionType
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengeSubmissionType
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
    #' @return ChallengeSubmissionType in JSON format
    #' @export
    toJSONString = function() {
      as.character(jsonlite::toJSON(private$value,
          auto_unbox = TRUE))
    },
    #' Deserialize JSON string into an instance of ChallengeSubmissionType
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengeSubmissionType
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengeSubmissionType
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
.parse_ChallengeSubmissionType <- function(vals) {
  res <- gsub("^\\[|\\]$", "", "[container_image, prediction_file, notebook, other]")
  unlist(strsplit(res, ", "))
}

