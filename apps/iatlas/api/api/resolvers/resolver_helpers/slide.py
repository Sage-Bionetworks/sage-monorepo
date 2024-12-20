from sqlalchemy import and_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import Patient, Sample, Slide
from .general_resolvers import build_join_condition, get_selected, get_value
from .paging_utils import get_pagination_queries


simple_slide_request_fields = {"description", "name"}

slide_request_fields = simple_slide_request_fields.union({"patient"})


def build_slide_graphql_response(prefix="slide_"):
    from .patient import build_patient_graphql_response

    def f(slide):
        if not slide:
            return None
        else:
            dict = {
                "id": get_value(slide, prefix + "id"),
                "description": get_value(slide, prefix + "description"),
                "name": get_value(slide, prefix + "name"),
                "patient": build_patient_graphql_response()(slide),
            }
            return dict

    return f


def build_slide_request(
    requested,
    patient_requested,
    max_age_at_diagnosis=None,
    min_age_at_diagnosis=None,
    barcode=None,
    ethnicity=None,
    gender=None,
    max_height=None,
    min_height=None,
    name=None,
    race=None,
    max_weight=None,
    min_weight=None,
    sample=None,
    distinct=False,
    paging=None,
):
    """
    Builds a SQL query.
    """
    sess = db.session

    has_patient_filters = bool(
        barcode
        or max_age_at_diagnosis
        or min_age_at_diagnosis
        or ethnicity
        or gender
        or max_height
        or min_height
        or race
        or max_weight
        or min_weight
    )

    patient_1 = aliased(Patient, name="p")
    sample_1 = aliased(Sample, name="s")
    slide_1 = aliased(Slide, name="sd")

    core_field_mapping = {
        "description": slide_1.description.label("slide_description"),
        "name": slide_1.name.label("slide_name"),
    }
    patient_core_field_mapping = {
        "ageAtDiagnosis": patient_1.age_at_diagnosis.label("patient_age_at_diagnosis"),
        "barcode": patient_1.name.label("patient_barcode"),
        "ethnicity": patient_1.ethnicity.label("patient_ethnicity"),
        "gender": patient_1.gender.label("patient_gender"),
        "height": patient_1.height.label("patient_height"),
        "race": patient_1.race.label("patient_race"),
        "weight": patient_1.weight.label("patient_weight"),
    }

    # Only select fields that were requested.
    core = get_selected(requested, core_field_mapping)
    core |= {slide_1.id.label("slide_id")}

    patient_core = get_selected(patient_requested, patient_core_field_mapping)

    query = sess.query(*[*core, *patient_core])
    query = query.select_from(slide_1)

    if name:
        query = query.filter(slide_1.name.in_(name))

    if has_patient_filters or "patient" in requested:
        is_outer = not has_patient_filters

        patient_join_condition = build_join_condition(
            slide_1.patient_id, patient_1.id, patient_1.name, barcode
        )

        if bool(max_age_at_diagnosis):
            patient_join_condition.append(
                patient_1.age_at_diagnosis <= max_age_at_diagnosis
            )

        if bool(min_age_at_diagnosis):
            patient_join_condition.append(
                patient_1.age_at_diagnosis >= min_age_at_diagnosis
            )

        if bool(ethnicity):
            patient_join_condition.append(patient_1.ethnicity.in_(ethnicity))

        if bool(gender):
            patient_join_condition.append(patient_1.gender.in_(gender))

        if bool(max_height):
            patient_join_condition.append(patient_1.height <= max_height)

        if bool(min_height):
            patient_join_condition.append(patient_1.height >= min_height)

        if bool(race):
            patient_join_condition.append(patient_1.race.in_(race))

        if bool(max_weight):
            patient_join_condition.append(patient_1.weight <= max_weight)

        if bool(min_weight):
            patient_join_condition.append(patient_1.weight >= min_weight)

        query = query.join(patient_1, and_(*patient_join_condition), isouter=is_outer)

    if sample:
        sample_join_condition = build_join_condition(
            slide_1.patient_id,
            sample_1.patient_id,
            filter_column=sample_1.name,
            filter_list=sample,
        )
        query = query.join(sample_1, and_(*sample_join_condition), isouter=False)

    order = []
    append_to_order = order.append
    if "name" in requested:
        append_to_order(slide_1.name)
    if "description" in requested:
        append_to_order(slide_1.description)

    query = query.order_by(*order) if order else query

    return get_pagination_queries(query, paging, distinct, cursor_field=slide_1.id)
