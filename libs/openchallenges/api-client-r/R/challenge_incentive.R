#' @docType class
#' @title ChallengeIncentive
#' @description ChallengeIncentive Class
#' @format An \code{R6Class} generator object
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
ChallengeIncentive <- R6::R6Class(
  "ChallengeIncentive",
  public = list(
    #' Initialize a new ChallengeIncentive class.
    #'
    #' @description
    #' Initialize a new ChallengeIncentive class.
    #'
    #' @param ... Optional arguments.
    #' @export
    initialize = function(...) {
      local.optional.var <- list(...)
      val <- unlist(local.optional.var)
      enumvec <- .parse_ChallengeIncentive()

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
    #' @return ChallengeIncentive in JSON format
    #' @export
    toJSON = function() {
        jsonlite::toJSON(private$value, auto_unbox = TRUE)
    },
    #' Deserialize JSON string into an instance of ChallengeIncentive
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengeIncentive
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengeIncentive
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
    #' @return ChallengeIncentive in JSON format
    #' @export
    toJSONString = function() {
      as.character(jsonlite::toJSON(private$value,
          auto_unbox = TRUE))
    },
    #' Deserialize JSON string into an instance of ChallengeIncentive
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengeIncentive
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengeIncentive
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
.parse_ChallengeIncentive <- function(vals) {
  res <- gsub("^\\[|\\]$", "", "[monetary, publication, speaking_engagement, other]")
  unlist(strsplit(res, ", "))
}

