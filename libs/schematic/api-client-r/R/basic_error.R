#' Create a new BasicError
#'
#' @description
#' Problem details (tools.ietf.org/html/rfc7807)
#'
#' @docType class
#' @title BasicError
#' @description BasicError Class
#' @format An \code{R6Class} generator object
#' @field title A human readable documentation for the problem type character
#' @field status The HTTP status code integer
#' @field detail A human readable explanation specific to this occurrence of the problem character [optional]
#' @field type An absolute URI that identifies the problem type character [optional]
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
BasicError <- R6::R6Class(
  "BasicError",
  public = list(
    `title` = NULL,
    `status` = NULL,
    `detail` = NULL,
    `type` = NULL,
    #' Initialize a new BasicError class.
    #'
    #' @description
    #' Initialize a new BasicError class.
    #'
    #' @param title A human readable documentation for the problem type
    #' @param status The HTTP status code
    #' @param detail A human readable explanation specific to this occurrence of the problem
    #' @param type An absolute URI that identifies the problem type
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`title`, `status`, `detail` = NULL, `type` = NULL, ...) {
      if (!missing(`title`)) {
        if (!(is.character(`title`) && length(`title`) == 1)) {
          stop(paste("Error! Invalid data for `title`. Must be a string:", `title`))
        }
        self$`title` <- `title`
      }
      if (!missing(`status`)) {
        if (!(is.numeric(`status`) && length(`status`) == 1)) {
          stop(paste("Error! Invalid data for `status`. Must be an integer:", `status`))
        }
        self$`status` <- `status`
      }
      if (!is.null(`detail`)) {
        if (!(is.character(`detail`) && length(`detail`) == 1)) {
          stop(paste("Error! Invalid data for `detail`. Must be a string:", `detail`))
        }
        self$`detail` <- `detail`
      }
      if (!is.null(`type`)) {
        if (!(is.character(`type`) && length(`type`) == 1)) {
          stop(paste("Error! Invalid data for `type`. Must be a string:", `type`))
        }
        self$`type` <- `type`
      }
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return BasicError in JSON format
    #' @export
    toJSON = function() {
      BasicErrorObject <- list()
      if (!is.null(self$`title`)) {
        BasicErrorObject[["title"]] <-
          self$`title`
      }
      if (!is.null(self$`status`)) {
        BasicErrorObject[["status"]] <-
          self$`status`
      }
      if (!is.null(self$`detail`)) {
        BasicErrorObject[["detail"]] <-
          self$`detail`
      }
      if (!is.null(self$`type`)) {
        BasicErrorObject[["type"]] <-
          self$`type`
      }
      BasicErrorObject
    },
    #' Deserialize JSON string into an instance of BasicError
    #'
    #' @description
    #' Deserialize JSON string into an instance of BasicError
    #'
    #' @param input_json the JSON input
    #' @return the instance of BasicError
    #' @export
    fromJSON = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      if (!is.null(this_object$`title`)) {
        self$`title` <- this_object$`title`
      }
      if (!is.null(this_object$`status`)) {
        self$`status` <- this_object$`status`
      }
      if (!is.null(this_object$`detail`)) {
        self$`detail` <- this_object$`detail`
      }
      if (!is.null(this_object$`type`)) {
        self$`type` <- this_object$`type`
      }
      self
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return BasicError in JSON format
    #' @export
    toJSONString = function() {
      jsoncontent <- c(
        if (!is.null(self$`title`)) {
          sprintf(
          '"title":
            "%s"
                    ',
          self$`title`
          )
        },
        if (!is.null(self$`status`)) {
          sprintf(
          '"status":
            %d
                    ',
          self$`status`
          )
        },
        if (!is.null(self$`detail`)) {
          sprintf(
          '"detail":
            "%s"
                    ',
          self$`detail`
          )
        },
        if (!is.null(self$`type`)) {
          sprintf(
          '"type":
            "%s"
                    ',
          self$`type`
          )
        }
      )
      jsoncontent <- paste(jsoncontent, collapse = ",")
      json_string <- as.character(jsonlite::minify(paste("{", jsoncontent, "}", sep = "")))
    },
    #' Deserialize JSON string into an instance of BasicError
    #'
    #' @description
    #' Deserialize JSON string into an instance of BasicError
    #'
    #' @param input_json the JSON input
    #' @return the instance of BasicError
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`title` <- this_object$`title`
      self$`status` <- this_object$`status`
      self$`detail` <- this_object$`detail`
      self$`type` <- this_object$`type`
      self
    },
    #' Validate JSON input with respect to BasicError
    #'
    #' @description
    #' Validate JSON input with respect to BasicError and throw an exception if invalid
    #'
    #' @param input the JSON input
    #' @export
    validateJSON = function(input) {
      input_json <- jsonlite::fromJSON(input)
      # check the required field `title`
      if (!is.null(input_json$`title`)) {
        if (!(is.character(input_json$`title`) && length(input_json$`title`) == 1)) {
          stop(paste("Error! Invalid data for `title`. Must be a string:", input_json$`title`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for BasicError: the required field `title` is missing."))
      }
      # check the required field `status`
      if (!is.null(input_json$`status`)) {
        if (!(is.numeric(input_json$`status`) && length(input_json$`status`) == 1)) {
          stop(paste("Error! Invalid data for `status`. Must be an integer:", input_json$`status`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for BasicError: the required field `status` is missing."))
      }
    },
    #' To string (JSON format)
    #'
    #' @description
    #' To string (JSON format)
    #'
    #' @return String representation of BasicError
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
      # check if the required `title` is null
      if (is.null(self$`title`)) {
        return(FALSE)
      }

      # check if the required `status` is null
      if (is.null(self$`status`)) {
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
      # check if the required `title` is null
      if (is.null(self$`title`)) {
        invalid_fields["title"] <- "Non-nullable required field `title` cannot be null."
      }

      # check if the required `status` is null
      if (is.null(self$`status`)) {
        invalid_fields["status"] <- "Non-nullable required field `status` cannot be null."
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
# BasicError$unlock()
#
## Below is an example to define the print function
# BasicError$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# BasicError$lock()

