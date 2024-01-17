"""Tests for schema endpoints"""

# pylint: disable=duplicate-code
import unittest

from schematic_api.test import BaseTestCase
from .conftest import TEST_SCHEMA_URL, PAGING_KEYS

HEADERS = {
    "Accept": "application/json",
    "Authorization": "Bearer xxx",
}

COMPONENT_URL = "/api/v1/components/Patient/?schemaUrl="
CONNECTED_NODE_PAIR_ARRAY_URL = "/api/v1/connectedNodePairArray?schemaUrl="
CONNECTED_NODE_PAIR_PAGE_URL = "/api/v1/connectedNodePairPage?schemaUrl="
NODE_IS_REQUIRED_URL = "/api/v1/nodes/FamilyHistory/isRequired?schemaUrl="
PROPERTY_LABEL_URL = "/api/v1/nodes/node_label/propertyLabel?schemaUrl="
SCHEMA_ATTRIBUTES_URL = "/api/v1/schemaAttributes?schemaUrl="
NODE_PROPERTIES_URL = "/api/v1/nodes/MolecularEntity/nodeProperties?schemaUrl="
NODE_VALIDATION_RULES_URL = "/api/v1/nodes/CheckRegexList/validationRules?schemaUrl="
NODE_DEPENDENCY_ARRAY_URL = "/api/v1/nodes/Patient/dependencyArray?schemaUrl="
NODE_DEPENDENCY_PAGE_URL = "/api/v1/nodes/Patient/dependencyPage?schemaUrl="


class TestGetComponent(BaseTestCase):
    """Test case for component endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""
        url = f"{COMPONENT_URL}{TEST_SCHEMA_URL}"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        assert isinstance(response.json, str)

    def test_parameters(self) -> None:
        """Test for successful result"""
        url = f"{COMPONENT_URL}{TEST_SCHEMA_URL}&includeIndex=True"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        assert isinstance(response.json, str)

    def test_404(self) -> None:
        """Test for 404 result"""
        url = f"{COMPONENT_URL}not_a_url"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert404(response, f"Response body is : {response.data.decode('utf-8')}")


class TestGetConnectedNodePairArray(BaseTestCase):
    """Tests for connected node pair array endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""
        url = (
            f"{CONNECTED_NODE_PAIR_ARRAY_URL}{TEST_SCHEMA_URL}"
            "&relationshipType=requiresDependency"
        )
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        result = response.json
        assert isinstance(result, dict)
        assert isinstance(result["connectedNodes"], list)
        for item in result["connectedNodes"]:
            assert isinstance(item, dict)
            assert isinstance(item["node1"], str)
            assert isinstance(item["node2"], str)

    def test_500(self) -> None:
        """Test for 500 result"""
        url = f"{CONNECTED_NODE_PAIR_ARRAY_URL}not_a_url&relationshipType=requiresDependency"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert500(response, f"Response body is : {response.data.decode('utf-8')}")


class TestGetConnectedNodePairPage(BaseTestCase):
    """Tests for connected node pair page endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""
        url = f"{CONNECTED_NODE_PAIR_PAGE_URL}{TEST_SCHEMA_URL}&relationshipType=requiresDependency"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        result = response.json
        assert isinstance(result, dict)
        assert list(result.keys()) == sorted(PAGING_KEYS + ["connectedNodes"])
        assert result["number"] == 1
        assert result["size"] == 100000
        assert not result["hasNext"]
        assert not result["hasPrevious"]
        assert result["totalPages"] == 1
        assert isinstance(result["totalElements"], int)
        assert isinstance(result["connectedNodes"], list)
        for item in result["connectedNodes"]:
            assert isinstance(item, dict)
            assert isinstance(item["node1"], str)
            assert isinstance(item["node2"], str)

    def test_500(self) -> None:
        """Test for 500 result"""
        url = f"{CONNECTED_NODE_PAIR_PAGE_URL}not_a_url&relationshipType=requiresDependency"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert500(response, f"Response body is : {response.data.decode('utf-8')}")


class TestGetNodeIsRequired(BaseTestCase):
    """Test case for node is required endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""
        url = f"{NODE_IS_REQUIRED_URL}{TEST_SCHEMA_URL}"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        assert response.json

    def test_500(self) -> None:
        """Test for 500 result"""
        url = f"{NODE_IS_REQUIRED_URL}not_a_url"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert500(response, f"Response body is : {response.data.decode('utf-8')}")


class TestGetPropertyLabel(BaseTestCase):
    """Test case for property label endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""
        url = f"{PROPERTY_LABEL_URL}{TEST_SCHEMA_URL}"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        assert response.json == "nodeLabel"

    def test_500(self) -> None:
        """Test for 500 result"""
        url = f"{PROPERTY_LABEL_URL}not_a_url"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert500(response, f"Response body is : {response.data.decode('utf-8')}")


class TestGetSchemaAttributes(BaseTestCase):
    """Test case for schema attributes endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""
        url = f"{SCHEMA_ATTRIBUTES_URL}{TEST_SCHEMA_URL}"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        assert isinstance(response.json, str)

    def test_404(self) -> None:
        """Test for 404 result"""
        url = f"{SCHEMA_ATTRIBUTES_URL}not_a_url"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert404(response, f"Response body is : {response.data.decode('utf-8')}")


