
#' Create Tilmap Slide Link
#'
#' @param slide_barcodes A vector of strings
create_tm_slide_link <- function(slide_barcodes){
    paste0(
        "<a href=\"",
        "https://quip1.bmi.stonybrook.edu:443/",
        "camicroscope/osdCamicroscope.php?tissueId=",
        slide_barcodes,
        "\">",
        slide_barcodes,
        "</a>"
    )
}
