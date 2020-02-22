multivariate_driver_server <- function(
    input,
    output,
    session,
    sample_tbl,
    group_name
){

    ns <- session$ns

    source("R/modules/server/submodules/model_selection_server.R", local = T)
    source("R/modules/ui/submodules/elements_ui.R", local = T)
    source("R/modules/server/submodules/elements_server.R", local = T)
    source("R/modules/server/submodules/plotly_server.R", local = T)
    source("R/driver_functions.R")

    output$response_options <- shiny::renderUI({
        shiny::selectInput(
            ns("response_choice_id"),
            "Select or Search for Response Variable",
            choices = .GlobalEnv$create_feature_named_list()
        )
    })

    numerical_covariate_tbl <- shiny::reactive(build_feature_tbl())

    categorical_covariate_tbl <- shiny::reactive({
        dplyr::tribble(
            ~class,     ~display,         ~feature,         ~internal,
            "Groups",   "Immune Subtype", "Immune_Subtype", "Immune_Subtype",
            "Groups",   "TCGA Study",     "TCGA_Study",     "TCGA_Study",
            "Groups",   "TCGA Subtype",   "TCGA_Subtype",   "TCGA_Subtype"
            # "Clinical", "Gender",         "Gender",
            # "Clinical", "Race",           "Race",
            # "Clinical", "Ethnicty",       "Ethnicty"
        )
    })

    response_variable_name <- shiny::reactive({
        shiny::req(input$response_choice_id)
        input$response_choice_id %>%
            as.integer() %>%
            .GlobalEnv$get_feature_display_from_id()
    })

    model_string_prefix <- shiny::reactive({
        shiny::req(response_variable_name())
        stringr::str_c(response_variable_name(), " ~ Mutation status")
    })

    module_parameters <- shiny::callModule(
        model_selection_server,
        "module1",
        numerical_covariate_tbl,
        categorical_covariate_tbl,
        model_string_prefix
    )

    output$model_text <- shiny::renderText({
        module_parameters()$display_string
    })

    response_tbl <- shiny::reactive({
        shiny::req(input$response_choice_id)
        build_md_response_tbl(input$response_choice_id)
    })

    status_tbl <- shiny::reactive(build_md_status_tbl())

    combined_tbl <- shiny::reactive({
        shiny::req(response_tbl(), sample_tbl(), status_tbl(), input$group_mode)
        build_md_combined_tbl(
            response_tbl(),
            sample_tbl(),
            status_tbl(),
            input$group_mode
        )
    })

    labels <- shiny::reactive({
        shiny::req(combined_tbl(), input$min_mutants, input$min_wildtype)
        filter_md_labels(combined_tbl(), input$min_mutants, input$min_wildtype)
    })

    filtered_tbl <- shiny::reactive({
        shiny::req(combined_tbl(), labels())
        dplyr::filter(combined_tbl(), label %in% labels())
    })

    pvalue_tbl <- shiny::reactive({
        shiny::req(filtered_tbl(), module_parameters()$formula_string)
        build_md_pvalue_tbl(filtered_tbl(), module_parameters()$formula_string)
    })

    effect_size_tbl <- shiny::reactive({
        shiny::req(filtered_tbl())
        build_md_effect_size_tbl(filtered_tbl())
    })

    volcano_plot_tbl <- shiny::eventReactive(input$calculate_button, {
        shiny::req(filtered_tbl(), pvalue_tbl(), effect_size_tbl())
        build_md_results_tbl(filtered_tbl(), pvalue_tbl(), effect_size_tbl())
    })

    output$volcano_plot <- plotly::renderPlotly({

        shiny::req(volcano_plot_tbl())

        shiny::validate(shiny::need(
            nrow(volcano_plot_tbl()) > 0,
            paste0(
                "Current parameters did not result in any linear regression",
                "results."
            )
        ))

        .GlobalEnv$create_scatterplot(
            volcano_plot_tbl(),
            x_col     = "log10_fold_change",
            y_col     = "log10_p_value",
            xlab      = "Log10(Fold Change)",
            ylab      = "- Log10(P-value)",
            title     = "Immune Response Association With Driver Mutations",
            source    = "multivariate_volcano_plot",
            key_col   = "label",
            label_col = "label",
            horizontal_line   = T,
            horizontal_line_y = (-log10(0.05))
        )
    })

    shiny::callModule(
        plotly_server,
        "volcano_plot",
        plot_tbl = volcano_plot_tbl
    )

    selected_volcano_result <- shiny::reactive({
        shiny::req(volcano_plot_tbl())

        eventdata <- plotly::event_data(
            "plotly_click",
            source = "multivariate_volcano_plot"
        )

        # plot not clicked on yet
        shiny::validate(shiny::need(
            !is.null(eventdata),
            paste0(
                "Click a point on the above scatterplot to see a violin plot ",
                "for the comparison"
            )
        ))

        clicked_label <- .GlobalEnv$get_values_from_eventdata(eventdata, "key")

        result <-  dplyr::filter(
            volcano_plot_tbl(),
            label == clicked_label
        )

        shiny::validate(shiny::need(
            nrow(result) == 1,
            paste0(
                "Click a point on the above scatterplot to see a violin plot ",
                "for the comparison"
            )
        ))
        return(result)
    })

    violin_tbl <- shiny::reactive({
        shiny::req(filtered_tbl(), selected_volcano_result(), input$group_mode)
        build_md_driver_violin_tbl(
            filtered_tbl(),
            selected_volcano_result()$label
        )
    })

    output$violin_plot <- plotly::renderPlotly({
        shiny::req(
            violin_tbl(),
            response_variable_name(),
            selected_volcano_result(),
            input$group_mode
        )

        mode_check <- "group" %in% colnames(selected_volcano_result())
        if (input$group_mode == "Across groups") {
            mode_check <- !mode_check
        }

        shiny::validate(shiny::need(
            mode_check,
            paste0(
                "Group mode changed, push recalculate button to see ",
                "violin plot"
            )
        ))

        title <- create_md_violin_plot_title(
            selected_volcano_result(),
            input$group_mode
        )

        xlab <- paste0(
            "Mutation Status ",
            selected_volcano_result()$gene,
            ":",
            selected_volcano_result()$mutation_code
        )

        .GlobalEnv$create_violinplot(
            violin_tbl(),
            xlab = xlab,
            ylab = response_variable_name(),
            title = title,
            fill_colors = c("blue"),
            showlegend = FALSE
        )
    })

    shiny::callModule(
        plotly_server,
        "violin_plot",
        plot_tbl = violin_tbl
    )

    #     create_connection("gene_types") %>%
    #         dplyr::filter(name == "driver_mutation") %>%
    #         dplyr::select(type_id = id) %>%
    #         dplyr::inner_join(
    #             create_connection("genes_to_types"),
    #             by = "type_id"
    #         ) %>%
    #         dplyr::select(gene_id) %>%
    #         dplyr::filter(gene_id < 5L) %>% # remove!
    #         dplyr::inner_join(
    #             create_connection("genes"),
    #             by = c("gene_id" = "id")
    #         ) %>%
    #         dplyr::select(gene_id, gene_name = hgnc) %>%
    #         dplyr::inner_join(
    #             create_connection("genes_to_samples") %>%
    #                 dplyr::filter(!is.na(status)),
    #             by = "gene_id"
    #         ) %>%
    #         dplyr::select(sample_id, gene_name, status) %>%
    #         dplyr::compute()
    # })

    # group_covariate_tbl <- shiny::reactive({
    #     covariates <-
    #         module_parameters()$categorical_covariates %>%
    #         intersect(c("Immune_Subtype", "TCGA_Subtype", "TCGA_Study"))
    #     if(length(covariates) == 0){
    #         res <- NULL
    #     } else {
    #         res <- create_connection("tags") %>%
    #             dplyr::filter(name %in% covariates) %>%
    #             dplyr::select(parent_group_id = id, parent_group = name) %>%
    #             dplyr::inner_join(
    #                 create_connection("tags_to_tags"),
    #                 by = c("parent_group_id" = "related_tag_id")
    #             ) %>%
    #             dplyr::inner_join(
    #                 create_connection("tags"),
    #                 by = c("tag_id" = "id")
    #             ) %>%
    #             dplyr::select(parent_group, group = name, tag_id) %>%
    #             dplyr::inner_join(
    #                 create_connection("samples_to_tags"),
    #                 by = "tag_id"
    #             ) %>%
    #             dplyr::collect() %>%
    #             dplyr::select(parent_group, group, sample_id) %>%
    #             tidyr::pivot_wider(values_from = group, names_from = parent_group) %>%
    #             tidyr::drop_na()
    #     }
    #     return(res)
    # })

    # feature_covariate_tbl <- shiny::reactive({
    #     covariate_ids <- module_parameters()$numerical_covariates
    #
    #     if(is.null(covariate_ids)){
    #         res <- NULL
    #     } else {
    #         req(feature_values_con())
    #         res <- feature_values_con() %>%
    #             dplyr::filter(feature_id %in% covariate_ids) %>%
    #             dplyr::inner_join(features_con(), by = "feature_id") %>%
    #             dplyr::select(sample_id, feature_internal_name, value) %>%
    #             dplyr::collect() %>%
    #             tidyr::pivot_wider(values_from = value, names_from = feature_internal_name)
    #     }
    #     return(res)
    # })




    # cov_combined_con <- shiny::reactive({
    #
    #     shiny::req(
    #         combined_con(),
    #         input$group_mode
    #     )
    #
    #     con <- combined_con()
    #
    #     if (!is.null(feature_covariate_tbl())){
    #         con <- dplyr::inner_join(
    #             con,
    #             feature_covariate_tbl(),
    #             by = "sample_id",
    #             copy = T
    #         )
    #     }
    #     if (!is.null(group_covariate_tbl())){
    #         con <- dplyr::inner_join(
    #             con,
    #             group_covariate_tbl(),
    #             by = "sample_id",
    #             copy = T
    #         )
    #     }
    #     con <- dplyr::select(con, -sample_id)
    #
    #     if(input$group_mode == "Across groups") {
    #         con <- con %>%
    #             dplyr::rename(label = gene_name) %>%
    #             dplyr::compute()
    #     } else{
    #         con <- con %>%
    #             dplyr::mutate(label = paste0(gene_name, group, sep = ";")) %>%
    #             dplyr::select(-c(gene_name, group)) %>%
    #             dplyr::compute()
    #     }
    #     return(con)
    # })



    # summary_con <- shiny::reactive({
    #     shiny::req(
    #         cov_combined_con(),
    #         input$min_mutants,
    #         input$min_wildtype,
    #     )
    #     summary_con <- cov_combined_con() %>%
    #         dplyr::group_by(label) %>%
    #         dplyr::mutate(status = dplyr::if_else(
    #             status == "Wt",
    #             1L,
    #             0L
    #         )) %>%
    #         dplyr::summarise(
    #             n_total = dplyr::n(),
    #             n_wt = sum(status),
    #         ) %>%
    #         dplyr::mutate(n_mut = n_total - n_wt) %>%
    #         dplyr::filter(
    #             n_mut >= local(input$min_mutants),
    #             n_wt >= local(input$min_wildtype),
    #         ) %>%
    #         dplyr::ungroup() %>%
    #         dplyr::select(-c(n_mut, n_total, n_wt)) %>%
    #         dplyr::compute()
    #
    # })

    # combined_con2 <- shiny::reactive({
    #
    #     shiny::req(
    #         cov_combined_con(),
    #         summary_con()
    #     )
    #
    #     cov_combined_con() %>%
    #         dplyr::inner_join(summary_con()) %>%
    #         dplyr::compute()
    # })

    # model_tbl <- shiny::reactive({
    #
    #     shiny::req(
    #         combined_con2(),
    #         module_parameters()$formula_string
    #     )
    #
    #     combined_con2() %>%
    #         dplyr::collect() %>%
    #         tidyr::nest(tbl = -label) %>%
    #         dplyr::mutate(p_value = as.double(parallel::mclapply(
    #             tbl,
    #             calculate_lm_pvalue,
    #             module_parameters()$formula_string,
    #             "statusWt"
    #         ))) %>%
    #         dplyr::filter(!is.na(p_value)) %>%
    #         dplyr::select(-tbl) %>%
    #         dplyr::mutate(log10_p_value = -log10(p_value))
    # })

    # effect_size_tbl <- shiny::reactive({
    #
    #     shiny::req(
    #         combined_con2()
    #     )
    #     combined_con2() %>%
    #         dplyr::collect() %>%
    #         dplyr::select(label, response, status) %>%
    #         dplyr::group_by(label, status) %>%
    #         dplyr::summarise(responses = list(response)) %>%
    #         dplyr::mutate(status = as.character(status)) %>%
    #         tidyr::pivot_wider(names_from = status, values_from = responses) %>%
    #         dplyr::rename(GROUP1 = Mut, GROUP2 = Wt) %>%
    #         tidyr::nest(data = c(GROUP1, GROUP2)) %>%
    #         dplyr::mutate(fold_change = as.double(parallel::mclapply(
    #             data,
    #             get_effect_size_from_df,
    #             ratio_effect_size
    #         ))) %>%
    #         dplyr::mutate(log10_fold_change = -log10(fold_change)) %>%
    #         dplyr::select(-data) %>%
    #         tidyr::drop_na()
    # })

    # volcano_tbl <- shiny::eventReactive(input$calculate_button, {
    #
    #     shiny::req(
    #         model_tbl(),
    #         effect_size_tbl()
    #     )
    #
    #     tbl <-
    #         dplyr::inner_join(
    #             model_tbl(),
    #             effect_size_tbl(),
    #             by = "label"
    #         )
    # })


}

