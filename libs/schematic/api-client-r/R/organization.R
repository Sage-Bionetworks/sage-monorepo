#' Create a new Organization
#'
#' @description
#' An organization
#'
#' @docType class
#' @title Organization
#' @description Organization Class
#' @format An \code{R6Class} generator object
#' @field id The unique identifier of an organization integer
#' @field name  character
#' @field email An email address. character
#' @field login The login of an organization character
#' @field description  character
#' @field avatarKey  character [optional]
#' @field websiteUrl  character
#' @field challengeCount  integer [optional]
#' @field createdAt  character
#' @field updatedAt  character
#' @field acronym  character [optional]
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
Organization <- R6::R6Class(
  "Organization",
  public = list(
    `id` = NULL,
    `name` = NULL,
    `email` = NULL,
    `login` = NULL,
    `description` = NULL,
    `avatarKey` = NULL,
    `websiteUrl` = NULL,
    `challengeCount` = NULL,
    `createdAt` = NULL,
    `updatedAt` = NULL,
    `acronym` = NULL,
    #' Initialize a new Organization class.
    #'
    #' @description
    #' Initialize a new Organization class.
    #'
    #' @param id The unique identifier of an organization
    #' @param name name
    #' @param email An email address.
    #' @param login The login of an organization
    #' @param description description
    #' @param websiteUrl websiteUrl
    #' @param createdAt createdAt
    #' @param updatedAt updatedAt
    #' @param avatarKey avatarKey
    #' @param challengeCount challengeCount
    #' @param acronym acronym
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`id`, `name`, `email`, `login`, `description`, `websiteUrl`, `createdAt`, `updatedAt`, `avatarKey` = NULL, `challengeCount` = NULL, `acronym` = NULL, ...) {
      if (!missing(`id`)) {
        if (!(is.numeric(`id`) && length(`id`) == 1)) {
          stop(paste("Error! Invalid data for `id`. Must be an integer:", `id`))
        }
        self$`id` <- `id`
      }
      if (!missing(`name`)) {
        if (!(is.character(`name`) && length(`name`) == 1)) {
          stop(paste("Error! Invalid data for `name`. Must be a string:", `name`))
        }
        self$`name` <- `name`
      }
      if (!missing(`email`)) {
        if (!(is.character(`email`) && length(`email`) == 1)) {
          stop(paste("Error! Invalid data for `email`. Must be a string:", `email`))
        }
        self$`email` <- `email`
      }
      if (!missing(`login`)) {
        if (!(is.character(`login`) && length(`login`) == 1)) {
          stop(paste("Error! Invalid data for `login`. Must be a string:", `login`))
        }
        self$`login` <- `login`
      }
      if (!missing(`description`)) {
        if (!(is.character(`description`) && length(`description`) == 1)) {
          stop(paste("Error! Invalid data for `description`. Must be a string:", `description`))
        }
        self$`description` <- `description`
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
      if (!is.null(`avatarKey`)) {
        if (!(is.character(`avatarKey`) && length(`avatarKey`) == 1)) {
          stop(paste("Error! Invalid data for `avatarKey`. Must be a string:", `avatarKey`))
        }
        self$`avatarKey` <- `avatarKey`
      }
      if (!is.null(`challengeCount`)) {
        if (!(is.numeric(`challengeCount`) && length(`challengeCount`) == 1)) {
          stop(paste("Error! Invalid data for `challengeCount`. Must be an integer:", `challengeCount`))
        }
        self$`challengeCount` <- `challengeCount`
      }
      if (!is.null(`acronym`)) {
        if (!(is.character(`acronym`) && length(`acronym`) == 1)) {
          stop(paste("Error! Invalid data for `acronym`. Must be a string:", `acronym`))
        }
        self$`acronym` <- `acronym`
      }
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return Organization in JSON format
    #' @export
    toJSON = function() {
      OrganizationObject <- list()
      if (!is.null(self$`id`)) {
        OrganizationObject[["id"]] <-
          self$`id`
      }
      if (!is.null(self$`name`)) {
        OrganizationObject[["name"]] <-
          self$`name`
      }
      if (!is.null(self$`email`)) {
        OrganizationObject[["email"]] <-
          self$`email`
      }
      if (!is.null(self$`login`)) {
        OrganizationObject[["login"]] <-
          self$`login`
      }
      if (!is.null(self$`description`)) {
        OrganizationObject[["description"]] <-
          self$`description`
      }
      if (!is.null(self$`avatarKey`)) {
        OrganizationObject[["avatarKey"]] <-
          self$`avatarKey`
      }
      if (!is.null(self$`websiteUrl`)) {
        OrganizationObject[["websiteUrl"]] <-
          self$`websiteUrl`
      }
      if (!is.null(self$`challengeCount`)) {
        OrganizationObject[["challengeCount"]] <-
          self$`challengeCount`
      }
      if (!is.null(self$`createdAt`)) {
        OrganizationObject[["createdAt"]] <-
          self$`createdAt`
      }
      if (!is.null(self$`updatedAt`)) {
        OrganizationObject[["updatedAt"]] <-
          self$`updatedAt`
      }
      if (!is.null(self$`acronym`)) {
        OrganizationObject[["acronym"]] <-
          self$`acronym`
      }
      OrganizationObject
    },
    #' Deserialize JSON string into an instance of Organization
    #'
    #' @description
    #' Deserialize JSON string into an instance of Organization
    #'
    #' @param input_json the JSON input
    #' @return the instance of Organization
    #' @export
    fromJSON = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      if (!is.null(this_object$`id`)) {
        self$`id` <- this_object$`id`
      }
      if (!is.null(this_object$`name`)) {
        self$`name` <- this_object$`name`
      }
      if (!is.null(this_object$`email`)) {
        self$`email` <- this_object$`email`
      }
      if (!is.null(this_object$`login`)) {
        self$`login` <- this_object$`login`
      }
      if (!is.null(this_object$`description`)) {
        self$`description` <- this_object$`description`
      }
      if (!is.null(this_object$`avatarKey`)) {
        self$`avatarKey` <- this_object$`avatarKey`
      }
      if (!is.null(this_object$`websiteUrl`)) {
        self$`websiteUrl` <- this_object$`websiteUrl`
      }
      if (!is.null(this_object$`challengeCount`)) {
        self$`challengeCount` <- this_object$`challengeCount`
      }
      if (!is.null(this_object$`createdAt`)) {
        self$`createdAt` <- this_object$`createdAt`
      }
      if (!is.null(this_object$`updatedAt`)) {
        self$`updatedAt` <- this_object$`updatedAt`
      }
      if (!is.null(this_object$`acronym`)) {
        self$`acronym` <- this_object$`acronym`
      }
      self
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return Organization in JSON format
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
        if (!is.null(self$`name`)) {
          sprintf(
          '"name":
            "%s"
                    ',
          self$`name`
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
        if (!is.null(self$`login`)) {
          sprintf(
          '"login":
            "%s"
                    ',
          self$`login`
          )
        },
        if (!is.null(self$`description`)) {
          sprintf(
          '"description":
            "%s"
                    ',
          self$`description`
          )
        },
        if (!is.null(self$`avatarKey`)) {
          sprintf(
          '"avatarKey":
            "%s"
                    ',
          self$`avatarKey`
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
        if (!is.null(self$`challengeCount`)) {
          sprintf(
          '"challengeCount":
            %d
                    ',
          self$`challengeCount`
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
        },
        if (!is.null(self$`acronym`)) {
          sprintf(
          '"acronym":
            "%s"
                    ',
          self$`acronym`
          )
        }
      )
      jsoncontent <- paste(jsoncontent, collapse = ",")
      json_string <- as.character(jsonlite::minify(paste("{", jsoncontent, "}", sep = "")))
    },
    #' Deserialize JSON string into an instance of Organization
    #'
    #' @description
    #' Deserialize JSON string into an instance of Organization
    #'
    #' @param input_json the JSON input
    #' @return the instance of Organization
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`id` <- this_object$`id`
      self$`name` <- this_object$`name`
      self$`email` <- this_object$`email`
      self$`login` <- this_object$`login`
      self$`description` <- this_object$`description`
      self$`avatarKey` <- this_object$`avatarKey`
      self$`websiteUrl` <- this_object$`websiteUrl`
      self$`challengeCount` <- this_object$`challengeCount`
      self$`createdAt` <- this_object$`createdAt`
      self$`updatedAt` <- this_object$`updatedAt`
      self$`acronym` <- this_object$`acronym`
      self
    },
    #' Validate JSON input with respect to Organization
    #'
    #' @description
    #' Validate JSON input with respect to Organization and throw an exception if invalid
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
        stop(paste("The JSON input `", input, "` is invalid for Organization: the required field `id` is missing."))
      }
      # check the required field `name`
      if (!is.null(input_json$`name`)) {
        if (!(is.character(input_json$`name`) && length(input_json$`name`) == 1)) {
          stop(paste("Error! Invalid data for `name`. Must be a string:", input_json$`name`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for Organization: the required field `name` is missing."))
      }
      # check the required field `email`
      if (!is.null(input_json$`email`)) {
        if (!(is.character(input_json$`email`) && length(input_json$`email`) == 1)) {
          stop(paste("Error! Invalid data for `email`. Must be a string:", input_json$`email`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for Organization: the required field `email` is missing."))
      }
      # check the required field `login`
      if (!is.null(input_json$`login`)) {
        if (!(is.character(input_json$`login`) && length(input_json$`login`) == 1)) {
          stop(paste("Error! Invalid data for `login`. Must be a string:", input_json$`login`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for Organization: the required field `login` is missing."))
      }
      # check the required field `description`
      if (!is.null(input_json$`description`)) {
        if (!(is.character(input_json$`description`) && length(input_json$`description`) == 1)) {
          stop(paste("Error! Invalid data for `description`. Must be a string:", input_json$`description`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for Organization: the required field `description` is missing."))
      }
      # check the required field `websiteUrl`
      if (!is.null(input_json$`websiteUrl`)) {
        if (!(is.character(input_json$`websiteUrl`) && length(input_json$`websiteUrl`) == 1)) {
          stop(paste("Error! Invalid data for `websiteUrl`. Must be a string:", input_json$`websiteUrl`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for Organization: the required field `websiteUrl` is missing."))
      }
      # check the required field `createdAt`
      if (!is.null(input_json$`createdAt`)) {
        if (!(is.character(input_json$`createdAt`) && length(input_json$`createdAt`) == 1)) {
          stop(paste("Error! Invalid data for `createdAt`. Must be a string:", input_json$`createdAt`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for Organization: the required field `createdAt` is missing."))
      }
      # check the required field `updatedAt`
      if (!is.null(input_json$`updatedAt`)) {
        if (!(is.character(input_json$`updatedAt`) && length(input_json$`updatedAt`) == 1)) {
          stop(paste("Error! Invalid data for `updatedAt`. Must be a string:", input_json$`updatedAt`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for Organization: the required field `updatedAt` is missing."))
      }
    },
    #' To string (JSON format)
    #'
    #' @description
    #' To string (JSON format)
    #'
    #' @return String representation of Organization
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

      # check if the required `name` is null
      if (is.null(self$`name`)) {
        return(FALSE)
      }

      # check if the required `email` is null
      if (is.null(self$`email`)) {
        return(FALSE)
      }

      # check if the required `login` is null
      if (is.null(self$`login`)) {
        return(FALSE)
      }

      if (nchar(self$`login`) > 64) {
        return(FALSE)
      }
      if (nchar(self$`login`) < 2) {
        return(FALSE)
      }
      if (!str_detect(self$`login`, "^[a-z0-9]+(?:-[a-z0-9]+)*$")) {
        return(FALSE)
      }

      # check if the required `description` is null
      if (is.null(self$`description`)) {
        return(FALSE)
      }

      # check if the required `websiteUrl` is null
      if (is.null(self$`websiteUrl`)) {
        return(FALSE)
      }

      if (self$`challengeCount` < 0) {
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

      # check if the required `name` is null
      if (is.null(self$`name`)) {
        invalid_fields["name"] <- "Non-nullable required field `name` cannot be null."
      }

      # check if the required `email` is null
      if (is.null(self$`email`)) {
        invalid_fields["email"] <- "Non-nullable required field `email` cannot be null."
      }

      # check if the required `login` is null
      if (is.null(self$`login`)) {
        invalid_fields["login"] <- "Non-nullable required field `login` cannot be null."
      }

      if (nchar(self$`login`) > 64) {
        invalid_fields["login"] <- "Invalid length for `login`, must be smaller than or equal to 64."
      }
      if (nchar(self$`login`) < 2) {
        invalid_fields["login"] <- "Invalid length for `login`, must be bigger than or equal to 2."
      }
      if (!str_detect(self$`login`, "^[a-z0-9]+(?:-[a-z0-9]+)*$")) {
        invalid_fields["login"] <- "Invalid value for `login`, must conform to the pattern ^[a-z0-9]+(?:-[a-z0-9]+)*$."
      }

      # check if the required `description` is null
      if (is.null(self$`description`)) {
        invalid_fields["description"] <- "Non-nullable required field `description` cannot be null."
      }

      # check if the required `websiteUrl` is null
      if (is.null(self$`websiteUrl`)) {
        invalid_fields["websiteUrl"] <- "Non-nullable required field `websiteUrl` cannot be null."
      }

      if (self$`challengeCount` < 0) {
        invalid_fields["challengeCount"] <- "Invalid value for `challengeCount`, must be bigger than or equal to 0."
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
# Organization$unlock()
#
## Below is an example to define the print function
# Organization$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# Organization$lock()

