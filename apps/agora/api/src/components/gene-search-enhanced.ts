import { SearchResult } from '@sagebionetworks/agora/api-client';
import { isEnsemblId, sanitizeSearchQuery } from '@sagebionetworks/agora/util';
import { escapeRegexChars } from '@sagebionetworks/shared/util';
import { NextFunction, Request, Response } from 'express';
import { cache, setHeaders } from '../helpers';
import { GeneCollection } from '../models';

async function searchEnsemblGene(queryEscaped: string): Promise<SearchResult[]> {
  return await GeneCollection.aggregate([
    {
      $match: {
        ensembl_gene_id: { $regex: queryEscaped, $options: 'i' },
      },
    },
    {
      $project: {
        id: '$ensembl_gene_id',
        match_field: { $literal: 'ensembl_gene_id' },
        match_value: '$ensembl_gene_id',
        hgnc_symbol: '$hgnc_symbol',
        _id: 0,
      },
    },
  ]).exec();
}

async function searchRegexMatches(
  queryEscaped: string,
  originalQuery: string,
): Promise<SearchResult[]> {
  return await GeneCollection.aggregate([
    {
      $match: {
        $or: [
          { hgnc_symbol: { $regex: queryEscaped, $options: 'i' } },
          { alias: { $elemMatch: { $regex: queryEscaped, $options: 'i' } } },
        ],
      },
    },
    {
      $addFields: {
        match_info: {
          $cond: {
            if: { $regexMatch: { input: '$hgnc_symbol', regex: queryEscaped, options: 'i' } },
            then: {
              field: 'hgnc_symbol',
              value: '$hgnc_symbol',
            },
            else: {
              field: 'alias',
              value: {
                $arrayElemAt: [
                  {
                    $filter: {
                      input: '$alias',
                      cond: {
                        $regexMatch: { input: '$$this', regex: queryEscaped, options: 'i' },
                      },
                    },
                  },
                  0,
                ],
              },
            },
          },
        },
      },
    },
    {
      $addFields: {
        match_field: '$match_info.field',
        match_value: '$match_info.value',
      },
    },
    {
      $addFields: {
        sort_priority: {
          $cond: {
            if: {
              $and: [
                { $eq: ['$match_field', 'hgnc_symbol'] },
                { $eq: [{ $strcasecmp: ['$hgnc_symbol', originalQuery] }, 0] },
              ],
            },
            then: 1, // Exact hgnc_symbol match
            else: {
              $cond: {
                if: {
                  $and: [
                    { $eq: ['$match_field', 'alias'] },
                    { $eq: [{ $strcasecmp: ['$match_value', originalQuery] }, 0] },
                  ],
                },
                then: 2, // Exact alias match
                else: {
                  $cond: {
                    if: { $eq: ['$match_field', 'hgnc_symbol'] },
                    then: 3, // Partial hgnc_symbol match
                    else: 4, // Partial alias match
                  },
                },
              },
            },
          },
        },
        match_position: {
          $cond: {
            if: { $in: ['$sort_priority', [1, 2]] },
            then: 0, // Exact matches have position 0
            else: {
              $let: {
                vars: { queryLower: { $toLower: originalQuery } },
                in: {
                  $indexOfCP: [{ $toLower: '$match_value' }, '$$queryLower'],
                },
              },
            },
          },
        },
      },
    },
    {
      $addFields: {
        sort_tertiary: {
          $cond: {
            if: { $eq: ['$sort_priority', 4] }, // Partial alias matches
            then: '$hgnc_symbol', // Sort by hgnc_symbol for partial alias matches
            else: '$match_value', // Sort by match_value for all other cases
          },
        },
      },
    },
    {
      $sort: {
        sort_priority: 1, // Sort by match type (1=exact hgnc_symbol, 2=exact alias, 3=partial hgnc_symbol, 4=partial alias)
        match_position: 1, // Sort by match position (0 for exact, then by position in string)
        sort_tertiary: 1, // Sort by hgnc_symbol for partial alias matches, match_value for others
        hgnc_symbol: 1,
        ensembl_gene_id: 1,
      },
    },
    {
      $project: {
        id: '$ensembl_gene_id',
        match_field: '$match_field',
        match_value: '$match_value',
        hgnc_symbol: '$hgnc_symbol',
        _id: 0,
      },
    },
  ]).exec();
}

export async function searchGeneEnhanced(query: string) {
  if (typeof query !== 'string') {
    throw new Error('Query must be a string');
  }

  // Query should already be cleaned by the route handler
  const queryEscaped = escapeRegexChars(query);

  const cacheKey = 'gene-search-enhanced-' + Buffer.from(queryEscaped).toString('base64');
  const cachedResult: SearchResult[] | null | undefined = cache.get(cacheKey);

  if (cachedResult !== undefined) {
    return cachedResult;
  }

  const isEnsemblSearch = query.length === 15 && isEnsemblId(query);
  let result: SearchResult[];

  if (isEnsemblSearch) {
    result = await searchEnsemblGene(queryEscaped);
  } else {
    result = await searchRegexMatches(queryEscaped, query);
  }

  cache.set(cacheKey, result);
  return result;
}

export async function searchGeneEnhancedRoute(req: Request, res: Response, next: NextFunction) {
  if (!req.query.q || typeof req.query.q !== 'string') {
    res.status(400).contentType('application/problem+json').json({
      title: 'Bad Request',
      status: 400,
      detail: 'Query parameter q is required and must be a string',
      instance: req.path,
    });
    return;
  }

  const rawQuery = req.query.q;

  if (rawQuery.length > 100) {
    res.status(400).contentType('application/problem+json').json({
      title: 'Bad Request',
      status: 400,
      detail: 'Query parameter q must be no more than 100 characters',
      instance: req.path,
    });
    return;
  }

  const query = sanitizeSearchQuery(rawQuery);
  if (query.length < 2) {
    res.status(400).contentType('application/problem+json').json({
      title: 'Bad Request',
      status: 400,
      detail:
        'Query must contain at least 2 valid characters (letters, numbers, hyphens, or underscores)',
      instance: req.path,
    });
    return;
  }

  if (isEnsemblId(query) && query.length !== 15) {
    res.status(400).contentType('application/problem+json').json({
      title: 'Bad Request',
      status: 400,
      detail: 'Ensembl Gene IDs must be exactly 15 characters long',
      instance: req.path,
    });
    return;
  }

  try {
    const result = await searchGeneEnhanced(query);
    setHeaders(res);
    res.json(result);
  } catch (err) {
    next(err);
  }
}
