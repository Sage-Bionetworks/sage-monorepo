// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { OverallScores } from '@sagebionetworks/agora/api-client';
import { cache } from '../helpers';
import { OverallScoresCollection } from '../models';

// -------------------------------------------------------------------------- //
// Functions
// -------------------------------------------------------------------------- //
export async function getOverallScores(ensg: string) {
  const cacheKey = ensg + '-overall-scores';
  let result: OverallScores | null | undefined = cache.get(cacheKey);

  if (result) {
    return result;
  }

  result = await OverallScoresCollection.findOne(
    {
      ensembl_gene_id: ensg,
    },
    { _id: 0, target_risk_score: 1, genetics_score: 1, multi_omics_score: 1, literature_score: 1 },
  )
    .lean()
    .exec();

  cache.set(cacheKey, result);
  return result || undefined;
}
