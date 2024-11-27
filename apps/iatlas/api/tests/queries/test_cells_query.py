import json
import pytest
from api.resolvers.resolver_helpers.paging_utils import (
    from_cursor_hash,
    to_cursor_hash,
    Paging,
)


@pytest.fixture(scope="module")
def common_query_builder():
    def f(query_fields):
        return (
            """query Cells(
        $paging: PagingInput
        $distinct:Boolean
        $cohort: [String!]
        $cell: [String!]
    ) {
        cells(
            paging: $paging
            distinct: $distinct
            cohort: $cohort
            cell: $cell
        )"""
            + query_fields
            + "}"
        )

    return f


@pytest.fixture(scope="module")
def common_query(common_query_builder):
    return common_query_builder(
        """
    {
        items {
            name
            type
        }
        paging {
            type
            pages
            total
            startCursor
            endCursor
            hasPreviousPage
            hasNextPage
            page
            limit
        }
        error
    }
    """
    )


@pytest.fixture(scope="module")
def feature_query(common_query_builder):
    return common_query_builder(
        """
    {
        items {
            name
            type
            features {
              name
              value
            }
        }
        paging {
            type
            pages
            total
            startCursor
            endCursor
            hasPreviousPage
            hasNextPage
            page
            limit
        }
        error
    }
    """
    )


def test_cells_cursor_pagination_first(client, common_query_builder):
    query = common_query_builder(
        """{
            items {
                id
            }
            paging {
                type
                pages
                total
                startCursor
                endCursor
                hasPreviousPage
                hasNextPage
                page
                limit
            }
        }"""
    )
    num = 10
    response = client.post(
        "/api", json={"query": query, "variables": {"paging": {"first": num}}}
    )
    json_data = json.loads(response.data)
    page = json_data["data"]["cells"]

    items = page["items"]
    paging = page["paging"]
    start = from_cursor_hash(paging["startCursor"])
    end = from_cursor_hash(paging["endCursor"])

    assert len(items) == num
    assert paging["hasNextPage"] == True
    assert paging["hasPreviousPage"] == False
    assert start == items[0]["id"]
    assert end == items[num - 1]["id"]


def test_cells_cursor_pagination_last(client, common_query_builder):
    query = common_query_builder(
        """{
            items {
                id
            }
            paging {
                type
                pages
                total
                startCursor
                endCursor
                hasPreviousPage
                hasNextPage
                page
                limit
            }
        }"""
    )
    num = 10
    response = client.post(
        "/api",
        json={
            "query": query,
            "variables": {"paging": {"last": num, "before": to_cursor_hash(1000)}},
        },
    )
    json_data = json.loads(response.data)
    page = json_data["data"]["cells"]
    items = page["items"]
    paging = page["paging"]
    start = from_cursor_hash(paging["startCursor"])
    end = from_cursor_hash(paging["endCursor"])

    assert len(items) == num
    assert paging["hasNextPage"] == False
    assert paging["hasPreviousPage"] == True
    assert start == items[0]["id"]
    assert end == items[num - 1]["id"]


def test_cells_cursor_distinct_pagination(client, common_query):
    page_num = 2
    num = 10
    response = client.post(
        "/api",
        json={
            "query": common_query,
            "variables": {
                "paging": {
                    "page": page_num,
                    "first": num,
                },
                "distinct": True,
            },
        },
    )
    json_data = json.loads(response.data)
    page = json_data["data"]["cells"]
    items = page["items"]

    assert len(items) == num
    assert page_num == page["paging"]["page"]


def test_cell_query_with_no_arguments(client, common_query):
    response = client.post("/api", json={"query": common_query})
    json_data = json.loads(response.data)
    page = json_data["data"]["cells"]
    results = page["items"]
    assert isinstance(results, list)
    assert len(results) > 10
    for result in results[0:10]:
        assert isinstance(result["name"], str)
        assert isinstance(result["type"], str)


def test_cell_query_with_cell(client, common_query):
    response = client.post(
        "/api",
        json={
            "query": common_query,
            "variables": {"cell": ["RU1311A_T_1_165945547864806"]},
        },
    )
    json_data = json.loads(response.data)
    page = json_data["data"]["cells"]
    results = page["items"]
    assert isinstance(results, list)
    assert len(results) == 1
    result = results[0]
    assert result["name"] == "RU1311A_T_1_165945547864806"
    assert isinstance(result["type"], str)


def test_feature_query_with_cohort(client, feature_query):
    response = client.post(
        "/api",
        json={"query": feature_query, "variables": {"cohort": ["MSK_Biopsy_Site"]}},
    )
    json_data = json.loads(response.data)
    page = json_data["data"]["cells"]
    results = page["items"]
    assert isinstance(results, list)
    assert len(results) > 10
    for result in results[0:10]:
        assert isinstance(result["name"], str)
        assert isinstance(result["type"], str)
        assert isinstance(result["features"], list)
        for feature in result["features"]:
            assert isinstance(feature["name"], str)
            assert isinstance(feature["value"], float)
