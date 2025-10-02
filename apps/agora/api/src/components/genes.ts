// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { NextFunction, Request, Response } from 'express';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import {
  getBioDomains,
  getExperimentalValidation,
  getGeneLinks,
  getMetabolomics,
  getNeuropathologicCorrelations,
  getOverallScores,
  getProteomicsLFQ,
  getProteomicsSRM,
  getProteomicsTMT,
  getRnaDifferentialExpression,
} from '.';
import { altCache, cache, setHeaders } from '../helpers';
import { Gene, GeneCollection } from '../models';
import { getSimilarGenesNetwork } from './similar-genes-network';

// -------------------------------------------------------------------------- //
// Functions
// -------------------------------------------------------------------------- //
export async function getAllGenes() {
  const cacheKey = 'genes';
  let result: Gene[] | undefined = altCache.get(cacheKey);

  if (result) {
    return result;
  }

  result = await GeneCollection.find().lean().sort({ hgnc_symbol: 1, ensembl_gene_id: 1 }).exec();

  altCache.set(cacheKey, result);
  return result;
}

export async function getGenes(ids?: string) {
  const genes: Gene[] = await getAllGenes();

  if (ids) {
    const ids_array = ids.split(',');
    return genes.filter((g: Gene) => ids_array.includes(g.ensembl_gene_id));
  }

  return genes;
}

export async function getGenesMap() {
  const genes = await getGenes();
  return new Map(genes.map((g: Gene) => [g.ensembl_gene_id, g]));
}

export async function getGene(ensg: string) {
  ensg = ensg.trim();
  const cacheKey = ensg + '-gene';
  let result: Gene | null | undefined = cache.get(cacheKey);

  if (result) {
    return result;
  }

  result = await GeneCollection.findOne({
    ensembl_gene_id: ensg,
  })
    .lean()
    .exec();

  if (result) {
    result.rna_differential_expression = await getRnaDifferentialExpression(ensg);
    result.proteomics_LFQ = await getProteomicsLFQ(ensg);
    result.proteomics_SRM = await getProteomicsSRM(ensg);
    result.proteomics_TMT = await getProteomicsTMT(ensg);
    result.metabolomics = await getMetabolomics(ensg);
    result.neuropathologic_correlations = await getNeuropathologicCorrelations(ensg);
    result.overall_scores = await getOverallScores(ensg);
    result.experimental_validation = await getExperimentalValidation(ensg);
    result.links = await getGeneLinks(ensg);
    result.bio_domains = await getBioDomains(ensg);
    result.similar_genes_network = getSimilarGenesNetwork(result);
  }

  cache.set(cacheKey, result);
  return result;
}

export async function getNominatedGenes() {
  const cacheKey = 'nominated-genes';
  let result: Gene[] | undefined = cache.get(cacheKey);

  if (result) {
    return result;
  }

  result = await GeneCollection.find({ total_nominations: { $gt: 0 } })
    .select(
      [
        'hgnc_symbol',
        'ensembl_gene_id',
        'total_nominations',
        'target_nominations.initial_nomination',
        'target_nominations.team',
        'target_nominations.study',
        'target_nominations.source',
        'target_nominations.input_data',
        'target_nominations.validation_study_details',
        'druggability.pharos_class',
        'druggability.sm_druggability_bucket',
        'druggability.classification',
        'druggability.safety_bucket',
        'druggability.safety_bucket_definition',
        'druggability.abability_bucket',
        'druggability.abability_bucket_definition',
      ].join(' '),
    )
    .lean()
    .sort({ nominations: -1, hgnc_symbol: 1 })
    .exec();

  cache.set(cacheKey, result);
  return result;
}

// -------------------------------------------------------------------------- //
// Routes
// -------------------------------------------------------------------------- //
export async function genesRoute(req: Request, res: Response, next: NextFunction) {
  if (!req.query || !req.query.ids) {
    res.status(404).send('Not found');
    return;
  }

  try {
    const result = await getGenes(<string>req.query.ids);
    setHeaders(res);
    res.json(result);
  } catch (err) {
    next(err);
  }
}

export async function geneRoute(req: Request, res: Response, next: NextFunction) {
  if (!req.params || !req.params.id) {
    res.status(404).send('Not found');
    return;
  }

  try {
    const result = await getGene(<string>req.params.id);
    setHeaders(res);
    res.json(result);
  } catch (err) {
    next(err);
  }
}

export async function nominatedGenesRoute(req: Request, res: Response, next: NextFunction) {
  try {
    const result = await getNominatedGenes();
    setHeaders(res);
    res.json({ items: result });
  } catch (err) {
    next(err);
  }
}
