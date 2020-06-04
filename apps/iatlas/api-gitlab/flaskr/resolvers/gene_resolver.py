from sqlalchemy import orm
from flaskr.db_models import (Gene, GeneFamily, GeneFunction,
                              ImmuneCheckpoint, NodeType, SuperCategory, TherapyType)
from .resolver_helpers import get_name


def resolve_gene(_obj, info, entrez):
    gene = Gene.query.options(orm.joinedload(
        'gene_family'), orm.joinedload(
        'gene_function'), orm.joinedload(
        'immune_checkpoint'), orm.joinedload(
        'node_type'), orm.joinedload(
        'pathway'), orm.joinedload(
        'super_category'), orm.joinedload(
        'therapy_type')).filter_by(entrez=entrez).first()

    return {
        "id": gene.id,
        "entrez": gene.entrez,
        "hgnc": gene.hgnc,
        "description": gene.description,
        "friendlyName": gene.friendly_name,
        "ioLandscapeName": gene.io_landscape_name,
        "geneFamily": get_name(gene.gene_family),
        "geneFunction": get_name(gene.gene_function),
        "immuneCheckpoint": get_name(gene.immune_checkpoint),
        "nodeType": get_name(gene.node_type),
        "pathway": get_name(gene.pathway),
        "superCategory": get_name(gene.super_category),
        "therapyType": get_name(gene.therapy_type)
    }


def resolve_genes(_obj, info, entrez=None):
    # classes = FeatureClass.query.

    # User.query.filter(User.roles.any(Role.id.in_(
    #     [role.id for role in current_user.roles]))).all()

    query = Gene.query.options(orm.joinedload(
        'gene_family'), orm.joinedload(
        'gene_function'), orm.joinedload(
        'immune_checkpoint'), orm.joinedload(
        'node_type'), orm.joinedload(
        'pathway'), orm.joinedload(
        'super_category'), orm.joinedload(
        'therapy_type'))
    if entrez is not None:
        query = query.filter(Gene.entrez.in_(entrez))
    genes = query.all()

    results = [
        {
            "id": gene.id,
            "entrez": gene.entrez,
            "hgnc": gene.hgnc,
            "description": gene.description,
            "friendlyName": gene.friendly_name,
            "ioLandscapeName": gene.io_landscape_name,
            "geneFamily": get_name(gene.gene_family),
            "geneFunction": get_name(gene.gene_function),
            "immuneCheckpoint": get_name(gene.immune_checkpoint),
            "nodeType": get_name(gene.node_type),
            "pathway": get_name(gene.pathway),
            "superCategory": get_name(gene.super_category),
            "therapyType": get_name(gene.therapy_type)
        } for gene in genes]
    return results
