from sqlalchemy import and_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import Neoantigen, Gene, Patient
from .general_resolvers import build_join_condition, get_selected, get_value
from .gene import build_gene_graphql_response
from .patient import build_patient_graphql_response
from .paging_utils import get_pagination_queries


neoantigen_request_fields = {
    "id",
    "pmhc",
    "freqPmhc",
    "tpm",
    "gene",
    "patient",
}


def build_neoantigen_graphql_response(neoantigen):
    result_dict = {
        'id': get_value(neoantigen, 'id'),
        'pmhc': get_value(neoantigen, 'pmhc'),
        'freqPmhc': get_value(neoantigen, 'freq_pmhc'),
        'tpm': get_value(neoantigen, 'tpm'),
        'gene': build_gene_graphql_response()(neoantigen),
        'patient': build_patient_graphql_response()(neoantigen),
    }
    return(result_dict)


def build_neoantigen_request(
    requested,
    patient_requested,
    gene_requested,
    distinct=False,
    paging=None,
    patient=None,
    entrez=None,
    pmhc=None,
):
    sess = db.session

    neoantigen_1 = aliased(Neoantigen, name='n')
    gene_1 = aliased(Gene, name='g')
    patient_1 = aliased(Patient, name='p')

    core_field_mapping = {
        'id': neoantigen_1.id.label('id'),
        'tpm': neoantigen_1.tpm.label('tpm'),
        'pmhc': neoantigen_1.pmhc.label('pmhc'),
        'freqPmhc': neoantigen_1.freq_pmhc.label('freq_pmhc'),
    }
    gene_core_field_mapping = {
        'entrez': gene_1.entrez_id.label('gene_entrez'),
        'hgnc': gene_1.hgnc_id.label('gene_hgnc'),
    }
    patient_core_field_mapping = {
        'barcode': patient_1.name.label('patient_barcode'),
    }

    core = get_selected(requested, core_field_mapping)
    core |= get_selected(gene_requested, gene_core_field_mapping)
    core |= get_selected(patient_requested, patient_core_field_mapping)

    if distinct == False:
        # Add the id as a cursor if not selecting distinct
        core.add(neoantigen_1.id)

    query = sess.query(*core)
    query = query.select_from(neoantigen_1)

    if pmhc:
        query = query.filter(neoantigen_1.pmhc.in_(pmhc))

    if 'gene' in requested or entrez:
        is_outer = not bool(entrez)
        gene_join_condition = build_join_condition(
            gene_1.id,
            neoantigen_1.neoantigen_gene_id,
            filter_column=gene_1.entrez_id,
            filter_list=entrez
        )
        query = query.join(
            gene_1,
            and_(*gene_join_condition),
            isouter=is_outer
        )

    if 'patient' in requested or patient:
        is_outer = not bool(patient)
        patient_join_condition = build_join_condition(
            patient_1.id,
            neoantigen_1.patient_id,
            filter_column=patient_1.name,
            filter_list=patient
        )
        query = query.join(
            patient_1,
            and_(*patient_join_condition),
            isouter=is_outer
        )

    return get_pagination_queries(query, paging, distinct, cursor_field=neoantigen_1.id)
