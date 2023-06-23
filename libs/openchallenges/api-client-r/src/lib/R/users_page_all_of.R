#' Create a new UsersPageAllOf
#'
#' @description
#' UsersPageAllOf Class
#'
#' @docType class
#' @title UsersPageAllOf
#' @description UsersPageAllOf Class
#' @format An \code{R6Class} generator object
#' @field users A list of users list(\link{User})
#' @field _field_list a list of fields list(character)
#' @field additional_properties additional properties list(character) [optional]
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
UsersPageAllOf <- R6::R6Class(
  "UsersPageAllOf",
  public = list(
    `users` = NULL,
    `_field_list` = c("users"),
    `additional_properties` = list(),
    #' Initialize a new UsersPageAllOf class.
    #'
    #' @description
    #' Initialize a new UsersPageAllOf class.
    #'
    #' @param users A list of users
    #' @param additional_properties additional properties (optional)
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`users`, additional_properties = NULL, ...) {
      if (!missing(`users`)) {
        stopifnot(is.vector(`users`), length(`users`) != 0)
        sapply(`users`, function(x) stopifnot(R6::is.R6(x)))
        self$`users` <- `users`
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
    #' @return UsersPageAllOf in JSON format
    #' @export
    toJSON = function() {
      UsersPageAllOfObject <- list()
      if (!is.null(self$`users`)) {
        UsersPageAllOfObject[["users"]] <-
          lapply(self$`users`, function(x) x$toJSON())
      }
      for (key in names(self$additional_properties)) {
        UsersPageAllOfObject[[key]] <- self$additional_properties[[key]]
      }

      UsersPageAllOfObject
    },
    #' Deserialize JSON string into an instance of UsersPageAllOf
    #'
    #' @description
    #' Deserialize JSON string into an instance of UsersPageAllOf
    #'
    #' @param input_json the JSON input
    #' @return the instance of UsersPageAllOf
    #' @export
    fromJSON = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      if (!is.null(this_object$`users`)) {
        self$`users` <- ApiClient$new()$deserializeObj(this_object$`users`, "array[User]", loadNamespace("openapi"))
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
    #' @return UsersPageAllOf in JSON format
    #' @export
    toJSONString = function() {
      jsoncontent <- c(
        if (!is.null(self$`users`)) {
          sprintf(
          '"users":
          [%s]
',
          paste(sapply(self$`users`, function(x) jsonlite::toJSON(x$toJSON(), auto_unbox = TRUE, digits = NA)), collapse = ",")
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
    #' Deserialize JSON string into an instance of UsersPageAllOf
    #'
    #' @description
    #' Deserialize JSON string into an instance of UsersPageAllOf
    #'
    #' @param input_json the JSON input
    #' @return the instance of UsersPageAllOf
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`users` <- ApiClient$new()$deserializeObj(this_object$`users`, "array[User]", loadNamespace("openapi"))
      # process additional properties/fields in the payload
      for (key in names(this_object)) {
        if (!(key %in% self$`_field_list`)) { # json key not in list of fields
          self$additional_properties[[key]] <- this_object[[key]]
        }
      }

      self
    },
    #' Validate JSON input with respect to UsersPageAllOf
    #'
    #' @description
    #' Validate JSON input with respect to UsersPageAllOf and throw an exception if invalid
    #'
    #' @param input the JSON input
    #' @export
    validateJSON = function(input) {
      input_json <- jsonlite::fromJSON(input)
      # check the required field `users`
      if (!is.null(input_json$`users`)) {
        stopifnot(is.vector(input_json$`users`), length(input_json$`users`) != 0)
        tmp <- sapply(input_json$`users`, function(x) stopifnot(R6::is.R6(x)))
      } else {
        stop(paste("The JSON input `", input, "` is invalid for UsersPageAllOf: the required field `users` is missing."))
      }
    },
    #' To string (JSON format)
    #'
    #' @description
    #' To string (JSON format)
    #'
    #' @return String representation of UsersPageAllOf
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
      # check if the required `users` is null
      if (is.null(self$`users`)) {
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
      # check if the required `users` is null
      if (is.null(self$`users`)) {
        invalid_fields["users"] <- "Non-nullable required field `users` cannot be null."
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
# UsersPageAllOf$unlock()
#
## Below is an example to define the print function
# UsersPageAllOf$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# UsersPageAllOf$lock()

