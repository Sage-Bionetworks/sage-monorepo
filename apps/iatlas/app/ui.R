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
    shiny::tags$footer(id = "myFooter", shiny::includeHTML("html/footer.html"))
)

################################################################################
# Define the full user-interface, `ui`
################################################################################

source("R/pages/aboutpage.R", local = T)
source("R/pages/explorepage.R", local = T)
source("R/pages/toolspage.R", local = T)
source("R/pages/docspage.R", local = T)
source("R/pages/resourcespage.R", local = T)

ui <- shiny::navbarPage(
    shiny::includeCSS("css/custom.css"),
    shiny::includeCSS("css/footer.css"),
    shiny::includeCSS("css/bootstrapTable.min.css"),
    title = shiny::strong("CRI iAtlas Portal"), selected = "Explore",
    shiny::tabPanel("Explore", explorepage, icon = icon("bar-chart")),
    shiny::tabPanel("Tools", toolspage, icon = icon("wrench")),
    shiny::tabPanel("About", aboutpage, icon = icon("info-circle")),
    shiny::tabPanel("Documentation", docspage, icon = icon("file-text")),
    shiny::tabPanel("Resources", resourcespage, icon = icon("link")),
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
        shiny::HTML(
            "
      <script>
      (function(i,s,o,g,r,a,m){
		  i['GoogleAnalyticsObject']=r;i[r]=i[r]||
		  function(){
		  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();
		  a=s.createElement(o), m=s.getElementsByTagName(o)[0];
		  a.async=1;
		  a.src=g;m.parentNode.insertBefore(a,m)
		  })
		  (window, document, 'script',
		  '//www.google-analytics.com/analytics.js','ga');

		  ga('create', 'UA-121820794-2', 'auto');
		  ga('send', 'pageview');

		  </script>
      "
        )
    )
)

shiny::shinyUI(ui)
