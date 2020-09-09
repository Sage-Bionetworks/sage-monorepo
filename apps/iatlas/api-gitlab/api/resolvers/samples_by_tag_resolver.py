from itertools import groupby
from .resolver_helpers import build_sample_graphql_response, get_value, request_samples


def resolve_samples_by_tag(_obj, info, dataSet=None, feature=None, featureClass=None,
                           name=None, patient=None, related=None, tag=None):
    sample_results = request_samples(_obj, info, data_set=dataSet, feature=feature, feature_class=featureClass,
                                     patient=patient, related=related, sample=name, tag=tag, by_tag=True)

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
            'samples': list(map(build_sample_graphql_response, samples)),
            'tag': sample_tag
        }

    return map(build_response, tag_dict.items())
