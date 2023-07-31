#' @docType class
#' @title OrganizationCategory
#' @description OrganizationCategory Class
#' @format An \code{R6Class} generator object
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
OrganizationCategory <- R6::R6Class(
  "OrganizationCategory",
  public = list(
    #' Initialize a new OrganizationCategory class.
    #'
    #' @description
    #' Initialize a new OrganizationCategory class.
    #'
    #' @param ... Optional arguments.
    #' @export
    initialize = function(...) {
      local.optional.var <- list(...)
      val <- unlist(local.optional.var)
      enumvec <- .parse_OrganizationCategory()

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
    #' @return OrganizationCategory in JSON format
    #' @export
    toJSON = function() {
        jsonlite::toJSON(private$value, auto_unbox = TRUE)
    },
    #' Deserialize JSON string into an instance of OrganizationCategory
    #'
    #' @description
    #' Deserialize JSON string into an instance of OrganizationCategory
    #'
    #' @param input_json the JSON input
    #' @return the instance of OrganizationCategory
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
    #' @return OrganizationCategory in JSON format
    #' @export
    toJSONString = function() {
      as.character(jsonlite::toJSON(private$value,
          auto_unbox = TRUE))
    },
    #' Deserialize JSON string into an instance of OrganizationCategory
    #'
    #' @description
    #' Deserialize JSON string into an instance of OrganizationCategory
    #'
    #' @param input_json the JSON input
    #' @return the instance of OrganizationCategory
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
.parse_OrganizationCategory <- function(vals) {
  res <- gsub("^\\[|\\]$", "", "[featured]")
  unlist(strsplit(res, ", "))
}

