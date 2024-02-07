"""Functionality to handle paginated endpoints"""

import math
from typing import TypeVar

ITEM_TYPE = TypeVar("ITEM_TYPE")
TOTAL_ITEMS_MSG = "total_items must be 0 or greater: "
PAGE_MAX_ITEMS_MSG = "page_max_items must be 1 or greater: "
PAGE_NUMBER_MSG = "page_number must be 1 or greater: "


class Page:
    """This represents a page for a generic list of items for a paginated endpoint"""

    def __init__(
        self, items: list[ITEM_TYPE], page_number: int = 1, page_max_items: int = 100000
    ) -> None:
        """
        Args:
            items (list[ITEM_TYPE]): A list of all items in the query
            page_number (int, optional): The page number the current request is for. Defaults to 1.
            page_max_items (int, optional): The maximum number of items per page. Defaults to 100000.
        """
        self.page_number = page_number
        self.page_max_items = page_max_items
        self.total_items = len(items)
        self.total_pages = get_page_amount(self.total_items, page_max_items)
        self.has_next = page_number < self.total_pages
        self.has_previous = page_number > 1
        self.items: list[ITEM_TYPE] = get_item_slice(items, page_max_items, page_number)


def get_page_amount(total_items: int, page_max_items: int) -> int:
    """Getes the amount of pages total based on the number of items and page size

    Args:
        total_items (int): The total number of items in the query
        page_max_items (int): The maximum number of items per page

    Raises:
        ValueError: total_items is less than 0
        ValueError: page_max_items is less than

    Returns:
        int: The amount of pages
    """
    if total_items < 0:
        raise ValueError(TOTAL_ITEMS_MSG, total_items)
    if page_max_items < 1:
        raise ValueError(PAGE_MAX_ITEMS_MSG, page_max_items)
    return math.ceil(total_items / page_max_items)


def get_item_slice(
    items: list[ITEM_TYPE], page_max_items: int, page_number: int
) -> list[ITEM_TYPE]:
    """Gets a list slice based on the paging parameters

    Args:
        items (list[ITEM_TYPE]): A list of items to be sliced
        page_max_items (int): The maximum number of items per page
        page_number (int): The page number the current request is for

    Returns:
        list[ITEM_TYPE]: The slice of items
    """
    page_indeces = get_page_indeces(len(items), page_max_items, page_number)
    return items[page_indeces[0] : page_indeces[1]]


def get_page_indeces(
    total_items: int, page_max_items: int, page_number: int
) -> tuple[int, int]:
    """Gets the indces used to slice the list of items

    Args:
        total_items (int): The total number of items in the query
        page_max_items (int): The maximum number of items per page
        page_number (int): The page number the current request is for

    Raises:
        ValueError: total_items is less than 0
        ValueError: page_max_items is less than 1
        ValueError: page_number is less than 1

    Returns:
        tuple[int, int]: The two indeces to slice the list of items with
    """
    if total_items < 0:
        raise ValueError(TOTAL_ITEMS_MSG, total_items)
    if page_max_items < 1:
        raise ValueError(PAGE_MAX_ITEMS_MSG, page_max_items)
    if page_number < 1:
        raise ValueError(PAGE_NUMBER_MSG, page_number)
    index1 = (page_number - 1) * page_max_items
    index2 = min(index1 + page_max_items, total_items)
    return (index1, index2)
