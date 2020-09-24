from itertools import groupby
from .resolver_helpers import (build_sample_graphql_response, get_requested, get_selection_set,
                               get_value, request_samples, sample_request_fields, simple_patient_request_fields, simple_tag_request_fields)


def resolve_samples_by_tag(_obj, info, ageAtDiagnosis=None, dataSet=None, ethnicity=None, feature=None,
                           featureClass=None, gender=None, height=None, name=None, patient=None, race=None, related=None, tag=None, weight=None):
    selection_set = get_selection_set(info.field_nodes[0].selection_set, True)
    tag_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_tag_request_fields.union({'samples'}))

    sample_selection_set = get_selection_set(
        selection_set, 'samples' in tag_requested, 'samples')
    requested = get_requested(
        selection_set=sample_selection_set, requested_field_mapping=sample_request_fields)

    patient_selection_set = get_selection_set(
        sample_selection_set, 'patient' in requested, 'patient')
    patient_requested = get_requested(
        selection_set=patient_selection_set, requested_field_mapping=simple_patient_request_fields)

    sample_results = request_samples(requested, patient_requested, tag_requested, age_at_diagnosis=ageAtDiagnosis, data_set=dataSet, ethnicity=ethnicity, feature=feature,
                                     feature_class=featureClass, gender=gender, height=height, patient=patient, race=race, related=related, sample=name, tag=tag, weight=weight, by_tag=True)

    tag_dict = dict()
    for sample_tag, samples_list in groupby(sample_results, key=lambda s: s.tag):
        tag_dict[sample_tag] = tag_dict.get(
            sample_tag, []) + list(samples_list)

    def build_response(sample_set):
        sample_tag, samples = sample_set
        return {
            'characteristics': get_value(samples[0], 'tag_characteristics'),
            'color': get_value(samples[0], 'tag_color'),
            'display': get_value(samples[0], 'tag_display'),
            'samples': map(build_sample_graphql_response, samples),
            'tag': sample_tag
        }

    return map(build_response, tag_dict.items())
