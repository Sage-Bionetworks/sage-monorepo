#' Create a new NodePropertyArray
#'
#' @description
#' An array of node properties.
#'
#' @docType class
#' @title NodePropertyArray
#' @description NodePropertyArray Class
#' @format An \code{R6Class} generator object
#' @field node_properties An array of node properties. list(character) [optional]
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
NodePropertyArray <- R6::R6Class(
  "NodePropertyArray",
  public = list(
    `node_properties` = NULL,
    #' Initialize a new NodePropertyArray class.
    #'
    #' @description
    #' Initialize a new NodePropertyArray class.
    #'
    #' @param node_properties An array of node properties.
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`node_properties` = NULL, ...) {
      if (!is.null(`node_properties`)) {
        stopifnot(is.vector(`node_properties`), length(`node_properties`) != 0)
        sapply(`node_properties`, function(x) stopifnot(is.character(x)))
        self$`node_properties` <- `node_properties`
      }
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return NodePropertyArray in JSON format
    #' @export
    toJSON = function() {
      NodePropertyArrayObject <- list()
      if (!is.null(self$`node_properties`)) {
        NodePropertyArrayObject[["node_properties"]] <-
          self$`node_properties`
      }
      NodePropertyArrayObject
    },
    #' Deserialize JSON string into an instance of NodePropertyArray
    #'
    #' @description
    #' Deserialize JSON string into an instance of NodePropertyArray
    #'
    #' @param input_json the JSON input
    #' @return the instance of NodePropertyArray
    #' @export
    fromJSON = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      if (!is.null(this_object$`node_properties`)) {
        self$`node_properties` <- ApiClient$new()$deserializeObj(this_object$`node_properties`, "array[character]", loadNamespace("openapi"))
      }
      self
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return NodePropertyArray in JSON format
    #' @export
    toJSONString = function() {
      jsoncontent <- c(
        if (!is.null(self$`node_properties`)) {
          sprintf(
          '"node_properties":
             [%s]
          ',
          paste(unlist(lapply(self$`node_properties`, function(x) paste0('"', x, '"'))), collapse = ",")
          )
        }
      )
      jsoncontent <- paste(jsoncontent, collapse = ",")
      json_string <- as.character(jsonlite::minify(paste("{", jsoncontent, "}", sep = "")))
    },
    #' Deserialize JSON string into an instance of NodePropertyArray
    #'
    #' @description
    #' Deserialize JSON string into an instance of NodePropertyArray
    #'
    #' @param input_json the JSON input
    #' @return the instance of NodePropertyArray
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`node_properties` <- ApiClient$new()$deserializeObj(this_object$`node_properties`, "array[character]", loadNamespace("openapi"))
      self
    },
    #' Validate JSON input with respect to NodePropertyArray
    #'
    #' @description
    #' Validate JSON input with respect to NodePropertyArray and throw an exception if invalid
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
    #' @return String representation of NodePropertyArray
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
# NodePropertyArray$unlock()
#
## Below is an example to define the print function
# NodePropertyArray$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# NodePropertyArray$lock()

