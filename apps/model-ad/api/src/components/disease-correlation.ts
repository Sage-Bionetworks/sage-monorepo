import { DiseaseCorrelation, ItemFilterTypeQuery } from '@sagebionetworks/model-ad/api-client';
import { NextFunction, Request, Response } from 'express';
import mongoose from 'mongoose';
import {
  buildCacheKey,
  buildIdQuery,
  cache,
  normalizeToStringArray,
  sendProblemJson,
  setHeaders,
  validateItemFilterType,
  validateItems,
} from '../helpers';
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
  const cacheKey = buildCacheKey('diseaseCorrelations', cluster, items, itemFilterType);
  const cachedResult: DiseaseCorrelation[] | null | undefined = cache.get(cacheKey);

  if (cachedResult !== undefined) {
    return cachedResult;
  }

  if (itemFilterType === ItemFilterTypeQuery.Include && items.length === 0) {
    return [];
  }

  const query: DiseaseCorrelationQuery = { cluster };
  if (items.length > 0) {
    query._id = buildIdQuery(items, itemFilterType);
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
    sendProblemJson(
      res,
      400,
      'Bad Request',
      `Query parameter category must repeat twice (e.g. ?category=${KNOWN_CATEGORIES.CONSENSUS_NETWORK_MODULES}&category=subcategory) and each value must be a string`,
      req.path,
    );
    return;
  }

  const [category, subcategory] = categories;

  if (category !== KNOWN_CATEGORIES.CONSENSUS_NETWORK_MODULES) {
    sendProblemJson(
      res,
      400,
      'Bad Request',
      `Only ${KNOWN_CATEGORIES.CONSENSUS_NETWORK_MODULES} category is supported`,
      req.path,
    );
    return;
  }

  const itemFilterType = req.query.itemFilterType as ItemFilterTypeQuery | undefined;
  const items = normalizeToStringArray(req.query.item as string[] | string | undefined);

  const itemFilterTypeError = validateItemFilterType(itemFilterType, req.path);
  if (itemFilterTypeError) {
    sendProblemJson(
      res,
      itemFilterTypeError.status,
      itemFilterTypeError.title,
      itemFilterTypeError.detail,
      itemFilterTypeError.instance,
    );
    return;
  }

  const itemsError = validateItems(items, req.path);
  if (itemsError) {
    sendProblemJson(
      res,
      itemsError.status,
      itemsError.title,
      itemsError.detail,
      itemsError.instance,
    );
    return;
  }

  try {
    const result = await getDiseaseCorrelations(subcategory, items, itemFilterType);

    if (!result) {
      sendProblemJson(res, 404, 'Not Found', 'Disease Correlation data not found', req.path);
      return;
    }

    setHeaders(res);
    res.json(result);
  } catch (err) {
    next(err);
  }
}
