#' Create a new OrganizationSearchQuery
#'
#' @description
#' An organization search query.
#'
#' @docType class
#' @title OrganizationSearchQuery
#' @description OrganizationSearchQuery Class
#' @format An \code{R6Class} generator object
#' @field pageNumber The page number. integer [optional]
#' @field pageSize The number of items in a single page. integer [optional]
#' @field categories The array of organization categories used to filter the results. list(\link{OrganizationCategory}) [optional]
#' @field challengeContributionRoles An array of challenge contribution roles used to filter the results. list(\link{ChallengeContributionRole}) [optional]
#' @field sort  \link{OrganizationSort} [optional]
#' @field direction  \link{OrganizationDirection} [optional]
#' @field searchTerms A string of search terms used to filter the results. character [optional]
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
OrganizationSearchQuery <- R6::R6Class(
  "OrganizationSearchQuery",
  public = list(
    `pageNumber` = NULL,
    `pageSize` = NULL,
    `categories` = NULL,
    `challengeContributionRoles` = NULL,
    `sort` = NULL,
    `direction` = NULL,
    `searchTerms` = NULL,
    #' Initialize a new OrganizationSearchQuery class.
    #'
    #' @description
    #' Initialize a new OrganizationSearchQuery class.
    #'
    #' @param pageNumber The page number.. Default to 0.
    #' @param pageSize The number of items in a single page.. Default to 100.
    #' @param categories The array of organization categories used to filter the results.
    #' @param challengeContributionRoles An array of challenge contribution roles used to filter the results.
    #' @param sort sort
    #' @param direction direction
    #' @param searchTerms A string of search terms used to filter the results.
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`pageNumber` = 0, `pageSize` = 100, `categories` = NULL, `challengeContributionRoles` = NULL, `sort` = NULL, `direction` = NULL, `searchTerms` = NULL, ...) {
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
      if (!is.null(`categories`)) {
        stopifnot(is.vector(`categories`), length(`categories`) != 0)
        sapply(`categories`, function(x) stopifnot(R6::is.R6(x)))
        self$`categories` <- `categories`
      }
      if (!is.null(`challengeContributionRoles`)) {
        stopifnot(is.vector(`challengeContributionRoles`), length(`challengeContributionRoles`) != 0)
        sapply(`challengeContributionRoles`, function(x) stopifnot(R6::is.R6(x)))
        self$`challengeContributionRoles` <- `challengeContributionRoles`
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
    #' @return OrganizationSearchQuery in JSON format
    #' @export
    toJSON = function() {
      OrganizationSearchQueryObject <- list()
      if (!is.null(self$`pageNumber`)) {
        OrganizationSearchQueryObject[["pageNumber"]] <-
          self$`pageNumber`
      }
      if (!is.null(self$`pageSize`)) {
        OrganizationSearchQueryObject[["pageSize"]] <-
          self$`pageSize`
      }
      if (!is.null(self$`categories`)) {
        OrganizationSearchQueryObject[["categories"]] <-
          lapply(self$`categories`, function(x) x$toJSON())
      }
      if (!is.null(self$`challengeContributionRoles`)) {
        OrganizationSearchQueryObject[["challengeContributionRoles"]] <-
          lapply(self$`challengeContributionRoles`, function(x) x$toJSON())
      }
      if (!is.null(self$`sort`)) {
        OrganizationSearchQueryObject[["sort"]] <-
          self$`sort`$toJSON()
      }
      if (!is.null(self$`direction`)) {
        OrganizationSearchQueryObject[["direction"]] <-
          self$`direction`$toJSON()
      }
      if (!is.null(self$`searchTerms`)) {
        OrganizationSearchQueryObject[["searchTerms"]] <-
          self$`searchTerms`
      }
      OrganizationSearchQueryObject
    },
    #' Deserialize JSON string into an instance of OrganizationSearchQuery
    #'
    #' @description
    #' Deserialize JSON string into an instance of OrganizationSearchQuery
    #'
    #' @param input_json the JSON input
    #' @return the instance of OrganizationSearchQuery
    #' @export
    fromJSON = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      if (!is.null(this_object$`pageNumber`)) {
        self$`pageNumber` <- this_object$`pageNumber`
      }
      if (!is.null(this_object$`pageSize`)) {
        self$`pageSize` <- this_object$`pageSize`
      }
      if (!is.null(this_object$`categories`)) {
        self$`categories` <- ApiClient$new()$deserializeObj(this_object$`categories`, "array[OrganizationCategory]", loadNamespace("openapi"))
      }
      if (!is.null(this_object$`challengeContributionRoles`)) {
        self$`challengeContributionRoles` <- ApiClient$new()$deserializeObj(this_object$`challengeContributionRoles`, "array[ChallengeContributionRole]", loadNamespace("openapi"))
      }
      if (!is.null(this_object$`sort`)) {
        `sort_object` <- OrganizationSort$new()
        `sort_object`$fromJSON(jsonlite::toJSON(this_object$`sort`, auto_unbox = TRUE, digits = NA))
        self$`sort` <- `sort_object`
      }
      if (!is.null(this_object$`direction`)) {
        `direction_object` <- OrganizationDirection$new()
        `direction_object`$fromJSON(jsonlite::toJSON(this_object$`direction`, auto_unbox = TRUE, digits = NA))
        self$`direction` <- `direction_object`
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
    #' @return OrganizationSearchQuery in JSON format
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
        if (!is.null(self$`categories`)) {
          sprintf(
          '"categories":
          [%s]
',
          paste(sapply(self$`categories`, function(x) jsonlite::toJSON(x$toJSON(), auto_unbox = TRUE, digits = NA)), collapse = ",")
          )
        },
        if (!is.null(self$`challengeContributionRoles`)) {
          sprintf(
          '"challengeContributionRoles":
          [%s]
',
          paste(sapply(self$`challengeContributionRoles`, function(x) jsonlite::toJSON(x$toJSON(), auto_unbox = TRUE, digits = NA)), collapse = ",")
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
    #' Deserialize JSON string into an instance of OrganizationSearchQuery
    #'
    #' @description
    #' Deserialize JSON string into an instance of OrganizationSearchQuery
    #'
    #' @param input_json the JSON input
    #' @return the instance of OrganizationSearchQuery
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`pageNumber` <- this_object$`pageNumber`
      self$`pageSize` <- this_object$`pageSize`
      self$`categories` <- ApiClient$new()$deserializeObj(this_object$`categories`, "array[OrganizationCategory]", loadNamespace("openapi"))
      self$`challengeContributionRoles` <- ApiClient$new()$deserializeObj(this_object$`challengeContributionRoles`, "array[ChallengeContributionRole]", loadNamespace("openapi"))
      self$`sort` <- OrganizationSort$new()$fromJSON(jsonlite::toJSON(this_object$`sort`, auto_unbox = TRUE, digits = NA))
      self$`direction` <- OrganizationDirection$new()$fromJSON(jsonlite::toJSON(this_object$`direction`, auto_unbox = TRUE, digits = NA))
      self$`searchTerms` <- this_object$`searchTerms`
      self
    },
    #' Validate JSON input with respect to OrganizationSearchQuery
    #'
    #' @description
    #' Validate JSON input with respect to OrganizationSearchQuery and throw an exception if invalid
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
    #' @return String representation of OrganizationSearchQuery
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
# OrganizationSearchQuery$unlock()
#
## Below is an example to define the print function
# OrganizationSearchQuery$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# OrganizationSearchQuery$lock()

