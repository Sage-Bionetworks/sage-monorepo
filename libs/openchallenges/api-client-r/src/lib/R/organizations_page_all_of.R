#' Create a new OrganizationsPageAllOf
#'
#' @description
#' OrganizationsPageAllOf Class
#'
#' @docType class
#' @title OrganizationsPageAllOf
#' @description OrganizationsPageAllOf Class
#' @format An \code{R6Class} generator object
#' @field organizations A list of organizations list(\link{Organization})
#' @field _field_list a list of fields list(character)
#' @field additional_properties additional properties list(character) [optional]
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
OrganizationsPageAllOf <- R6::R6Class(
  "OrganizationsPageAllOf",
  public = list(
    `organizations` = NULL,
    `_field_list` = c("organizations"),
    `additional_properties` = list(),
    #' Initialize a new OrganizationsPageAllOf class.
    #'
    #' @description
    #' Initialize a new OrganizationsPageAllOf class.
    #'
    #' @param organizations A list of organizations
    #' @param additional_properties additional properties (optional)
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`organizations`, additional_properties = NULL, ...) {
      if (!missing(`organizations`)) {
        stopifnot(is.vector(`organizations`), length(`organizations`) != 0)
        sapply(`organizations`, function(x) stopifnot(R6::is.R6(x)))
        self$`organizations` <- `organizations`
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
    #' @return OrganizationsPageAllOf in JSON format
    #' @export
    toJSON = function() {
      OrganizationsPageAllOfObject <- list()
      if (!is.null(self$`organizations`)) {
        OrganizationsPageAllOfObject[["organizations"]] <-
          lapply(self$`organizations`, function(x) x$toJSON())
      }
      for (key in names(self$additional_properties)) {
        OrganizationsPageAllOfObject[[key]] <- self$additional_properties[[key]]
      }

      OrganizationsPageAllOfObject
    },
    #' Deserialize JSON string into an instance of OrganizationsPageAllOf
    #'
    #' @description
    #' Deserialize JSON string into an instance of OrganizationsPageAllOf
    #'
    #' @param input_json the JSON input
    #' @return the instance of OrganizationsPageAllOf
    #' @export
    fromJSON = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      if (!is.null(this_object$`organizations`)) {
        self$`organizations` <- ApiClient$new()$deserializeObj(this_object$`organizations`, "array[Organization]", loadNamespace("openapi"))
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
    #' @return OrganizationsPageAllOf in JSON format
    #' @export
    toJSONString = function() {
      jsoncontent <- c(
        if (!is.null(self$`organizations`)) {
          sprintf(
          '"organizations":
          [%s]
',
          paste(sapply(self$`organizations`, function(x) jsonlite::toJSON(x$toJSON(), auto_unbox = TRUE, digits = NA)), collapse = ",")
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
    #' Deserialize JSON string into an instance of OrganizationsPageAllOf
    #'
    #' @description
    #' Deserialize JSON string into an instance of OrganizationsPageAllOf
    #'
    #' @param input_json the JSON input
    #' @return the instance of OrganizationsPageAllOf
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`organizations` <- ApiClient$new()$deserializeObj(this_object$`organizations`, "array[Organization]", loadNamespace("openapi"))
      # process additional properties/fields in the payload
      for (key in names(this_object)) {
        if (!(key %in% self$`_field_list`)) { # json key not in list of fields
          self$additional_properties[[key]] <- this_object[[key]]
        }
      }

      self
    },
    #' Validate JSON input with respect to OrganizationsPageAllOf
    #'
    #' @description
    #' Validate JSON input with respect to OrganizationsPageAllOf and throw an exception if invalid
    #'
    #' @param input the JSON input
    #' @export
    validateJSON = function(input) {
      input_json <- jsonlite::fromJSON(input)
      # check the required field `organizations`
      if (!is.null(input_json$`organizations`)) {
        stopifnot(is.vector(input_json$`organizations`), length(input_json$`organizations`) != 0)
        tmp <- sapply(input_json$`organizations`, function(x) stopifnot(R6::is.R6(x)))
      } else {
        stop(paste("The JSON input `", input, "` is invalid for OrganizationsPageAllOf: the required field `organizations` is missing."))
      }
    },
    #' To string (JSON format)
    #'
    #' @description
    #' To string (JSON format)
    #'
    #' @return String representation of OrganizationsPageAllOf
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
      # check if the required `organizations` is null
      if (is.null(self$`organizations`)) {
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
      # check if the required `organizations` is null
      if (is.null(self$`organizations`)) {
        invalid_fields["organizations"] <- "Non-nullable required field `organizations` cannot be null."
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
# OrganizationsPageAllOf$unlock()
#
## Below is an example to define the print function
# OrganizationsPageAllOf$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# OrganizationsPageAllOf$lock()

