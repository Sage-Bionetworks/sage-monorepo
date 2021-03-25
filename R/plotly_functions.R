#' Create Group Text from Eventdata Dataframe
#'
#' @param eventdata Eventdata from "plotly_click" plotly::event_data
#' @param group_tbl A Tibble with columns group, name, characteristics
#'
#' @importFrom magrittr %>%
#' @importFrom dplyr filter mutate
#' @importFrom rlang .data
create_group_text_from_eventdata <- function(eventdata, group_tbl){
    selected_group <- iatlas.modules::get_values_from_eventdata(eventdata)
    group_tbl %>%
        dplyr::filter(.data$group == selected_group) %>%
        dplyr::mutate(text = paste0(
            .data$name,
            ": ",
            .data$characteristics
        )) %>%
        dplyr::pull(.data$text)
}
