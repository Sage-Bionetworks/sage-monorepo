// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { cache } from '../helpers';
import { GeneLinkCollection } from '../models';

// -------------------------------------------------------------------------- //
// Functions
// -------------------------------------------------------------------------- //
export async function getGeneLinks(ensg: string) {
  const cacheKey = ensg + '-gene-links';
  let result: any = cache.get(cacheKey);

  if (result) {
    return result;
  }

  const listA = await GeneLinkCollection.find({
    geneA_ensembl_gene_id: ensg,
  })
    .lean()
    .exec();
  if (!listA) {
    return;
  }

  const listB = await GeneLinkCollection.find({
    geneB_ensembl_gene_id: ensg,
  })
    .lean()
    .exec();
  if (!listB) {
    return;
  }

  const ids = [
    ...listA.map((link) => {
      return link.geneB_ensembl_gene_id;
    }),
    ...listB.map((link) => {
      return link.geneA_ensembl_gene_id;
    }),
  ];

  const listC = await GeneLinkCollection.find({
    $and: [{ geneA_ensembl_gene_id: { $in: ids } }, { geneB_ensembl_gene_id: { $in: ids } }],
  })
    .lean()
    .exec();
  if (!listC) {
    return;
  }

  result = [...listA, ...listB, ...listC];

  cache.set(cacheKey, result);
  return result;
}
