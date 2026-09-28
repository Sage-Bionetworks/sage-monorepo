import { Params } from '@angular/router';
import { ComparisonToolUrlParams } from '@sagebionetworks/explorers/models';
import {
  deserializeComparisonToolUrlParams,
  INVALID_SORT_ORDERS_MESSAGE,
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

  describe('sort orders', () => {
    const sortFields = 'gene_symbol,model';
    let logger: { warn: jest.Mock };

    beforeEach(() => {
      logger = { warn: jest.fn() };
    });

    it('should keep an unreadable order in place so later orders stay with their fields', () => {
      const result = deserializeComparisonToolUrlParams({ sortFields, sortOrders: '5,-1' }, logger);

      expect(result).toEqual({ sortFields: ['gene_symbol', 'model'], sortOrders: [1, -1] });
    });

    it('should warn with the raw params when an order is unreadable', () => {
      deserializeComparisonToolUrlParams({ sortFields, sortOrders: 'ascending,-1' }, logger);

      expect(logger.warn).toHaveBeenCalledWith(INVALID_SORT_ORDERS_MESSAGE, {
        sortFields,
        sortOrders: 'ascending,-1',
      });
    });

    it('should warn once when there are fewer orders than fields', () => {
      deserializeComparisonToolUrlParams({ sortFields, sortOrders: '-1' }, logger);

      expect(logger.warn).toHaveBeenCalledTimes(1);
    });

    it('should warn when there are sort orders but no sort fields', () => {
      deserializeComparisonToolUrlParams({ sortOrders: '-1' }, logger);

      expect(logger.warn).toHaveBeenCalledWith(INVALID_SORT_ORDERS_MESSAGE, {
        sortFields: null,
        sortOrders: '-1',
      });
    });

    it('should not warn when every field has a readable order', () => {
      deserializeComparisonToolUrlParams({ sortFields, sortOrders: '1,-1' }, logger);

      expect(logger.warn).not.toHaveBeenCalled();
    });

    it('should not warn when the URL carries no sort', () => {
      deserializeComparisonToolUrlParams({ categories: ENCODED_CATEGORY }, logger);

      expect(logger.warn).not.toHaveBeenCalled();
    });
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
