#' Create a new ValidationRuleArray
#'
#' @description
#' An array of validation rules.
#'
#' @docType class
#' @title ValidationRuleArray
#' @description ValidationRuleArray Class
#' @format An \code{R6Class} generator object
#' @field validation_rules An array of validation rules. list(\link{ValidationRule}) [optional]
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
ValidationRuleArray <- R6::R6Class(
  "ValidationRuleArray",
  public = list(
    `validation_rules` = NULL,
    #' Initialize a new ValidationRuleArray class.
    #'
    #' @description
    #' Initialize a new ValidationRuleArray class.
    #'
    #' @param validation_rules An array of validation rules.
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`validation_rules` = NULL, ...) {
      if (!is.null(`validation_rules`)) {
        stopifnot(is.vector(`validation_rules`), length(`validation_rules`) != 0)
        sapply(`validation_rules`, function(x) stopifnot(R6::is.R6(x)))
        self$`validation_rules` <- `validation_rules`
      }
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ValidationRuleArray in JSON format
    #' @export
    toJSON = function() {
      ValidationRuleArrayObject <- list()
      if (!is.null(self$`validation_rules`)) {
        ValidationRuleArrayObject[["validation_rules"]] <-
          lapply(self$`validation_rules`, function(x) x$toJSON())
      }
      ValidationRuleArrayObject
    },
    #' Deserialize JSON string into an instance of ValidationRuleArray
    #'
    #' @description
    #' Deserialize JSON string into an instance of ValidationRuleArray
    #'
    #' @param input_json the JSON input
    #' @return the instance of ValidationRuleArray
    #' @export
    fromJSON = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      if (!is.null(this_object$`validation_rules`)) {
        self$`validation_rules` <- ApiClient$new()$deserializeObj(this_object$`validation_rules`, "array[ValidationRule]", loadNamespace("openapi"))
      }
      self
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ValidationRuleArray in JSON format
    #' @export
    toJSONString = function() {
      jsoncontent <- c(
        if (!is.null(self$`validation_rules`)) {
          sprintf(
          '"validation_rules":
          [%s]
',
          paste(sapply(self$`validation_rules`, function(x) jsonlite::toJSON(x$toJSON(), auto_unbox = TRUE, digits = NA)), collapse = ",")
          )
        }
      )
      jsoncontent <- paste(jsoncontent, collapse = ",")
      json_string <- as.character(jsonlite::minify(paste("{", jsoncontent, "}", sep = "")))
    },
    #' Deserialize JSON string into an instance of ValidationRuleArray
    #'
    #' @description
    #' Deserialize JSON string into an instance of ValidationRuleArray
    #'
    #' @param input_json the JSON input
    #' @return the instance of ValidationRuleArray
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`validation_rules` <- ApiClient$new()$deserializeObj(this_object$`validation_rules`, "array[ValidationRule]", loadNamespace("openapi"))
      self
    },
    #' Validate JSON input with respect to ValidationRuleArray
    #'
    #' @description
    #' Validate JSON input with respect to ValidationRuleArray and throw an exception if invalid
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
    #' @return String representation of ValidationRuleArray
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
# ValidationRuleArray$unlock()
#
## Below is an example to define the print function
# ValidationRuleArray$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# ValidationRuleArray$lock()

