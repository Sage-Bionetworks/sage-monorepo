// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { RnaDifferentialExpression } from '@sagebionetworks/agora/api-client';
import { cache } from '../helpers';
import { RnaDifferentialExpressionCollection } from '../models';
// -------------------------------------------------------------------------- //
// Functions
// -------------------------------------------------------------------------- //
export async function getRnaDifferentialExpression(ensg: string) {
  const cacheKey = ensg + '-rna-differential-expression';
  let result: RnaDifferentialExpression[] | undefined = cache.get(cacheKey);

  if (result) {
    return result;
  }

  result = await RnaDifferentialExpressionCollection.find({
    ensembl_gene_id: ensg,
  })
    .lean()
    .sort({ hgnc_symbol: 1, tissue: 1, model: 1 })
    .exec();

  if (result) {
    const models: { [key: string]: string[] } = {};

    // Filter out duplicates
    result = result.filter((item: any) => {
      if (!models[item['model']]) {
        models[item['model']] = [];
      }

      if (!models[item['model']].includes(item['tissue'])) {
        models[item['model']].push(item['tissue']);
        return true;
      }

      return false;
    });
  }

  cache.set(cacheKey, result);
  return result;
}
