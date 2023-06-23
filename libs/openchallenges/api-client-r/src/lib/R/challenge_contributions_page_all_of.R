#' Create a new ChallengeContributionsPageAllOf
#'
#' @description
#' ChallengeContributionsPageAllOf Class
#'
#' @docType class
#' @title ChallengeContributionsPageAllOf
#' @description ChallengeContributionsPageAllOf Class
#' @format An \code{R6Class} generator object
#' @field challengeContributions A list of challenge contributions. list(\link{ChallengeContribution})
#' @field _field_list a list of fields list(character)
#' @field additional_properties additional properties list(character) [optional]
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
ChallengeContributionsPageAllOf <- R6::R6Class(
  "ChallengeContributionsPageAllOf",
  public = list(
    `challengeContributions` = NULL,
    `_field_list` = c("challengeContributions"),
    `additional_properties` = list(),
    #' Initialize a new ChallengeContributionsPageAllOf class.
    #'
    #' @description
    #' Initialize a new ChallengeContributionsPageAllOf class.
    #'
    #' @param challengeContributions A list of challenge contributions.
    #' @param additional_properties additional properties (optional)
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`challengeContributions`, additional_properties = NULL, ...) {
      if (!missing(`challengeContributions`)) {
        stopifnot(is.vector(`challengeContributions`), length(`challengeContributions`) != 0)
        sapply(`challengeContributions`, function(x) stopifnot(R6::is.R6(x)))
        self$`challengeContributions` <- `challengeContributions`
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
    #' @return ChallengeContributionsPageAllOf in JSON format
    #' @export
    toJSON = function() {
      ChallengeContributionsPageAllOfObject <- list()
      if (!is.null(self$`challengeContributions`)) {
        ChallengeContributionsPageAllOfObject[["challengeContributions"]] <-
          lapply(self$`challengeContributions`, function(x) x$toJSON())
      }
      for (key in names(self$additional_properties)) {
        ChallengeContributionsPageAllOfObject[[key]] <- self$additional_properties[[key]]
      }

      ChallengeContributionsPageAllOfObject
    },
    #' Deserialize JSON string into an instance of ChallengeContributionsPageAllOf
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengeContributionsPageAllOf
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengeContributionsPageAllOf
    #' @export
    fromJSON = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      if (!is.null(this_object$`challengeContributions`)) {
        self$`challengeContributions` <- ApiClient$new()$deserializeObj(this_object$`challengeContributions`, "array[ChallengeContribution]", loadNamespace("openapi"))
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
    #' @return ChallengeContributionsPageAllOf in JSON format
    #' @export
    toJSONString = function() {
      jsoncontent <- c(
        if (!is.null(self$`challengeContributions`)) {
          sprintf(
          '"challengeContributions":
          [%s]
',
          paste(sapply(self$`challengeContributions`, function(x) jsonlite::toJSON(x$toJSON(), auto_unbox = TRUE, digits = NA)), collapse = ",")
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
    #' Deserialize JSON string into an instance of ChallengeContributionsPageAllOf
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengeContributionsPageAllOf
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengeContributionsPageAllOf
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`challengeContributions` <- ApiClient$new()$deserializeObj(this_object$`challengeContributions`, "array[ChallengeContribution]", loadNamespace("openapi"))
      # process additional properties/fields in the payload
      for (key in names(this_object)) {
        if (!(key %in% self$`_field_list`)) { # json key not in list of fields
          self$additional_properties[[key]] <- this_object[[key]]
        }
      }

      self
    },
    #' Validate JSON input with respect to ChallengeContributionsPageAllOf
    #'
    #' @description
    #' Validate JSON input with respect to ChallengeContributionsPageAllOf and throw an exception if invalid
    #'
    #' @param input the JSON input
    #' @export
    validateJSON = function(input) {
      input_json <- jsonlite::fromJSON(input)
      # check the required field `challengeContributions`
      if (!is.null(input_json$`challengeContributions`)) {
        stopifnot(is.vector(input_json$`challengeContributions`), length(input_json$`challengeContributions`) != 0)
        tmp <- sapply(input_json$`challengeContributions`, function(x) stopifnot(R6::is.R6(x)))
      } else {
        stop(paste("The JSON input `", input, "` is invalid for ChallengeContributionsPageAllOf: the required field `challengeContributions` is missing."))
      }
    },
    #' To string (JSON format)
    #'
    #' @description
    #' To string (JSON format)
    #'
    #' @return String representation of ChallengeContributionsPageAllOf
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
      # check if the required `challengeContributions` is null
      if (is.null(self$`challengeContributions`)) {
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
      # check if the required `challengeContributions` is null
      if (is.null(self$`challengeContributions`)) {
        invalid_fields["challengeContributions"] <- "Non-nullable required field `challengeContributions` cannot be null."
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
# ChallengeContributionsPageAllOf$unlock()
#
## Below is an example to define the print function
# ChallengeContributionsPageAllOf$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# ChallengeContributionsPageAllOf$lock()

