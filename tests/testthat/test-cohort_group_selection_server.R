test_that("cohort_group_selection_server_immune_subtype", {
  shiny::testServer(
    cohort_group_selection_server,
    args = list("selected_dataset" = shiny::reactiveVal("TCGA")),
    {
      expect_type(tag_group_tbl(), "list")
      expect_named(tag_group_tbl(), c("display", "name"))
      expect_type(custom_group_tbl(), "list")
      expect_named(custom_group_tbl(), c("display", "name"))
      expect_type(available_groups_list(), "character")
      expect_named(available_groups_list())
      expect_type(default_group(), "character")
      expect_type(output$select_group_ui, "list")
      expect_type(group_choice(), "character")
      expect_equal(group_choice(), default_group())

      session$setInputs("group_choice" = "Immune_Subtype")
      expect_equal(group_choice(), "Immune_Subtype")
      expect_false(display_driver_mutation_ui())
      expect_false(display_immune_feature_bins_ui())

      group_object <- session$getReturned()()
      expect_named(
        group_object,
        c("dataset", "group_name", "group_display", "group_type")
      )
      expect_equal(group_object$dataset, "TCGA")
      expect_equal(group_object$group_name, "Immune_Subtype")
      expect_equal(group_object$group_display, "Immune Subtype")
      expect_equal(group_object$group_type, "tag")
    }
  )
})

test_that("cohort_group_selection_server_driver_mutation", {
  shiny::testServer(
    cohort_group_selection_server,
    args = list("selected_dataset" = shiny::reactiveVal("TCGA")),
    {
      session$setInputs("group_choice" = "Driver Mutation")
      session$setInputs("driver_mutation_choice_id" = 1L)
      expect_equal(group_choice(), "Driver Mutation")
      expect_true(display_driver_mutation_ui())
      expect_false(display_immune_feature_bins_ui())

      group_object <- session$getReturned()()
      expect_named(
        group_object,
        c(
          "dataset",
          "group_name",
          "group_display",
          "group_type",
          "mutation_id"
        )
      )
      expect_equal(group_object$dataset, "TCGA")
      expect_equal(group_object$group_name, "Driver Mutation")
      expect_equal(group_object$group_display, "Driver Mutation")
      expect_equal(group_object$group_type, "custom")
      expect_equal(group_object$mutation_id, 1L)
    }
  )
})

test_that("cohort_group_selection_server_immune_feature_bin", {
  shiny::testServer(
    cohort_group_selection_server,
    args = list("selected_dataset" = shiny::reactiveVal("TCGA")),
    {
      session$setInputs("group_choice" = "Immune Feature Bins")
      session$setInputs("bin_immune_feature_choice" = "feature1")
      session$setInputs("bin_number_choice" = 2)
      expect_equal(group_choice(), "Immune Feature Bins")
      expect_false(display_driver_mutation_ui())
      expect_true(display_immune_feature_bins_ui())

      group_object <- session$getReturned()()
      expect_named(
        group_object,
        c(
          "dataset",
          "group_name",
          "group_display",
          "group_type",
          "bin_immune_feature",
          "bin_number"
        )
      )
      expect_equal(group_object$dataset, "TCGA")
      expect_equal(group_object$group_name, "Immune Feature Bins")
      expect_equal(group_object$group_display, "Immune Feature Bins")
      expect_equal(group_object$group_type, "custom")
      expect_equal(group_object$bin_immune_feature, "feature1")
      expect_equal(group_object$bin_number, 2)
    }
  )
})

test_that("cohort_group_selection_server_tcga_clinical", {
  shiny::testServer(
    cohort_group_selection_server,
    args = list("selected_dataset" = shiny::reactiveVal("TCGA")),
    {
      expect_named(tag_group_tbl(), c("display", "name"))
      expect_true(nrow(tag_group_tbl()) > 0)

      expect_named(custom_group_tbl(), c("display", "name"))
      expect_true(nrow(custom_group_tbl()) > 0)

      expect_named(clinical_group_tbl(), c("display", "name"))
      expect_true(nrow(clinical_group_tbl()) > 0)

      expect_type(available_groups_list(), "character")
      expect_true(length(clinical_group_tbl()) > 0)

      session$setInputs("group_choice" = "gender")
      expect_equal(group_choice(), "gender")
      expect_false(display_driver_mutation_ui())
      expect_false(display_immune_feature_bins_ui())

      group_object <- session$getReturned()()
      expect_named(
        group_object,
        c("dataset", "group_name", "group_display", "group_type")
      )
      expect_equal(group_object$dataset, "TCGA")
      expect_equal(group_object$group_name, "gender")
      expect_equal(group_object$group_type, "clinical")
    }
  )
})

test_that("cohort_group_selection_server_pcawg_clinical", {
  shiny::testServer(
    cohort_group_selection_server,
    args = list("selected_dataset" = shiny::reactiveVal("PCAWG")),
    {
      session$setInputs("group_choice" = "Immune Feature Bins")
      session$setInputs("bin_immune_feature_choice" = "feature1")
      session$setInputs("bin_number_choice" = 2)
      expect_equal(group_choice(), "Immune Feature Bins")
      expect_false(display_driver_mutation_ui())
      expect_true(display_immune_feature_bins_ui())

      group_object <- session$getReturned()()
      expect_named(
        group_object,
        c(
          "dataset",
          "group_name",
          "group_display",
          "group_type",
          "bin_immune_feature",
          "bin_number"
        )
      )
      expect_equal(group_object$dataset, "PCAWG")
      expect_equal(group_object$group_name, "Immune Feature Bins")
      expect_equal(group_object$group_type, "custom")
      expect_equal(group_object$bin_immune_feature, "feature1")
      expect_equal(group_object$bin_number, 2)
    }
  )
})
