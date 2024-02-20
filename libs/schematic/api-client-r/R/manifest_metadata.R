#' Create a new ManifestMetadata
#'
#' @description
#' The metadata for a manifest file
#'
#' @docType class
#' @title ManifestMetadata
#' @description ManifestMetadata Class
#' @format An \code{R6Class} generator object
#' @field name The name of the manifest file. character
#' @field id The id of the manifest file. character
#' @field datasetName The name of the dataset the manifest belongs to. character [optional]
#' @field datasetId The id of the dataset the manifest belongs to. character [optional]
#' @field componentName The name of the component the manifest is of. character [optional]
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
ManifestMetadata <- R6::R6Class(
  "ManifestMetadata",
  public = list(
    `name` = NULL,
    `id` = NULL,
    `datasetName` = NULL,
    `datasetId` = NULL,
    `componentName` = NULL,
    #' Initialize a new ManifestMetadata class.
    #'
    #' @description
    #' Initialize a new ManifestMetadata class.
    #'
    #' @param name The name of the manifest file.
    #' @param id The id of the manifest file.
    #' @param datasetName The name of the dataset the manifest belongs to.
    #' @param datasetId The id of the dataset the manifest belongs to.
    #' @param componentName The name of the component the manifest is of.
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`name`, `id`, `datasetName` = NULL, `datasetId` = NULL, `componentName` = NULL, ...) {
      if (!missing(`name`)) {
        if (!(is.character(`name`) && length(`name`) == 1)) {
          stop(paste("Error! Invalid data for `name`. Must be a string:", `name`))
        }
        self$`name` <- `name`
      }
      if (!missing(`id`)) {
        if (!(is.character(`id`) && length(`id`) == 1)) {
          stop(paste("Error! Invalid data for `id`. Must be a string:", `id`))
        }
        self$`id` <- `id`
      }
      if (!is.null(`datasetName`)) {
        if (!(is.character(`datasetName`) && length(`datasetName`) == 1)) {
          stop(paste("Error! Invalid data for `datasetName`. Must be a string:", `datasetName`))
        }
        self$`datasetName` <- `datasetName`
      }
      if (!is.null(`datasetId`)) {
        if (!(is.character(`datasetId`) && length(`datasetId`) == 1)) {
          stop(paste("Error! Invalid data for `datasetId`. Must be a string:", `datasetId`))
        }
        self$`datasetId` <- `datasetId`
      }
      if (!is.null(`componentName`)) {
        if (!(is.character(`componentName`) && length(`componentName`) == 1)) {
          stop(paste("Error! Invalid data for `componentName`. Must be a string:", `componentName`))
        }
        self$`componentName` <- `componentName`
      }
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ManifestMetadata in JSON format
    #' @export
    toJSON = function() {
      ManifestMetadataObject <- list()
      if (!is.null(self$`name`)) {
        ManifestMetadataObject[["name"]] <-
          self$`name`
      }
      if (!is.null(self$`id`)) {
        ManifestMetadataObject[["id"]] <-
          self$`id`
      }
      if (!is.null(self$`datasetName`)) {
        ManifestMetadataObject[["datasetName"]] <-
          self$`datasetName`
      }
      if (!is.null(self$`datasetId`)) {
        ManifestMetadataObject[["datasetId"]] <-
          self$`datasetId`
      }
      if (!is.null(self$`componentName`)) {
        ManifestMetadataObject[["componentName"]] <-
          self$`componentName`
      }
      ManifestMetadataObject
    },
    #' Deserialize JSON string into an instance of ManifestMetadata
    #'
    #' @description
    #' Deserialize JSON string into an instance of ManifestMetadata
    #'
    #' @param input_json the JSON input
    #' @return the instance of ManifestMetadata
    #' @export
    fromJSON = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      if (!is.null(this_object$`name`)) {
        self$`name` <- this_object$`name`
      }
      if (!is.null(this_object$`id`)) {
        self$`id` <- this_object$`id`
      }
      if (!is.null(this_object$`datasetName`)) {
        self$`datasetName` <- this_object$`datasetName`
      }
      if (!is.null(this_object$`datasetId`)) {
        self$`datasetId` <- this_object$`datasetId`
      }
      if (!is.null(this_object$`componentName`)) {
        self$`componentName` <- this_object$`componentName`
      }
      self
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ManifestMetadata in JSON format
    #' @export
    toJSONString = function() {
      jsoncontent <- c(
        if (!is.null(self$`name`)) {
          sprintf(
          '"name":
            "%s"
                    ',
          self$`name`
          )
        },
        if (!is.null(self$`id`)) {
          sprintf(
          '"id":
            "%s"
                    ',
          self$`id`
          )
        },
        if (!is.null(self$`datasetName`)) {
          sprintf(
          '"datasetName":
            "%s"
                    ',
          self$`datasetName`
          )
        },
        if (!is.null(self$`datasetId`)) {
          sprintf(
          '"datasetId":
            "%s"
                    ',
          self$`datasetId`
          )
        },
        if (!is.null(self$`componentName`)) {
          sprintf(
          '"componentName":
            "%s"
                    ',
          self$`componentName`
          )
        }
      )
      jsoncontent <- paste(jsoncontent, collapse = ",")
      json_string <- as.character(jsonlite::minify(paste("{", jsoncontent, "}", sep = "")))
    },
    #' Deserialize JSON string into an instance of ManifestMetadata
    #'
    #' @description
    #' Deserialize JSON string into an instance of ManifestMetadata
    #'
    #' @param input_json the JSON input
    #' @return the instance of ManifestMetadata
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`name` <- this_object$`name`
      self$`id` <- this_object$`id`
      self$`datasetName` <- this_object$`datasetName`
      self$`datasetId` <- this_object$`datasetId`
      self$`componentName` <- this_object$`componentName`
      self
    },
    #' Validate JSON input with respect to ManifestMetadata
    #'
    #' @description
    #' Validate JSON input with respect to ManifestMetadata and throw an exception if invalid
    #'
    #' @param input the JSON input
    #' @export
    validateJSON = function(input) {
      input_json <- jsonlite::fromJSON(input)
      # check the required field `name`
      if (!is.null(input_json$`name`)) {
        if (!(is.character(input_json$`name`) && length(input_json$`name`) == 1)) {
          stop(paste("Error! Invalid data for `name`. Must be a string:", input_json$`name`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for ManifestMetadata: the required field `name` is missing."))
      }
      # check the required field `id`
      if (!is.null(input_json$`id`)) {
        if (!(is.character(input_json$`id`) && length(input_json$`id`) == 1)) {
          stop(paste("Error! Invalid data for `id`. Must be a string:", input_json$`id`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for ManifestMetadata: the required field `id` is missing."))
      }
    },
    #' To string (JSON format)
    #'
    #' @description
    #' To string (JSON format)
    #'
    #' @return String representation of ManifestMetadata
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
      # check if the required `name` is null
      if (is.null(self$`name`)) {
        return(FALSE)
      }

      # check if the required `id` is null
      if (is.null(self$`id`)) {
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
      # check if the required `name` is null
      if (is.null(self$`name`)) {
        invalid_fields["name"] <- "Non-nullable required field `name` cannot be null."
      }

      # check if the required `id` is null
      if (is.null(self$`id`)) {
        invalid_fields["id"] <- "Non-nullable required field `id` cannot be null."
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
# ManifestMetadata$unlock()
#
## Below is an example to define the print function
# ManifestMetadata$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# ManifestMetadata$lock()

