import { ItemFilterTypeQuery, ModelOverview } from '@sagebionetworks/model-ad/api-client';
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
import { ModelOverviewCollection } from '../models';

type ModelOverviewQuery = {
  _id?: { $in?: mongoose.Types.ObjectId[]; $nin?: mongoose.Types.ObjectId[] };
};

export async function getModelOverviews(
  items: string[] = [],
  itemFilterType: ItemFilterTypeQuery = ItemFilterTypeQuery.Include,
) {
  const cacheKey = buildCacheKey('modelOverview', items, itemFilterType);
  const cachedResult: ModelOverview[] | null | undefined = cache.get(cacheKey);

  if (cachedResult !== undefined) {
    return cachedResult;
  }

  if (itemFilterType === ItemFilterTypeQuery.Include && items.length === 0) {
    return [];
  }

  const query: ModelOverviewQuery = {};
  if (items.length > 0) {
    query._id = buildIdQuery(items, itemFilterType);
  }
  const result = await ModelOverviewCollection.find(query).lean().exec();

  cache.set(cacheKey, result);
  return result;
}

export async function modelOverviewRoute(req: Request, res: Response, next: NextFunction) {
  const itemFilterType = req.query.itemFilterType as ItemFilterTypeQuery | undefined;
  const items = normalizeToStringArray(req.query.item as string | string[] | undefined);

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
    const result = await getModelOverviews(items, itemFilterType);

    if (!result) {
      sendProblemJson(res, 404, 'Not Found', 'Model Overview data not found', req.path);
      return;
    }

    setHeaders(res);
    res.json(result);
  } catch (err) {
    next(err);
  }
}
