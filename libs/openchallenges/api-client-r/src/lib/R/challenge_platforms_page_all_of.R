#' Create a new ChallengePlatformsPageAllOf
#'
#' @description
#' ChallengePlatformsPageAllOf Class
#'
#' @docType class
#' @title ChallengePlatformsPageAllOf
#' @description ChallengePlatformsPageAllOf Class
#' @format An \code{R6Class} generator object
#' @field challengePlatforms A list of challenge platforms. list(\link{ChallengePlatform})
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
ChallengePlatformsPageAllOf <- R6::R6Class(
  "ChallengePlatformsPageAllOf",
  public = list(
    `challengePlatforms` = NULL,
    #' Initialize a new ChallengePlatformsPageAllOf class.
    #'
    #' @description
    #' Initialize a new ChallengePlatformsPageAllOf class.
    #'
    #' @param challengePlatforms A list of challenge platforms.
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`challengePlatforms`, ...) {
      if (!missing(`challengePlatforms`)) {
        stopifnot(is.vector(`challengePlatforms`), length(`challengePlatforms`) != 0)
        sapply(`challengePlatforms`, function(x) stopifnot(R6::is.R6(x)))
        self$`challengePlatforms` <- `challengePlatforms`
      }
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ChallengePlatformsPageAllOf in JSON format
    #' @export
    toJSON = function() {
      ChallengePlatformsPageAllOfObject <- list()
      if (!is.null(self$`challengePlatforms`)) {
        ChallengePlatformsPageAllOfObject[["challengePlatforms"]] <-
          lapply(self$`challengePlatforms`, function(x) x$toJSON())
      }
      ChallengePlatformsPageAllOfObject
    },
    #' Deserialize JSON string into an instance of ChallengePlatformsPageAllOf
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengePlatformsPageAllOf
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengePlatformsPageAllOf
    #' @export
    fromJSON = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      if (!is.null(this_object$`challengePlatforms`)) {
        self$`challengePlatforms` <- ApiClient$new()$deserializeObj(this_object$`challengePlatforms`, "array[ChallengePlatform]", loadNamespace("openapi"))
      }
      self
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ChallengePlatformsPageAllOf in JSON format
    #' @export
    toJSONString = function() {
      jsoncontent <- c(
        if (!is.null(self$`challengePlatforms`)) {
          sprintf(
          '"challengePlatforms":
          [%s]
',
          paste(sapply(self$`challengePlatforms`, function(x) jsonlite::toJSON(x$toJSON(), auto_unbox = TRUE, digits = NA)), collapse = ",")
          )
        }
      )
      jsoncontent <- paste(jsoncontent, collapse = ",")
      json_string <- as.character(jsonlite::minify(paste("{", jsoncontent, "}", sep = "")))
    },
    #' Deserialize JSON string into an instance of ChallengePlatformsPageAllOf
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengePlatformsPageAllOf
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengePlatformsPageAllOf
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`challengePlatforms` <- ApiClient$new()$deserializeObj(this_object$`challengePlatforms`, "array[ChallengePlatform]", loadNamespace("openapi"))
      self
    },
    #' Validate JSON input with respect to ChallengePlatformsPageAllOf
    #'
    #' @description
    #' Validate JSON input with respect to ChallengePlatformsPageAllOf and throw an exception if invalid
    #'
    #' @param input the JSON input
    #' @export
    validateJSON = function(input) {
      input_json <- jsonlite::fromJSON(input)
      # check the required field `challengePlatforms`
      if (!is.null(input_json$`challengePlatforms`)) {
        stopifnot(is.vector(input_json$`challengePlatforms`), length(input_json$`challengePlatforms`) != 0)
        tmp <- sapply(input_json$`challengePlatforms`, function(x) stopifnot(R6::is.R6(x)))
      } else {
        stop(paste("The JSON input `", input, "` is invalid for ChallengePlatformsPageAllOf: the required field `challengePlatforms` is missing."))
      }
    },
    #' To string (JSON format)
    #'
    #' @description
    #' To string (JSON format)
    #'
    #' @return String representation of ChallengePlatformsPageAllOf
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
      # check if the required `challengePlatforms` is null
      if (is.null(self$`challengePlatforms`)) {
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
      # check if the required `challengePlatforms` is null
      if (is.null(self$`challengePlatforms`)) {
        invalid_fields["challengePlatforms"] <- "Non-nullable required field `challengePlatforms` cannot be null."
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
# ChallengePlatformsPageAllOf$unlock()
#
## Below is an example to define the print function
# ChallengePlatformsPageAllOf$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# ChallengePlatformsPageAllOf$lock()

