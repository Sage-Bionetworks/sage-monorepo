# flake8: noqa

# import all models into this package
# if you have many models here with many references from one model to another this may
# raise a RecursionError
# to avoid this, import only the models that you directly need like:
# from from schematic_client.model.pet import Pet
# or import this package, but before doing it, use:
# import sys
# sys.setrecursionlimit(n)

from schematic_client.model.basic_error import BasicError
from schematic_client.model.dataset import Dataset
from schematic_client.model.datasets_page import DatasetsPage
from schematic_client.model.datasets_page_all_of import DatasetsPageAllOf
from schematic_client.model.page_metadata import PageMetadata
