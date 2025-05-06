provider "azurerm" {
  features {}
}

resource "azurerm_resource_group" "app" {
  name     = "tp-devops"
  location = "West Europe"
}

resource "azurerm_app_service_plan" "plan" {
  name                = "tp-devops"
  location            = azurerm_resource_group.app.location
  resource_group_name = azurerm_resource_group.app.name
  kind                = "Linux"
  reserved            = true

  sku {
    tier = "Basic"
    size = "B1"
  }
}

resource "azurerm_linux_web_app" "app" {
  name                = "tp-devops-${lower(substr(md5(azurerm_resource_group.app.name), 0, 8))}"
  location            = azurerm_resource_group.app.location
  resource_group_name = azurerm_resource_group.app.name
  service_plan_id     = azurerm_app_service_plan.plan.id

  site_config {
    application_stack {
      docker_image     = "chaddathekhobza/devops-tp2"
      docker_image_tag = "latest"
    }
  }

  app_settings = {
    WEBSITES_PORT = "8080"
  }
}

output "webapp_name" {
  value = azurerm_linux_web_app.app.name
}