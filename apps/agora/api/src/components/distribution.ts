// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { Request, Response, NextFunction } from 'express';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { setHeaders, cache } from '../helpers';
import {
  RnaDistributionCollection,
  ProteomicDistributionCollection,
  OverallScoresDistributionCollection,
} from '../models';
import {
  RnaDistribution,
  Distribution,
  OverallScoresDistribution,
  ProteomicsDistribution,
} from '@sagebionetworks/agora/api-client';

// -------------------------------------------------------------------------- //
// Functions
// -------------------------------------------------------------------------- //

export async function getRnaDistribution() {
  const cacheKey = 'rna-distribution';
  let result: RnaDistribution[] | undefined = cache.get(cacheKey);

  if (result) {
    return result;
  }

  result = await RnaDistributionCollection.find().lean().exec();

  cache.set(cacheKey, result);
  return result;
}

export async function getProteomicDistribution(type: string) {
  const cacheKey = 'proteomics-' + type + '-distribution';
  let result: ProteomicsDistribution[] | undefined = cache.get(cacheKey);

  if (result) {
    return result;
  }

  result = await ProteomicDistributionCollection.find({ type: type }).lean().exec();

  cache.set(cacheKey, result);
  return result;
}

export async function getOverallScoresDistribution() {
  const cacheKey = 'overall-scores-distribution';
  let result: OverallScoresDistribution[] | undefined = cache.get(cacheKey);

  if (result) {
    return result;
  }

  result = await OverallScoresDistributionCollection.find({}).sort('name').lean().exec();

  // Handle old format
  if (result.length === 1) {
    result = Object.values(result[0]).filter((d: any) => d.distribution?.length);
  }

  cache.set(cacheKey, result);
  return result;
}

export async function getDistribution() {
  const cacheKey = 'distribution';
  let result: Distribution | undefined = cache.get(cacheKey);

  if (result) {
    return result;
  }

  result = {
    rna_differential_expression: await getRnaDistribution(),
    proteomics_LFQ: await getProteomicDistribution('LFQ'),
    proteomics_SRM: await getProteomicDistribution('SRM'),
    proteomics_TMT: await getProteomicDistribution('TMT'),
    overall_scores: await getOverallScoresDistribution(),
  };

  cache.set(cacheKey, result);
  return result;
}

// -------------------------------------------------------------------------- //
//
// -------------------------------------------------------------------------- //

export async function distributionRoute(req: Request, res: Response, next: NextFunction) {
  try {
    const result = await getDistribution();
    setHeaders(res);
    res.json(result);
  } catch (err) {
    next(err);
  }
}
