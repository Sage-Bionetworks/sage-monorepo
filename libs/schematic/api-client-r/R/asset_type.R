#' @docType class
#' @title AssetType
#' @description AssetType Class
#' @format An \code{R6Class} generator object
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
AssetType <- R6::R6Class(
  "AssetType",
  public = list(
    #' Initialize a new AssetType class.
    #'
    #' @description
    #' Initialize a new AssetType class.
    #'
    #' @param ... Optional arguments.
    #' @export
    initialize = function(...) {
      local.optional.var <- list(...)
      val <- unlist(local.optional.var)
      enumvec <- .parse_AssetType()

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
    #' @return AssetType in JSON format
    #' @export
    toJSON = function() {
        jsonlite::toJSON(private$value, auto_unbox = TRUE)
    },
    #' Deserialize JSON string into an instance of AssetType
    #'
    #' @description
    #' Deserialize JSON string into an instance of AssetType
    #'
    #' @param input_json the JSON input
    #' @return the instance of AssetType
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
    #' @return AssetType in JSON format
    #' @export
    toJSONString = function() {
      as.character(jsonlite::toJSON(private$value,
          auto_unbox = TRUE))
    },
    #' Deserialize JSON string into an instance of AssetType
    #'
    #' @description
    #' Deserialize JSON string into an instance of AssetType
    #'
    #' @param input_json the JSON input
    #' @return the instance of AssetType
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
.parse_AssetType <- function(vals) {
  res <- gsub("^\\[|\\]$", "", "[synapse]")
  unlist(strsplit(res, ", "))
}

