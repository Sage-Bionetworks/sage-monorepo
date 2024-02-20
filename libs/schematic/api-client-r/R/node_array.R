#' Create a new NodeArray
#'
#' @description
#' An array of nodes.
#'
#' @docType class
#' @title NodeArray
#' @description NodeArray Class
#' @format An \code{R6Class} generator object
#' @field nodes An array of nodes. list(\link{Node}) [optional]
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
NodeArray <- R6::R6Class(
  "NodeArray",
  public = list(
    `nodes` = NULL,
    #' Initialize a new NodeArray class.
    #'
    #' @description
    #' Initialize a new NodeArray class.
    #'
    #' @param nodes An array of nodes.
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`nodes` = NULL, ...) {
      if (!is.null(`nodes`)) {
        stopifnot(is.vector(`nodes`), length(`nodes`) != 0)
        sapply(`nodes`, function(x) stopifnot(R6::is.R6(x)))
        self$`nodes` <- `nodes`
      }
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return NodeArray in JSON format
    #' @export
    toJSON = function() {
      NodeArrayObject <- list()
      if (!is.null(self$`nodes`)) {
        NodeArrayObject[["nodes"]] <-
          lapply(self$`nodes`, function(x) x$toJSON())
      }
      NodeArrayObject
    },
    #' Deserialize JSON string into an instance of NodeArray
    #'
    #' @description
    #' Deserialize JSON string into an instance of NodeArray
    #'
    #' @param input_json the JSON input
    #' @return the instance of NodeArray
    #' @export
    fromJSON = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      if (!is.null(this_object$`nodes`)) {
        self$`nodes` <- ApiClient$new()$deserializeObj(this_object$`nodes`, "array[Node]", loadNamespace("openapi"))
      }
      self
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return NodeArray in JSON format
    #' @export
    toJSONString = function() {
      jsoncontent <- c(
        if (!is.null(self$`nodes`)) {
          sprintf(
          '"nodes":
          [%s]
',
          paste(sapply(self$`nodes`, function(x) jsonlite::toJSON(x$toJSON(), auto_unbox = TRUE, digits = NA)), collapse = ",")
          )
        }
      )
      jsoncontent <- paste(jsoncontent, collapse = ",")
      json_string <- as.character(jsonlite::minify(paste("{", jsoncontent, "}", sep = "")))
    },
    #' Deserialize JSON string into an instance of NodeArray
    #'
    #' @description
    #' Deserialize JSON string into an instance of NodeArray
    #'
    #' @param input_json the JSON input
    #' @return the instance of NodeArray
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`nodes` <- ApiClient$new()$deserializeObj(this_object$`nodes`, "array[Node]", loadNamespace("openapi"))
      self
    },
    #' Validate JSON input with respect to NodeArray
    #'
    #' @description
    #' Validate JSON input with respect to NodeArray and throw an exception if invalid
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
    #' @return String representation of NodeArray
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
# NodeArray$unlock()
#
## Below is an example to define the print function
# NodeArray$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# NodeArray$lock()

