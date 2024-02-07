"""Testing for pagination utilities"""

import pytest

from schematic_api.controllers.paging import (
    get_item_slice,
    get_page_amount,
    get_page_indeces,
    Page,
)


class TestPage:
    """Tests for Page class"""

    def test_page1(self) -> None:
        """Tests for Page class"""
        items = list(range(1, 22))
        page = Page(items, page_max_items=5, page_number=1)
        assert page.page_number == 1
        assert page.page_max_items == 5
        assert page.total_items == 21
        assert page.total_pages == 5
        assert page.has_next
        assert not page.has_previous
        assert page.items == [1, 2, 3, 4, 5]

    def test_page2(self) -> None:
        """Tests for Page class"""
        items = list(range(1, 22))
        page = Page(items, page_max_items=5, page_number=2)
        assert page.page_number == 2
        assert page.page_max_items == 5
        assert page.total_items == 21
        assert page.total_pages == 5
        assert page.has_next
        assert page.has_previous
        assert page.items == [6, 7, 8, 9, 10]

    def test_page3(self) -> None:
        """Tests for Page class"""
        items = list(range(1, 22))
        page = Page(items, page_max_items=5, page_number=5)
        assert page.page_number == 5
        assert page.page_max_items == 5
        assert page.total_items == 21
        assert page.total_pages == 5
        assert not page.has_next
        assert page.has_previous
        assert page.items == [21]


class TestPagingUtils:
    """Tests for various paging utils"""

    def test_get_page_amount(self) -> None:
        """Tests for get_page_amount"""
        assert get_page_amount(1, 1) == 1
        assert get_page_amount(2, 1) == 2
        assert get_page_amount(11, 10) == 2
        assert get_page_amount(0, 1) == 0
        with pytest.raises(
            ValueError, match="('total_items must be 0 or greater: ', -1)"
        ):
            get_page_amount(-1, 1)
        with pytest.raises(
            ValueError, match="('page_max_items must be 1 or greater: ', 0)"
        ):
            get_page_amount(0, 0)

    def test_get_item_slice(self) -> None:
        """Tests for get_item_slice"""
        lst = list(range(1, 8))
        assert get_item_slice(items=lst, page_max_items=1, page_number=1) == [1]
        assert get_item_slice(items=lst, page_max_items=1, page_number=2) == [2]
        assert get_item_slice(items=lst, page_max_items=1, page_number=7) == [7]
        assert get_item_slice(items=lst, page_max_items=1, page_number=8) == []
        assert get_item_slice(items=lst, page_max_items=1, page_number=2) == [2]
        assert get_item_slice(items=lst, page_max_items=3, page_number=1) == [1, 2, 3]
        assert get_item_slice(items=lst, page_max_items=3, page_number=2) == [4, 5, 6]
        assert get_item_slice(items=lst, page_max_items=3, page_number=3) == [7]

    def test_get_page_indeces(self) -> None:
        """Tests for get_page_indeces"""
        assert get_page_indeces(total_items=21, page_max_items=10, page_number=1) == (
            0,
            10,
        )
        assert get_page_indeces(total_items=21, page_max_items=10, page_number=2) == (
            10,
            20,
        )
        assert get_page_indeces(total_items=21, page_max_items=10, page_number=3) == (
            20,
            21,
        )
        with pytest.raises(
            ValueError, match="('total_items must be 0 or greater: ', -1)"
        ):
            get_page_indeces(-1, 1, 1)
        with pytest.raises(
            ValueError, match="('page_max_items must be 1 or greater: ', 0)"
        ):
            get_page_indeces(0, 0, 1)
        with pytest.raises(
            ValueError, match="('page_number must be 1 or greater: ', 0)"
        ):
            get_page_indeces(0, 1, 0)
