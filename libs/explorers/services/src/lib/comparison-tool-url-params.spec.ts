import { Params } from '@angular/router';
import { ComparisonToolUrlParams } from '@sagebionetworks/explorers/models';
import {
  deserializeComparisonToolUrlParams,
  serializeComparisonToolUrlParams,
} from './comparison-tool-url-params';

const ENCODED_CATEGORY = 'RNA%20-%20DIFFERENTIAL%20EXPRESSION';
const DECODED_CATEGORY = 'RNA - DIFFERENTIAL EXPRESSION';

function serialize(state: ComparisonToolUrlParams, currentQueryParams: Params = {}): Params {
  return serializeComparisonToolUrlParams(state, currentQueryParams);
}

describe('deserializeComparisonToolUrlParams', () => {
  it('should omit every param for an empty URL', () => {
    expect(deserializeComparisonToolUrlParams({})).toEqual({});
  });

  it('should omit params whose values are blank', () => {
    expect(
      deserializeComparisonToolUrlParams({
        categories: '',
        pinned: ' ',
        sortFields: '',
        sortOrders: '',
        models: '',
      }),
    ).toEqual({});
  });

  it('should decode each param value', () => {
    expect(
      deserializeComparisonToolUrlParams({
        categories: `${ENCODED_CATEGORY},Tissue%20-%20Hemibrain`,
        pinned: 'ENSG1~APOE4~Female,ENSG2~3xTg-AD~Male',
        sortFields: 'gene_symbol,4%20months',
        sortOrders: '1,-1',
        models: 'APOE4%2FTrem2%2AR47H',
      }),
    ).toEqual({
      categories: [DECODED_CATEGORY, 'Tissue - Hemibrain'],
      pinnedItems: ['ENSG1~APOE4~Female', 'ENSG2~3xTg-AD~Male'],
      sortFields: ['gene_symbol', '4 months'],
      sortOrders: [1, -1],
      filterSelections: { models: ['APOE4/Trem2*R47H'] },
    });
  });

  it('should treat every non-reserved param as a filter', () => {
    const { filterSelections } = deserializeComparisonToolUrlParams({
      categories: DECODED_CATEGORY,
      models: 'APOE4,3xTg-AD',
      sex: 'Female',
    });

    expect(filterSelections).toEqual({ models: ['APOE4', '3xTg-AD'], sex: ['Female'] });
  });

  it('should drop sort orders outside the supported values while keeping their sort fields', () => {
    expect(
      deserializeComparisonToolUrlParams({
        sortFields: 'gene_symbol,model',
        sortOrders: '5,-1',
      }),
    ).toEqual({ sortFields: ['gene_symbol', 'model'], sortOrders: [-1] });
  });

  it('should omit sort orders when none are supported', () => {
    expect(deserializeComparisonToolUrlParams({ sortOrders: 'ascending' })).toEqual({});
  });

  it('should round-trip a serialized state', () => {
    const state: ComparisonToolUrlParams = {
      categories: [DECODED_CATEGORY, 'Tissue - Hemibrain'],
      pinnedItems: ['ENSG1~APOE4~Female'],
      sortFields: ['4 months'],
      sortOrders: [-1],
      filterSelections: { models: ['APOE4/Trem2*R47H'] },
    };

    expect(deserializeComparisonToolUrlParams(serialize(state))).toEqual(state);
  });
});

describe('serializeComparisonToolUrlParams', () => {
  it('should encode each param value', () => {
    expect(
      serialize({
        categories: [DECODED_CATEGORY],
        pinnedItems: ['ENSG1~APOE4~Female'],
        sortFields: ['4 months'],
        sortOrders: [1, -1],
        filterSelections: { models: ['APOE4,3xTg-AD'] },
      }),
    ).toEqual({
      categories: ENCODED_CATEGORY,
      pinned: 'ENSG1~APOE4~Female',
      sortFields: '4%20months',
      sortOrders: '1,-1',
      models: 'APOE4%2C3xTg-AD',
    });
  });

  it('should leave omitted params out of the patch', () => {
    expect(serialize({ sortFields: ['gene_symbol'] })).toEqual({ sortFields: 'gene_symbol' });
  });

  it('should emit null for a param set to null', () => {
    expect(serialize({ categories: null, pinnedItems: null, sortOrders: null })).toEqual({
      categories: null,
      pinned: null,
      sortOrders: null,
    });
  });

  it('should emit null for a param set to an empty list', () => {
    expect(serialize({ categories: [], pinnedItems: [], sortFields: [], sortOrders: [] })).toEqual({
      categories: null,
      pinned: null,
      sortFields: null,
      sortOrders: null,
    });
  });

  describe('filter selections', () => {
    const currentQueryParams: Params = {
      categories: DECODED_CATEGORY,
      models: 'APOE4',
      sex: 'Male',
    };

    it('should leave the current filter params alone when omitted', () => {
      expect(serialize({ categories: [DECODED_CATEGORY] }, currentQueryParams)).toEqual({
        categories: ENCODED_CATEGORY,
      });
    });

    it('should replace the current filter params when provided', () => {
      expect(serialize({ filterSelections: { models: ['3xTg-AD'] } }, currentQueryParams)).toEqual({
        models: '3xTg-AD',
        sex: null,
      });
    });

    it('should remove every current filter param when null', () => {
      expect(serialize({ filterSelections: null }, currentQueryParams)).toEqual({
        models: null,
        sex: null,
      });
    });

    it('should remove a filter param whose values are empty', () => {
      expect(serialize({ filterSelections: { models: [] } }, currentQueryParams)).toEqual({
        models: null,
        sex: null,
      });
    });
  });
});
