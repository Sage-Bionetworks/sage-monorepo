from sqlalchemy import and_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import (
    Feature,
    FeatureToSample,
    Sample,
    Cohort,
    CohortToSample,
    CohortToFeature,
    SingleCellPseudobulkFeature,
    CellToFeature,
    CellToSample,
    Cell,
)
from .sample import build_sample_graphql_response
from .general_resolvers import build_join_condition, get_selected, get_value
from .paging_utils import get_pagination_queries
from decimal import Decimal

feature_class_request_fields = {"name"}

simple_feature_request_fields = {
    "display",
    "name",
    "order",
    "unit",
    "germlineModule",
    "germlineCategory",
}

simple_feature_request_fields2 = {"display", "name", "order", "class"}

cell_feature_request_fields = simple_feature_request_fields.union({"value"})

cell_related_feature_request_fields = {"name", "value"}

feature_request_fields = simple_feature_request_fields.union(
    {
        "class",
        "methodTag",
        "samples",
        "pseudoBulkSamples",
        "cells",
        "valueMax",
        "valueMin",
    }
)


def build_feature_graphql_response(
    requested=[],
    sample_requested=[],
    pseudobulk_sample_requested=[],
    cell_requested=[],
    max_value=None,
    min_value=None,
    cohort=None,
    sample=None,
    prefix="feature_",
):

    def f(feature):

        from .cell import build_cell_graphql_response

        if not feature:
            return None
        id = get_value(feature, prefix + "id")
        samples = get_samples(
            [id],
            requested=requested,
            sample_requested=sample_requested,
            max_value=max_value,
            min_value=min_value,
            cohort=cohort,
            sample=sample,
        )
        pseudobulk_samples = get_pseudobulk_samples(
            [id],
            requested=requested,
            sample_requested=pseudobulk_sample_requested,
            cohort=cohort,
            sample=sample,
        )
        cells = get_cells(
            [id], requested=requested, cell_requested=cell_requested, cohort=None
        )
        if "valueMax" in requested or "valueMin" in requested:
            values = [get_value(sample, "sample_feature_value") for sample in samples]
        value_min = min(values) if "valueMin" in requested else None
        value_max = max(values) if "valueMax" in requested else None
        result = {
            "id": id,
            "class": get_value(feature, prefix + "class"),
            "display": get_value(feature, prefix + "display"),
            "methodTag": get_value(feature, prefix + "method_tag"),
            "name": get_value(feature, prefix + "name"),
            "order": get_value(feature, prefix + "order"),
            "germlineModule": get_value(feature, prefix + "germline_module"),
            "germlineCategory": get_value(feature, prefix + "germline_category"),
            "unit": get_value(feature, prefix + "unit"),
            "samples": map(build_sample_graphql_response(), samples),
            "pseudoBulkSamples": map(
                build_sample_graphql_response(), pseudobulk_samples
            ),
            "cells": map(build_cell_graphql_response(), cells),
            "valueMin": value_min if type(value_min) is Decimal else None,
            "valueMax": value_max if type(value_max) is Decimal else None,
            "value": get_value(feature, prefix + "value"),
        }

        return result

    return f


