// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { ProteinDifferentialExpression } from '@sagebionetworks/agora/api-client';
import { cache } from '../helpers';
import {
  ProteomicsLFQCollection,
  ProteomicsSRMCollection,
  ProteomicsTMTCollection,
} from '../models';

// -------------------------------------------------------------------------- //
// Functions
// -------------------------------------------------------------------------- //
export async function getProteomicsLFQ(ensg: string) {
  const cacheKey = ensg + '-protein-LFQ';
  let result: ProteinDifferentialExpression[] | undefined = cache.get(cacheKey);

  if (result) {
    return result;
  }

  result = await ProteomicsLFQCollection.find({
    ensembl_gene_id: ensg,
  })
    .lean()
    .exec();

  if (result) {
    result = result.filter((item: any) => {
      return item.log2_fc;
    });
  }

  cache.set(cacheKey, result);
  return result;
}

export async function getProteomicsSRM(ensg: string) {
  const cacheKey = ensg + '-protein-SRM';
  let result: ProteinDifferentialExpression[] | undefined = cache.get(cacheKey);

  if (result) {
    return result;
  }

  result = await ProteomicsSRMCollection.find({
    ensembl_gene_id: ensg,
  })
    .lean()
    .exec();

  if (result) {
    result = result.filter((item: any) => {
      return item.log2_fc;
    });
  }

  cache.set(cacheKey, result);
  return result;
}

export async function getProteomicsTMT(ensg: string) {
  const cacheKey = ensg + '-protein-TMT';
  let result: ProteinDifferentialExpression[] | undefined = cache.get(cacheKey);

  if (result) {
    return result;
  }

  result = await ProteomicsTMTCollection.find({
    ensembl_gene_id: ensg,
  })
    .lean()
    .exec();

  if (result) {
    result = result.filter((item: any) => {
      return item.log2_fc;
    });
  }

  cache.set(cacheKey, result);
  return result;
}
