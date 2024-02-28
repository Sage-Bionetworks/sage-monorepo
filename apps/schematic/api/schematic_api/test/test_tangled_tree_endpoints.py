"""Tests for schema endpoints"""

# pylint: disable=duplicate-code
from __future__ import absolute_import
from unittest.mock import patch

import schematic_api.controllers.tangled_tree_controller_impl
from schematic_api.test import BaseTestCase

HEADERS = {
    "Accept": "application/json",
    "Authorization": "Bearer xxx",
}

TANGLED_TREE_LAYERS_URL = "/api/v1/tangledTreeLayers"
TANGLED_TREE_TEXT_URL = "/api/v1/tangledTreeText"


class TestGetTangledTreeLayers(BaseTestCase):
    """Test case for tangled tree layers endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""

        with patch.object(
            schematic_api.controllers.tangled_tree_controller_impl,
            "get_tangled_tree_layers",
            return_value="xxx",
        ) as mock_function:
            url = f"{TANGLED_TREE_LAYERS_URL}?schemaUrl=url"
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            mock_function.assert_called_once_with("url", "component", "class_label")
            assert response.json == "xxx"

    def test_parameters(self) -> None:
        """Test for successful result"""

        with patch.object(
            schematic_api.controllers.tangled_tree_controller_impl,
            "get_tangled_tree_layers",
            return_value="xxx",
        ) as mock_function:
            url = f"{TANGLED_TREE_LAYERS_URL}?schemaUrl=url2&figureType=dependency"
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            mock_function.assert_called_once_with("url2", "dependency", "class_label")
            assert response.json == "xxx"

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.tangled_tree_controller_impl,
            "get_tangled_tree_layers",
            side_effect=TypeError,
        ):
            url = f"{TANGLED_TREE_LAYERS_URL}?schemaUrl=url"
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )


class TestGetTangledTreeText(BaseTestCase):
    """Test case for tangled tree text endpoint"""

    def test_success(self) -> None:
        """Test for successful result"""

        with patch.object(
            schematic_api.controllers.tangled_tree_controller_impl,
            "get_tangled_tree_text",
            return_value="xxx",
        ) as mock_function:
            url = f"{TANGLED_TREE_TEXT_URL}?schemaUrl=url"
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            mock_function.assert_called_once_with(
                "url", "component", "plain", "class_label"
            )
            assert response.json == "xxx"

    def test_parameters(self) -> None:
        """Test for successful result"""

        with patch.object(
            schematic_api.controllers.tangled_tree_controller_impl,
            "get_tangled_tree_text",
            return_value="xxx",
        ) as mock_function:
            url = (
                f"{TANGLED_TREE_TEXT_URL}"
                "?schemaUrl=url2&figureType=dependency&text_format=highligted"
            )
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert200(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
            mock_function.assert_called_once_with(
                "url2", "dependency", "plain", "class_label"
            )
            assert response.json == "xxx"

    def test_500(self) -> None:
        """Test for 500 result"""
        with patch.object(
            schematic_api.controllers.tangled_tree_controller_impl,
            "get_tangled_tree_text",
            side_effect=TypeError,
        ):
            url = f"{TANGLED_TREE_TEXT_URL}?schemaUrl=url"
            response = self.client.open(url, method="GET", headers=HEADERS)
            self.assert500(
                response, f"Response body is : {response.data.decode('utf-8')}"
            )
