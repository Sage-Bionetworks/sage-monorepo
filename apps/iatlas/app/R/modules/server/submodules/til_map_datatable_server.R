til_map_datatable_server <- function(
    input,
    output,
    session,
    sample_tbl
){

    source(
        "R/modules/server/submodules/data_table_server.R",
        local = T
    )

    tilmap_tbl <- shiny::reactive({
        subquery1 <- "SELECT id FROM classes WHERE name = 'TIL Map Characteristic'"

        subquery2 <- paste(
            "SELECT id AS feature FROM features",
            "WHERE class_id = (",
            subquery1,
            ")"
        )

        subquery3 <- paste(
            "SELECT feature_id, sample_id, value FROM features_to_samples",
            "WHERE feature_id IN (",
            subquery2,
            ")"
        )

        query <- paste(
            "SELECT a.sample_id, a.value, b.display FROM",
            "(", subquery3, ") a",
            "INNER JOIN",
            "(SELECT id, display from features) b",
            "ON a.feature_id = b.id"
        )

        query %>%
            perform_query("build feature table") %>%
            dplyr::mutate(value = round(value, digits = 1)) %>%
            tidyr::pivot_wider(., names_from = display, values_from = value) %>%
            dplyr::inner_join(sample_tbl(), by = "sample_id") %>%
            dplyr::filter(!is.na(slide_barcode)) %>%
            dplyr::mutate(Image = paste0(
                "<a href=\"",
                "https://quip1.bmi.stonybrook.edu:443/",
                "camicroscope/osdCamicroscope.php?tissueId=",
                slide_barcode,
                "\">",
                slide_barcode,
                "</a>"
            )) %>%
            dplyr::select(-c(slide_barcode, sample_id)) %>%
            dplyr::select(
                Sample = sample_name,
                `Selected Group` = group,
                Image,
                dplyr::everything()
            )
    })

    shiny::callModule(data_table_server, "til_table", tilmap_tbl, escape = F)
}
