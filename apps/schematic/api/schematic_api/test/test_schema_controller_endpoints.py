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
COMPONENT_PARENTS_URL = "/api/v1/components/component1/parents?schemaUrl=url1"
RELATIONSHIPS_URL = "/api/v1/relationships/type?schemaUrl=url1"


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


class TestComponentParents(BaseTestCase):
    """Test case for component parents endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""

        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_component_parents",
            return_value=["dependency1", "dependency2"],
        ) as mock_function:
            response = self.client.open(
                COMPONENT_PARENTS_URL, method="GET", headers=HEADERS
            )
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

            mock_function.assert_called_once_with("component1", "url1", True, True)

            assert not response.json["hasNext"]
            assert not response.json["hasPrevious"]
            assert response.json["number"] == 0
            assert response.json["size"] == 100
            assert response.json["totalElements"] == 2
            assert response.json["totalPages"] == 1
            dependencies = response.json["components"]
            assert len(dependencies) == 2
            dependency = dependencies[0]
            assert list(dependency.keys()) == ["name"]
            assert dependency["name"] == "dependency1"

    def test_return_display_names(self) -> None:
        """Test for returnDisplayNames parameter"""

        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_component_parents",
            return_value=["dependency1", "dependency2"],
        ) as mock_function:
            url = f"{COMPONENT_PARENTS_URL}&returnDisplayNames=true"
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            mock_function.assert_called_with("component1", "url1", True, True)

            url = f"{COMPONENT_PARENTS_URL}&returnDisplayNames=false"
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            mock_function.assert_called_with("component1", "url1", False, True)

    def test_return_ordered_by_schema(self) -> None:
        """Test for returnOrderedBySchema parameter"""

        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_component_parents",
            return_value=["dependency1", "dependency2"],
        ) as mock_function:
            url = f"{COMPONENT_PARENTS_URL}&returnOrderedBySchema=true"
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            mock_function.assert_called_with("component1", "url1", True, True)

            url = f"{COMPONENT_PARENTS_URL}&returnOrderedBySchema=false"
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            mock_function.assert_called_with("component1", "url1", True, False)

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_component_parents",
            side_effect=TypeError,
        ):
            response = self.client.open(
                COMPONENT_PARENTS_URL, method="GET", headers=HEADERS
            )
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )


class TestListRelationships(BaseTestCase):
    """Tests for relationships endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""

        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_relationships",
            return_value=[["componentA", "componentB"], ["componentB", "componentC"]],
        ) as mock_function:
            response = self.client.open(
                RELATIONSHIPS_URL, method="GET", headers=HEADERS
            )
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )

            mock_function.assert_called_once_with("type", "url1")

            assert not response.json["hasNext"]
            assert not response.json["hasPrevious"]
            assert response.json["number"] == 0
            assert response.json["size"] == 100
            assert response.json["totalElements"] == 2
            assert response.json["totalPages"] == 1
            relationships = response.json["relationships"]
            assert len(relationships) == 2
            relationship = relationships[0]
            assert list(relationship.keys()) == ["component1", "component2"]
            assert relationship["component1"] == "componentA"
            assert relationship["component2"] == "componentB"

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.schema_controller_impl,
            "get_relationships",
            side_effect=TypeError,
        ):
            response = self.client.open(
                RELATIONSHIPS_URL, method="GET", headers=HEADERS
            )
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )


if __name__ == "__main__":
    unittest.main()
