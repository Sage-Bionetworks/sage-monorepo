################################################################################
# Define general header tag list
# List of tags to display as a common header above all tabPanels.
################################################################################

headerTagList <- list(
  shiny::tags$style(
    type = "text/css",
    ".navbar .navbar-nav {float: right; font-size: 14px} .navbar .navbar-nav li a {font-size: 14px} .nav-tabs {font-size: 12px}"
  ),
  shiny::tags$base(target = "_blank")
)

footerTagList <- list(
  shiny::tags$footer(
    id = "myFooter",
    shiny::includeHTML(get_html_path("footer"))
  )
)

################################################################################
# Define the full user-interface, `ui`
################################################################################

ui <- shiny::navbarPage(
  shiny::includeCSS("css/custom.css"),
  shiny::includeCSS("css/footer.css"),
  shiny::includeCSS("css/bootstrapTable.min.css"),
  title = shiny::strong("CRI iAtlas Portal"), selected = "Explore",
  shiny::tabPanel("Explore", explorepage_ui(), icon = icon("chart-bar")),
  shiny::tabPanel("Notebooks", notebookpage_ui(), icon = icon("book-open")),
  shiny::tabPanel("About", aboutpage_ui(), icon = icon("info-circle")),
  shiny::tabPanel("Resources", resourcespage_ui(), icon = icon("link")),
  shiny::tabPanel("Documentation", docspage_ui(), icon = icon("file-alt")),
  header = headerTagList,
  footer = footerTagList,
  collapsible = TRUE,
  inverse = TRUE,
  windowTitle = "CRI iAtlas Portal",
  # This make shiny::need messages easier to see
  shiny::tags$head(shiny::tags$style(shiny::HTML(
    ".shiny-output-error-validation {color: black; font-size: large}"
  ))),
  shiny::tags$head(
    shiny::tags$script(
      '
      var dimension = [0, 0];
      $(document).on("shiny:connected", function(e) {
      dimension[0] = window.innerWidth;
      dimension[1] = window.innerHeight;
      Shiny.onInputChange("dimension", dimension);
      });
      $(window).resize(function(e) {
      dimension[0] = window.innerWidth;
      dimension[1] = window.innerHeight;
      Shiny.onInputChange("dimension", dimension);
      });
      '
    ),
    shiny::includeHTML("google_analytics.html")
  )
)

shiny::shinyUI(ui)
