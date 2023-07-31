#' @docType class
#' @title ChallengeDifficulty
#' @description ChallengeDifficulty Class
#' @format An \code{R6Class} generator object
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
ChallengeDifficulty <- R6::R6Class(
  "ChallengeDifficulty",
  public = list(
    #' Initialize a new ChallengeDifficulty class.
    #'
    #' @description
    #' Initialize a new ChallengeDifficulty class.
    #'
    #' @param ... Optional arguments.
    #' @export
    initialize = function(...) {
      local.optional.var <- list(...)
      val <- unlist(local.optional.var)
      enumvec <- .parse_ChallengeDifficulty()

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
    #' @return ChallengeDifficulty in JSON format
    #' @export
    toJSON = function() {
        jsonlite::toJSON(private$value, auto_unbox = TRUE)
    },
    #' Deserialize JSON string into an instance of ChallengeDifficulty
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengeDifficulty
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengeDifficulty
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
    #' @return ChallengeDifficulty in JSON format
    #' @export
    toJSONString = function() {
      as.character(jsonlite::toJSON(private$value,
          auto_unbox = TRUE))
    },
    #' Deserialize JSON string into an instance of ChallengeDifficulty
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengeDifficulty
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengeDifficulty
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
.parse_ChallengeDifficulty <- function(vals) {
  res <- gsub("^\\[|\\]$", "", "[good_for_beginners, intermediate, advanced]")
  unlist(strsplit(res, ", "))
}

