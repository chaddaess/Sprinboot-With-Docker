terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.99.0"
    }
  }

  backend "azurerm" {
    resource_group_name  = "tfstate-rg"
    storage_account_name = "tfstatestorage123"
    container_name       = "tfstate"
    key                  = "terraform.tfstate"
  }
}

provider "azurerm" {
  features {}
}

resource "azurerm_resource_group" "app" {
  name     = "tp-devops"
  location = "West Europe"
}

resource "azurerm_service_plan" "plan" {
  name                = "tp-devops"
  resource_group_name = azurerm_resource_group.app.name
  location            = azurerm_resource_group.app.location
  os_type             = "Linux"
  sku_name            = "F1" # Changer en "F1" pour le tier gratuit
}

resource "azurerm_linux_web_app" "app" {
  name                = "tp-devops-${lower(substr(md5(azurerm_resource_group.app.name),0,8))}"
  resource_group_name = azurerm_resource_group.app.name
  location            = azurerm_service_plan.plan.location
  service_plan_id     = azurerm_service_plan.plan.id

  site_config {
    application_stack {
      docker_image        = "chaddathekhobza/devops-tp2"
      docker_image_tag    = "latest"
      docker_registry_url = "https://index.docker.io/v1/"
    }
  }

  app_settings = {
    WEBSITES_PORT = "8080"
  }

  logs {
    application_logs {
      file_system_level = "Information"
    }
    http_logs {
      file_system {
        retention_in_days = 7
        retention_in_mb   = 100
      }
    }
  }
}

output "webapp_name" {
  value = azurerm_linux_web_app.app.name
}

output "webapp_url" {
  value = "https://${azurerm_linux_web_app.app.default_hostname}"
}