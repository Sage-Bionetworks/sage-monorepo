#' @docType class
#' @title ChallengeSort
#' @description ChallengeSort Class
#' @format An \code{R6Class} generator object
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
ChallengeSort <- R6::R6Class(
  "ChallengeSort",
  public = list(
    #' Initialize a new ChallengeSort class.
    #'
    #' @description
    #' Initialize a new ChallengeSort class.
    #'
    #' @param ... Optional arguments.
    #' @export
    initialize = function(...) {
      local.optional.var <- list(...)
      val <- unlist(local.optional.var)
      enumvec <- .parse_ChallengeSort()

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
    #' @return ChallengeSort in JSON format
    #' @export
    toJSON = function() {
        jsonlite::toJSON(private$value, auto_unbox = TRUE)
    },
    #' Deserialize JSON string into an instance of ChallengeSort
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengeSort
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengeSort
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
    #' @return ChallengeSort in JSON format
    #' @export
    toJSONString = function() {
      as.character(jsonlite::toJSON(private$value,
          auto_unbox = TRUE))
    },
    #' Deserialize JSON string into an instance of ChallengeSort
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengeSort
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengeSort
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
.parse_ChallengeSort <- function(vals) {
  res <- gsub("^\\[|\\]$", "", "[created, ending_soon, recently_ended, recently_started, relevance, starred, starting_soon]")
  unlist(strsplit(res, ", "))
}

