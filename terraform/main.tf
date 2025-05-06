provider "azurerm" {
  features {}
}

resource "random_id" "suffix" {
  byte_length = 4
}

resource "azurerm_resource_group" "rg" {
  name     = "simple-rg"
  location = "East US"
}

resource "azurerm_service_plan" "plan" {
  name                = "simple-plan"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  os_type             = "Linux"
  sku_name            = "F1" # Free tier
}

resource "azurerm_linux_web_app" "webapp" {
  name                = "simplewebapp-${random_id.suffix.hex}"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  service_plan_id     = azurerm_service_plan.plan.id

  site_config {
    application_stack {
      docker_image_name   = "hello-world"
      docker_image_tag    = "latest"
      docker_registry_url = "https://index.docker.io"
    }
  }

  app_settings = {
    WEBSITES_PORT = "80"
  }
}

output "webapp_url" {
  value = "https://${azurerm_linux_web_app.webapp.default_hostname}"
}
