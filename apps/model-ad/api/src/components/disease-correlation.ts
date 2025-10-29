import { DiseaseCorrelation, ItemFilterTypeQuery } from '@sagebionetworks/model-ad/api-client';
import { NextFunction, Request, Response } from 'express';
import mongoose from 'mongoose';
import { cache, normalizeToStringArray, setHeaders } from '../helpers';
import { DiseaseCorrelationCollection } from '../models';

enum KNOWN_CATEGORIES {
  CONSENSUS_NETWORK_MODULES = 'CONSENSUS NETWORK MODULES',
}

type DiseaseCorrelationQuery = {
  cluster: string;
  _id?: { $in?: mongoose.Types.ObjectId[]; $nin?: mongoose.Types.ObjectId[] };
};

export async function getDiseaseCorrelations(
  cluster: string,
  items: string[] = [],
  itemFilterType: ItemFilterTypeQuery = ItemFilterTypeQuery.Include,
) {
  const cacheKey =
    'diseaseCorrelations-' + cluster + '-' + JSON.stringify(items) + '-' + itemFilterType;
  const cachedResult: DiseaseCorrelation[] | null | undefined = cache.get(cacheKey);

  if (cachedResult !== undefined) {
    return cachedResult;
  }

  if (itemFilterType === ItemFilterTypeQuery.Include && items.length === 0) {
    return [];
  }

  const query: DiseaseCorrelationQuery = { cluster };
  const objectIds = items.map((id) => new mongoose.Types.ObjectId(id));
  if (itemFilterType === ItemFilterTypeQuery.Include) {
    query._id = { $in: objectIds };
  } else {
    query._id = { $nin: objectIds };
  }
  const result = await DiseaseCorrelationCollection.find(query).lean().exec();

  cache.set(cacheKey, result);
  return result;
}

export async function diseaseCorrelationRoute(req: Request, res: Response, next: NextFunction) {
  const categories = req.query.category;
  if (
    !categories ||
    !Array.isArray(categories) ||
    categories.length !== 2 ||
    !categories.every((f) => typeof f === 'string')
  ) {
    res
      .status(400)
      .contentType('application/problem+json')
      .json({
        title: 'Bad Request',
        status: 400,
        detail: `Query parameter category must repeat twice (e.g. ?category=${KNOWN_CATEGORIES.CONSENSUS_NETWORK_MODULES}&category=subcategory) and each value must be a string`,
        instance: req.path,
      });
    return;
  }

  const [category, subcategory] = categories;

  if (category !== KNOWN_CATEGORIES.CONSENSUS_NETWORK_MODULES) {
    res
      .status(400)
      .contentType('application/problem+json')
      .json({
        title: 'Bad Request',
        status: 400,
        detail: `Only ${KNOWN_CATEGORIES.CONSENSUS_NETWORK_MODULES} category is supported`,
        instance: req.path,
      });
    return;
  }

  const itemFilterType = req.query.itemFilterType as ItemFilterTypeQuery | undefined;
  const items = normalizeToStringArray(req.query.item as string[] | string | undefined);

  if (
    itemFilterType &&
    itemFilterType !== ItemFilterTypeQuery.Include &&
    itemFilterType !== ItemFilterTypeQuery.Exclude
  ) {
    res
      .status(400)
      .contentType('application/problem+json')
      .json({
        title: 'Bad Request',
        status: 400,
        detail: `Query parameter itemFilterType must be either ${ItemFilterTypeQuery.Include} or ${ItemFilterTypeQuery.Exclude} if provided`,
        instance: req.path,
      });
    return;
  }

  if (items) {
    if (!Array.isArray(items) || !items.every((f) => typeof f === 'string')) {
      res.status(400).contentType('application/problem+json').json({
        title: 'Bad Request',
        status: 400,
        detail: `Query parameter items must be a list of strings`,
        instance: req.path,
      });
      return;
    }
  }

  try {
    const result = await getDiseaseCorrelations(subcategory, items, itemFilterType);

    if (!result) {
      res.status(404).contentType('application/problem+json').json({
        title: 'Not Found',
        status: 404,
        detail: 'Disease Correlation data not found',
        instance: req.path,
      });
      return;
    }

    setHeaders(res);
    res.json(result);
  } catch (err) {
    next(err);
  }
}
