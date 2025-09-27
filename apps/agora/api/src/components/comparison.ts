// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { NextFunction, Request, Response } from 'express';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import {
  BioDomains,
  ProteinDifferentialExpression,
  RnaDifferentialExpression,
  TargetNomination,
  Team,
} from '@sagebionetworks/agora/api-client';
import { Scores } from '@sagebionetworks/agora/models';
import { altCache, setHeaders } from '../helpers';
import {
  GCTGene,
  GCTGeneNominations,
  Gene,
  ProteomicsLFQCollection,
  ProteomicsSRMCollection,
  ProteomicsTMTCollection,
  RnaDifferentialExpressionCollection,
} from '../models';
import { getAllGeneBioDomains, getAllScores, getGenesMap, getTeams } from './';
// -------------------------------------------------------------------------- //
// Functions
// -------------------------------------------------------------------------- //

function getComparisonGeneAssociations(gene: Gene) {
  const data: number[] = [];

  // Genetically Associated with LOAD
  if (gene.is_igap) {
    data.push(1);
  }

  // eQTL in Brain
  if (gene.is_eqtl) {
    data.push(2);
  }

  // RNA Expression Changed in AD Brain
  if (gene.rna_brain_change_studied && gene.is_any_rna_changed_in_ad_brain) {
    data.push(3);
  }

  // Protein Expression Changed in AD Brain
  if (gene.protein_brain_change_studied && gene.is_any_protein_changed_in_ad_brain) {
    data.push(4);
  }

  return data;
}

function getComparisonGeneNominations(gene: Gene, teams: Team[]) {
  const data: GCTGeneNominations = {
    count: gene.total_nominations || 0,
    year: 0,
    teams: [],
    studies: [],
    inputs: [],
    programs: [],
    validations: [],
  };

  gene.target_nominations?.forEach((n: TargetNomination) => {
    // Year
    if (n.initial_nomination && (!data.year || n.initial_nomination < data.year)) {
      data.year = n.initial_nomination;
    }

    // Team / Programs
    if (n.team) {
      const team = teams.find((item: Team) => item.team === n.team);

      if (team && !data.programs.includes(team.program)) {
        data.programs.push(team.program);
      }

      data.teams.push(n.team);
    }

    // Studies
    if (n.study) {
      n.study.split(', ').forEach((item: string) => {
        if (!data.studies.includes(item)) {
          data.studies.push(item);
        }
      });
    }

    // Inputs
    if (n.input_data) {
      n.input_data.split(', ').forEach((item: string) => {
        if (!data.inputs.includes(item)) {
          data.inputs.push(item);
        }
      });
    }

    // Validations
    if (n.validation_study_details) {
      const validation_study_detail_clean = n.validation_study_details.trim().toLowerCase();
      if (!data.validations.includes(validation_study_detail_clean)) {
        data.validations.push(validation_study_detail_clean);
      }
    }
  });

  return data;
}

function getComparisonGene(
  gene: Gene,
  teams: Team[],
  scores: Scores[],
  allBiodomains: BioDomains[] | undefined,
) {
  const geneScores = scores.find((score) => score.ensembl_gene_id == gene.ensembl_gene_id);
  const geneBiodomains = allBiodomains
    ?.find((b) => b.ensembl_gene_id === gene.ensembl_gene_id)
    ?.gene_biodomains.map((b) => b.biodomain);

  const data: GCTGene = {
    ensembl_gene_id: gene.ensembl_gene_id || '',
    hgnc_symbol: gene.hgnc_symbol || '',
    tissues: [],
    nominations: getComparisonGeneNominations(gene, teams),
    associations: getComparisonGeneAssociations(gene),
    target_risk_score: geneScores ? geneScores.target_risk_score : null,
    genetics_score: geneScores ? geneScores.genetics_score : null,
    multi_omics_score: geneScores ? geneScores.multi_omics_score : null,
    biodomains: geneBiodomains,
    target_enabling_resources: getTargetEnablingResources(gene),
  };

  return data;
}

export function getTargetEnablingResources(gene: Gene) {
  const resources: string[] = [];
  if (gene.is_adi) resources.push('AD Informer Set');
  if (gene.is_tep) resources.push('Target Enabling Package');
  return resources;
}

