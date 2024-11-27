import json
import pytest
from tests import NoneType
from api.resolvers.resolver_helpers.paging_utils import (
    from_cursor_hash,
    to_cursor_hash,
    Paging,
)
from api.database import return_cell_stat_query


@pytest.fixture(scope="module")
def common_query_builder():
    def f(query_fields):
        return (
            """query CellStats(
        $paging: PagingInput
        $distinct:Boolean
        $entrez: [Int!]
    ) {
        cellStats(
            paging: $paging
            distinct: $distinct
            entrez: $entrez
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
            dataSet { name }
            gene { entrez }
            type
            count
            avgExpr
            percExpr
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


def test_cell_stats_cursor_pagination_first(client, common_query_builder):
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
    page = json_data["data"]["cellStats"]
    items = page["items"]
    paging = page["paging"]
    start = from_cursor_hash(paging["startCursor"])
    end = from_cursor_hash(paging["endCursor"])

    assert len(items) == num
    assert paging["hasNextPage"] == True
    assert paging["hasPreviousPage"] == False
    assert start == items[0]["id"]
    assert end == items[num - 1]["id"]


def test_cell_stats_cursor_pagination_last(client, common_query_builder):
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
    page = json_data["data"]["cellStats"]
    items = page["items"]
    paging = page["paging"]
    start = from_cursor_hash(paging["startCursor"])
    end = from_cursor_hash(paging["endCursor"])

    assert len(items) == num
    assert paging["hasNextPage"] == False
    assert paging["hasPreviousPage"] == True
    assert start == items[0]["id"]
    assert end == items[num - 1]["id"]


def test_cell_stats_cursor_distinct_pagination(client, common_query):
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
    page = json_data["data"]["cellStats"]
    items = page["items"]

    assert len(items) == num
    assert page_num == page["paging"]["page"]


def test_cell_stats_with_entrez(client, common_query):
    response = client.post(
        "/api", json={"query": common_query, "variables": {"entrez": [3001]}}
    )

    json_data = json.loads(response.data)
    page = json_data["data"]["cellStats"]
    results = page["items"]

    assert isinstance(results, list)
    assert len(results) > 5
    for result in results[0:5]:
        assert isinstance(result["dataSet"]["name"], str)
        assert result["gene"]["entrez"] == 3001
        assert isinstance(result["type"], str)
        assert isinstance(result["count"], int)
        assert result["avgExpr"] is None or isinstance(result["avgExpr"], float)
        assert result["percExpr"] is None or isinstance(result["percExpr"], float)


def test_cell_stats_query_with_no_arguments(client, common_query):
    response = client.post("/api", json={"query": common_query})
    json_data = json.loads(response.data)
    page = json_data["data"]["cellStats"]
    results = page["items"]
    assert isinstance(results, list)
    assert len(results) > 10
    for result in results[0:10]:
        assert isinstance(result["dataSet"]["name"], str)
        assert isinstance(result["gene"]["entrez"], int)
        assert isinstance(result["type"], str)
        assert isinstance(result["count"], int)
        assert result["avgExpr"] is None or isinstance(result["avgExpr"], float)
        assert result["percExpr"] is None or isinstance(result["percExpr"], float)
