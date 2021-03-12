from itertools import groupby
from .resolver_helpers import (build_sample_graphql_response, get_requested, get_selection_set,
                               get_value, request_samples, sample_request_fields, simple_patient_request_fields, simple_tag_request_fields)


def resolve_samples_by_tag(_obj, info, maxAgeAtDiagnosis=None, minAgeAtDiagnosis=None, dataSet=None, ethnicity=None, feature=None,
                           featureClass=None, gender=None, maxHeight=None, minHeight=None, name=None, patient=None, race=None, related=None, tag=None, maxWeight=None, minWeight=None):
    tag_requested = get_requested(
        info, simple_tag_request_fields.union({'samples'}))

    sample_selection_set = get_selection_set(info=info, child_node='samples')
    requested = get_requested(
        selection_set=sample_selection_set, requested_field_mapping=sample_request_fields)

    patient_requested = get_requested(
        selection_set=sample_selection_set, requested_field_mapping=simple_patient_request_fields, child_node='patient')

    sample_results = request_samples(requested, patient_requested, tag_requested, max_age_at_diagnosis=maxAgeAtDiagnosis, min_age_at_diagnosis=minAgeAtDiagnosis, data_set=dataSet, ethnicity=ethnicity, feature=feature,
                                     feature_class=featureClass, gender=gender, max_height=maxHeight, min_height=minHeight, patient=patient, race=race, related=related, sample=name, tag=tag, max_weight=maxWeight, min_weight=minWeight, by_tag=True)

    tag_dict = dict()
    for sample_tag, samples_list in groupby(sample_results, key=lambda s: s.tag):
        tag_dict[sample_tag] = tag_dict.get(
            sample_tag, []) + list(samples_list)

    def build_response(sample_set):
        sample_tag, samples = sample_set
        return {
            'characteristics': get_value(samples[0], 'characteristics'),
            'color': get_value(samples[0], 'color'),
            'longDisplay': get_value(samples[0], 'tag_long_display'),
            'samples': map(build_sample_graphql_response, samples),
            'shortDisplay': get_value(samples[0], 'tag_short_display'),
            'tag': sample_tag
        }

    return map(build_response, tag_dict.items())
