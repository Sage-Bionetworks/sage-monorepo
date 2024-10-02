// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { cache } from '../helpers';
import { Metabolomics, MetabolomicsCollection } from '../models';

// -------------------------------------------------------------------------- //
// Functions
// -------------------------------------------------------------------------- //
export async function getMetabolomics(ensg: string) {
  const cacheKey = ensg + '-metabolomics';
  let result: Metabolomics | null | undefined = cache.get(cacheKey);

  if (result) {
    return result;
  }

  result = await MetabolomicsCollection.findOne({
    ensembl_gene_id: ensg,
  })
    .lean()
    .exec();

  cache.set(cacheKey, result);
  return result;
}
