#' Create a new ProjectMetadataArray
#'
#' @description
#' An array of project metadata.
#'
#' @docType class
#' @title ProjectMetadataArray
#' @description ProjectMetadataArray Class
#' @format An \code{R6Class} generator object
#' @field projects An array of project metadata. list(\link{ProjectMetadata}) [optional]
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
ProjectMetadataArray <- R6::R6Class(
  "ProjectMetadataArray",
  public = list(
    `projects` = NULL,
    #' Initialize a new ProjectMetadataArray class.
    #'
    #' @description
    #' Initialize a new ProjectMetadataArray class.
    #'
    #' @param projects An array of project metadata.
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`projects` = NULL, ...) {
      if (!is.null(`projects`)) {
        stopifnot(is.vector(`projects`), length(`projects`) != 0)
        sapply(`projects`, function(x) stopifnot(R6::is.R6(x)))
        self$`projects` <- `projects`
      }
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ProjectMetadataArray in JSON format
    #' @export
    toJSON = function() {
      ProjectMetadataArrayObject <- list()
      if (!is.null(self$`projects`)) {
        ProjectMetadataArrayObject[["projects"]] <-
          lapply(self$`projects`, function(x) x$toJSON())
      }
      ProjectMetadataArrayObject
    },
    #' Deserialize JSON string into an instance of ProjectMetadataArray
    #'
    #' @description
    #' Deserialize JSON string into an instance of ProjectMetadataArray
    #'
    #' @param input_json the JSON input
    #' @return the instance of ProjectMetadataArray
    #' @export
    fromJSON = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      if (!is.null(this_object$`projects`)) {
        self$`projects` <- ApiClient$new()$deserializeObj(this_object$`projects`, "array[ProjectMetadata]", loadNamespace("openapi"))
      }
      self
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ProjectMetadataArray in JSON format
    #' @export
    toJSONString = function() {
      jsoncontent <- c(
        if (!is.null(self$`projects`)) {
          sprintf(
          '"projects":
          [%s]
',
          paste(sapply(self$`projects`, function(x) jsonlite::toJSON(x$toJSON(), auto_unbox = TRUE, digits = NA)), collapse = ",")
          )
        }
      )
      jsoncontent <- paste(jsoncontent, collapse = ",")
      json_string <- as.character(jsonlite::minify(paste("{", jsoncontent, "}", sep = "")))
    },
    #' Deserialize JSON string into an instance of ProjectMetadataArray
    #'
    #' @description
    #' Deserialize JSON string into an instance of ProjectMetadataArray
    #'
    #' @param input_json the JSON input
    #' @return the instance of ProjectMetadataArray
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`projects` <- ApiClient$new()$deserializeObj(this_object$`projects`, "array[ProjectMetadata]", loadNamespace("openapi"))
      self
    },
    #' Validate JSON input with respect to ProjectMetadataArray
    #'
    #' @description
    #' Validate JSON input with respect to ProjectMetadataArray and throw an exception if invalid
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
    #' @return String representation of ProjectMetadataArray
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
# ProjectMetadataArray$unlock()
#
## Below is an example to define the print function
# ProjectMetadataArray$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# ProjectMetadataArray$lock()

