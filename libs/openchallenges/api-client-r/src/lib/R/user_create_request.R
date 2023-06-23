#' Create a new UserCreateRequest
#'
#' @description
#' The information required to create a user account
#'
#' @docType class
#' @title UserCreateRequest
#' @description UserCreateRequest Class
#' @format An \code{R6Class} generator object
#' @field login  character
#' @field email An email address. character
#' @field password  character
#' @field name  character [optional]
#' @field avatarUrl  character [optional]
#' @field bio  character [optional]
#' @field _field_list a list of fields list(character)
#' @field additional_properties additional properties list(character) [optional]
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
UserCreateRequest <- R6::R6Class(
  "UserCreateRequest",
  public = list(
    `login` = NULL,
    `email` = NULL,
    `password` = NULL,
    `name` = NULL,
    `avatarUrl` = NULL,
    `bio` = NULL,
    `_field_list` = c("login", "email", "password", "name", "avatarUrl", "bio"),
    `additional_properties` = list(),
    #' Initialize a new UserCreateRequest class.
    #'
    #' @description
    #' Initialize a new UserCreateRequest class.
    #'
    #' @param login login
    #' @param email An email address.
    #' @param password password
    #' @param name name
    #' @param avatarUrl avatarUrl
    #' @param bio bio
    #' @param additional_properties additional properties (optional)
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`login`, `email`, `password`, `name` = NULL, `avatarUrl` = NULL, `bio` = NULL, additional_properties = NULL, ...) {
      if (!missing(`login`)) {
        if (!(is.character(`login`) && length(`login`) == 1)) {
          stop(paste("Error! Invalid data for `login`. Must be a string:", `login`))
        }
        self$`login` <- `login`
      }
      if (!missing(`email`)) {
        if (!(is.character(`email`) && length(`email`) == 1)) {
          stop(paste("Error! Invalid data for `email`. Must be a string:", `email`))
        }
        self$`email` <- `email`
      }
      if (!missing(`password`)) {
        if (!(is.character(`password`) && length(`password`) == 1)) {
          stop(paste("Error! Invalid data for `password`. Must be a string:", `password`))
        }
        self$`password` <- `password`
      }
      if (!is.null(`name`)) {
        if (!(is.character(`name`) && length(`name`) == 1)) {
          stop(paste("Error! Invalid data for `name`. Must be a string:", `name`))
        }
        self$`name` <- `name`
      }
      if (!is.null(`avatarUrl`)) {
        if (!(is.character(`avatarUrl`) && length(`avatarUrl`) == 1)) {
          stop(paste("Error! Invalid data for `avatarUrl`. Must be a string:", `avatarUrl`))
        }
        self$`avatarUrl` <- `avatarUrl`
      }
      if (!is.null(`bio`)) {
        if (!(is.character(`bio`) && length(`bio`) == 1)) {
          stop(paste("Error! Invalid data for `bio`. Must be a string:", `bio`))
        }
        self$`bio` <- `bio`
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
    #' @return UserCreateRequest in JSON format
    #' @export
    toJSON = function() {
      UserCreateRequestObject <- list()
      if (!is.null(self$`login`)) {
        UserCreateRequestObject[["login"]] <-
          self$`login`
      }
      if (!is.null(self$`email`)) {
        UserCreateRequestObject[["email"]] <-
          self$`email`
      }
      if (!is.null(self$`password`)) {
        UserCreateRequestObject[["password"]] <-
          self$`password`
      }
      if (!is.null(self$`name`)) {
        UserCreateRequestObject[["name"]] <-
          self$`name`
      }
      if (!is.null(self$`avatarUrl`)) {
        UserCreateRequestObject[["avatarUrl"]] <-
          self$`avatarUrl`
      }
      if (!is.null(self$`bio`)) {
        UserCreateRequestObject[["bio"]] <-
          self$`bio`
      }
      for (key in names(self$additional_properties)) {
        UserCreateRequestObject[[key]] <- self$additional_properties[[key]]
      }

      UserCreateRequestObject
    },
    #' Deserialize JSON string into an instance of UserCreateRequest
    #'
    #' @description
    #' Deserialize JSON string into an instance of UserCreateRequest
    #'
    #' @param input_json the JSON input
    #' @return the instance of UserCreateRequest
    #' @export
    fromJSON = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      if (!is.null(this_object$`login`)) {
        self$`login` <- this_object$`login`
      }
      if (!is.null(this_object$`email`)) {
        self$`email` <- this_object$`email`
      }
      if (!is.null(this_object$`password`)) {
        self$`password` <- this_object$`password`
      }
      if (!is.null(this_object$`name`)) {
        self$`name` <- this_object$`name`
      }
      if (!is.null(this_object$`avatarUrl`)) {
        self$`avatarUrl` <- this_object$`avatarUrl`
      }
      if (!is.null(this_object$`bio`)) {
        self$`bio` <- this_object$`bio`
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
    #' @return UserCreateRequest in JSON format
    #' @export
    toJSONString = function() {
      jsoncontent <- c(
        if (!is.null(self$`login`)) {
          sprintf(
          '"login":
            "%s"
                    ',
          self$`login`
          )
        },
        if (!is.null(self$`email`)) {
          sprintf(
          '"email":
            "%s"
                    ',
          self$`email`
          )
        },
        if (!is.null(self$`password`)) {
          sprintf(
          '"password":
            "%s"
                    ',
          self$`password`
          )
        },
        if (!is.null(self$`name`)) {
          sprintf(
          '"name":
            "%s"
                    ',
          self$`name`
          )
        },
        if (!is.null(self$`avatarUrl`)) {
          sprintf(
          '"avatarUrl":
            "%s"
                    ',
          self$`avatarUrl`
          )
        },
        if (!is.null(self$`bio`)) {
          sprintf(
          '"bio":
            "%s"
                    ',
          self$`bio`
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
    #' Deserialize JSON string into an instance of UserCreateRequest
    #'
    #' @description
    #' Deserialize JSON string into an instance of UserCreateRequest
    #'
    #' @param input_json the JSON input
    #' @return the instance of UserCreateRequest
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`login` <- this_object$`login`
      self$`email` <- this_object$`email`
      self$`password` <- this_object$`password`
      self$`name` <- this_object$`name`
      self$`avatarUrl` <- this_object$`avatarUrl`
      self$`bio` <- this_object$`bio`
      # process additional properties/fields in the payload
      for (key in names(this_object)) {
        if (!(key %in% self$`_field_list`)) { # json key not in list of fields
          self$additional_properties[[key]] <- this_object[[key]]
        }
      }

      self
    },
    #' Validate JSON input with respect to UserCreateRequest
    #'
    #' @description
    #' Validate JSON input with respect to UserCreateRequest and throw an exception if invalid
    #'
    #' @param input the JSON input
    #' @export
    validateJSON = function(input) {
      input_json <- jsonlite::fromJSON(input)
      # check the required field `login`
      if (!is.null(input_json$`login`)) {
        if (!(is.character(input_json$`login`) && length(input_json$`login`) == 1)) {
          stop(paste("Error! Invalid data for `login`. Must be a string:", input_json$`login`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for UserCreateRequest: the required field `login` is missing."))
      }
      # check the required field `email`
      if (!is.null(input_json$`email`)) {
        if (!(is.character(input_json$`email`) && length(input_json$`email`) == 1)) {
          stop(paste("Error! Invalid data for `email`. Must be a string:", input_json$`email`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for UserCreateRequest: the required field `email` is missing."))
      }
      # check the required field `password`
      if (!is.null(input_json$`password`)) {
        if (!(is.character(input_json$`password`) && length(input_json$`password`) == 1)) {
          stop(paste("Error! Invalid data for `password`. Must be a string:", input_json$`password`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for UserCreateRequest: the required field `password` is missing."))
      }
    },
    #' To string (JSON format)
    #'
    #' @description
    #' To string (JSON format)
    #'
    #' @return String representation of UserCreateRequest
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
      # check if the required `login` is null
      if (is.null(self$`login`)) {
        return(FALSE)
      }

      # check if the required `email` is null
      if (is.null(self$`email`)) {
        return(FALSE)
      }

      # check if the required `password` is null
      if (is.null(self$`password`)) {
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
      # check if the required `login` is null
      if (is.null(self$`login`)) {
        invalid_fields["login"] <- "Non-nullable required field `login` cannot be null."
      }

      # check if the required `email` is null
      if (is.null(self$`email`)) {
        invalid_fields["email"] <- "Non-nullable required field `email` cannot be null."
      }

      # check if the required `password` is null
      if (is.null(self$`password`)) {
        invalid_fields["password"] <- "Non-nullable required field `password` cannot be null."
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
# UserCreateRequest$unlock()
#
## Below is an example to define the print function
# UserCreateRequest$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# UserCreateRequest$lock()

