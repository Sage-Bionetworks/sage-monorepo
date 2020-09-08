test_that("cohort_group_selection_server", {
  shiny::testServer(
    cohort_group_selection_server,
    args = list("selected_dataset" = shiny::reactiveVal("TCGA")),
    {
      expect_type(tag_group_tbl(), "list")
      expect_named(tag_group_tbl(), c("name", "display"))
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

      # expect_type(cohort_obj(), "list")

      # expect_equal(session$getReturned()(), "list")
    }
  )
})
