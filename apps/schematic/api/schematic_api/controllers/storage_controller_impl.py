from schematic_api.models.basic_error import BasicError  # noqa: E501
from schematic_api.models.dataset import Dataset  # noqa: E501
from schematic_api.models.datasets_page import DatasetsPage  # noqa: E501


def list_storage_project_datasets(project_id):  # noqa: E501
    try:
        datasets = []
        datasets.append(Dataset(name="dataset-1"))
        datasets.append(Dataset(name="dataset-2"))
        datasets.append(Dataset(name="dataset-3"))

        page = DatasetsPage(
            number=0,
            size=100,
            total_elements=len(datasets),
            total_pages=1,
            has_next=False,
            has_previous=False,
            datasets=datasets,
        )
        res = page
        status = 200
    # except DoesNotExist:
    #     status = 404
    #     res = BasicError("The specified resource was not found", status)
    except Exception as error:
        status = 500
        res = BasicError("Internal error", status, str(error))
    return res, status
