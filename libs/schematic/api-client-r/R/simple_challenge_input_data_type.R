#' Create a new SimpleChallengeInputDataType
#'
#' @description
#' A simple challenge input data type.
#'
#' @docType class
#' @title SimpleChallengeInputDataType
#' @description SimpleChallengeInputDataType Class
#' @format An \code{R6Class} generator object
#' @field id The unique identifier of a challenge input data type. integer
#' @field slug The slug of the challenge input data type. character
#' @field name The name of the challenge input data type. character
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
SimpleChallengeInputDataType <- R6::R6Class(
  "SimpleChallengeInputDataType",
  public = list(
    `id` = NULL,
    `slug` = NULL,
    `name` = NULL,
    #' Initialize a new SimpleChallengeInputDataType class.
    #'
    #' @description
    #' Initialize a new SimpleChallengeInputDataType class.
    #'
    #' @param id The unique identifier of a challenge input data type.
    #' @param slug The slug of the challenge input data type.
    #' @param name The name of the challenge input data type.
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`id`, `slug`, `name`, ...) {
      if (!missing(`id`)) {
        if (!(is.numeric(`id`) && length(`id`) == 1)) {
          stop(paste("Error! Invalid data for `id`. Must be an integer:", `id`))
        }
        self$`id` <- `id`
      }
      if (!missing(`slug`)) {
        if (!(is.character(`slug`) && length(`slug`) == 1)) {
          stop(paste("Error! Invalid data for `slug`. Must be a string:", `slug`))
        }
        self$`slug` <- `slug`
      }
      if (!missing(`name`)) {
        if (!(is.character(`name`) && length(`name`) == 1)) {
          stop(paste("Error! Invalid data for `name`. Must be a string:", `name`))
        }
        self$`name` <- `name`
      }
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return SimpleChallengeInputDataType in JSON format
    #' @export
    toJSON = function() {
      SimpleChallengeInputDataTypeObject <- list()
      if (!is.null(self$`id`)) {
        SimpleChallengeInputDataTypeObject[["id"]] <-
          self$`id`
      }
      if (!is.null(self$`slug`)) {
        SimpleChallengeInputDataTypeObject[["slug"]] <-
          self$`slug`
      }
      if (!is.null(self$`name`)) {
        SimpleChallengeInputDataTypeObject[["name"]] <-
          self$`name`
      }
      SimpleChallengeInputDataTypeObject
    },
    #' Deserialize JSON string into an instance of SimpleChallengeInputDataType
    #'
    #' @description
    #' Deserialize JSON string into an instance of SimpleChallengeInputDataType
    #'
    #' @param input_json the JSON input
    #' @return the instance of SimpleChallengeInputDataType
    #' @export
    fromJSON = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      if (!is.null(this_object$`id`)) {
        self$`id` <- this_object$`id`
      }
      if (!is.null(this_object$`slug`)) {
        self$`slug` <- this_object$`slug`
      }
      if (!is.null(this_object$`name`)) {
        self$`name` <- this_object$`name`
      }
      self
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return SimpleChallengeInputDataType in JSON format
    #' @export
    toJSONString = function() {
      jsoncontent <- c(
        if (!is.null(self$`id`)) {
          sprintf(
          '"id":
            %d
                    ',
          self$`id`
          )
        },
        if (!is.null(self$`slug`)) {
          sprintf(
          '"slug":
            "%s"
                    ',
          self$`slug`
          )
        },
        if (!is.null(self$`name`)) {
          sprintf(
          '"name":
            "%s"
                    ',
          self$`name`
          )
        }
      )
      jsoncontent <- paste(jsoncontent, collapse = ",")
      json_string <- as.character(jsonlite::minify(paste("{", jsoncontent, "}", sep = "")))
    },
    #' Deserialize JSON string into an instance of SimpleChallengeInputDataType
    #'
    #' @description
    #' Deserialize JSON string into an instance of SimpleChallengeInputDataType
    #'
    #' @param input_json the JSON input
    #' @return the instance of SimpleChallengeInputDataType
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`id` <- this_object$`id`
      self$`slug` <- this_object$`slug`
      self$`name` <- this_object$`name`
      self
    },
    #' Validate JSON input with respect to SimpleChallengeInputDataType
    #'
    #' @description
    #' Validate JSON input with respect to SimpleChallengeInputDataType and throw an exception if invalid
    #'
    #' @param input the JSON input
    #' @export
    validateJSON = function(input) {
      input_json <- jsonlite::fromJSON(input)
      # check the required field `id`
      if (!is.null(input_json$`id`)) {
        if (!(is.numeric(input_json$`id`) && length(input_json$`id`) == 1)) {
          stop(paste("Error! Invalid data for `id`. Must be an integer:", input_json$`id`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for SimpleChallengeInputDataType: the required field `id` is missing."))
      }
      # check the required field `slug`
      if (!is.null(input_json$`slug`)) {
        if (!(is.character(input_json$`slug`) && length(input_json$`slug`) == 1)) {
          stop(paste("Error! Invalid data for `slug`. Must be a string:", input_json$`slug`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for SimpleChallengeInputDataType: the required field `slug` is missing."))
      }
      # check the required field `name`
      if (!is.null(input_json$`name`)) {
        if (!(is.character(input_json$`name`) && length(input_json$`name`) == 1)) {
          stop(paste("Error! Invalid data for `name`. Must be a string:", input_json$`name`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for SimpleChallengeInputDataType: the required field `name` is missing."))
      }
    },
    #' To string (JSON format)
    #'
    #' @description
    #' To string (JSON format)
    #'
    #' @return String representation of SimpleChallengeInputDataType
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
      # check if the required `id` is null
      if (is.null(self$`id`)) {
        return(FALSE)
      }

      # check if the required `slug` is null
      if (is.null(self$`slug`)) {
        return(FALSE)
      }

      if (nchar(self$`slug`) > 30) {
        return(FALSE)
      }
      if (nchar(self$`slug`) < 3) {
        return(FALSE)
      }
      if (!str_detect(self$`slug`, "^[a-z0-9]+(?:-[a-z0-9]+)*$")) {
        return(FALSE)
      }

      # check if the required `name` is null
      if (is.null(self$`name`)) {
        return(FALSE)
      }

      if (nchar(self$`name`) > 50) {
        return(FALSE)
      }
      if (nchar(self$`name`) < 3) {
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
      # check if the required `id` is null
      if (is.null(self$`id`)) {
        invalid_fields["id"] <- "Non-nullable required field `id` cannot be null."
      }

      # check if the required `slug` is null
      if (is.null(self$`slug`)) {
        invalid_fields["slug"] <- "Non-nullable required field `slug` cannot be null."
      }

      if (nchar(self$`slug`) > 30) {
        invalid_fields["slug"] <- "Invalid length for `slug`, must be smaller than or equal to 30."
      }
      if (nchar(self$`slug`) < 3) {
        invalid_fields["slug"] <- "Invalid length for `slug`, must be bigger than or equal to 3."
      }
      if (!str_detect(self$`slug`, "^[a-z0-9]+(?:-[a-z0-9]+)*$")) {
        invalid_fields["slug"] <- "Invalid value for `slug`, must conform to the pattern ^[a-z0-9]+(?:-[a-z0-9]+)*$."
      }

      # check if the required `name` is null
      if (is.null(self$`name`)) {
        invalid_fields["name"] <- "Non-nullable required field `name` cannot be null."
      }

      if (nchar(self$`name`) > 50) {
        invalid_fields["name"] <- "Invalid length for `name`, must be smaller than or equal to 50."
      }
      if (nchar(self$`name`) < 3) {
        invalid_fields["name"] <- "Invalid length for `name`, must be bigger than or equal to 3."
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
# SimpleChallengeInputDataType$unlock()
#
## Below is an example to define the print function
# SimpleChallengeInputDataType$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# SimpleChallengeInputDataType$lock()

