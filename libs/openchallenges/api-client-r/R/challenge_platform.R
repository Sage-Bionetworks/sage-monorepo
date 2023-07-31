#' Create a new ChallengePlatform
#'
#' @description
#' A challenge platform
#'
#' @docType class
#' @title ChallengePlatform
#' @description ChallengePlatform Class
#' @format An \code{R6Class} generator object
#' @field id The unique identifier of a challenge platform. integer
#' @field slug The slug of the challenge platform. character
#' @field name The name of the challenge platform. character
#' @field avatarUrl  character
#' @field websiteUrl  character
#' @field createdAt  character
#' @field updatedAt  character
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
ChallengePlatform <- R6::R6Class(
  "ChallengePlatform",
  public = list(
    `id` = NULL,
    `slug` = NULL,
    `name` = NULL,
    `avatarUrl` = NULL,
    `websiteUrl` = NULL,
    `createdAt` = NULL,
    `updatedAt` = NULL,
    #' Initialize a new ChallengePlatform class.
    #'
    #' @description
    #' Initialize a new ChallengePlatform class.
    #'
    #' @param id The unique identifier of a challenge platform.
    #' @param slug The slug of the challenge platform.
    #' @param name The name of the challenge platform.
    #' @param avatarUrl avatarUrl
    #' @param websiteUrl websiteUrl
    #' @param createdAt createdAt
    #' @param updatedAt updatedAt
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`id`, `slug`, `name`, `avatarUrl`, `websiteUrl`, `createdAt`, `updatedAt`, ...) {
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
      if (!missing(`avatarUrl`)) {
        if (!(is.character(`avatarUrl`) && length(`avatarUrl`) == 1)) {
          stop(paste("Error! Invalid data for `avatarUrl`. Must be a string:", `avatarUrl`))
        }
        self$`avatarUrl` <- `avatarUrl`
      }
      if (!missing(`websiteUrl`)) {
        if (!(is.character(`websiteUrl`) && length(`websiteUrl`) == 1)) {
          stop(paste("Error! Invalid data for `websiteUrl`. Must be a string:", `websiteUrl`))
        }
        self$`websiteUrl` <- `websiteUrl`
      }
      if (!missing(`createdAt`)) {
        if (!(is.character(`createdAt`) && length(`createdAt`) == 1)) {
          stop(paste("Error! Invalid data for `createdAt`. Must be a string:", `createdAt`))
        }
        self$`createdAt` <- `createdAt`
      }
      if (!missing(`updatedAt`)) {
        if (!(is.character(`updatedAt`) && length(`updatedAt`) == 1)) {
          stop(paste("Error! Invalid data for `updatedAt`. Must be a string:", `updatedAt`))
        }
        self$`updatedAt` <- `updatedAt`
      }
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ChallengePlatform in JSON format
    #' @export
    toJSON = function() {
      ChallengePlatformObject <- list()
      if (!is.null(self$`id`)) {
        ChallengePlatformObject[["id"]] <-
          self$`id`
      }
      if (!is.null(self$`slug`)) {
        ChallengePlatformObject[["slug"]] <-
          self$`slug`
      }
      if (!is.null(self$`name`)) {
        ChallengePlatformObject[["name"]] <-
          self$`name`
      }
      if (!is.null(self$`avatarUrl`)) {
        ChallengePlatformObject[["avatarUrl"]] <-
          self$`avatarUrl`
      }
      if (!is.null(self$`websiteUrl`)) {
        ChallengePlatformObject[["websiteUrl"]] <-
          self$`websiteUrl`
      }
      if (!is.null(self$`createdAt`)) {
        ChallengePlatformObject[["createdAt"]] <-
          self$`createdAt`
      }
      if (!is.null(self$`updatedAt`)) {
        ChallengePlatformObject[["updatedAt"]] <-
          self$`updatedAt`
      }
      ChallengePlatformObject
    },
    #' Deserialize JSON string into an instance of ChallengePlatform
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengePlatform
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengePlatform
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
      if (!is.null(this_object$`avatarUrl`)) {
        self$`avatarUrl` <- this_object$`avatarUrl`
      }
      if (!is.null(this_object$`websiteUrl`)) {
        self$`websiteUrl` <- this_object$`websiteUrl`
      }
      if (!is.null(this_object$`createdAt`)) {
        self$`createdAt` <- this_object$`createdAt`
      }
      if (!is.null(this_object$`updatedAt`)) {
        self$`updatedAt` <- this_object$`updatedAt`
      }
      self
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ChallengePlatform in JSON format
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
        },
        if (!is.null(self$`avatarUrl`)) {
          sprintf(
          '"avatarUrl":
            "%s"
                    ',
          self$`avatarUrl`
          )
        },
        if (!is.null(self$`websiteUrl`)) {
          sprintf(
          '"websiteUrl":
            "%s"
                    ',
          self$`websiteUrl`
          )
        },
        if (!is.null(self$`createdAt`)) {
          sprintf(
          '"createdAt":
            "%s"
                    ',
          self$`createdAt`
          )
        },
        if (!is.null(self$`updatedAt`)) {
          sprintf(
          '"updatedAt":
            "%s"
                    ',
          self$`updatedAt`
          )
        }
      )
      jsoncontent <- paste(jsoncontent, collapse = ",")
      json_string <- as.character(jsonlite::minify(paste("{", jsoncontent, "}", sep = "")))
    },
    #' Deserialize JSON string into an instance of ChallengePlatform
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengePlatform
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengePlatform
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`id` <- this_object$`id`
      self$`slug` <- this_object$`slug`
      self$`name` <- this_object$`name`
      self$`avatarUrl` <- this_object$`avatarUrl`
      self$`websiteUrl` <- this_object$`websiteUrl`
      self$`createdAt` <- this_object$`createdAt`
      self$`updatedAt` <- this_object$`updatedAt`
      self
    },
    #' Validate JSON input with respect to ChallengePlatform
    #'
    #' @description
    #' Validate JSON input with respect to ChallengePlatform and throw an exception if invalid
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
        stop(paste("The JSON input `", input, "` is invalid for ChallengePlatform: the required field `id` is missing."))
      }
      # check the required field `slug`
      if (!is.null(input_json$`slug`)) {
        if (!(is.character(input_json$`slug`) && length(input_json$`slug`) == 1)) {
          stop(paste("Error! Invalid data for `slug`. Must be a string:", input_json$`slug`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for ChallengePlatform: the required field `slug` is missing."))
      }
      # check the required field `name`
      if (!is.null(input_json$`name`)) {
        if (!(is.character(input_json$`name`) && length(input_json$`name`) == 1)) {
          stop(paste("Error! Invalid data for `name`. Must be a string:", input_json$`name`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for ChallengePlatform: the required field `name` is missing."))
      }
      # check the required field `avatarUrl`
      if (!is.null(input_json$`avatarUrl`)) {
        if (!(is.character(input_json$`avatarUrl`) && length(input_json$`avatarUrl`) == 1)) {
          stop(paste("Error! Invalid data for `avatarUrl`. Must be a string:", input_json$`avatarUrl`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for ChallengePlatform: the required field `avatarUrl` is missing."))
      }
      # check the required field `websiteUrl`
      if (!is.null(input_json$`websiteUrl`)) {
        if (!(is.character(input_json$`websiteUrl`) && length(input_json$`websiteUrl`) == 1)) {
          stop(paste("Error! Invalid data for `websiteUrl`. Must be a string:", input_json$`websiteUrl`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for ChallengePlatform: the required field `websiteUrl` is missing."))
      }
      # check the required field `createdAt`
      if (!is.null(input_json$`createdAt`)) {
        if (!(is.character(input_json$`createdAt`) && length(input_json$`createdAt`) == 1)) {
          stop(paste("Error! Invalid data for `createdAt`. Must be a string:", input_json$`createdAt`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for ChallengePlatform: the required field `createdAt` is missing."))
      }
      # check the required field `updatedAt`
      if (!is.null(input_json$`updatedAt`)) {
        if (!(is.character(input_json$`updatedAt`) && length(input_json$`updatedAt`) == 1)) {
          stop(paste("Error! Invalid data for `updatedAt`. Must be a string:", input_json$`updatedAt`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for ChallengePlatform: the required field `updatedAt` is missing."))
      }
    },
    #' To string (JSON format)
    #'
    #' @description
    #' To string (JSON format)
    #'
    #' @return String representation of ChallengePlatform
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

      if (nchar(self$`name`) > 30) {
        return(FALSE)
      }
      if (nchar(self$`name`) < 3) {
        return(FALSE)
      }

      # check if the required `avatarUrl` is null
      if (is.null(self$`avatarUrl`)) {
        return(FALSE)
      }

      # check if the required `websiteUrl` is null
      if (is.null(self$`websiteUrl`)) {
        return(FALSE)
      }

      # check if the required `createdAt` is null
      if (is.null(self$`createdAt`)) {
        return(FALSE)
      }

      # check if the required `updatedAt` is null
      if (is.null(self$`updatedAt`)) {
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

      if (nchar(self$`name`) > 30) {
        invalid_fields["name"] <- "Invalid length for `name`, must be smaller than or equal to 30."
      }
      if (nchar(self$`name`) < 3) {
        invalid_fields["name"] <- "Invalid length for `name`, must be bigger than or equal to 3."
      }

      # check if the required `avatarUrl` is null
      if (is.null(self$`avatarUrl`)) {
        invalid_fields["avatarUrl"] <- "Non-nullable required field `avatarUrl` cannot be null."
      }

      # check if the required `websiteUrl` is null
      if (is.null(self$`websiteUrl`)) {
        invalid_fields["websiteUrl"] <- "Non-nullable required field `websiteUrl` cannot be null."
      }

      # check if the required `createdAt` is null
      if (is.null(self$`createdAt`)) {
        invalid_fields["createdAt"] <- "Non-nullable required field `createdAt` cannot be null."
      }

      # check if the required `updatedAt` is null
      if (is.null(self$`updatedAt`)) {
        invalid_fields["updatedAt"] <- "Non-nullable required field `updatedAt` cannot be null."
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
# ChallengePlatform$unlock()
#
## Below is an example to define the print function
# ChallengePlatform$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# ChallengePlatform$lock()

