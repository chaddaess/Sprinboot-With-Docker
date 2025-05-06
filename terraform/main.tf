provider "azurerm" {
  features {}
  resource_provider_registrations = "none"
}

resource "azurerm_resource_group" "test" {
  name     = "jenkins-tf-rg"
  location = "East US"
}

output "resource_group_name" {
  value = azurerm_resource_group.test.name
}
