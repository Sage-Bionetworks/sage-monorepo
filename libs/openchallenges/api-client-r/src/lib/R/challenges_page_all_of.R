#' Create a new ChallengesPageAllOf
#'
#' @description
#' ChallengesPageAllOf Class
#'
#' @docType class
#' @title ChallengesPageAllOf
#' @description ChallengesPageAllOf Class
#' @format An \code{R6Class} generator object
#' @field challenges A list of challenges. list(\link{Challenge})
#' @field _field_list a list of fields list(character)
#' @field additional_properties additional properties list(character) [optional]
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
ChallengesPageAllOf <- R6::R6Class(
  "ChallengesPageAllOf",
  public = list(
    `challenges` = NULL,
    `_field_list` = c("challenges"),
    `additional_properties` = list(),
    #' Initialize a new ChallengesPageAllOf class.
    #'
    #' @description
    #' Initialize a new ChallengesPageAllOf class.
    #'
    #' @param challenges A list of challenges.
    #' @param additional_properties additional properties (optional)
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`challenges`, additional_properties = NULL, ...) {
      if (!missing(`challenges`)) {
        stopifnot(is.vector(`challenges`), length(`challenges`) != 0)
        sapply(`challenges`, function(x) stopifnot(R6::is.R6(x)))
        self$`challenges` <- `challenges`
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
    #' @return ChallengesPageAllOf in JSON format
    #' @export
    toJSON = function() {
      ChallengesPageAllOfObject <- list()
      if (!is.null(self$`challenges`)) {
        ChallengesPageAllOfObject[["challenges"]] <-
          lapply(self$`challenges`, function(x) x$toJSON())
      }
      for (key in names(self$additional_properties)) {
        ChallengesPageAllOfObject[[key]] <- self$additional_properties[[key]]
      }

      ChallengesPageAllOfObject
    },
    #' Deserialize JSON string into an instance of ChallengesPageAllOf
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengesPageAllOf
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengesPageAllOf
    #' @export
    fromJSON = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      if (!is.null(this_object$`challenges`)) {
        self$`challenges` <- ApiClient$new()$deserializeObj(this_object$`challenges`, "array[Challenge]", loadNamespace("openapi"))
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
    #' @return ChallengesPageAllOf in JSON format
    #' @export
    toJSONString = function() {
      jsoncontent <- c(
        if (!is.null(self$`challenges`)) {
          sprintf(
          '"challenges":
          [%s]
',
          paste(sapply(self$`challenges`, function(x) jsonlite::toJSON(x$toJSON(), auto_unbox = TRUE, digits = NA)), collapse = ",")
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
    #' Deserialize JSON string into an instance of ChallengesPageAllOf
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengesPageAllOf
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengesPageAllOf
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`challenges` <- ApiClient$new()$deserializeObj(this_object$`challenges`, "array[Challenge]", loadNamespace("openapi"))
      # process additional properties/fields in the payload
      for (key in names(this_object)) {
        if (!(key %in% self$`_field_list`)) { # json key not in list of fields
          self$additional_properties[[key]] <- this_object[[key]]
        }
      }

      self
    },
    #' Validate JSON input with respect to ChallengesPageAllOf
    #'
    #' @description
    #' Validate JSON input with respect to ChallengesPageAllOf and throw an exception if invalid
    #'
    #' @param input the JSON input
    #' @export
    validateJSON = function(input) {
      input_json <- jsonlite::fromJSON(input)
      # check the required field `challenges`
      if (!is.null(input_json$`challenges`)) {
        stopifnot(is.vector(input_json$`challenges`), length(input_json$`challenges`) != 0)
        tmp <- sapply(input_json$`challenges`, function(x) stopifnot(R6::is.R6(x)))
      } else {
        stop(paste("The JSON input `", input, "` is invalid for ChallengesPageAllOf: the required field `challenges` is missing."))
      }
    },
    #' To string (JSON format)
    #'
    #' @description
    #' To string (JSON format)
    #'
    #' @return String representation of ChallengesPageAllOf
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
      # check if the required `challenges` is null
      if (is.null(self$`challenges`)) {
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
      # check if the required `challenges` is null
      if (is.null(self$`challenges`)) {
        invalid_fields["challenges"] <- "Non-nullable required field `challenges` cannot be null."
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
# ChallengesPageAllOf$unlock()
#
## Below is an example to define the print function
# ChallengesPageAllOf$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# ChallengesPageAllOf$lock()

