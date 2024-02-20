#' Create a new FileMetadataArray
#'
#' @description
#' A list of file metadata.
#'
#' @docType class
#' @title FileMetadataArray
#' @description FileMetadataArray Class
#' @format An \code{R6Class} generator object
#' @field files A list of file metadata. list(\link{FileMetadata}) [optional]
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
FileMetadataArray <- R6::R6Class(
  "FileMetadataArray",
  public = list(
    `files` = NULL,
    #' Initialize a new FileMetadataArray class.
    #'
    #' @description
    #' Initialize a new FileMetadataArray class.
    #'
    #' @param files A list of file metadata.
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`files` = NULL, ...) {
      if (!is.null(`files`)) {
        stopifnot(is.vector(`files`), length(`files`) != 0)
        sapply(`files`, function(x) stopifnot(R6::is.R6(x)))
        self$`files` <- `files`
      }
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return FileMetadataArray in JSON format
    #' @export
    toJSON = function() {
      FileMetadataArrayObject <- list()
      if (!is.null(self$`files`)) {
        FileMetadataArrayObject[["files"]] <-
          lapply(self$`files`, function(x) x$toJSON())
      }
      FileMetadataArrayObject
    },
    #' Deserialize JSON string into an instance of FileMetadataArray
    #'
    #' @description
    #' Deserialize JSON string into an instance of FileMetadataArray
    #'
    #' @param input_json the JSON input
    #' @return the instance of FileMetadataArray
    #' @export
    fromJSON = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      if (!is.null(this_object$`files`)) {
        self$`files` <- ApiClient$new()$deserializeObj(this_object$`files`, "array[FileMetadata]", loadNamespace("openapi"))
      }
      self
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return FileMetadataArray in JSON format
    #' @export
    toJSONString = function() {
      jsoncontent <- c(
        if (!is.null(self$`files`)) {
          sprintf(
          '"files":
          [%s]
',
          paste(sapply(self$`files`, function(x) jsonlite::toJSON(x$toJSON(), auto_unbox = TRUE, digits = NA)), collapse = ",")
          )
        }
      )
      jsoncontent <- paste(jsoncontent, collapse = ",")
      json_string <- as.character(jsonlite::minify(paste("{", jsoncontent, "}", sep = "")))
    },
    #' Deserialize JSON string into an instance of FileMetadataArray
    #'
    #' @description
    #' Deserialize JSON string into an instance of FileMetadataArray
    #'
    #' @param input_json the JSON input
    #' @return the instance of FileMetadataArray
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`files` <- ApiClient$new()$deserializeObj(this_object$`files`, "array[FileMetadata]", loadNamespace("openapi"))
      self
    },
    #' Validate JSON input with respect to FileMetadataArray
    #'
    #' @description
    #' Validate JSON input with respect to FileMetadataArray and throw an exception if invalid
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
    #' @return String representation of FileMetadataArray
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
# FileMetadataArray$unlock()
#
## Below is an example to define the print function
# FileMetadataArray$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# FileMetadataArray$lock()

