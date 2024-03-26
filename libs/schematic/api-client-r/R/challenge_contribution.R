#' Create a new ChallengeContribution
#'
#' @description
#' A challenge contribution.
#'
#' @docType class
#' @title ChallengeContribution
#' @description ChallengeContribution Class
#' @format An \code{R6Class} generator object
#' @field challengeId The unique identifier of the challenge. integer
#' @field organizationId The unique identifier of an organization integer
#' @field role  \link{ChallengeContributionRole}
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
ChallengeContribution <- R6::R6Class(
  "ChallengeContribution",
  public = list(
    `challengeId` = NULL,
    `organizationId` = NULL,
    `role` = NULL,
    #' Initialize a new ChallengeContribution class.
    #'
    #' @description
    #' Initialize a new ChallengeContribution class.
    #'
    #' @param challengeId The unique identifier of the challenge.
    #' @param organizationId The unique identifier of an organization
    #' @param role role
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`challengeId`, `organizationId`, `role`, ...) {
      if (!missing(`challengeId`)) {
        if (!(is.numeric(`challengeId`) && length(`challengeId`) == 1)) {
          stop(paste("Error! Invalid data for `challengeId`. Must be an integer:", `challengeId`))
        }
        self$`challengeId` <- `challengeId`
      }
      if (!missing(`organizationId`)) {
        if (!(is.numeric(`organizationId`) && length(`organizationId`) == 1)) {
          stop(paste("Error! Invalid data for `organizationId`. Must be an integer:", `organizationId`))
        }
        self$`organizationId` <- `organizationId`
      }
      if (!missing(`role`)) {
        if (!(`role` %in% c())) {
          stop(paste("Error! \"", `role`, "\" cannot be assigned to `role`. Must be .", sep = ""))
        }
        stopifnot(R6::is.R6(`role`))
        self$`role` <- `role`
      }
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ChallengeContribution in JSON format
    #' @export
    toJSON = function() {
      ChallengeContributionObject <- list()
      if (!is.null(self$`challengeId`)) {
        ChallengeContributionObject[["challengeId"]] <-
          self$`challengeId`
      }
      if (!is.null(self$`organizationId`)) {
        ChallengeContributionObject[["organizationId"]] <-
          self$`organizationId`
      }
      if (!is.null(self$`role`)) {
        ChallengeContributionObject[["role"]] <-
          self$`role`$toJSON()
      }
      ChallengeContributionObject
    },
    #' Deserialize JSON string into an instance of ChallengeContribution
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengeContribution
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengeContribution
    #' @export
    fromJSON = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      if (!is.null(this_object$`challengeId`)) {
        self$`challengeId` <- this_object$`challengeId`
      }
      if (!is.null(this_object$`organizationId`)) {
        self$`organizationId` <- this_object$`organizationId`
      }
      if (!is.null(this_object$`role`)) {
        `role_object` <- ChallengeContributionRole$new()
        `role_object`$fromJSON(jsonlite::toJSON(this_object$`role`, auto_unbox = TRUE, digits = NA))
        self$`role` <- `role_object`
      }
      self
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ChallengeContribution in JSON format
    #' @export
    toJSONString = function() {
      jsoncontent <- c(
        if (!is.null(self$`challengeId`)) {
          sprintf(
          '"challengeId":
            %d
                    ',
          self$`challengeId`
          )
        },
        if (!is.null(self$`organizationId`)) {
          sprintf(
          '"organizationId":
            %d
                    ',
          self$`organizationId`
          )
        },
        if (!is.null(self$`role`)) {
          sprintf(
          '"role":
          %s
          ',
          jsonlite::toJSON(self$`role`$toJSON(), auto_unbox = TRUE, digits = NA)
          )
        }
      )
      jsoncontent <- paste(jsoncontent, collapse = ",")
      json_string <- as.character(jsonlite::minify(paste("{", jsoncontent, "}", sep = "")))
    },
    #' Deserialize JSON string into an instance of ChallengeContribution
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengeContribution
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengeContribution
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`challengeId` <- this_object$`challengeId`
      self$`organizationId` <- this_object$`organizationId`
      self$`role` <- ChallengeContributionRole$new()$fromJSON(jsonlite::toJSON(this_object$`role`, auto_unbox = TRUE, digits = NA))
      self
    },
    #' Validate JSON input with respect to ChallengeContribution
    #'
    #' @description
    #' Validate JSON input with respect to ChallengeContribution and throw an exception if invalid
    #'
    #' @param input the JSON input
    #' @export
    validateJSON = function(input) {
      input_json <- jsonlite::fromJSON(input)
      # check the required field `challengeId`
      if (!is.null(input_json$`challengeId`)) {
        if (!(is.numeric(input_json$`challengeId`) && length(input_json$`challengeId`) == 1)) {
          stop(paste("Error! Invalid data for `challengeId`. Must be an integer:", input_json$`challengeId`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for ChallengeContribution: the required field `challengeId` is missing."))
      }
      # check the required field `organizationId`
      if (!is.null(input_json$`organizationId`)) {
        if (!(is.numeric(input_json$`organizationId`) && length(input_json$`organizationId`) == 1)) {
          stop(paste("Error! Invalid data for `organizationId`. Must be an integer:", input_json$`organizationId`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for ChallengeContribution: the required field `organizationId` is missing."))
      }
      # check the required field `role`
      if (!is.null(input_json$`role`)) {
        stopifnot(R6::is.R6(input_json$`role`))
      } else {
        stop(paste("The JSON input `", input, "` is invalid for ChallengeContribution: the required field `role` is missing."))
      }
    },
    #' To string (JSON format)
    #'
    #' @description
    #' To string (JSON format)
    #'
    #' @return String representation of ChallengeContribution
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
      # check if the required `challengeId` is null
      if (is.null(self$`challengeId`)) {
        return(FALSE)
      }

      # check if the required `organizationId` is null
      if (is.null(self$`organizationId`)) {
        return(FALSE)
      }

      # check if the required `role` is null
      if (is.null(self$`role`)) {
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
      # check if the required `challengeId` is null
      if (is.null(self$`challengeId`)) {
        invalid_fields["challengeId"] <- "Non-nullable required field `challengeId` cannot be null."
      }

      # check if the required `organizationId` is null
      if (is.null(self$`organizationId`)) {
        invalid_fields["organizationId"] <- "Non-nullable required field `organizationId` cannot be null."
      }

      # check if the required `role` is null
      if (is.null(self$`role`)) {
        invalid_fields["role"] <- "Non-nullable required field `role` cannot be null."
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
# ChallengeContribution$unlock()
#
## Below is an example to define the print function
# ChallengeContribution$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# ChallengeContribution$lock()

