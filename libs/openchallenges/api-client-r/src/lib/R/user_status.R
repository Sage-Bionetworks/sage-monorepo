#' @docType class
#' @title UserStatus
#' @description UserStatus Class
#' @format An \code{R6Class} generator object
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
UserStatus <- R6::R6Class(
  "UserStatus",
  public = list(
    #' Initialize a new UserStatus class.
    #'
    #' @description
    #' Initialize a new UserStatus class.
    #'
    #' @param ... Optional arguments.
    #' @export
    initialize = function(...) {
      local.optional.var <- list(...)
      val <- unlist(local.optional.var)
      enumvec <- .parse_UserStatus()

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
    #' @return UserStatus in JSON format
    #' @export
    toJSON = function() {
        jsonlite::toJSON(private$value, auto_unbox = TRUE)
    },
    #' Deserialize JSON string into an instance of UserStatus
    #'
    #' @description
    #' Deserialize JSON string into an instance of UserStatus
    #'
    #' @param input_json the JSON input
    #' @return the instance of UserStatus
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
    #' @return UserStatus in JSON format
    #' @export
    toJSONString = function() {
      as.character(jsonlite::toJSON(private$value,
          auto_unbox = TRUE))
    },
    #' Deserialize JSON string into an instance of UserStatus
    #'
    #' @description
    #' Deserialize JSON string into an instance of UserStatus
    #'
    #' @param input_json the JSON input
    #' @return the instance of UserStatus
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
.parse_UserStatus <- function(vals) {
  res <- gsub("^\\[|\\]$", "", "[pending, approved, disabled, blacklist]")
  unlist(strsplit(res, ", "))
}