def build_features_query(
    requested,
    distinct=False,
    paging=None,
    feature=None,
    feature_class=None,
    max_value=None,
    min_value=None,
    sample=None,
    cohort=None,
):
    """
    Builds a SQL request.
    """
    sess = db.session

    has_min_max = "valueMax" in requested or "valueMin" in requested

    feature_1 = aliased(Feature, name="f")
    feature_to_sample_1 = aliased(FeatureToSample, name="fts")
    sample_1 = aliased(Sample, name="s")
    cohort_1 = aliased(Cohort, name="c")
    cohort_to_feature_1 = aliased(CohortToFeature, name="ctf")
    pseudobulk_feature_1 = aliased(SingleCellPseudobulkFeature, name="scpf")
    cohort_to_sample_1 = aliased(CohortToSample, name="cts")
    cell_to_feature_1 = aliased(CellToFeature, name="celltofeature")
    cell_to_sample_1 = aliased(CellToSample, name="celltosample")
    cell_1 = aliased(Cell, name="cell")

    core_field_mapping = {
        "id": feature_1.id.label("feature_id"),
        "class": feature_1.feature_class.label("feature_class"),
        "display": feature_1.display.label("feature_display"),
        "methodTag": feature_1.method_tag.label("feature_method_tag"),
        "name": feature_1.name.label("feature_name"),
        "order": feature_1.order.label("feature_order"),
        "germlineModule": feature_1.germline_module.label("feature_germline_module"),
        "germlineCategory": feature_1.germline_category.label(
            "feature_germline_category"
        ),
        "unit": feature_1.unit.label("feature_unit"),
    }

    core = get_selected(requested, core_field_mapping)
    core |= {feature_1.id.label("feature_id")}

    query = sess.query(*core)
    query = query.select_from(feature_1)

    if feature:
        query = query.filter(feature_1.name.in_(feature))

    if feature_class:
        query = query.filter(feature_1.feature_class.in_(feature_class))

    if has_min_max or sample:
        feature_to_sample_subquery = sess.query(feature_to_sample_1.feature_id)

        if max_value:
            feature_to_sample_subquery = feature_to_sample_subquery.filter(
                feature_to_sample_1.feature_to_sample_value <= max_value
            )

        if min_value:
            feature_to_sample_subquery = feature_to_sample_subquery.filter(
                feature_to_sample_1.feature_to_sample_value >= min_value
            )

        if sample:

            sample_join_condition = build_join_condition(
                feature_to_sample_1.sample_id,
                sample_1.id,
                filter_column=sample_1.name,
                filter_list=sample,
            )
            cohort_subquery = feature_to_sample_subquery.join(
                sample_1, and_(*sample_join_condition), isouter=False
            )

            feature_to_sample_subquery = feature_to_sample_subquery.filter(
                sample_1.name.in_(sample)
            )

        query = query.filter(feature_1.id.in_(feature_to_sample_subquery))

    if cohort:

        cohort_subquery1 = sess.query(pseudobulk_feature_1.feature_id)

        cohort_to_sample_join_condition1 = build_join_condition(
            pseudobulk_feature_1.sample_id, cohort_to_sample_1.sample_id
        )
        cohort_subquery1 = cohort_subquery1.join(
            cohort_to_sample_1, and_(*cohort_to_sample_join_condition1), isouter=False
        )

        cohort_join_condition1 = build_join_condition(
            cohort_to_sample_1.cohort_id,
            cohort_1.id,
            filter_column=cohort_1.name,
            filter_list=cohort,
        )
        cohort_subquery1 = cohort_subquery1.join(
            cohort_1, and_(*cohort_join_condition1), isouter=False
        )

        cohort_subquery2 = sess.query(cohort_to_feature_1.feature_id)

        cohort_join_condition2 = build_join_condition(
            cohort_to_feature_1.cohort_id,
            cohort_1.id,
            filter_column=cohort_1.name,
            filter_list=cohort,
        )
        cohort_subquery2 = cohort_subquery2.join(
            cohort_1, and_(*cohort_join_condition2), isouter=False
        )

        union_subquery = cohort_subquery1.union(cohort_subquery2)

        query = query.filter(feature_1.id.in_(union_subquery))

    return get_pagination_queries(query, paging, distinct, cursor_field=feature_1.id)


def get_samples(
    feature_id,
    requested,
    sample_requested,
    max_value=None,
    min_value=None,
    cohort=None,
    sample=None,
):
    has_samples = "samples" in requested
    has_max_min = "valueMax" in requested or "valueMin" in requested

    if has_samples or has_max_min:

        sess = db.session

        feature_to_sample_1 = aliased(FeatureToSample, name="fts")
        sample_1 = aliased(Sample, name="s")
        cohort_1 = aliased(Cohort, name="c")
        cohort_to_sample_1 = aliased(CohortToSample, name="cts")

        sample_core_field_mapping = {"name": sample_1.name.label("sample_name")}

        sample_core = get_selected(sample_requested, sample_core_field_mapping)
        # Always select the sample id and the feature id.
        sample_core |= {
            sample_1.id.label("id"),
            feature_to_sample_1.feature_id.label("feature_id"),
        }

        if has_max_min or "value" in sample_requested:
            sample_core |= {
                feature_to_sample_1.feature_to_sample_value.label(
                    "sample_feature_value"
                )
            }

        sample_query = sess.query(*sample_core)
        sample_query = sample_query.select_from(sample_1)

        if sample:
            sample_query = sample_query.filter(sample_1.name.in_(sample))

        feature_sample_join_condition = build_join_condition(
            feature_to_sample_1.sample_id,
            sample_1.id,
            feature_to_sample_1.feature_id,
            feature_id,
        )

        if max_value:
            feature_sample_join_condition.append(
                feature_to_sample_1.feature_to_sample_value <= max_value
            )

        if min_value:
            feature_sample_join_condition.append(
                feature_to_sample_1.feature_to_sample_value >= min_value
            )

        sample_query = sample_query.join(
            feature_to_sample_1, and_(*feature_sample_join_condition)
        )

        if cohort:
            cohort_subquery = sess.query(cohort_to_sample_1.sample_id)

            cohort_join_condition = build_join_condition(
                cohort_to_sample_1.cohort_id,
                cohort_1.id,
                filter_column=cohort_1.name,
                filter_list=cohort,
            )
            cohort_subquery = cohort_subquery.join(
                cohort_1, and_(*cohort_join_condition), isouter=False
            )

            sample_query = sample_query.filter(sample_1.id.in_(cohort_subquery))

        samples = sample_query.distinct().all()
        return samples

    return []


