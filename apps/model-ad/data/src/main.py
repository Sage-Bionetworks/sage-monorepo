from explorers_data_loader import ProductSettings, run

PRODUCT_SETTINGS = ProductSettings(
    name="model-ad", insertion_ordered_collection_name="ui_config"
)

if __name__ == "__main__":
    run(PRODUCT_SETTINGS)
