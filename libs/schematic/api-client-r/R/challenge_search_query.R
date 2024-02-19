#' Create a new ChallengeSearchQuery
#'
#' @description
#' A challenge search query.
#'
#' @docType class
#' @title ChallengeSearchQuery
#' @description ChallengeSearchQuery Class
#' @format An \code{R6Class} generator object
#' @field pageNumber The page number. integer [optional]
#' @field pageSize The number of items in a single page. integer [optional]
#' @field sort  \link{ChallengeSort} [optional]
#' @field direction  \link{ChallengeDirection} [optional]
#' @field difficulties An array of challenge difficulty levels used to filter the results. list(\link{ChallengeDifficulty}) [optional]
#' @field incentives An array of challenge incentive types used to filter the results. list(\link{ChallengeIncentive}) [optional]
#' @field minStartDate Keep the challenges that start at this date or later. character [optional]
#' @field maxStartDate Keep the challenges that start at this date or sooner. character [optional]
#' @field platforms An array of challenge platform ids used to filter the results. list(character) [optional]
#' @field organizations An array of organization ids used to filter the results. list(integer) [optional]
#' @field inputDataTypes An array of challenge input data type ids used to filter the results. list(character) [optional]
#' @field status An array of challenge status used to filter the results. list(\link{ChallengeStatus}) [optional]
#' @field submissionTypes An array of challenge submission types used to filter the results. list(\link{ChallengeSubmissionType}) [optional]
#' @field searchTerms A string of search terms used to filter the results. character [optional]
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
ChallengeSearchQuery <- R6::R6Class(
  "ChallengeSearchQuery",
  public = list(
    `pageNumber` = NULL,
    `pageSize` = NULL,
    `sort` = NULL,
    `direction` = NULL,
    `difficulties` = NULL,
    `incentives` = NULL,
    `minStartDate` = NULL,
    `maxStartDate` = NULL,
    `platforms` = NULL,
    `organizations` = NULL,
    `inputDataTypes` = NULL,
    `status` = NULL,
    `submissionTypes` = NULL,
    `searchTerms` = NULL,
    #' Initialize a new ChallengeSearchQuery class.
    #'
    #' @description
    #' Initialize a new ChallengeSearchQuery class.
    #'
    #' @param pageNumber The page number.. Default to 0.
    #' @param pageSize The number of items in a single page.. Default to 100.
    #' @param sort sort
    #' @param direction direction
    #' @param difficulties An array of challenge difficulty levels used to filter the results.
    #' @param incentives An array of challenge incentive types used to filter the results.
    #' @param minStartDate Keep the challenges that start at this date or later.
    #' @param maxStartDate Keep the challenges that start at this date or sooner.
    #' @param platforms An array of challenge platform ids used to filter the results.
    #' @param organizations An array of organization ids used to filter the results.
    #' @param inputDataTypes An array of challenge input data type ids used to filter the results.
    #' @param status An array of challenge status used to filter the results.
    #' @param submissionTypes An array of challenge submission types used to filter the results.
    #' @param searchTerms A string of search terms used to filter the results.
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`pageNumber` = 0, `pageSize` = 100, `sort` = NULL, `direction` = NULL, `difficulties` = NULL, `incentives` = NULL, `minStartDate` = NULL, `maxStartDate` = NULL, `platforms` = NULL, `organizations` = NULL, `inputDataTypes` = NULL, `status` = NULL, `submissionTypes` = NULL, `searchTerms` = NULL, ...) {
      if (!is.null(`pageNumber`)) {
        if (!(is.numeric(`pageNumber`) && length(`pageNumber`) == 1)) {
          stop(paste("Error! Invalid data for `pageNumber`. Must be an integer:", `pageNumber`))
        }
        self$`pageNumber` <- `pageNumber`
      }
      if (!is.null(`pageSize`)) {
        if (!(is.numeric(`pageSize`) && length(`pageSize`) == 1)) {
          stop(paste("Error! Invalid data for `pageSize`. Must be an integer:", `pageSize`))
        }
        self$`pageSize` <- `pageSize`
      }
      if (!is.null(`sort`)) {
        if (!(`sort` %in% c())) {
          stop(paste("Error! \"", `sort`, "\" cannot be assigned to `sort`. Must be .", sep = ""))
        }
        stopifnot(R6::is.R6(`sort`))
        self$`sort` <- `sort`
      }
      if (!is.null(`direction`)) {
        if (!(`direction` %in% c())) {
          stop(paste("Error! \"", `direction`, "\" cannot be assigned to `direction`. Must be .", sep = ""))
        }
        stopifnot(R6::is.R6(`direction`))
        self$`direction` <- `direction`
      }
      if (!is.null(`difficulties`)) {
        stopifnot(is.vector(`difficulties`), length(`difficulties`) != 0)
        sapply(`difficulties`, function(x) stopifnot(R6::is.R6(x)))
        self$`difficulties` <- `difficulties`
      }
      if (!is.null(`incentives`)) {
        stopifnot(is.vector(`incentives`), length(`incentives`) != 0)
        sapply(`incentives`, function(x) stopifnot(R6::is.R6(x)))
        self$`incentives` <- `incentives`
      }
      if (!is.null(`minStartDate`)) {
        if (!is.character(`minStartDate`)) {
          stop(paste("Error! Invalid data for `minStartDate`. Must be a string:", `minStartDate`))
        }
        self$`minStartDate` <- `minStartDate`
      }
      if (!is.null(`maxStartDate`)) {
        if (!is.character(`maxStartDate`)) {
          stop(paste("Error! Invalid data for `maxStartDate`. Must be a string:", `maxStartDate`))
        }
        self$`maxStartDate` <- `maxStartDate`
      }
      if (!is.null(`platforms`)) {
        stopifnot(is.vector(`platforms`), length(`platforms`) != 0)
        sapply(`platforms`, function(x) stopifnot(is.character(x)))
        self$`platforms` <- `platforms`
      }
      if (!is.null(`organizations`)) {
        stopifnot(is.vector(`organizations`), length(`organizations`) != 0)
        sapply(`organizations`, function(x) stopifnot(is.character(x)))
        self$`organizations` <- `organizations`
      }
      if (!is.null(`inputDataTypes`)) {
        stopifnot(is.vector(`inputDataTypes`), length(`inputDataTypes`) != 0)
        sapply(`inputDataTypes`, function(x) stopifnot(is.character(x)))
        self$`inputDataTypes` <- `inputDataTypes`
      }
      if (!is.null(`status`)) {
        stopifnot(is.vector(`status`), length(`status`) != 0)
        sapply(`status`, function(x) stopifnot(R6::is.R6(x)))
        self$`status` <- `status`
      }
      if (!is.null(`submissionTypes`)) {
        stopifnot(is.vector(`submissionTypes`), length(`submissionTypes`) != 0)
        sapply(`submissionTypes`, function(x) stopifnot(R6::is.R6(x)))
        self$`submissionTypes` <- `submissionTypes`
      }
      if (!is.null(`searchTerms`)) {
        if (!(is.character(`searchTerms`) && length(`searchTerms`) == 1)) {
          stop(paste("Error! Invalid data for `searchTerms`. Must be a string:", `searchTerms`))
        }
        self$`searchTerms` <- `searchTerms`
      }
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ChallengeSearchQuery in JSON format
    #' @export
    toJSON = function() {
      ChallengeSearchQueryObject <- list()
      if (!is.null(self$`pageNumber`)) {
        ChallengeSearchQueryObject[["pageNumber"]] <-
          self$`pageNumber`
      }
      if (!is.null(self$`pageSize`)) {
        ChallengeSearchQueryObject[["pageSize"]] <-
          self$`pageSize`
      }
      if (!is.null(self$`sort`)) {
        ChallengeSearchQueryObject[["sort"]] <-
          self$`sort`$toJSON()
      }
      if (!is.null(self$`direction`)) {
        ChallengeSearchQueryObject[["direction"]] <-
          self$`direction`$toJSON()
      }
      if (!is.null(self$`difficulties`)) {
        ChallengeSearchQueryObject[["difficulties"]] <-
          lapply(self$`difficulties`, function(x) x$toJSON())
      }
      if (!is.null(self$`incentives`)) {
        ChallengeSearchQueryObject[["incentives"]] <-
          lapply(self$`incentives`, function(x) x$toJSON())
      }
      if (!is.null(self$`minStartDate`)) {
        ChallengeSearchQueryObject[["minStartDate"]] <-
          self$`minStartDate`
      }
      if (!is.null(self$`maxStartDate`)) {
        ChallengeSearchQueryObject[["maxStartDate"]] <-
          self$`maxStartDate`
      }
      if (!is.null(self$`platforms`)) {
        ChallengeSearchQueryObject[["platforms"]] <-
          self$`platforms`
      }
      if (!is.null(self$`organizations`)) {
        ChallengeSearchQueryObject[["organizations"]] <-
          self$`organizations`
      }
      if (!is.null(self$`inputDataTypes`)) {
        ChallengeSearchQueryObject[["inputDataTypes"]] <-
          self$`inputDataTypes`
      }
      if (!is.null(self$`status`)) {
        ChallengeSearchQueryObject[["status"]] <-
          lapply(self$`status`, function(x) x$toJSON())
      }
      if (!is.null(self$`submissionTypes`)) {
        ChallengeSearchQueryObject[["submissionTypes"]] <-
          lapply(self$`submissionTypes`, function(x) x$toJSON())
      }
      if (!is.null(self$`searchTerms`)) {
        ChallengeSearchQueryObject[["searchTerms"]] <-
          self$`searchTerms`
      }
      ChallengeSearchQueryObject
    },
    #' Deserialize JSON string into an instance of ChallengeSearchQuery
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengeSearchQuery
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengeSearchQuery
    #' @export
    fromJSON = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      if (!is.null(this_object$`pageNumber`)) {
        self$`pageNumber` <- this_object$`pageNumber`
      }
      if (!is.null(this_object$`pageSize`)) {
        self$`pageSize` <- this_object$`pageSize`
      }
      if (!is.null(this_object$`sort`)) {
        `sort_object` <- ChallengeSort$new()
        `sort_object`$fromJSON(jsonlite::toJSON(this_object$`sort`, auto_unbox = TRUE, digits = NA))
        self$`sort` <- `sort_object`
      }
      if (!is.null(this_object$`direction`)) {
        `direction_object` <- ChallengeDirection$new()
        `direction_object`$fromJSON(jsonlite::toJSON(this_object$`direction`, auto_unbox = TRUE, digits = NA))
        self$`direction` <- `direction_object`
      }
      if (!is.null(this_object$`difficulties`)) {
        self$`difficulties` <- ApiClient$new()$deserializeObj(this_object$`difficulties`, "array[ChallengeDifficulty]", loadNamespace("openapi"))
      }
      if (!is.null(this_object$`incentives`)) {
        self$`incentives` <- ApiClient$new()$deserializeObj(this_object$`incentives`, "array[ChallengeIncentive]", loadNamespace("openapi"))
      }
      if (!is.null(this_object$`minStartDate`)) {
        self$`minStartDate` <- this_object$`minStartDate`
      }
      if (!is.null(this_object$`maxStartDate`)) {
        self$`maxStartDate` <- this_object$`maxStartDate`
      }
      if (!is.null(this_object$`platforms`)) {
        self$`platforms` <- ApiClient$new()$deserializeObj(this_object$`platforms`, "array[character]", loadNamespace("openapi"))
      }
      if (!is.null(this_object$`organizations`)) {
        self$`organizations` <- ApiClient$new()$deserializeObj(this_object$`organizations`, "array[integer]", loadNamespace("openapi"))
      }
      if (!is.null(this_object$`inputDataTypes`)) {
        self$`inputDataTypes` <- ApiClient$new()$deserializeObj(this_object$`inputDataTypes`, "array[character]", loadNamespace("openapi"))
      }
      if (!is.null(this_object$`status`)) {
        self$`status` <- ApiClient$new()$deserializeObj(this_object$`status`, "array[ChallengeStatus]", loadNamespace("openapi"))
      }
      if (!is.null(this_object$`submissionTypes`)) {
        self$`submissionTypes` <- ApiClient$new()$deserializeObj(this_object$`submissionTypes`, "array[ChallengeSubmissionType]", loadNamespace("openapi"))
      }
      if (!is.null(this_object$`searchTerms`)) {
        self$`searchTerms` <- this_object$`searchTerms`
      }
      self
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ChallengeSearchQuery in JSON format
    #' @export
    toJSONString = function() {
      jsoncontent <- c(
        if (!is.null(self$`pageNumber`)) {
          sprintf(
          '"pageNumber":
            %d
                    ',
          self$`pageNumber`
          )
        },
        if (!is.null(self$`pageSize`)) {
          sprintf(
          '"pageSize":
            %d
                    ',
          self$`pageSize`
          )
        },
        if (!is.null(self$`sort`)) {
          sprintf(
          '"sort":
          %s
          ',
          jsonlite::toJSON(self$`sort`$toJSON(), auto_unbox = TRUE, digits = NA)
          )
        },
        if (!is.null(self$`direction`)) {
          sprintf(
          '"direction":
          %s
          ',
          jsonlite::toJSON(self$`direction`$toJSON(), auto_unbox = TRUE, digits = NA)
          )
        },
        if (!is.null(self$`difficulties`)) {
          sprintf(
          '"difficulties":
          [%s]
',
          paste(sapply(self$`difficulties`, function(x) jsonlite::toJSON(x$toJSON(), auto_unbox = TRUE, digits = NA)), collapse = ",")
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
        if (!is.null(self$`minStartDate`)) {
          sprintf(
          '"minStartDate":
            "%s"
                    ',
          self$`minStartDate`
          )
        },
        if (!is.null(self$`maxStartDate`)) {
          sprintf(
          '"maxStartDate":
            "%s"
                    ',
          self$`maxStartDate`
          )
        },
        if (!is.null(self$`platforms`)) {
          sprintf(
          '"platforms":
             [%s]
          ',
          paste(unlist(lapply(self$`platforms`, function(x) paste0('"', x, '"'))), collapse = ",")
          )
        },
        if (!is.null(self$`organizations`)) {
          sprintf(
          '"organizations":
             [%s]
          ',
          paste(unlist(lapply(self$`organizations`, function(x) paste0('"', x, '"'))), collapse = ",")
          )
        },
        if (!is.null(self$`inputDataTypes`)) {
          sprintf(
          '"inputDataTypes":
             [%s]
          ',
          paste(unlist(lapply(self$`inputDataTypes`, function(x) paste0('"', x, '"'))), collapse = ",")
          )
        },
        if (!is.null(self$`status`)) {
          sprintf(
          '"status":
          [%s]
',
          paste(sapply(self$`status`, function(x) jsonlite::toJSON(x$toJSON(), auto_unbox = TRUE, digits = NA)), collapse = ",")
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
        if (!is.null(self$`searchTerms`)) {
          sprintf(
          '"searchTerms":
            "%s"
                    ',
          self$`searchTerms`
          )
        }
      )
      jsoncontent <- paste(jsoncontent, collapse = ",")
      json_string <- as.character(jsonlite::minify(paste("{", jsoncontent, "}", sep = "")))
    },
    #' Deserialize JSON string into an instance of ChallengeSearchQuery
    #'
    #' @description
    #' Deserialize JSON string into an instance of ChallengeSearchQuery
    #'
    #' @param input_json the JSON input
    #' @return the instance of ChallengeSearchQuery
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`pageNumber` <- this_object$`pageNumber`
      self$`pageSize` <- this_object$`pageSize`
      self$`sort` <- ChallengeSort$new()$fromJSON(jsonlite::toJSON(this_object$`sort`, auto_unbox = TRUE, digits = NA))
      self$`direction` <- ChallengeDirection$new()$fromJSON(jsonlite::toJSON(this_object$`direction`, auto_unbox = TRUE, digits = NA))
      self$`difficulties` <- ApiClient$new()$deserializeObj(this_object$`difficulties`, "array[ChallengeDifficulty]", loadNamespace("openapi"))
      self$`incentives` <- ApiClient$new()$deserializeObj(this_object$`incentives`, "array[ChallengeIncentive]", loadNamespace("openapi"))
      self$`minStartDate` <- this_object$`minStartDate`
      self$`maxStartDate` <- this_object$`maxStartDate`
      self$`platforms` <- ApiClient$new()$deserializeObj(this_object$`platforms`, "array[character]", loadNamespace("openapi"))
      self$`organizations` <- ApiClient$new()$deserializeObj(this_object$`organizations`, "array[integer]", loadNamespace("openapi"))
      self$`inputDataTypes` <- ApiClient$new()$deserializeObj(this_object$`inputDataTypes`, "array[character]", loadNamespace("openapi"))
      self$`status` <- ApiClient$new()$deserializeObj(this_object$`status`, "array[ChallengeStatus]", loadNamespace("openapi"))
      self$`submissionTypes` <- ApiClient$new()$deserializeObj(this_object$`submissionTypes`, "array[ChallengeSubmissionType]", loadNamespace("openapi"))
      self$`searchTerms` <- this_object$`searchTerms`
      self
    },
    #' Validate JSON input with respect to ChallengeSearchQuery
    #'
    #' @description
    #' Validate JSON input with respect to ChallengeSearchQuery and throw an exception if invalid
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
    #' @return String representation of ChallengeSearchQuery
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
      if (self$`pageNumber` < 0) {
        return(FALSE)
      }

      if (self$`pageSize` < 1) {
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
      if (self$`pageNumber` < 0) {
        invalid_fields["pageNumber"] <- "Invalid value for `pageNumber`, must be bigger than or equal to 0."
      }

      if (self$`pageSize` < 1) {
        invalid_fields["pageSize"] <- "Invalid value for `pageSize`, must be bigger than or equal to 1."
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
# ChallengeSearchQuery$unlock()
#
## Below is an example to define the print function
# ChallengeSearchQuery$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# ChallengeSearchQuery$lock()

