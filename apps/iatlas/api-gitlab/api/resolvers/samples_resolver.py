from .resolver_helpers import get_value, request_samples


def resolve_samples(_obj, info, name=None, patient=None):
    samples = request_samples(_obj, info, name=name, patient=patient)

    return [{
        'name': get_value(sample, 'name'),
        'patient': get_value(sample, 'patient')
    } for sample in samples]
