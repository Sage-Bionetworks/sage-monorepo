from explorers_data_loader import ProductSettings, run

PRODUCT_SETTINGS = ProductSettings(
    name="agora", insertion_ordered_collection_name="uiconfig"
)

if __name__ == "__main__":
    run(PRODUCT_SETTINGS)
