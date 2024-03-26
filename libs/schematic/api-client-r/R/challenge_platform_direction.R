#' @docType class
#' @title ChallengePlatformDirection
#' @description ChallengePlatformDirection Class
#' @format An \code{R6Class} generator object
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
ChallengePlatformDirection <- R6::R6Class(
  "ChallengePlatformDirection",
  public = list(
    #' Initialize a new ChallengePlatformDirection class.
    #'
    #' @description
    #' Initialize a new ChallengePlatformDirection class.
    #'
    #' @param ... Optional arguments.
    #' @export
    initialize = function(...) {
      local.optional.var <- list(...)
      val <- unlist(local.optional.var)
      enumvec <- .parse_ChallengePlatformDirection()

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
    #' @return ChallengePlatformDirection in JSON format
    #' @export
    toJSON = function() {
        jsonlite::toJSON(private$value, auto_unbox = TRUE)
    },
    #' Deserialize JSON string into an instance of ChallengePlatformDirection
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengePlatformDirection
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengePlatformDirection
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
    #' @return ChallengePlatformDirection in JSON format
    #' @export
    toJSONString = function() {
      as.character(jsonlite::toJSON(private$value,
          auto_unbox = TRUE))
    },
    #' Deserialize JSON string into an instance of ChallengePlatformDirection
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengePlatformDirection
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengePlatformDirection
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
.parse_ChallengePlatformDirection <- function(vals) {
  res <- gsub("^\\[|\\]$", "", "[asc, desc]")
  unlist(strsplit(res, ", "))
}