class TestGetNodeProperties(BaseTestCase):
    """Test case for node attributes endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""
        url = f"{NODE_PROPERTIES_URL}{TEST_SCHEMA_URL}"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        result = response.json
        assert isinstance(result, dict)
        assert list(result.keys()) == ["node_properties"]
        for item in result["node_properties"]:
            assert isinstance(item, str)

    def test_500(self) -> None:
        """Test for 500 result"""
        url = f"{NODE_PROPERTIES_URL}not_a_url"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert500(response, f"Response body is : {response.data.decode('utf-8')}")


class TestGetNodeValidationRules(BaseTestCase):
    """Test case for node validation rules endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""
        url = f"{NODE_VALIDATION_RULES_URL}{TEST_SCHEMA_URL}"
        response = self.client.open(url, method="GET", headers=HEADERS)
        result = response.json
        assert isinstance(result, dict)
        assert list(result.keys()) == ["validation_rules"]
        assert isinstance(result["validation_rules"], list)
        for item in result["validation_rules"]:
            assert isinstance(item, dict)
            assert list(item.keys()) == ["name"]
            assert isinstance(item["name"], str)

    def test_500(self) -> None:
        """Test for 500 result"""
        url = f"{NODE_VALIDATION_RULES_URL}not_a_url"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert500(response, f"Response body is : {response.data.decode('utf-8')}")


class TestGetNodeDependencyArray(BaseTestCase):
    """Test case for node depencencies endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""
        url = f"{NODE_DEPENDENCY_ARRAY_URL}{TEST_SCHEMA_URL}"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        result = response.json
        assert isinstance(result, dict)
        assert list(result.keys()) == ["nodes"]
        assert isinstance(result["nodes"], list)
        for item in result["nodes"]:
            assert isinstance(item, dict)
            assert list(item.keys()) == ["name"]
            assert isinstance(item["name"], str)

    def test_return_display_names(self) -> None:
        """Test for returnDisplayNames parameter"""
        url = f"{NODE_DEPENDENCY_ARRAY_URL}{TEST_SCHEMA_URL}&returnDisplayNames=true"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")

        url = f"{NODE_DEPENDENCY_ARRAY_URL}{TEST_SCHEMA_URL}&returnDisplayNames=false"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")

    def test_return_ordered_by_schema(self) -> None:
        """Test for returnOrderedBySchema parameter"""
        url = f"{NODE_DEPENDENCY_ARRAY_URL}{TEST_SCHEMA_URL}&returnOrderedBySchema=true"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")

        url = (
            f"{NODE_DEPENDENCY_ARRAY_URL}{TEST_SCHEMA_URL}&returnOrderedBySchema=false"
        )
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")

    def test_500(self) -> None:
        """Test for 500 result"""
        url = f"{NODE_DEPENDENCY_ARRAY_URL}not_a_url"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert500(response, f"Response body is : {response.data.decode('utf-8')}")


class TestGetNodeDependencyPage(BaseTestCase):
    """Test case for node depencencies endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""
        url = f"{NODE_DEPENDENCY_PAGE_URL}{TEST_SCHEMA_URL}"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")
        result = response.json
        assert isinstance(result, dict)
        assert list(result.keys()) == sorted(PAGING_KEYS + ["nodes"])
        assert result["number"] == 1
        assert result["size"] == 100000
        assert not result["hasNext"]
        assert not result["hasPrevious"]
        assert result["totalPages"] == 1
        assert isinstance(result["totalElements"], int)
        assert isinstance(result["nodes"], list)
        for item in result["nodes"]:
            assert isinstance(item, dict)
            assert list(item.keys()) == ["name"]
            assert isinstance(item["name"], str)

    def test_return_display_names(self) -> None:
        """Test for returnDisplayNames parameter"""
        url = f"{NODE_DEPENDENCY_PAGE_URL}{TEST_SCHEMA_URL}&returnDisplayNames=true"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")

        url = f"{NODE_DEPENDENCY_PAGE_URL}{TEST_SCHEMA_URL}&returnDisplayNames=false"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")

    def test_return_ordered_by_schema(self) -> None:
        """Test for returnOrderedBySchema parameter"""
        url = f"{NODE_DEPENDENCY_PAGE_URL}{TEST_SCHEMA_URL}&returnOrderedBySchema=true"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")

        url = f"{NODE_DEPENDENCY_PAGE_URL}{TEST_SCHEMA_URL}&returnOrderedBySchema=false"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert200(response, f"Response body is : {response.data.decode('utf-8')}")

    def test_500(self) -> None:
        """Test for 500 result"""
        url = f"{NODE_DEPENDENCY_PAGE_URL}not_a_url"
        response = self.client.open(url, method="GET", headers=HEADERS)
        self.assert500(response, f"Response body is : {response.data.decode('utf-8')}")


if __name__ == "__main__":
    unittest.main()
