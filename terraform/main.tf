provider "azurerm" {
  features {}
  client_id       = var.client_id
  client_secret   = var.client_secret
  tenant_id       = var.tenant_id
  subscription_id = var.subscription_id
  resource_provider_registrations = "none"
}

resource "azurerm_resource_group" "devops_rg" {
  name     = "devops-student-rg"
  location = "westeurope"
}