export async function getRnaComparisonGenes(model: string) {
  const cacheKey = 'rna-comparison-' + model.replace(/[^a-z0-9]/gi, '');

  let result: GCTGene[] | undefined = altCache.get(cacheKey);

  if (result) {
    return result;
  }

  const differentialExpression: RnaDifferentialExpression[] =
    await RnaDifferentialExpressionCollection.find({
      model: model,
    })
      .lean()
      .sort({ hgnc_symbol: 1, tissue: 1 })
      .exec();

  if (differentialExpression) {
    const genes: { [key: string]: GCTGene } = {};
    const allGenes = await getGenesMap();
    const teams = await getTeams();
    const scores = await getAllScores();
    const allBiodomains = await getAllGeneBioDomains();

    differentialExpression.forEach((exp: RnaDifferentialExpression) => {
      if (!genes[exp.ensembl_gene_id]) {
        const gene: Gene =
          allGenes.get(exp.ensembl_gene_id) ||
          ({
            ensembl_gene_id: exp.ensembl_gene_id || '',
            hgnc_symbol: exp.hgnc_symbol || '',
          } as Gene);
        genes[exp.ensembl_gene_id] = getComparisonGene(gene, teams, scores, allBiodomains);
      }

      genes[exp.ensembl_gene_id].tissues.push({
        name: exp.tissue,
        logfc: exp.logfc,
        adj_p_val: exp.adj_p_val,
        ci_l: exp.ci_l,
        ci_r: exp.ci_r,
      });
    });

    result = Object.values(genes);
  }

  altCache.set(cacheKey, result);
  return result;
}

export async function getProteinComparisonGenes(method: string) {
  const cacheKey = 'rna-comparison-' + method.replace(/[^a-z0-9]/gi, '');

  let result = altCache.get(cacheKey);

  if (result?.length) {
    return result;
  }

  let items: ProteinDifferentialExpression[] = [];

  if ('TMT' === method) {
    items = await ProteomicsTMTCollection.find().lean().sort({ hgnc_symbol: 1, tissue: 1 }).exec();
  } else if ('LFQ' === method) {
    items = await ProteomicsLFQCollection.find().lean().sort({ hgnc_symbol: 1, tissue: 1 }).exec();
  } else if ('SRM' === method) {
    items = await ProteomicsSRMCollection.find().lean().sort({ hgnc_symbol: 1, tissue: 1 }).exec();
  } else {
    // TODO capture corner scenarios
    throw 'unknown method selected: ' + method;
  }

  if (items) {
    const genes: { [key: string]: GCTGene } = {};
    const allGenes = await getGenesMap();
    const teams = await getTeams();
    const scores = await getAllScores();
    const allBiodomains = await getAllGeneBioDomains();

    items.forEach((item: ProteinDifferentialExpression) => {
      if (!genes[item.uniqid]) {
        const gene: Gene =
          allGenes.get(item.ensembl_gene_id) ||
          ({
            ensembl_gene_id: item.ensembl_gene_id || '',
            hgnc_symbol: item.hgnc_symbol || '',
          } as Gene);
        genes[item.uniqid] = getComparisonGene(gene, teams, scores, allBiodomains);
        genes[item.uniqid].uniprotid = item.uniprotid;
      }

      genes[item.uniqid].tissues.push({
        name: item.tissue,
        logfc: item.log2_fc,
        adj_p_val: item.cor_pval,
        ci_l: item.ci_lwr,
        ci_r: item.ci_upr,
      });
    });

    result = Object.values(genes);
  }

  altCache.set(cacheKey, result);
  return result;
}

// -------------------------------------------------------------------------- //
// Routes
// -------------------------------------------------------------------------- //

export async function comparisonGenesRoute(req: Request, res: Response, next: NextFunction) {
  if (
    !req.query ||
    !req.query.category ||
    typeof req.query.category !== 'string' ||
    !req.query.subCategory ||
    typeof req.query.subCategory !== 'string'
  ) {
    res.status(404).send('Not found');
    return;
  }

  try {
    let items: GCTGene[] | undefined = [] as GCTGene[];

    if ('RNA - Differential Expression' === req.query.category) {
      items = await getRnaComparisonGenes(req.query.subCategory);
    } else if ('Protein - Differential Expression' === req.query.category) {
      items = await getProteinComparisonGenes(req.query.subCategory);
    }

    setHeaders(res);
    res.json({ items });
  } catch (err) {
    next(err);
  }
}
