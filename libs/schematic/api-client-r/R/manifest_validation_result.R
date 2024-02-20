#' Create a new ManifestValidationResult
#'
#' @description
#' The results of manifest validation
#'
#' @docType class
#' @title ManifestValidationResult
#' @description ManifestValidationResult Class
#' @format An \code{R6Class} generator object
#' @field errors Any errors from validation list(character) [optional]
#' @field warnings Any warnings from validation list(character) [optional]
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
ManifestValidationResult <- R6::R6Class(
  "ManifestValidationResult",
  public = list(
    `errors` = NULL,
    `warnings` = NULL,
    #' Initialize a new ManifestValidationResult class.
    #'
    #' @description
    #' Initialize a new ManifestValidationResult class.
    #'
    #' @param errors Any errors from validation
    #' @param warnings Any warnings from validation
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`errors` = NULL, `warnings` = NULL, ...) {
      if (!is.null(`errors`)) {
        stopifnot(is.vector(`errors`), length(`errors`) != 0)
        sapply(`errors`, function(x) stopifnot(is.character(x)))
        self$`errors` <- `errors`
      }
      if (!is.null(`warnings`)) {
        stopifnot(is.vector(`warnings`), length(`warnings`) != 0)
        sapply(`warnings`, function(x) stopifnot(is.character(x)))
        self$`warnings` <- `warnings`
      }
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ManifestValidationResult in JSON format
    #' @export
    toJSON = function() {
      ManifestValidationResultObject <- list()
      if (!is.null(self$`errors`)) {
        ManifestValidationResultObject[["errors"]] <-
          self$`errors`
      }
      if (!is.null(self$`warnings`)) {
        ManifestValidationResultObject[["warnings"]] <-
          self$`warnings`
      }
      ManifestValidationResultObject
    },
    #' Deserialize JSON string into an instance of ManifestValidationResult
    #'
    #' @description
    #' Deserialize JSON string into an instance of ManifestValidationResult
    #'
    #' @param input_json the JSON input
    #' @return the instance of ManifestValidationResult
    #' @export
    fromJSON = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      if (!is.null(this_object$`errors`)) {
        self$`errors` <- ApiClient$new()$deserializeObj(this_object$`errors`, "array[character]", loadNamespace("openapi"))
      }
      if (!is.null(this_object$`warnings`)) {
        self$`warnings` <- ApiClient$new()$deserializeObj(this_object$`warnings`, "array[character]", loadNamespace("openapi"))
      }
      self
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ManifestValidationResult in JSON format
    #' @export
    toJSONString = function() {
      jsoncontent <- c(
        if (!is.null(self$`errors`)) {
          sprintf(
          '"errors":
             [%s]
          ',
          paste(unlist(lapply(self$`errors`, function(x) paste0('"', x, '"'))), collapse = ",")
          )
        },
        if (!is.null(self$`warnings`)) {
          sprintf(
          '"warnings":
             [%s]
          ',
          paste(unlist(lapply(self$`warnings`, function(x) paste0('"', x, '"'))), collapse = ",")
          )
        }
      )
      jsoncontent <- paste(jsoncontent, collapse = ",")
      json_string <- as.character(jsonlite::minify(paste("{", jsoncontent, "}", sep = "")))
    },
    #' Deserialize JSON string into an instance of ManifestValidationResult
    #'
    #' @description
    #' Deserialize JSON string into an instance of ManifestValidationResult
    #'
    #' @param input_json the JSON input
    #' @return the instance of ManifestValidationResult
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`errors` <- ApiClient$new()$deserializeObj(this_object$`errors`, "array[character]", loadNamespace("openapi"))
      self$`warnings` <- ApiClient$new()$deserializeObj(this_object$`warnings`, "array[character]", loadNamespace("openapi"))
      self
    },
    #' Validate JSON input with respect to ManifestValidationResult
    #'
    #' @description
    #' Validate JSON input with respect to ManifestValidationResult and throw an exception if invalid
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
    #' @return String representation of ManifestValidationResult
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
# ManifestValidationResult$unlock()
#
## Below is an example to define the print function
# ManifestValidationResult$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# ManifestValidationResult$lock()

