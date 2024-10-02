// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { cache } from '../helpers';
import { NeuropathologicCorrelation, NeuropathologicCorrelationCollection } from '../models';

// -------------------------------------------------------------------------- //
// Functions
// -------------------------------------------------------------------------- //
export async function getNeuropathologicCorrelations(ensg: string) {
  const cacheKey = ensg + '-neuropathology-correlations';
  let result: NeuropathologicCorrelation[] | undefined = cache.get(cacheKey);

  if (result) {
    return result;
  }

  result = await NeuropathologicCorrelationCollection.find({
    ensg: ensg,
  })
    .lean()
    .exec();

  cache.set(cacheKey, result);
  return result;
}
