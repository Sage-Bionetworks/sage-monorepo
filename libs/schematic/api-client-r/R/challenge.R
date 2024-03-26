#' Create a new Challenge
#'
#' @description
#' A challenge
#'
#' @docType class
#' @title Challenge
#' @description Challenge Class
#' @format An \code{R6Class} generator object
#' @field id The unique identifier of the challenge. integer
#' @field slug The slug of the challenge. character
#' @field name The name of the challenge. character
#' @field headline The headline of the challenge. character [optional]
#' @field description The description of the challenge. character
#' @field doi  character [optional]
#' @field status  \link{ChallengeStatus}
#' @field difficulty  \link{ChallengeDifficulty}
#' @field platform  \link{SimpleChallengePlatform}
#' @field websiteUrl  character [optional]
#' @field avatarUrl  character [optional]
#' @field incentives  list(\link{ChallengeIncentive})
#' @field submissionTypes  list(\link{ChallengeSubmissionType})
#' @field inputDataTypes  list(\link{SimpleChallengeInputDataType}) [optional]
#' @field startDate The start date of the challenge. character [optional]
#' @field endDate The end date of the challenge. character [optional]
#' @field starredCount The number of times the challenge has been starred by users. integer
#' @field createdAt  character
#' @field updatedAt  character
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
Challenge <- R6::R6Class(
  "Challenge",
  public = list(
    `id` = NULL,
    `slug` = NULL,
    `name` = NULL,
    `headline` = NULL,
    `description` = NULL,
    `doi` = NULL,
    `status` = NULL,
    `difficulty` = NULL,
    `platform` = NULL,
    `websiteUrl` = NULL,
    `avatarUrl` = NULL,
    `incentives` = NULL,
    `submissionTypes` = NULL,
    `inputDataTypes` = NULL,
    `startDate` = NULL,
    `endDate` = NULL,
    `starredCount` = NULL,
    `createdAt` = NULL,
    `updatedAt` = NULL,
    #' Initialize a new Challenge class.
    #'
    #' @description
    #' Initialize a new Challenge class.
    #'
    #' @param id The unique identifier of the challenge.
    #' @param slug The slug of the challenge.
    #' @param name The name of the challenge.
    #' @param description The description of the challenge.
    #' @param status status
    #' @param difficulty difficulty
    #' @param platform platform
    #' @param incentives incentives
    #' @param submissionTypes submissionTypes
    #' @param starredCount The number of times the challenge has been starred by users.
    #' @param createdAt createdAt
    #' @param updatedAt updatedAt
    #' @param headline The headline of the challenge.
    #' @param doi doi
    #' @param websiteUrl websiteUrl
    #' @param avatarUrl avatarUrl
    #' @param inputDataTypes inputDataTypes
    #' @param startDate The start date of the challenge.
    #' @param endDate The end date of the challenge.
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`id`, `slug`, `name`, `description`, `status`, `difficulty`, `platform`, `incentives`, `submissionTypes`, `starredCount`, `createdAt`, `updatedAt`, `headline` = NULL, `doi` = NULL, `websiteUrl` = NULL, `avatarUrl` = NULL, `inputDataTypes` = NULL, `startDate` = NULL, `endDate` = NULL, ...) {
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
      if (!missing(`description`)) {
        if (!(is.character(`description`) && length(`description`) == 1)) {
          stop(paste("Error! Invalid data for `description`. Must be a string:", `description`))
        }
        self$`description` <- `description`
      }
      if (!missing(`status`)) {
        if (!(`status` %in% c())) {
          stop(paste("Error! \"", `status`, "\" cannot be assigned to `status`. Must be .", sep = ""))
        }
        stopifnot(R6::is.R6(`status`))
        self$`status` <- `status`
      }
      if (!missing(`difficulty`)) {
        if (!(`difficulty` %in% c())) {
          stop(paste("Error! \"", `difficulty`, "\" cannot be assigned to `difficulty`. Must be .", sep = ""))
        }
        stopifnot(R6::is.R6(`difficulty`))
        self$`difficulty` <- `difficulty`
      }
      if (!missing(`platform`)) {
        stopifnot(R6::is.R6(`platform`))
        self$`platform` <- `platform`
      }
      if (!missing(`incentives`)) {
        stopifnot(is.vector(`incentives`), length(`incentives`) != 0)
        sapply(`incentives`, function(x) stopifnot(R6::is.R6(x)))
        self$`incentives` <- `incentives`
      }
      if (!missing(`submissionTypes`)) {
        stopifnot(is.vector(`submissionTypes`), length(`submissionTypes`) != 0)
        sapply(`submissionTypes`, function(x) stopifnot(R6::is.R6(x)))
        self$`submissionTypes` <- `submissionTypes`
      }
      if (!missing(`starredCount`)) {
        if (!(is.numeric(`starredCount`) && length(`starredCount`) == 1)) {
          stop(paste("Error! Invalid data for `starredCount`. Must be an integer:", `starredCount`))
        }
        self$`starredCount` <- `starredCount`
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
      if (!is.null(`headline`)) {
        if (!(is.character(`headline`) && length(`headline`) == 1)) {
          stop(paste("Error! Invalid data for `headline`. Must be a string:", `headline`))
        }
        self$`headline` <- `headline`
      }
      if (!is.null(`doi`)) {
        if (!(is.character(`doi`) && length(`doi`) == 1)) {
          stop(paste("Error! Invalid data for `doi`. Must be a string:", `doi`))
        }
        self$`doi` <- `doi`
      }
      if (!is.null(`websiteUrl`)) {
        if (!(is.character(`websiteUrl`) && length(`websiteUrl`) == 1)) {
          stop(paste("Error! Invalid data for `websiteUrl`. Must be a string:", `websiteUrl`))
        }
        self$`websiteUrl` <- `websiteUrl`
      }
      if (!is.null(`avatarUrl`)) {
        if (!(is.character(`avatarUrl`) && length(`avatarUrl`) == 1)) {
          stop(paste("Error! Invalid data for `avatarUrl`. Must be a string:", `avatarUrl`))
        }
        self$`avatarUrl` <- `avatarUrl`
      }
      if (!is.null(`inputDataTypes`)) {
        stopifnot(is.vector(`inputDataTypes`), length(`inputDataTypes`) != 0)
        sapply(`inputDataTypes`, function(x) stopifnot(R6::is.R6(x)))
        self$`inputDataTypes` <- `inputDataTypes`
      }
      if (!is.null(`startDate`)) {
        if (!is.character(`startDate`)) {
          stop(paste("Error! Invalid data for `startDate`. Must be a string:", `startDate`))
        }
        self$`startDate` <- `startDate`
      }
      if (!is.null(`endDate`)) {
        if (!is.character(`endDate`)) {
          stop(paste("Error! Invalid data for `endDate`. Must be a string:", `endDate`))
        }
        self$`endDate` <- `endDate`
      }
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return Challenge in JSON format
    #' @export
    toJSON = function() {
      ChallengeObject <- list()
      if (!is.null(self$`id`)) {
        ChallengeObject[["id"]] <-
          self$`id`
      }
      if (!is.null(self$`slug`)) {
        ChallengeObject[["slug"]] <-
          self$`slug`
      }
      if (!is.null(self$`name`)) {
        ChallengeObject[["name"]] <-
          self$`name`
      }
      if (!is.null(self$`headline`)) {
        ChallengeObject[["headline"]] <-
          self$`headline`
      }
      if (!is.null(self$`description`)) {
        ChallengeObject[["description"]] <-
          self$`description`
      }
      if (!is.null(self$`doi`)) {
        ChallengeObject[["doi"]] <-
          self$`doi`
      }
      if (!is.null(self$`status`)) {
        ChallengeObject[["status"]] <-
          self$`status`$toJSON()
      }
      if (!is.null(self$`difficulty`)) {
        ChallengeObject[["difficulty"]] <-
          self$`difficulty`$toJSON()
      }
      if (!is.null(self$`platform`)) {
        ChallengeObject[["platform"]] <-
          self$`platform`$toJSON()
      }
      if (!is.null(self$`websiteUrl`)) {
        ChallengeObject[["websiteUrl"]] <-
          self$`websiteUrl`
      }
      if (!is.null(self$`avatarUrl`)) {
        ChallengeObject[["avatarUrl"]] <-
          self$`avatarUrl`
      }
      if (!is.null(self$`incentives`)) {
        ChallengeObject[["incentives"]] <-
          lapply(self$`incentives`, function(x) x$toJSON())
      }
      if (!is.null(self$`submissionTypes`)) {
        ChallengeObject[["submissionTypes"]] <-
          lapply(self$`submissionTypes`, function(x) x$toJSON())
      }
      if (!is.null(self$`inputDataTypes`)) {
        ChallengeObject[["inputDataTypes"]] <-
          lapply(self$`inputDataTypes`, function(x) x$toJSON())
      }
      if (!is.null(self$`startDate`)) {
        ChallengeObject[["startDate"]] <-
          self$`startDate`
      }
      if (!is.null(self$`endDate`)) {
        ChallengeObject[["endDate"]] <-
          self$`endDate`
      }
      if (!is.null(self$`starredCount`)) {
        ChallengeObject[["starredCount"]] <-
          self$`starredCount`
      }
      if (!is.null(self$`createdAt`)) {
        ChallengeObject[["createdAt"]] <-
          self$`createdAt`
      }
      if (!is.null(self$`updatedAt`)) {
        ChallengeObject[["updatedAt"]] <-
          self$`updatedAt`
      }
      ChallengeObject
    },
    #' Deserialize JSON string into an instance of Challenge
    #'
    #' @description
    #' Deserialize JSON string into an instance of Challenge
    #'
    #' @param input_json the JSON input
    #' @return the instance of Challenge
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
      if (!is.null(this_object$`headline`)) {
        self$`headline` <- this_object$`headline`
      }
      if (!is.null(this_object$`description`)) {
        self$`description` <- this_object$`description`
      }
      if (!is.null(this_object$`doi`)) {
        self$`doi` <- this_object$`doi`
      }
      if (!is.null(this_object$`status`)) {
        `status_object` <- ChallengeStatus$new()
        `status_object`$fromJSON(jsonlite::toJSON(this_object$`status`, auto_unbox = TRUE, digits = NA))
        self$`status` <- `status_object`
      }
      if (!is.null(this_object$`difficulty`)) {
        `difficulty_object` <- ChallengeDifficulty$new()
        `difficulty_object`$fromJSON(jsonlite::toJSON(this_object$`difficulty`, auto_unbox = TRUE, digits = NA))
        self$`difficulty` <- `difficulty_object`
      }
      if (!is.null(this_object$`platform`)) {
        `platform_object` <- SimpleChallengePlatform$new()
        `platform_object`$fromJSON(jsonlite::toJSON(this_object$`platform`, auto_unbox = TRUE, digits = NA))
        self$`platform` <- `platform_object`
      }
      if (!is.null(this_object$`websiteUrl`)) {
        self$`websiteUrl` <- this_object$`websiteUrl`
      }
      if (!is.null(this_object$`avatarUrl`)) {
        self$`avatarUrl` <- this_object$`avatarUrl`
      }
      if (!is.null(this_object$`incentives`)) {
        self$`incentives` <- ApiClient$new()$deserializeObj(this_object$`incentives`, "array[ChallengeIncentive]", loadNamespace("openapi"))
      }
      if (!is.null(this_object$`submissionTypes`)) {
        self$`submissionTypes` <- ApiClient$new()$deserializeObj(this_object$`submissionTypes`, "array[ChallengeSubmissionType]", loadNamespace("openapi"))
      }
      if (!is.null(this_object$`inputDataTypes`)) {
        self$`inputDataTypes` <- ApiClient$new()$deserializeObj(this_object$`inputDataTypes`, "array[SimpleChallengeInputDataType]", loadNamespace("openapi"))
      }
      if (!is.null(this_object$`startDate`)) {
        self$`startDate` <- this_object$`startDate`
      }
      if (!is.null(this_object$`endDate`)) {
        self$`endDate` <- this_object$`endDate`
      }
      if (!is.null(this_object$`starredCount`)) {
        self$`starredCount` <- this_object$`starredCount`
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
    #' @return Challenge in JSON format
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
        if (!is.null(self$`headline`)) {
          sprintf(
          '"headline":
            "%s"
                    ',
          self$`headline`
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
        if (!is.null(self$`doi`)) {
          sprintf(
          '"doi":
            "%s"
                    ',
          self$`doi`
          )
        },
        if (!is.null(self$`status`)) {
          sprintf(
          '"status":
          %s
          ',
          jsonlite::toJSON(self$`status`$toJSON(), auto_unbox = TRUE, digits = NA)
          )
        },
        if (!is.null(self$`difficulty`)) {
          sprintf(
          '"difficulty":
          %s
          ',
          jsonlite::toJSON(self$`difficulty`$toJSON(), auto_unbox = TRUE, digits = NA)
          )
        },
        if (!is.null(self$`platform`)) {
          sprintf(
          '"platform":
          %s
          ',
          jsonlite::toJSON(self$`platform`$toJSON(), auto_unbox = TRUE, digits = NA)
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
        if (!is.null(self$`avatarUrl`)) {
          sprintf(
          '"avatarUrl":
            "%s"
                    ',
          self$`avatarUrl`
          )
        },
        if (!is.null(self$`incentives`)) {
          sprintf(
          '"incentives":
          [%s]
',
          paste(sapply(self$`incentives`, function(x) jsonlite::toJSON(x$toJSON(), auto_unbox = TRUE, digits = NA)), collapse = ",")
          )
        },
        if (!is.null(self$`submissionTypes`)) {
          sprintf(
          '"submissionTypes":
          [%s]
',
          paste(sapply(self$`submissionTypes`, function(x) jsonlite::toJSON(x$toJSON(), auto_unbox = TRUE, digits = NA)), collapse = ",")
          )
        },
        if (!is.null(self$`inputDataTypes`)) {
          sprintf(
          '"inputDataTypes":
          [%s]
',
          paste(sapply(self$`inputDataTypes`, function(x) jsonlite::toJSON(x$toJSON(), auto_unbox = TRUE, digits = NA)), collapse = ",")
          )
        },
        if (!is.null(self$`startDate`)) {
          sprintf(
          '"startDate":
            "%s"
                    ',
          self$`startDate`
          )
        },
        if (!is.null(self$`endDate`)) {
          sprintf(
          '"endDate":
            "%s"
                    ',
          self$`endDate`
          )
        },
        if (!is.null(self$`starredCount`)) {
          sprintf(
          '"starredCount":
            %d
                    ',
          self$`starredCount`
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
    #' Deserialize JSON string into an instance of Challenge
    #'
    #' @description
    #' Deserialize JSON string into an instance of Challenge
    #'
    #' @param input_json the JSON input
    #' @return the instance of Challenge
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`id` <- this_object$`id`
      self$`slug` <- this_object$`slug`
      self$`name` <- this_object$`name`
      self$`headline` <- this_object$`headline`
      self$`description` <- this_object$`description`
      self$`doi` <- this_object$`doi`
      self$`status` <- ChallengeStatus$new()$fromJSON(jsonlite::toJSON(this_object$`status`, auto_unbox = TRUE, digits = NA))
      self$`difficulty` <- ChallengeDifficulty$new()$fromJSON(jsonlite::toJSON(this_object$`difficulty`, auto_unbox = TRUE, digits = NA))
      self$`platform` <- SimpleChallengePlatform$new()$fromJSON(jsonlite::toJSON(this_object$`platform`, auto_unbox = TRUE, digits = NA))
      self$`websiteUrl` <- this_object$`websiteUrl`
      self$`avatarUrl` <- this_object$`avatarUrl`
      self$`incentives` <- ApiClient$new()$deserializeObj(this_object$`incentives`, "array[ChallengeIncentive]", loadNamespace("openapi"))
      self$`submissionTypes` <- ApiClient$new()$deserializeObj(this_object$`submissionTypes`, "array[ChallengeSubmissionType]", loadNamespace("openapi"))
      self$`inputDataTypes` <- ApiClient$new()$deserializeObj(this_object$`inputDataTypes`, "array[SimpleChallengeInputDataType]", loadNamespace("openapi"))
      self$`startDate` <- this_object$`startDate`
      self$`endDate` <- this_object$`endDate`
      self$`starredCount` <- this_object$`starredCount`
      self$`createdAt` <- this_object$`createdAt`
      self$`updatedAt` <- this_object$`updatedAt`
      self
    },
    #' Validate JSON input with respect to Challenge
    #'
    #' @description
    #' Validate JSON input with respect to Challenge and throw an exception if invalid
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
        stop(paste("The JSON input `", input, "` is invalid for Challenge: the required field `id` is missing."))
      }
      # check the required field `slug`
      if (!is.null(input_json$`slug`)) {
        if (!(is.character(input_json$`slug`) && length(input_json$`slug`) == 1)) {
          stop(paste("Error! Invalid data for `slug`. Must be a string:", input_json$`slug`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for Challenge: the required field `slug` is missing."))
      }
      # check the required field `name`
      if (!is.null(input_json$`name`)) {
        if (!(is.character(input_json$`name`) && length(input_json$`name`) == 1)) {
          stop(paste("Error! Invalid data for `name`. Must be a string:", input_json$`name`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for Challenge: the required field `name` is missing."))
      }
      # check the required field `description`
      if (!is.null(input_json$`description`)) {
        if (!(is.character(input_json$`description`) && length(input_json$`description`) == 1)) {
          stop(paste("Error! Invalid data for `description`. Must be a string:", input_json$`description`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for Challenge: the required field `description` is missing."))
      }
      # check the required field `status`
      if (!is.null(input_json$`status`)) {
        stopifnot(R6::is.R6(input_json$`status`))
      } else {
        stop(paste("The JSON input `", input, "` is invalid for Challenge: the required field `status` is missing."))
      }
      # check the required field `difficulty`
      if (!is.null(input_json$`difficulty`)) {
        stopifnot(R6::is.R6(input_json$`difficulty`))
      } else {
        stop(paste("The JSON input `", input, "` is invalid for Challenge: the required field `difficulty` is missing."))
      }
      # check the required field `platform`
      if (!is.null(input_json$`platform`)) {
        stopifnot(R6::is.R6(input_json$`platform`))
      } else {
        stop(paste("The JSON input `", input, "` is invalid for Challenge: the required field `platform` is missing."))
      }
      # check the required field `incentives`
      if (!is.null(input_json$`incentives`)) {
        stopifnot(is.vector(input_json$`incentives`), length(input_json$`incentives`) != 0)
        tmp <- sapply(input_json$`incentives`, function(x) stopifnot(R6::is.R6(x)))
      } else {
        stop(paste("The JSON input `", input, "` is invalid for Challenge: the required field `incentives` is missing."))
      }
      # check the required field `submissionTypes`
      if (!is.null(input_json$`submissionTypes`)) {
        stopifnot(is.vector(input_json$`submissionTypes`), length(input_json$`submissionTypes`) != 0)
        tmp <- sapply(input_json$`submissionTypes`, function(x) stopifnot(R6::is.R6(x)))
      } else {
        stop(paste("The JSON input `", input, "` is invalid for Challenge: the required field `submissionTypes` is missing."))
      }
      # check the required field `starredCount`
      if (!is.null(input_json$`starredCount`)) {
        if (!(is.numeric(input_json$`starredCount`) && length(input_json$`starredCount`) == 1)) {
          stop(paste("Error! Invalid data for `starredCount`. Must be an integer:", input_json$`starredCount`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for Challenge: the required field `starredCount` is missing."))
      }
      # check the required field `createdAt`
      if (!is.null(input_json$`createdAt`)) {
        if (!(is.character(input_json$`createdAt`) && length(input_json$`createdAt`) == 1)) {
          stop(paste("Error! Invalid data for `createdAt`. Must be a string:", input_json$`createdAt`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for Challenge: the required field `createdAt` is missing."))
      }
      # check the required field `updatedAt`
      if (!is.null(input_json$`updatedAt`)) {
        if (!(is.character(input_json$`updatedAt`) && length(input_json$`updatedAt`) == 1)) {
          stop(paste("Error! Invalid data for `updatedAt`. Must be a string:", input_json$`updatedAt`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for Challenge: the required field `updatedAt` is missing."))
      }
    },
    #' To string (JSON format)
    #'
    #' @description
    #' To string (JSON format)
    #'
    #' @return String representation of Challenge
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

      if (nchar(self$`slug`) > 60) {
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

      if (nchar(self$`name`) > 60) {
        return(FALSE)
      }
      if (nchar(self$`name`) < 3) {
        return(FALSE)
      }

      if (nchar(self$`headline`) > 80) {
        return(FALSE)
      }
      if (nchar(self$`headline`) < 0) {
        return(FALSE)
      }

      # check if the required `description` is null
      if (is.null(self$`description`)) {
        return(FALSE)
      }

      if (nchar(self$`description`) > 280) {
        return(FALSE)
      }
      if (nchar(self$`description`) < 0) {
        return(FALSE)
      }

      # check if the required `status` is null
      if (is.null(self$`status`)) {
        return(FALSE)
      }

      # check if the required `difficulty` is null
      if (is.null(self$`difficulty`)) {
        return(FALSE)
      }

      # check if the required `platform` is null
      if (is.null(self$`platform`)) {
        return(FALSE)
      }

      # check if the required `incentives` is null
      if (is.null(self$`incentives`)) {
        return(FALSE)
      }

      # check if the required `submissionTypes` is null
      if (is.null(self$`submissionTypes`)) {
        return(FALSE)
      }

      # check if the required `starredCount` is null
      if (is.null(self$`starredCount`)) {
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

      if (nchar(self$`slug`) > 60) {
        invalid_fields["slug"] <- "Invalid length for `slug`, must be smaller than or equal to 60."
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

      if (nchar(self$`name`) > 60) {
        invalid_fields["name"] <- "Invalid length for `name`, must be smaller than or equal to 60."
      }
      if (nchar(self$`name`) < 3) {
        invalid_fields["name"] <- "Invalid length for `name`, must be bigger than or equal to 3."
      }

      if (nchar(self$`headline`) > 80) {
        invalid_fields["headline"] <- "Invalid length for `headline`, must be smaller than or equal to 80."
      }
      if (nchar(self$`headline`) < 0) {
        invalid_fields["headline"] <- "Invalid length for `headline`, must be bigger than or equal to 0."
      }

      # check if the required `description` is null
      if (is.null(self$`description`)) {
        invalid_fields["description"] <- "Non-nullable required field `description` cannot be null."
      }

      if (nchar(self$`description`) > 280) {
        invalid_fields["description"] <- "Invalid length for `description`, must be smaller than or equal to 280."
      }
      if (nchar(self$`description`) < 0) {
        invalid_fields["description"] <- "Invalid length for `description`, must be bigger than or equal to 0."
      }

      # check if the required `status` is null
      if (is.null(self$`status`)) {
        invalid_fields["status"] <- "Non-nullable required field `status` cannot be null."
      }

      # check if the required `difficulty` is null
      if (is.null(self$`difficulty`)) {
        invalid_fields["difficulty"] <- "Non-nullable required field `difficulty` cannot be null."
      }

      # check if the required `platform` is null
      if (is.null(self$`platform`)) {
        invalid_fields["platform"] <- "Non-nullable required field `platform` cannot be null."
      }

      # check if the required `incentives` is null
      if (is.null(self$`incentives`)) {
        invalid_fields["incentives"] <- "Non-nullable required field `incentives` cannot be null."
      }

      # check if the required `submissionTypes` is null
      if (is.null(self$`submissionTypes`)) {
        invalid_fields["submissionTypes"] <- "Non-nullable required field `submissionTypes` cannot be null."
      }

      # check if the required `starredCount` is null
      if (is.null(self$`starredCount`)) {
        invalid_fields["starredCount"] <- "Non-nullable required field `starredCount` cannot be null."
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
# Challenge$unlock()
#
## Below is an example to define the print function
# Challenge$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# Challenge$lock()

