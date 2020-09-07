explorepage <- shinydashboard::dashboardPage(
  header  = shinydashboard::dashboardHeader(disable = TRUE),
  sidebar = shinydashboard::dashboardSidebar(
    shinydashboard::sidebarMenuOutput("sidebar_menu")
  ),
  body = shinydashboard::dashboardBody(shiny::uiOutput("dashboard_body"))
)
