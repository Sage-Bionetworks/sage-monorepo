immune_feature_distributions_server <- function(
    input,
    output,
    session,
    cohort_obj
){

    ns <- session$ns

    source("R/immune_feature_distributions_functions.R", local = T)
    source("R/modules/server/submodules/distribution_plot_server.R", local = T)

    features_tbl <- shiny::reactive({
        shiny::req(cohort_obj())
        iatlas.app::query_features_by_class(
            cohort_obj()$dataset,
            cohort_obj()$group_name
        ) %>%
            tidyr::unnest(cols = c("features"))
    })

    output$selection_ui <- shiny::renderUI({
        shiny::req(features_tbl())
        shiny::selectInput(
            ns("feature_choice_name"),
            label = "Select or Search for Variable",
            selected = "leukocyte_fraction",
            choices = iatlas.app::create_nested_named_list(
                features_tbl(), values_col = "name"
            )
        )
    })

    feature_choice_display <- shiny::reactive({
        shiny::req(input$feature_choice_name, features_tbl())
        features_tbl() %>%
            dplyr::filter(name == input$feature_choice_name) %>%
            dplyr::pull(display)
    })

    feature_plot_label <- shiny::reactive({
        shiny::req(input$scale_method_choice, feature_choice_display())
        iatlas.app::transform_feature_string(
            feature_choice_display(),
            input$scale_method_choice
        )
    })

    distplot_tbl <- shiny::reactive({
        shiny::req(
            cohort_obj(),
            input$feature_choice_name,
            input$scale_method_choice
        )
        iatlas.app::build_ifd_distplot_tbl(
            cohort_obj()$sample_tbl,
            input$feature_choice_name,
            input$scale_method_choice
        )
    })

    shiny::callModule(
        distribution_plot_server,
        "immune_feature_dist_plot",
        cohort_obj,
        distplot_tbl    = distplot_tbl,
        distplot_type   = shiny::reactive(input$plot_type_choice),
        distplot_ylab   = feature_plot_label,
        distplot_title  = feature_choice_display
    )
}
