from .resolver_helpers import get_value, request_samples, request_tags


def resolve_samples_by_tag(_obj, info, dataSet=None, related=None, tag=None, feature=None,
                           featureClass=None, name=None, patient=None):
    results = []
    append = results.append
    intersection = set(name).intersection if name else set().intersection
    tag_results = request_tags(_obj, info=info, data_set=dataSet,
                               related=related, tag=tag, feature=feature,
                               feature_class=featureClass, get_samples=True)

    for row in tag_results:
        samples_in_tag = get_value(row, 'samples')
        samples_in_tag = intersection(
            samples_in_tag) if name else samples_in_tag

        if samples_in_tag:
            sample_results = request_samples(
                _obj, info, name=samples_in_tag, patient=patient, by_tag=True)

            if sample_results:
                append({
                    'characteristics': get_value(row, 'characteristics'),
                    'color': get_value(row, 'color'),
                    'display': get_value(row, 'display'),
                    'tag': get_value(row, 'tag'),
                    'samples': [{
                        'name': get_value(sample),
                        'patient': get_value(sample, 'patient', [])
                    } for sample in sample_results]
                })

    return results
