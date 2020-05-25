import graphene
from graphene import relay
from graphene_sqlalchemy import SQLAlchemyConnectionField, SQLAlchemyObjectType
from app.models import Department as DepartmentModel
from app.models import Employee as EmployeeModel
from app.models import Role as RoleModel


class Department(SQLAlchemyObjectType):
    class Meta:
        model = DepartmentModel
        interfaces = (relay.Node, )


class Employee(SQLAlchemyObjectType):
    class Meta:
        model = EmployeeModel
        interfaces = (relay.Node, )


class Role(SQLAlchemyObjectType):
    class Meta:
        model = RoleModel
        interfaces = (relay.Node, )


class Query(graphene.ObjectType):
    node = relay.Node.Field()
    # Allow only single column sorting
    all_employees = SQLAlchemyConnectionField(
        Employee.connection, sort=Employee.sort_argument())
    # Allows sorting over multiple columns, by default over the primary key
    all_roles = SQLAlchemyConnectionField(Role.connection)
    # Disable sorting over this field
    all_departments = SQLAlchemyConnectionField(Department.connection, sort=None)


schema = graphene.Schema(query=Query)
