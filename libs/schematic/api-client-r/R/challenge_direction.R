#' @docType class
#' @title ChallengeDirection
#' @description ChallengeDirection Class
#' @format An \code{R6Class} generator object
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
ChallengeDirection <- R6::R6Class(
  "ChallengeDirection",
  public = list(
    #' Initialize a new ChallengeDirection class.
    #'
    #' @description
    #' Initialize a new ChallengeDirection class.
    #'
    #' @param ... Optional arguments.
    #' @export
    initialize = function(...) {
      local.optional.var <- list(...)
      val <- unlist(local.optional.var)
      enumvec <- .parse_ChallengeDirection()

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
    #' @return ChallengeDirection in JSON format
    #' @export
    toJSON = function() {
        jsonlite::toJSON(private$value, auto_unbox = TRUE)
    },
    #' Deserialize JSON string into an instance of ChallengeDirection
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengeDirection
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengeDirection
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
    #' @return ChallengeDirection in JSON format
    #' @export
    toJSONString = function() {
      as.character(jsonlite::toJSON(private$value,
          auto_unbox = TRUE))
    },
    #' Deserialize JSON string into an instance of ChallengeDirection
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengeDirection
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengeDirection
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
.parse_ChallengeDirection <- function(vals) {
  res <- gsub("^\\[|\\]$", "", "[asc, desc]")
  unlist(strsplit(res, ", "))
}

