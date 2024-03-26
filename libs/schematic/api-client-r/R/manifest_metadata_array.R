#' Create a new ManifestMetadataArray
#'
#' @description
#' An array of manifest metadata
#'
#' @docType class
#' @title ManifestMetadataArray
#' @description ManifestMetadataArray Class
#' @format An \code{R6Class} generator object
#' @field manifests A list of manifest metadata list(\link{ManifestMetadata}) [optional]
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
ManifestMetadataArray <- R6::R6Class(
  "ManifestMetadataArray",
  public = list(
    `manifests` = NULL,
    #' Initialize a new ManifestMetadataArray class.
    #'
    #' @description
    #' Initialize a new ManifestMetadataArray class.
    #'
    #' @param manifests A list of manifest metadata
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`manifests` = NULL, ...) {
      if (!is.null(`manifests`)) {
        stopifnot(is.vector(`manifests`), length(`manifests`) != 0)
        sapply(`manifests`, function(x) stopifnot(R6::is.R6(x)))
        self$`manifests` <- `manifests`
      }
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ManifestMetadataArray in JSON format
    #' @export
    toJSON = function() {
      ManifestMetadataArrayObject <- list()
      if (!is.null(self$`manifests`)) {
        ManifestMetadataArrayObject[["manifests"]] <-
          lapply(self$`manifests`, function(x) x$toJSON())
      }
      ManifestMetadataArrayObject
    },
    #' Deserialize JSON string into an instance of ManifestMetadataArray
    #'
    #' @description
    #' Deserialize JSON string into an instance of ManifestMetadataArray
    #'
    #' @param input_json the JSON input
    #' @return the instance of ManifestMetadataArray
    #' @export
    fromJSON = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      if (!is.null(this_object$`manifests`)) {
        self$`manifests` <- ApiClient$new()$deserializeObj(this_object$`manifests`, "array[ManifestMetadata]", loadNamespace("openapi"))
      }
      self
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ManifestMetadataArray in JSON format
    #' @export
    toJSONString = function() {
      jsoncontent <- c(
        if (!is.null(self$`manifests`)) {
          sprintf(
          '"manifests":
          [%s]
',
          paste(sapply(self$`manifests`, function(x) jsonlite::toJSON(x$toJSON(), auto_unbox = TRUE, digits = NA)), collapse = ",")
          )
        }
      )
      jsoncontent <- paste(jsoncontent, collapse = ",")
      json_string <- as.character(jsonlite::minify(paste("{", jsoncontent, "}", sep = "")))
    },
    #' Deserialize JSON string into an instance of ManifestMetadataArray
    #'
    #' @description
    #' Deserialize JSON string into an instance of ManifestMetadataArray
    #'
    #' @param input_json the JSON input
    #' @return the instance of ManifestMetadataArray
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`manifests` <- ApiClient$new()$deserializeObj(this_object$`manifests`, "array[ManifestMetadata]", loadNamespace("openapi"))
      self
    },
    #' Validate JSON input with respect to ManifestMetadataArray
    #'
    #' @description
    #' Validate JSON input with respect to ManifestMetadataArray and throw an exception if invalid
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
    #' @return String representation of ManifestMetadataArray
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
# ManifestMetadataArray$unlock()
#
## Below is an example to define the print function
# ManifestMetadataArray$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# ManifestMetadataArray$lock()

