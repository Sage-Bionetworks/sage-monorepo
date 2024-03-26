#' @docType class
#' @title OrganizationDirection
#' @description OrganizationDirection Class
#' @format An \code{R6Class} generator object
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
OrganizationDirection <- R6::R6Class(
  "OrganizationDirection",
  public = list(
    #' Initialize a new OrganizationDirection class.
    #'
    #' @description
    #' Initialize a new OrganizationDirection class.
    #'
    #' @param ... Optional arguments.
    #' @export
    initialize = function(...) {
      local.optional.var <- list(...)
      val <- unlist(local.optional.var)
      enumvec <- .parse_OrganizationDirection()

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
    #' @return OrganizationDirection in JSON format
    #' @export
    toJSON = function() {
        jsonlite::toJSON(private$value, auto_unbox = TRUE)
    },
    #' Deserialize JSON string into an instance of OrganizationDirection
    #'
    #' @description
    #' Deserialize JSON string into an instance of OrganizationDirection
    #'
    #' @param input_json the JSON input
    #' @return the instance of OrganizationDirection
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
    #' @return OrganizationDirection in JSON format
    #' @export
    toJSONString = function() {
      as.character(jsonlite::toJSON(private$value,
          auto_unbox = TRUE))
    },
    #' Deserialize JSON string into an instance of OrganizationDirection
    #'
    #' @description
    #' Deserialize JSON string into an instance of OrganizationDirection
    #'
    #' @param input_json the JSON input
    #' @return the instance of OrganizationDirection
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
.parse_OrganizationDirection <- function(vals) {
  res <- gsub("^\\[|\\]$", "", "[asc, desc]")
  unlist(strsplit(res, ", "))
}

