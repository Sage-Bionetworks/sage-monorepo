from flaskr.db_models import FeatureClass


def resolve_dataSet(_obj, info, name, group, feature=None):
    # classes = FeatureClass.query.

    # User.query.filter(User.roles.any(Role.id.in_(
    #     [role.id for role in current_user.roles]))).all()

    # cars = CarsModel.query.all()
    # results = [
    #     {
    #         "name": car.name,
    #         "model": car.model,
    #         "doors": car.doors
    #     } for car in cars]
    return [{
        "sampleGroup": name[0],
        "groupName": group[0],
        "groupSize": 42,
        "characteristics": feature[0] if feature is not None else None
    }]
