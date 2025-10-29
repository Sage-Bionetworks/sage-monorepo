import { ItemFilterTypeQuery, ModelOverview } from '@sagebionetworks/model-ad/api-client';
import { NextFunction, Request, Response } from 'express';
import mongoose from 'mongoose';
import { cache, normalizeToStringArray, setHeaders } from '../helpers';
import { ModelOverviewCollection } from '../models';

type ModelOverviewQuery = {
  _id?: { $in?: mongoose.Types.ObjectId[]; $nin?: mongoose.Types.ObjectId[] };
};

export async function getModelOverviews(
  items: string[] = [],
  itemFilterType: ItemFilterTypeQuery = ItemFilterTypeQuery.Include,
) {
  const cacheKey = 'modelOverview-' + JSON.stringify(items) + '-' + itemFilterType;
  const cachedResult: ModelOverview[] | null | undefined = cache.get(cacheKey);

  if (cachedResult !== undefined) {
    return cachedResult;
  }

  if (itemFilterType === ItemFilterTypeQuery.Include && items.length === 0) {
    return [];
  }

  const query: ModelOverviewQuery = {};
  const objectIds = items.map((id) => new mongoose.Types.ObjectId(id));
  if (itemFilterType === ItemFilterTypeQuery.Include) {
    query._id = { $in: objectIds };
  } else {
    query._id = { $nin: objectIds };
  }
  const result = await ModelOverviewCollection.find(query).lean().exec();

  cache.set(cacheKey, result);
  return result;
}

export async function modelOverviewRoute(req: Request, res: Response, next: NextFunction) {
  const itemFilterType = req.query.itemFilterType as ItemFilterTypeQuery | undefined;
  const items = normalizeToStringArray(req.query.item as string | string[] | undefined);

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
    const result = await getModelOverviews(items, itemFilterType);

    if (!result) {
      res.status(404).contentType('application/problem+json').json({
        title: 'Not Found',
        status: 404,
        detail: 'Model Overview data not found',
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
