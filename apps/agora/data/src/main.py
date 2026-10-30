from explorers_data_loader import ProductSettings, run

PRODUCT_SETTINGS = ProductSettings(name="agora", loads_team_images=True)

if __name__ == "__main__":
    run(PRODUCT_SETTINGS)