def get_pseudobulk_samples(
    feature_id, requested, sample_requested, cohort=None, sample=None
):

    if "pseudoBulkSamples" not in requested:
        return []

    sess = db.session

    sample_1 = aliased(Sample, name="s")
    cohort_1 = aliased(Cohort, name="c")
    cohort_to_sample_1 = aliased(CohortToSample, name="cts")
    pseudobulk_feature_1 = aliased(SingleCellPseudobulkFeature, name="scpf")

    sample_core_field_mapping = {"name": sample_1.name.label("sample_name")}

    sample_core = get_selected(sample_requested, sample_core_field_mapping)

    sample_core |= {
        sample_1.id.label("sample_id"),
        pseudobulk_feature_1.feature_id.label("sample_feature_id"),
    }

    if "value" in sample_requested:
        sample_core |= {pseudobulk_feature_1.value.label("sample_feature_value")}

    if "cellType" in sample_requested:
        sample_core |= {pseudobulk_feature_1.cell_type.label("sample_cell_type")}

    query = sess.query(*sample_core)
    query = query.select_from(sample_1)

    sample_join_condition = build_join_condition(
        pseudobulk_feature_1.sample_id,
        sample_1.id,
        pseudobulk_feature_1.feature_id,
        feature_id,
    )
    query = query.join(pseudobulk_feature_1, and_(*sample_join_condition))

    if sample:
        query = query.filter(sample_1.name.in_(sample))

    if cohort:
        cohort_subquery = sess.query(pseudobulk_feature_1.feature_id)

        cohort_to_sample_join_condition = build_join_condition(
            pseudobulk_feature_1.sample_id, cohort_to_sample_1.sample_id
        )
        cohort_subquery = cohort_subquery.join(
            cohort_to_sample_1, and_(*cohort_to_sample_join_condition), isouter=False
        )

        cohort_join_condition = build_join_condition(
            cohort_to_sample_1.cohort_id,
            cohort_1.id,
            filter_column=cohort_1.name,
            filter_list=cohort,
        )
        cohort_subquery = cohort_subquery.join(
            cohort_1, and_(*cohort_join_condition), isouter=False
        )

        query = query.filter(pseudobulk_feature_1.feature_id.in_(cohort_subquery))

    samples = query.distinct().all()
    return samples


def get_cells(feature_id, requested, cell_requested, cohort=None):

    if "cells" not in requested:
        return []

    sess = db.session

    cell_1 = aliased(Cell, name="c")
    cell_to_feature_1 = aliased(CellToFeature, name="ctf")
    cohort_1 = aliased(Cohort, name="c")
    cohort_to_sample_1 = aliased(CohortToSample, name="cts")
    cell_to_sample_1 = aliased(CellToSample, name="celltosample")
    cell_to_feature_1 = aliased(CellToFeature, name="celltofeature")
    cell_1 = aliased(Cell, name="cell")

    cell_core_field_mapping = {"name": cell_1.name.label("cell_name")}

    cell_core = get_selected(cell_requested, cell_core_field_mapping)

    cell_core |= {cell_1.id.label("cell_id")}

    if "value" in cell_requested:
        cell_core |= {cell_to_feature_1.feature_value.label("cell_feature_value")}

    if "type" in cell_requested:
        cell_core |= {cell_1.cell_type.label("cell_type")}

    query = sess.query(*cell_core)
    query = query.select_from(cell_1)

    cell_to_feature_join_condition = build_join_condition(
        cell_1.id, cell_to_feature_1.cell_id, cell_to_feature_1.feature_id, feature_id
    )
    query = query.join(cell_to_feature_1, and_(*cell_to_feature_join_condition))

    if cohort:

        cohort_id_subquery = sess.query(cohort_1.id)
        cohort_id_subquery = cohort_id_subquery.filter(cohort_1.name.in_(cohort))

        sample_id_subquery = sess.query(cohort_to_sample_1.sample_id)
        sample_id_subquery = sample_id_subquery.filter(
            cohort_to_sample_1.cohort_id.in_(cohort_id_subquery)
        )

        cell_id_subquery = sess.query(cell_to_sample_1.cell_id)
        cell_id_subquery = cell_id_subquery.filter(
            cell_to_sample_1.sample_id.in_(sample_id_subquery)
        )

        feature_id_subquery = sess.query(cell_to_feature_1.feature_id)
        feature_id_subquery = feature_id_subquery.filter(
            cell_to_feature_1.cell_id.in_(cell_id_subquery)
        )

        query = query.filter(feature_1.id.in_(feature_id_subquery))

    cells = query.distinct().all()
    return cells
