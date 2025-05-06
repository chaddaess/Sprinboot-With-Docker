provider "azurerm" {
  features {}
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
    linux_fx_version = "DOCKER|hello-world"
  }

  app_settings = {
    WEBSITES_PORT = "80"
  }
}

resource "random_id" "suffix" {
  byte_length = 4
}

output "webapp_url" {
  value = "https://${azurerm_linux_web_app.webapp.default_hostname}"
}
