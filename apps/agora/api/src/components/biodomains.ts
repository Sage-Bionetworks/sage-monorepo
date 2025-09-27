// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { Request, Response, NextFunction } from 'express';
import { cache, setHeaders } from '../helpers';
import { AllBioDomainsCollection, BioDomainsCollection } from '../models';
import { BioDomains, BioDomainInfo } from '@sagebionetworks/agora/api-client';

// -------------------------------------------------------------------------- //
// Functions
// -------------------------------------------------------------------------- //
export async function getAllBioDomains() {
  const cacheKey = 'all-biodomains';
  let result: BioDomainInfo[] | null | undefined = cache.get(cacheKey);

  if (result) {
    return result;
  }

  result = await AllBioDomainsCollection.find().lean().sort().exec();

  cache.set(cacheKey, result);
  return result || undefined;
}

export async function getAllGeneBioDomains() {
  const cacheKey = 'all-genebiodomains';
  let result: BioDomains[] | null | undefined = cache.get(cacheKey);

  if (result) {
    return result;
  }

  result = await BioDomainsCollection.find().lean().sort().exec();

  cache.set(cacheKey, result);
  return result || undefined;
}

export async function getBioDomains(ensg: string) {
  const cacheKey = ensg + '-biodomains';
  let result: BioDomains | null | undefined = cache.get(cacheKey);

  if (result) {
    return result;
  }

  result = await BioDomainsCollection.findOne({
    ensembl_gene_id: ensg,
  })
    .lean()
    .exec();

  cache.set(cacheKey, result);
  return result || undefined;
}

// -------------------------------------------------------------------------- //
// Routes
// -------------------------------------------------------------------------- //
export async function allBiodomainsRoute(req: Request, res: Response, next: NextFunction) {
  try {
    const result = await getAllBioDomains();
    setHeaders(res);
    res.json(result);
  } catch (err) {
    next(err);
  }
}

export async function biodomainsRoute(req: Request, res: Response, next: NextFunction) {
  if (!req.params || !req.params.id) {
    res.status(404).send('Not found');
    return;
  }

  try {
    const result = await getBioDomains(<string>req.params.id);
    setHeaders(res);
    res.json(result?.gene_biodomains);
  } catch (err) {
    next(err);
  }
}
