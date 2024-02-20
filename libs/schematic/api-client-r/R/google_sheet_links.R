#' Create a new GoogleSheetLinks
#'
#' @description
#' An array of google sheet links
#'
#' @docType class
#' @title GoogleSheetLinks
#' @description GoogleSheetLinks Class
#' @format An \code{R6Class} generator object
#' @field links  list(character) [optional]
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
GoogleSheetLinks <- R6::R6Class(
  "GoogleSheetLinks",
  public = list(
    `links` = NULL,
    #' Initialize a new GoogleSheetLinks class.
    #'
    #' @description
    #' Initialize a new GoogleSheetLinks class.
    #'
    #' @param links links
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`links` = NULL, ...) {
      if (!is.null(`links`)) {
        stopifnot(is.vector(`links`), length(`links`) != 0)
        sapply(`links`, function(x) stopifnot(is.character(x)))
        self$`links` <- `links`
      }
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return GoogleSheetLinks in JSON format
    #' @export
    toJSON = function() {
      GoogleSheetLinksObject <- list()
      if (!is.null(self$`links`)) {
        GoogleSheetLinksObject[["links"]] <-
          self$`links`
      }
      GoogleSheetLinksObject
    },
    #' Deserialize JSON string into an instance of GoogleSheetLinks
    #'
    #' @description
    #' Deserialize JSON string into an instance of GoogleSheetLinks
    #'
    #' @param input_json the JSON input
    #' @return the instance of GoogleSheetLinks
    #' @export
    fromJSON = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      if (!is.null(this_object$`links`)) {
        self$`links` <- ApiClient$new()$deserializeObj(this_object$`links`, "array[character]", loadNamespace("openapi"))
      }
      self
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return GoogleSheetLinks in JSON format
    #' @export
    toJSONString = function() {
      jsoncontent <- c(
        if (!is.null(self$`links`)) {
          sprintf(
          '"links":
             [%s]
          ',
          paste(unlist(lapply(self$`links`, function(x) paste0('"', x, '"'))), collapse = ",")
          )
        }
      )
      jsoncontent <- paste(jsoncontent, collapse = ",")
      json_string <- as.character(jsonlite::minify(paste("{", jsoncontent, "}", sep = "")))
    },
    #' Deserialize JSON string into an instance of GoogleSheetLinks
    #'
    #' @description
    #' Deserialize JSON string into an instance of GoogleSheetLinks
    #'
    #' @param input_json the JSON input
    #' @return the instance of GoogleSheetLinks
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`links` <- ApiClient$new()$deserializeObj(this_object$`links`, "array[character]", loadNamespace("openapi"))
      self
    },
    #' Validate JSON input with respect to GoogleSheetLinks
    #'
    #' @description
    #' Validate JSON input with respect to GoogleSheetLinks and throw an exception if invalid
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
    #' @return String representation of GoogleSheetLinks
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
# GoogleSheetLinks$unlock()
#
## Below is an example to define the print function
# GoogleSheetLinks$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# GoogleSheetLinks$lock()

