import { SearchResult } from '@sagebionetworks/model-ad/api-client-angular';
import { NextFunction, Request, Response } from 'express';
import { cache, setHeaders } from '../helpers';
import { ModelsCollection } from '../models';

export async function searchModels(query: string) {
  const cacheKey = 'model-search-' + query;
  const cachedResult: SearchResult[] | null | undefined = cache.get(cacheKey);

  // If we have a cached result (including null), return it
  if (cachedResult !== undefined) {
    return cachedResult;
  }

  const queryTrimmed = query.trim();
  const result: SearchResult[] = await ModelsCollection.aggregate([
    {
      $addFields: {
        match_info: {
          $let: {
            vars: {
              filtered_aliases: {
                $filter: {
                  input: '$aliases',
                  cond: { $regexMatch: { input: '$$this', regex: queryTrimmed, options: 'i' } },
                },
              },
            },
            in: {
              $switch: {
                branches: [
                  {
                    case: { $regexMatch: { input: '$name', regex: queryTrimmed, options: 'i' } },
                    then: { precedence: 1, match_field: 'name', match_value: '$name' },
                  },
                  {
                    case: { $gt: [{ $size: '$$filtered_aliases' }, 0] },
                    then: {
                      precedence: 2,
                      match_field: 'aliases',
                      match_value: { $arrayElemAt: ['$$filtered_aliases', 0] },
                    },
                  },
                  {
                    case: { $regexMatch: { input: '$jax_id', regex: queryTrimmed, options: 'i' } },
                    then: { precedence: 3, match_field: 'jax_id', match_value: '$jax_id' },
                  },
                  {
                    case: { $regexMatch: { input: '$rrid', regex: queryTrimmed, options: 'i' } },
                    then: { precedence: 4, match_field: 'rrid', match_value: '$rrid' },
                  },
                ],
                default: null,
              },
            },
          },
        },
      },
    },
    { $match: { match_info: { $ne: null } } },
    { $sort: { 'match_info.precedence': 1, name: 1 } },
    {
      $project: {
        name: 1,
        match_field: '$match_info.match_field',
        match_value: '$match_info.match_value',
      },
    },
  ]).exec();

  cache.set(cacheKey, result);
  return result;
}

export async function modelsSearchRoute(req: Request, res: Response, next: NextFunction) {
  if (!req.query.q) {
    res.status(400).contentType('application/problem+json').json({
      title: 'Bad Request',
      status: 400,
      detail: 'Query parameter q is required',
      instance: req.path,
    });
    return;
  }

  try {
    const result = await searchModels(req.query.q as string);
    setHeaders(res);
    res.json(result);
  } catch (err) {
    next(err);
  }
}
