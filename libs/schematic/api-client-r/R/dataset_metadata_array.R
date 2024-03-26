#' Create a new DatasetMetadataArray
#'
#' @description
#' An array of dataset metadata.
#'
#' @docType class
#' @title DatasetMetadataArray
#' @description DatasetMetadataArray Class
#' @format An \code{R6Class} generator object
#' @field datasets An array of dataset meatdata. list(\link{DatasetMetadata}) [optional]
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
DatasetMetadataArray <- R6::R6Class(
  "DatasetMetadataArray",
  public = list(
    `datasets` = NULL,
    #' Initialize a new DatasetMetadataArray class.
    #'
    #' @description
    #' Initialize a new DatasetMetadataArray class.
    #'
    #' @param datasets An array of dataset meatdata.
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`datasets` = NULL, ...) {
      if (!is.null(`datasets`)) {
        stopifnot(is.vector(`datasets`), length(`datasets`) != 0)
        sapply(`datasets`, function(x) stopifnot(R6::is.R6(x)))
        self$`datasets` <- `datasets`
      }
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return DatasetMetadataArray in JSON format
    #' @export
    toJSON = function() {
      DatasetMetadataArrayObject <- list()
      if (!is.null(self$`datasets`)) {
        DatasetMetadataArrayObject[["datasets"]] <-
          lapply(self$`datasets`, function(x) x$toJSON())
      }
      DatasetMetadataArrayObject
    },
    #' Deserialize JSON string into an instance of DatasetMetadataArray
    #'
    #' @description
    #' Deserialize JSON string into an instance of DatasetMetadataArray
    #'
    #' @param input_json the JSON input
    #' @return the instance of DatasetMetadataArray
    #' @export
    fromJSON = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      if (!is.null(this_object$`datasets`)) {
        self$`datasets` <- ApiClient$new()$deserializeObj(this_object$`datasets`, "array[DatasetMetadata]", loadNamespace("openapi"))
      }
      self
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return DatasetMetadataArray in JSON format
    #' @export
    toJSONString = function() {
      jsoncontent <- c(
        if (!is.null(self$`datasets`)) {
          sprintf(
          '"datasets":
          [%s]
',
          paste(sapply(self$`datasets`, function(x) jsonlite::toJSON(x$toJSON(), auto_unbox = TRUE, digits = NA)), collapse = ",")
          )
        }
      )
      jsoncontent <- paste(jsoncontent, collapse = ",")
      json_string <- as.character(jsonlite::minify(paste("{", jsoncontent, "}", sep = "")))
    },
    #' Deserialize JSON string into an instance of DatasetMetadataArray
    #'
    #' @description
    #' Deserialize JSON string into an instance of DatasetMetadataArray
    #'
    #' @param input_json the JSON input
    #' @return the instance of DatasetMetadataArray
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`datasets` <- ApiClient$new()$deserializeObj(this_object$`datasets`, "array[DatasetMetadata]", loadNamespace("openapi"))
      self
    },
    #' Validate JSON input with respect to DatasetMetadataArray
    #'
    #' @description
    #' Validate JSON input with respect to DatasetMetadataArray and throw an exception if invalid
    #'
    #' @param input the JSON input
    #' @export
    validateJSON = function(input) {
      input_json <- jsonlite::fromJSON(input)
    },
    #' To string (JSON format)
    #'
    #' @description
    #' To string (JSON format)
    #'
    #' @return String representation of DatasetMetadataArray
    #' @export
    toString = function() {
      self$toJSONString()
    },
    #' Return true if the values in all fields are valid.
    #'
    #' @description
    #' Return true if the values in all fields are valid.
    #'
    #' @return true if the values in all fields are valid.
    #' @export
    isValid = function() {
      TRUE
    },
    #' Return a list of invalid fields (if any).
    #'
    #' @description
    #' Return a list of invalid fields (if any).
    #'
    #' @return A list of invalid fields (if any).
    #' @export
    getInvalidFields = function() {
      invalid_fields <- list()
      invalid_fields
    },
    #' Print the object
    #'
    #' @description
    #' Print the object
    #'
    #' @export
    print = function() {
      print(jsonlite::prettify(self$toJSONString()))
      invisible(self)
    }
  ),
  # Lock the class to prevent modifications to the method or field
  lock_class = TRUE
)
## Uncomment below to unlock the class to allow modifications of the method or field
# DatasetMetadataArray$unlock()
#
## Below is an example to define the print function
# DatasetMetadataArray$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# DatasetMetadataArray$lock()

