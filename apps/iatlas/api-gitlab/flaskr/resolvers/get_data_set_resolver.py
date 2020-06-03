def resolve_getDataSet(_obj, info, name, group, feature=None):
    return {
        "sampleGroup": name[0],
        "groupName": group[0],
        "groupSize": 42,
        "characteristics": feature[0] if feature is not None else None
    }
