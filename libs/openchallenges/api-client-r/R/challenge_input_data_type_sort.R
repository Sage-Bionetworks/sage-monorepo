#' @docType class
#' @title ChallengeInputDataTypeSort
#' @description ChallengeInputDataTypeSort Class
#' @format An \code{R6Class} generator object
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
ChallengeInputDataTypeSort <- R6::R6Class(
  "ChallengeInputDataTypeSort",
  public = list(
    #' Initialize a new ChallengeInputDataTypeSort class.
    #'
    #' @description
    #' Initialize a new ChallengeInputDataTypeSort class.
    #'
    #' @param ... Optional arguments.
    #' @export
    initialize = function(...) {
      local.optional.var <- list(...)
      val <- unlist(local.optional.var)
      enumvec <- .parse_ChallengeInputDataTypeSort()

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
    #' @return ChallengeInputDataTypeSort in JSON format
    #' @export
    toJSON = function() {
        jsonlite::toJSON(private$value, auto_unbox = TRUE)
    },
    #' Deserialize JSON string into an instance of ChallengeInputDataTypeSort
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengeInputDataTypeSort
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengeInputDataTypeSort
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
    #' @return ChallengeInputDataTypeSort in JSON format
    #' @export
    toJSONString = function() {
      as.character(jsonlite::toJSON(private$value,
          auto_unbox = TRUE))
    },
    #' Deserialize JSON string into an instance of ChallengeInputDataTypeSort
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengeInputDataTypeSort
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengeInputDataTypeSort
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
.parse_ChallengeInputDataTypeSort <- function(vals) {
  res <- gsub("^\\[|\\]$", "", "[name, relevance]")
  unlist(strsplit(res, ", "))
}

