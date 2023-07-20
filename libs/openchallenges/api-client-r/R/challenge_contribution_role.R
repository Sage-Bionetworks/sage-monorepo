#' @docType class
#' @title ChallengeContributionRole
#' @description ChallengeContributionRole Class
#' @format An \code{R6Class} generator object
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
ChallengeContributionRole <- R6::R6Class(
  "ChallengeContributionRole",
  public = list(
    #' Initialize a new ChallengeContributionRole class.
    #'
    #' @description
    #' Initialize a new ChallengeContributionRole class.
    #'
    #' @param ... Optional arguments.
    #' @export
    initialize = function(...) {
      local.optional.var <- list(...)
      val <- unlist(local.optional.var)
      enumvec <- .parse_ChallengeContributionRole()

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
    #' @return ChallengeContributionRole in JSON format
    #' @export
    toJSON = function() {
        jsonlite::toJSON(private$value, auto_unbox = TRUE)
    },
    #' Deserialize JSON string into an instance of ChallengeContributionRole
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengeContributionRole
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengeContributionRole
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
    #' @return ChallengeContributionRole in JSON format
    #' @export
    toJSONString = function() {
      as.character(jsonlite::toJSON(private$value,
          auto_unbox = TRUE))
    },
    #' Deserialize JSON string into an instance of ChallengeContributionRole
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengeContributionRole
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengeContributionRole
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
.parse_ChallengeContributionRole <- function(vals) {
  res <- gsub("^\\[|\\]$", "", "[challenge_organizer, data_contributor, sponsor]")
  unlist(strsplit(res, ", "))
}

