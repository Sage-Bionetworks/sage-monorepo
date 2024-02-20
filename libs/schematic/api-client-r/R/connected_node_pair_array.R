#' Create a new ConnectedNodePairArray
#'
#' @description
#' An array of conncted node pairs
#'
#' @docType class
#' @title ConnectedNodePairArray
#' @description ConnectedNodePairArray Class
#' @format An \code{R6Class} generator object
#' @field connectedNodes An array of conncted node pairs. list(\link{ConnectedNodePair}) [optional]
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
ConnectedNodePairArray <- R6::R6Class(
  "ConnectedNodePairArray",
  public = list(
    `connectedNodes` = NULL,
    #' Initialize a new ConnectedNodePairArray class.
    #'
    #' @description
    #' Initialize a new ConnectedNodePairArray class.
    #'
    #' @param connectedNodes An array of conncted node pairs.
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`connectedNodes` = NULL, ...) {
      if (!is.null(`connectedNodes`)) {
        stopifnot(is.vector(`connectedNodes`), length(`connectedNodes`) != 0)
        sapply(`connectedNodes`, function(x) stopifnot(R6::is.R6(x)))
        self$`connectedNodes` <- `connectedNodes`
      }
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ConnectedNodePairArray in JSON format
    #' @export
    toJSON = function() {
      ConnectedNodePairArrayObject <- list()
      if (!is.null(self$`connectedNodes`)) {
        ConnectedNodePairArrayObject[["connectedNodes"]] <-
          lapply(self$`connectedNodes`, function(x) x$toJSON())
      }
      ConnectedNodePairArrayObject
    },
    #' Deserialize JSON string into an instance of ConnectedNodePairArray
    #'
    #' @description
    #' Deserialize JSON string into an instance of ConnectedNodePairArray
    #'
    #' @param input_json the JSON input
    #' @return the instance of ConnectedNodePairArray
    #' @export
    fromJSON = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      if (!is.null(this_object$`connectedNodes`)) {
        self$`connectedNodes` <- ApiClient$new()$deserializeObj(this_object$`connectedNodes`, "array[ConnectedNodePair]", loadNamespace("openapi"))
      }
      self
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ConnectedNodePairArray in JSON format
    #' @export
    toJSONString = function() {
      jsoncontent <- c(
        if (!is.null(self$`connectedNodes`)) {
          sprintf(
          '"connectedNodes":
          [%s]
',
          paste(sapply(self$`connectedNodes`, function(x) jsonlite::toJSON(x$toJSON(), auto_unbox = TRUE, digits = NA)), collapse = ",")
          )
        }
      )
      jsoncontent <- paste(jsoncontent, collapse = ",")
      json_string <- as.character(jsonlite::minify(paste("{", jsoncontent, "}", sep = "")))
    },
    #' Deserialize JSON string into an instance of ConnectedNodePairArray
    #'
    #' @description
    #' Deserialize JSON string into an instance of ConnectedNodePairArray
    #'
    #' @param input_json the JSON input
    #' @return the instance of ConnectedNodePairArray
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`connectedNodes` <- ApiClient$new()$deserializeObj(this_object$`connectedNodes`, "array[ConnectedNodePair]", loadNamespace("openapi"))
      self
    },
    #' Validate JSON input with respect to ConnectedNodePairArray
    #'
    #' @description
    #' Validate JSON input with respect to ConnectedNodePairArray and throw an exception if invalid
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
    #' @return String representation of ConnectedNodePairArray
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
# ConnectedNodePairArray$unlock()
#
## Below is an example to define the print function
# ConnectedNodePairArray$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# ConnectedNodePairArray$lock()

