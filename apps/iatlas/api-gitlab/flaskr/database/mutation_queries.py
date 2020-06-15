from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import Mutation, MutationCode, MutationType, Sample
from .database_helpers import accepted_simple_table_query_args, build_general_query

accepted_mutation_option_args=['gene', 
                'mutation_code', 
                'mutation_type']

accepted_mutation_query_args = [
    'gene_id',
    'mutation_code_id',
    'mutation_type_id'
]
 
def return_mutation_query(*args):
    return build_general_query(
        Mutation, args=args,
        accepted_option_args=accepted_mutation_option_args,
        accepted_query_args=accepted_mutation_query_args)

def return_mutation_code_query():
    return build_general_query(
        MutationCode, args=args,
        accepted_query_args=accepted_mutation_query_args
    )

def return_mutation_type_query():
    return build_general_query(
        MutationType, args=args,
        accepted_query_args=accepted_mutation_query_args
    )
