#' @docType class
#' @title ImageHeight
#' @description ImageHeight Class
#' @format An \code{R6Class} generator object
#' @importFrom R6 R6Class
#' @importFrom jsonlite fromJSON toJSON
#' @export
ImageHeight <- R6::R6Class(
  "ImageHeight",
  public = list(
    #' Initialize a new ImageHeight class.
    #'
    #' @description
    #' Initialize a new ImageHeight class.
    #'
    #' @param ... Optional arguments.
    #' @export
    initialize = function(...) {
      local.optional.var <- list(...)
      val <- unlist(local.optional.var)
      enumvec <- .parse_ImageHeight()

      stopifnot(length(val) == 1L)

      if (!val %in% enumvec)
          stop("Use one of the valid values: ",
              paste0(enumvec, collapse = ", "))
      private$value <- val
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ImageHeight in JSON format
    #' @export
    toJSON = function() {
        jsonlite::toJSON(private$value, auto_unbox = TRUE)
    },
    #' Deserialize JSON string into an instance of ImageHeight
    #'
    #' @description
    #' Deserialize JSON string into an instance of ImageHeight
    #'
    #' @param input_json the JSON input
    #' @return the instance of ImageHeight
    #' @export
    fromJSON = function(input_json) {
      private$value <- jsonlite::fromJSON(input_json,
          simplifyVector = FALSE)
      self
    },
    #' To JSON string
    #'
    #' @description
    #' To JSON String
    #'
    #' @return ImageHeight in JSON format
    #' @export
    toJSONString = function() {
      as.character(jsonlite::toJSON(private$value,
          auto_unbox = TRUE))
    },
    #' Deserialize JSON string into an instance of ImageHeight
    #'
    #' @description
    #' Deserialize JSON string into an instance of ImageHeight
    #'
    #' @param input_json the JSON input
    #' @return the instance of ImageHeight
    #' @export
    fromJSONString = function(input_json) {
      private$value <- jsonlite::fromJSON(input_json,
          simplifyVector = FALSE)
      self
    }
  ),
  private = list(
    value = NULL
  )
)

# add to utils.R
.parse_ImageHeight <- function(vals) {
  res <- gsub("^\\[|\\]$", "", "[original, 32px, 100px, 140px, 250px, 500px]")
  unlist(strsplit(res, ", "))
}

