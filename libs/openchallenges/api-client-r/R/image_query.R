#' Create a new ImageQuery
#'
#' @description
#' An image query.
#'
#' @docType class
#' @title ImageQuery
#' @description ImageQuery Class
#' @format An \code{R6Class} generator object
#' @field objectKey The unique identifier of the image. character
#' @field height  \link{ImageHeight} [optional]
#' @field aspectRatio  \link{ImageAspectRatio} [optional]
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
ImageQuery <- R6::R6Class(
  "ImageQuery",
  public = list(
    `objectKey` = NULL,
    `height` = NULL,
    `aspectRatio` = NULL,
    #' Initialize a new ImageQuery class.
    #'
    #' @description
    #' Initialize a new ImageQuery class.
    #'
    #' @param objectKey The unique identifier of the image.
    #' @param height height
    #' @param aspectRatio aspectRatio
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`objectKey`, `height` = NULL, `aspectRatio` = NULL, ...) {
      if (!missing(`objectKey`)) {
        if (!(is.character(`objectKey`) && length(`objectKey`) == 1)) {
          stop(paste("Error! Invalid data for `objectKey`. Must be a string:", `objectKey`))
        }
        self$`objectKey` <- `objectKey`
      }
      if (!is.null(`height`)) {
        if (!(`height` %in% c())) {
          stop(paste("Error! \"", `height`, "\" cannot be assigned to `height`. Must be .", sep = ""))
        }
        stopifnot(R6::is.R6(`height`))
        self$`height` <- `height`
      }
      if (!is.null(`aspectRatio`)) {
        if (!(`aspectRatio` %in% c())) {
          stop(paste("Error! \"", `aspectRatio`, "\" cannot be assigned to `aspectRatio`. Must be .", sep = ""))
        }
        stopifnot(R6::is.R6(`aspectRatio`))
        self$`aspectRatio` <- `aspectRatio`
      }
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ImageQuery in JSON format
    #' @export
    toJSON = function() {
      ImageQueryObject <- list()
      if (!is.null(self$`objectKey`)) {
        ImageQueryObject[["objectKey"]] <-
          self$`objectKey`
      }
      if (!is.null(self$`height`)) {
        ImageQueryObject[["height"]] <-
          self$`height`$toJSON()
      }
      if (!is.null(self$`aspectRatio`)) {
        ImageQueryObject[["aspectRatio"]] <-
          self$`aspectRatio`$toJSON()
      }
      ImageQueryObject
    },
    #' Deserialize JSON string into an instance of ImageQuery
    #'
    #' @description
    #' Deserialize JSON string into an instance of ImageQuery
    #'
    #' @param input_json the JSON input
    #' @return the instance of ImageQuery
    #' @export
    fromJSON = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      if (!is.null(this_object$`objectKey`)) {
        self$`objectKey` <- this_object$`objectKey`
      }
      if (!is.null(this_object$`height`)) {
        `height_object` <- ImageHeight$new()
        `height_object`$fromJSON(jsonlite::toJSON(this_object$`height`, auto_unbox = TRUE, digits = NA))
        self$`height` <- `height_object`
      }
      if (!is.null(this_object$`aspectRatio`)) {
        `aspectratio_object` <- ImageAspectRatio$new()
        `aspectratio_object`$fromJSON(jsonlite::toJSON(this_object$`aspectRatio`, auto_unbox = TRUE, digits = NA))
        self$`aspectRatio` <- `aspectratio_object`
      }
      self
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ImageQuery in JSON format
    #' @export
    toJSONString = function() {
      jsoncontent <- c(
        if (!is.null(self$`objectKey`)) {
          sprintf(
          '"objectKey":
            "%s"
                    ',
          self$`objectKey`
          )
        },
        if (!is.null(self$`height`)) {
          sprintf(
          '"height":
          %s
          ',
          jsonlite::toJSON(self$`height`$toJSON(), auto_unbox = TRUE, digits = NA)
          )
        },
        if (!is.null(self$`aspectRatio`)) {
          sprintf(
          '"aspectRatio":
          %s
          ',
          jsonlite::toJSON(self$`aspectRatio`$toJSON(), auto_unbox = TRUE, digits = NA)
          )
        }
      )
      jsoncontent <- paste(jsoncontent, collapse = ",")
      json_string <- as.character(jsonlite::minify(paste("{", jsoncontent, "}", sep = "")))
    },
    #' Deserialize JSON string into an instance of ImageQuery
    #'
    #' @description
    #' Deserialize JSON string into an instance of ImageQuery
    #'
    #' @param input_json the JSON input
    #' @return the instance of ImageQuery
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`objectKey` <- this_object$`objectKey`
      self$`height` <- ImageHeight$new()$fromJSON(jsonlite::toJSON(this_object$`height`, auto_unbox = TRUE, digits = NA))
      self$`aspectRatio` <- ImageAspectRatio$new()$fromJSON(jsonlite::toJSON(this_object$`aspectRatio`, auto_unbox = TRUE, digits = NA))
      self
    },
    #' Validate JSON input with respect to ImageQuery
    #'
    #' @description
    #' Validate JSON input with respect to ImageQuery and throw an exception if invalid
    #'
    #' @param input the JSON input
    #' @export
    validateJSON = function(input) {
      input_json <- jsonlite::fromJSON(input)
      # check the required field `objectKey`
      if (!is.null(input_json$`objectKey`)) {
        if (!(is.character(input_json$`objectKey`) && length(input_json$`objectKey`) == 1)) {
          stop(paste("Error! Invalid data for `objectKey`. Must be a string:", input_json$`objectKey`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for ImageQuery: the required field `objectKey` is missing."))
      }
    },
    #' To string (JSON format)
    #'
    #' @description
    #' To string (JSON format)
    #'
    #' @return String representation of ImageQuery
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
      # check if the required `objectKey` is null
      if (is.null(self$`objectKey`)) {
        return(FALSE)
      }

      if (!str_detect(self$`objectKey`, "^[a-zA-Z0-9/_-]+.[a-zA-Z0-9/_-]+")) {
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
      # check if the required `objectKey` is null
      if (is.null(self$`objectKey`)) {
        invalid_fields["objectKey"] <- "Non-nullable required field `objectKey` cannot be null."
      }

      if (!str_detect(self$`objectKey`, "^[a-zA-Z0-9/_-]+.[a-zA-Z0-9/_-]+")) {
        invalid_fields["objectKey"] <- "Invalid value for `objectKey`, must conform to the pattern ^[a-zA-Z0-9/_-]+.[a-zA-Z0-9/_-]+."
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
# ImageQuery$unlock()
#
## Below is an example to define the print function
# ImageQuery$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# ImageQuery$lock()

