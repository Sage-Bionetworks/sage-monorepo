"""Tests for schema endpoints"""

# pylint: disable=duplicate-code
from __future__ import absolute_import
import unittest
from unittest.mock import patch

import schematic_api.controllers.schema_controller_impl
from schematic_api.test import BaseTestCase

HEADERS = {
    "Accept": "application/json",
    "Authorization": "Bearer xxx",
}
COMPONENT_ATTRIBUTES_URL = "/api/v1/components/component1/attributes?schemaUrl=url1"
NODE_VALIDATION_RULES_URL = "/api/v1/nodes/node1/validationRules?schemaUrl=url1"
NODE_DEPENDENCIES_URL = "/api/v1/nodes/node1/dependencies?schemaUrl=url1"


class TestComponentAttributes(BaseTestCase):
    """Test case for component attributes endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""

        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_component_attributes",
            return_value=["attribute1", "attribute2"],
        ) as mock_function:
            response = self.client.open(
                COMPONENT_ATTRIBUTES_URL, method="GET", headers=HEADERS
            )
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

            mock_function.assert_called_once_with("component1", "url1")

            assert not response.json["hasNext"]
            assert not response.json["hasPrevious"]
            assert response.json["number"] == 0
            assert response.json["size"] == 100
            assert response.json["totalElements"] == 2
            assert response.json["totalPages"] == 1
            attributes = response.json["attributes"]
            assert len(attributes) == 2
            attribute = attributes[0]
            assert list(attribute.keys()) == ["name"]
            assert attribute["name"] == "attribute1"

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_component_attributes",
            side_effect=TypeError,
        ):
            response = self.client.open(
                COMPONENT_ATTRIBUTES_URL, method="GET", headers=HEADERS
            )
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )


class TestNodeValidationRules(BaseTestCase):
    """Test case for node validation rules endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""

        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_node_validation_rules",
            return_value=["rule1", "rule2"],
        ) as mock_function:
            response = self.client.open(
                NODE_VALIDATION_RULES_URL, method="GET", headers=HEADERS
            )
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

            mock_function.assert_called_once_with("node1", "url1")

            assert not response.json["hasNext"]
            assert not response.json["hasPrevious"]
            assert response.json["number"] == 0
            assert response.json["size"] == 100
            assert response.json["totalElements"] == 2
            assert response.json["totalPages"] == 1
            validation_rules = response.json["validation_rules"]
            assert len(validation_rules) == 2
            validation_rule = validation_rules[0]
            assert list(validation_rule.keys()) == ["name"]
            assert validation_rule["name"] == "rule1"

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_node_validation_rules",
            side_effect=TypeError,
        ):
            response = self.client.open(
                NODE_VALIDATION_RULES_URL, method="GET", headers=HEADERS
            )
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )


class TestNodeDependencies(BaseTestCase):
    """Test case for node depencencies endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""

        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_node_dependencies",
            return_value=["dependency1", "dependency2"],
        ) as mock_function:
            response = self.client.open(
                NODE_DEPENDENCIES_URL, method="GET", headers=HEADERS
            )
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

            mock_function.assert_called_once_with("node1", "url1", True, True)

            assert not response.json["hasNext"]
            assert not response.json["hasPrevious"]
            assert response.json["number"] == 0
            assert response.json["size"] == 100
            assert response.json["totalElements"] == 2
            assert response.json["totalPages"] == 1
            dependencies = response.json["nodes"]
            assert len(dependencies) == 2
            dependency = dependencies[0]
            assert list(dependency.keys()) == ["name"]
            assert dependency["name"] == "dependency1"

    def test_return_display_names(self) -> None:
        """Test for returnDisplayNames parameter"""

        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_node_dependencies",
            return_value=["dependency1", "dependency2"],
        ) as mock_function:
            url = f"{NODE_DEPENDENCIES_URL}&returnDisplayNames=true"
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            mock_function.assert_called_with("node1", "url1", True, True)

            url = f"{NODE_DEPENDENCIES_URL}&returnDisplayNames=false"
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            mock_function.assert_called_with("node1", "url1", False, True)

    def test_return_ordered_by_schema(self) -> None:
        """Test for returnOrderedBySchema parameter"""

        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_node_dependencies",
            return_value=["dependency1", "dependency2"],
        ) as mock_function:
            url = f"{NODE_DEPENDENCIES_URL}&returnOrderedBySchema=true"
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            mock_function.assert_called_with("node1", "url1", True, True)

            url = f"{NODE_DEPENDENCIES_URL}&returnOrderedBySchema=false"
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            mock_function.assert_called_with("node1", "url1", True, False)

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_node_dependencies",
            side_effect=TypeError,
        ):
            response = self.client.open(
                NODE_DEPENDENCIES_URL, method="GET", headers=HEADERS
            )
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )


if __name__ == "__main__":
    unittest.main()
