#' @docType class
#' @title OrganizationSort
#' @description OrganizationSort Class
#' @format An \code{R6Class} generator object
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
OrganizationSort <- R6::R6Class(
  "OrganizationSort",
  public = list(
    #' Initialize a new OrganizationSort class.
    #'
    #' @description
    #' Initialize a new OrganizationSort class.
    #'
    #' @param ... Optional arguments.
    #' @export
    initialize = function(...) {
      local.optional.var <- list(...)
      val <- unlist(local.optional.var)
      enumvec <- .parse_OrganizationSort()

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
    #' @return OrganizationSort in JSON format
    #' @export
    toJSON = function() {
        jsonlite::toJSON(private$value, auto_unbox = TRUE)
    },
    #' Deserialize JSON string into an instance of OrganizationSort
    #'
    #' @description
    #' Deserialize JSON string into an instance of OrganizationSort
    #'
    #' @param input_json the JSON input
    #' @return the instance of OrganizationSort
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
    #' @return OrganizationSort in JSON format
    #' @export
    toJSONString = function() {
      as.character(jsonlite::toJSON(private$value,
          auto_unbox = TRUE))
    },
    #' Deserialize JSON string into an instance of OrganizationSort
    #'
    #' @description
    #' Deserialize JSON string into an instance of OrganizationSort
    #'
    #' @param input_json the JSON input
    #' @return the instance of OrganizationSort
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
.parse_OrganizationSort <- function(vals) {
  res <- gsub("^\\[|\\]$", "", "[challenge_count, created, name, relevance]")
  unlist(strsplit(res, ", "))
}

