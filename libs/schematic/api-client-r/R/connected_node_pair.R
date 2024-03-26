#' Create a new ConnectedNodePair
#'
#' @description
#' A pair of conncted nodes
#'
#' @docType class
#' @title ConnectedNodePair
#' @description ConnectedNodePair Class
#' @format An \code{R6Class} generator object
#' @field node1 The disaplay name of the first node. character
#' @field node2 The display name of the second node. character
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
ConnectedNodePair <- R6::R6Class(
  "ConnectedNodePair",
  public = list(
    `node1` = NULL,
    `node2` = NULL,
    #' Initialize a new ConnectedNodePair class.
    #'
    #' @description
    #' Initialize a new ConnectedNodePair class.
    #'
    #' @param node1 The disaplay name of the first node.
    #' @param node2 The display name of the second node.
    #' @param ... Other optional arguments.
    #' @export
    initialize = function(`node1`, `node2`, ...) {
      if (!missing(`node1`)) {
        if (!(is.character(`node1`) && length(`node1`) == 1)) {
          stop(paste("Error! Invalid data for `node1`. Must be a string:", `node1`))
        }
        self$`node1` <- `node1`
      }
      if (!missing(`node2`)) {
        if (!(is.character(`node2`) && length(`node2`) == 1)) {
          stop(paste("Error! Invalid data for `node2`. Must be a string:", `node2`))
        }
        self$`node2` <- `node2`
      }
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ConnectedNodePair in JSON format
    #' @export
    toJSON = function() {
      ConnectedNodePairObject <- list()
      if (!is.null(self$`node1`)) {
        ConnectedNodePairObject[["node1"]] <-
          self$`node1`
      }
      if (!is.null(self$`node2`)) {
        ConnectedNodePairObject[["node2"]] <-
          self$`node2`
      }
      ConnectedNodePairObject
    },
    #' Deserialize JSON string into an instance of ConnectedNodePair
    #'
    #' @description
    #' Deserialize JSON string into an instance of ConnectedNodePair
    #'
    #' @param input_json the JSON input
    #' @return the instance of ConnectedNodePair
    #' @export
    fromJSON = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      if (!is.null(this_object$`node1`)) {
        self$`node1` <- this_object$`node1`
      }
      if (!is.null(this_object$`node2`)) {
        self$`node2` <- this_object$`node2`
      }
      self
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ConnectedNodePair in JSON format
    #' @export
    toJSONString = function() {
      jsoncontent <- c(
        if (!is.null(self$`node1`)) {
          sprintf(
          '"node1":
            "%s"
                    ',
          self$`node1`
          )
        },
        if (!is.null(self$`node2`)) {
          sprintf(
          '"node2":
            "%s"
                    ',
          self$`node2`
          )
        }
      )
      jsoncontent <- paste(jsoncontent, collapse = ",")
      json_string <- as.character(jsonlite::minify(paste("{", jsoncontent, "}", sep = "")))
    },
    #' Deserialize JSON string into an instance of ConnectedNodePair
    #'
    #' @description
    #' Deserialize JSON string into an instance of ConnectedNodePair
    #'
    #' @param input_json the JSON input
    #' @return the instance of ConnectedNodePair
    #' @export
    fromJSONString = function(input_json) {
      this_object <- jsonlite::fromJSON(input_json)
      self$`node1` <- this_object$`node1`
      self$`node2` <- this_object$`node2`
      self
    },
    #' Validate JSON input with respect to ConnectedNodePair
    #'
    #' @description
    #' Validate JSON input with respect to ConnectedNodePair and throw an exception if invalid
    #'
    #' @param input the JSON input
    #' @export
    validateJSON = function(input) {
      input_json <- jsonlite::fromJSON(input)
      # check the required field `node1`
      if (!is.null(input_json$`node1`)) {
        if (!(is.character(input_json$`node1`) && length(input_json$`node1`) == 1)) {
          stop(paste("Error! Invalid data for `node1`. Must be a string:", input_json$`node1`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for ConnectedNodePair: the required field `node1` is missing."))
      }
      # check the required field `node2`
      if (!is.null(input_json$`node2`)) {
        if (!(is.character(input_json$`node2`) && length(input_json$`node2`) == 1)) {
          stop(paste("Error! Invalid data for `node2`. Must be a string:", input_json$`node2`))
        }
      } else {
        stop(paste("The JSON input `", input, "` is invalid for ConnectedNodePair: the required field `node2` is missing."))
      }
    },
    #' To string (JSON format)
    #'
    #' @description
    #' To string (JSON format)
    #'
    #' @return String representation of ConnectedNodePair
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
      # check if the required `node1` is null
      if (is.null(self$`node1`)) {
        return(FALSE)
      }

      # check if the required `node2` is null
      if (is.null(self$`node2`)) {
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
      # check if the required `node1` is null
      if (is.null(self$`node1`)) {
        invalid_fields["node1"] <- "Non-nullable required field `node1` cannot be null."
      }

      # check if the required `node2` is null
      if (is.null(self$`node2`)) {
        invalid_fields["node2"] <- "Non-nullable required field `node2` cannot be null."
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
# ConnectedNodePair$unlock()
#
## Below is an example to define the print function
# ConnectedNodePair$set("public", "print", function(...) {
#   print(jsonlite::prettify(self$toJSONString()))
#   invisible(self)
# })
## Uncomment below to lock the class to prevent modifications to the method or field
# ConnectedNodePair$lock()

