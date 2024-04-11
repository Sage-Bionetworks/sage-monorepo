server <- function(input, output) {
    
  output$map <- renderLeaflet({
    leaflet() %>%
      addTiles() %>%
      setView(lng = -122.335167, lat = 47.608013, zoom = 12) %>%
      addMarkers(lng = -122.351860, lat = 47.618180, popup = "2901 Third Ave. Suite 330 Seattle, WA 98121")
    })
}