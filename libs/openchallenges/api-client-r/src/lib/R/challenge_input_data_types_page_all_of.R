#' Create a new ChallengeInputDataTypesPageAllOf
#'
#' @description
#' ChallengeInputDataTypesPageAllOf Class
#'
#' @docType class
#' @title ChallengeInputDataTypesPageAllOf
#' @description ChallengeInputDataTypesPageAllOf Class
#' @format An \code{R6Class} generator object
#' @field challengeInputDataTypes A list of challenge input data types. list(\link{ChallengeInputDataType})
#' @field _field_list a list of fields list(character)
#' @field additional_properties additional properties list(character) [optional]
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
ChallengeInputDataTypesPageAllOf <- R6::R6Class(
  "ChallengeInputDataTypesPageAllOf",
  public = list(
    `challengeInputDataTypes` = NULL,
    `_field_list` = c("challengeInputDataTypes"),
    `additional_properties` = list(),
    #' Initialize a new ChallengeInputDataTypesPageAllOf class.
    #'
    #' @description
    #' Initialize a new ChallengeInputDataTypesPageAllOf class.
    #'
    #' @param challengeInputDataTypes A list of challenge input data types.
    #' @param additional_properties additional properties (optional)
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`challengeInputDataTypes`, additional_properties = NULL, ...) {
      if (!missing(`challengeInputDataTypes`)) {
        stopifnot(is.vector(`challengeInputDataTypes`), length(`challengeInputDataTypes`) != 0)
        sapply(`challengeInputDataTypes`, function(x) stopifnot(R6::is.R6(x)))
        self$`challengeInputDataTypes` <- `challengeInputDataTypes`
      }
      if (!is.null(additional_properties)) {
        for (key in names(additional_properties)) {
          self$additional_properties[[key]] <- additional_properties[[key]]
        }
      }
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ChallengeInputDataTypesPageAllOf in JSON format
    #' @export
    toJSON = function() {
      ChallengeInputDataTypesPageAllOfObject <- list()
      if (!is.null(self$`challengeInputDataTypes`)) {
        ChallengeInputDataTypesPageAllOfObject[["challengeInputDataTypes"]] <-
          lapply(self$`challengeInputDataTypes`, function(x) x$toJSON())
      }
      for (key in names(self$additional_properties)) {
        ChallengeInputDataTypesPageAllOfObject[[key]] <- self$additional_properties[[key]]
      }

      ChallengeInputDataTypesPageAllOfObject
    },
    #' Deserialize JSON string into an instance of ChallengeInputDataTypesPageAllOf
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengeInputDataTypesPageAllOf
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengeInputDataTypesPageAllOf
    #' @export
    fromJSON = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      if (!is.null(this_object$`challengeInputDataTypes`)) {
        self$`challengeInputDataTypes` <- ApiClient$new()$deserializeObj(this_object$`challengeInputDataTypes`, "array[ChallengeInputDataType]", loadNamespace("openapi"))
      }
      # process additional properties/fields in the payload
      for (key in names(this_object)) {
        if (!(key %in% self$`_field_list`)) { # json key not in list of fields
          self$additional_properties[[key]] <- this_object[[key]]
        }
      }

      self
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ChallengeInputDataTypesPageAllOf in JSON format
    #' @export
    toJSONString = function() {
      jsoncontent <- c(
        if (!is.null(self$`challengeInputDataTypes`)) {
          sprintf(
          '"challengeInputDataTypes":
          [%s]
',
          paste(sapply(self$`challengeInputDataTypes`, function(x) jsonlite::toJSON(x$toJSON(), auto_unbox = TRUE, digits = NA)), collapse = ",")
          )
        }
      )
      jsoncontent <- paste(jsoncontent, collapse = ",")
      json_string <- as.character(jsonlite::minify(paste("{", jsoncontent, "}", sep = "")))
      json_obj <- jsonlite::fromJSON(json_string)
      for (key in names(self$additional_properties)) {
        json_obj[[key]] <- self$additional_properties[[key]]
      }
      json_string <- as.character(jsonlite::minify(jsonlite::toJSON(json_obj, auto_unbox = TRUE, digits = NA)))
    },
    #' Deserialize JSON string into an instance of ChallengeInputDataTypesPageAllOf
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengeInputDataTypesPageAllOf
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengeInputDataTypesPageAllOf
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`challengeInputDataTypes` <- ApiClient$new()$deserializeObj(this_object$`challengeInputDataTypes`, "array[ChallengeInputDataType]", loadNamespace("openapi"))
      # process additional properties/fields in the payload
      for (key in names(this_object)) {
        if (!(key %in% self$`_field_list`)) { # json key not in list of fields
          self$additional_properties[[key]] <- this_object[[key]]
        }
      }

      self
    },
    #' Validate JSON input with respect to ChallengeInputDataTypesPageAllOf
    #'
    #' @description
    #' Validate JSON input with respect to ChallengeInputDataTypesPageAllOf and throw an exception if invalid
    #'
    #' @param input the JSON input
    #' @export
    validateJSON = function(input) {
      input_json <- jsonlite::fromJSON(input)
      # check the required field `challengeInputDataTypes`
      if (!is.null(input_json$`challengeInputDataTypes`)) {
        stopifnot(is.vector(input_json$`challengeInputDataTypes`), length(input_json$`challengeInputDataTypes`) != 0)
        tmp <- sapply(input_json$`challengeInputDataTypes`, function(x) stopifnot(R6::is.R6(x)))
      } else {
        stop(paste("The JSON input `", input, "` is invalid for ChallengeInputDataTypesPageAllOf: the required field `challengeInputDataTypes` is missing."))
      }
    },
    #' To string (JSON format)
    #'
    #' @description
    #' To string (JSON format)
    #'
    #' @return String representation of ChallengeInputDataTypesPageAllOf
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
      # check if the required `challengeInputDataTypes` is null
      if (is.null(self$`challengeInputDataTypes`)) {
        return(FALSE)
      }

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
      # check if the required `challengeInputDataTypes` is null
      if (is.null(self$`challengeInputDataTypes`)) {
        invalid_fields["challengeInputDataTypes"] <- "Non-nullable required field `challengeInputDataTypes` cannot be null."
      }

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
# ChallengeInputDataTypesPageAllOf$unlock()
#
## Below is an example to define the print function
# ChallengeInputDataTypesPageAllOf$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# ChallengeInputDataTypesPageAllOf$lock()

