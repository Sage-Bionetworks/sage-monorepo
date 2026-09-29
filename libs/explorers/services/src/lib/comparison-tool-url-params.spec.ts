import { Params } from '@angular/router';
import { DEFAULT_SORT_ORDER } from '@sagebionetworks/explorers/constants';
import { ComparisonToolUrlParams } from '@sagebionetworks/explorers/models';
import {
  deserializeComparisonToolUrlParams,
  INVALID_SORT_ORDERS_MESSAGE,
  serializeComparisonToolUrlParams,
} from './comparison-tool-url-params';

function serialize(state: ComparisonToolUrlParams, currentQueryParams: Params = {}): Params {
  return serializeComparisonToolUrlParams(state, currentQueryParams);
}

describe('deserializeComparisonToolUrlParams', () => {
  it('should return an empty state when the URL has no query params', () => {
    const URL_WITHOUT_QUERY_PARAMS: Params = {};
    const EXPECTED_STATE: ComparisonToolUrlParams = {};

    expect(deserializeComparisonToolUrlParams(URL_WITHOUT_QUERY_PARAMS)).toEqual(EXPECTED_STATE);
  });

  it('should return an empty state when every query param is empty or only whitespace', () => {
    const EMPTY_VALUE = '';
    const WHITESPACE_ONLY_VALUE = ' ';
    const EXPECTED_STATE: ComparisonToolUrlParams = {};

    expect(
      deserializeComparisonToolUrlParams({
        categories: EMPTY_VALUE,
        pinned: WHITESPACE_ONLY_VALUE,
        sortFields: EMPTY_VALUE,
        sortOrders: EMPTY_VALUE,
        models: EMPTY_VALUE,
      }),
    ).toEqual(EXPECTED_STATE);
  });

  it('should decode URL-encoded values and split comma-separated values into lists', () => {
    expect(
      deserializeComparisonToolUrlParams({
        categories: 'RNA%20-%20DIFFERENTIAL%20EXPRESSION,Tissue%20-%20Hemibrain',
        pinned: 'ENSG1~APOE4~Female,ENSG2~3xTg-AD~Male', // pinned item ids are gene~model~sex tokens
        sortFields: 'gene_symbol,4%20months',
        sortOrders: '1,-1',
        models: 'APOE4%2FTrem2%2AR47H',
      }),
    ).toEqual({
      categories: ['RNA - DIFFERENTIAL EXPRESSION', 'Tissue - Hemibrain'],
      pinnedItems: ['ENSG1~APOE4~Female', 'ENSG2~3xTg-AD~Male'],
      sortFields: ['gene_symbol', '4 months'],
      sortOrders: [1, -1],
      filterSelections: { models: ['APOE4/Trem2*R47H'] },
    });
  });

  it('should return every query param except categories, pinned, sortFields and sortOrders as a filter selection', () => {
    const { filterSelections } = deserializeComparisonToolUrlParams({
      categories: 'RNA - DIFFERENTIAL EXPRESSION',
      models: 'APOE4,3xTg-AD',
      sex: 'Female',
    });

    // categories is a comparison tool param, so it is not returned as a filter.
    expect(filterSelections).toEqual({ models: ['APOE4', '3xTg-AD'], sex: ['Female'] });
  });

  describe('sort orders', () => {
    const SORT_FIELDS_PARAM = 'gene_symbol,model';
    const DESCENDING_SORT_ORDER = -1;
    let logger: { warn: jest.Mock };

    beforeEach(() => {
      logger = { warn: jest.fn() };
    });

    it('should replace an invalid sort order with the default and leave the other sort orders unchanged', () => {
      const INVALID_SORT_ORDER_VALUE = 5; // only 1 (ascending) and -1 (descending) are valid
      const EXPECTED_SORT_ORDER_VALUE = DEFAULT_SORT_ORDER; // the default sort order (ascending)

      const result = deserializeComparisonToolUrlParams(
        {
          sortFields: SORT_FIELDS_PARAM,
          sortOrders: `${INVALID_SORT_ORDER_VALUE},${DESCENDING_SORT_ORDER}`,
        },
        logger,
      );

      // The second field keeps its own descending order rather than taking the first field's place.
      expect(result).toEqual({
        sortFields: ['gene_symbol', 'model'],
        sortOrders: [EXPECTED_SORT_ORDER_VALUE, DESCENDING_SORT_ORDER],
      });
    });

    it('should warn with the URL sort params as written when a sort order is not a number', () => {
      const SORT_ORDERS_WITH_NON_NUMERIC_VALUE = 'ascending,-1';

      deserializeComparisonToolUrlParams(
        { sortFields: SORT_FIELDS_PARAM, sortOrders: SORT_ORDERS_WITH_NON_NUMERIC_VALUE },
        logger,
      );

      expect(logger.warn).toHaveBeenCalledWith(INVALID_SORT_ORDERS_MESSAGE, {
        sortFields: SORT_FIELDS_PARAM,
        sortOrders: SORT_ORDERS_WITH_NON_NUMERIC_VALUE,
      });
    });

    it('should warn once when a sort field has no sort order', () => {
      const SORT_ORDERS_FOR_FIRST_FIELD_ONLY = '-1';

      deserializeComparisonToolUrlParams(
        { sortFields: SORT_FIELDS_PARAM, sortOrders: SORT_ORDERS_FOR_FIRST_FIELD_ONLY },
        logger,
      );

      expect(logger.warn).toHaveBeenCalledTimes(1);
    });

    it('should warn when the URL has sort orders but no sort fields', () => {
      deserializeComparisonToolUrlParams({ sortOrders: '-1' }, logger);

      expect(logger.warn).toHaveBeenCalledWith(INVALID_SORT_ORDERS_MESSAGE, {
        sortFields: null,
        sortOrders: '-1',
      });
    });

    it('should not warn when every sort field has a valid sort order', () => {
      deserializeComparisonToolUrlParams(
        { sortFields: SORT_FIELDS_PARAM, sortOrders: '1,-1' },
        logger,
      );

      expect(logger.warn).not.toHaveBeenCalled();
    });

    it('should not warn when the URL has no sort params', () => {
      deserializeComparisonToolUrlParams(
        { categories: 'RNA%20-%20DIFFERENTIAL%20EXPRESSION' },
        logger,
      );

      expect(logger.warn).not.toHaveBeenCalled();
    });
  });

  it('should read back the same state that was written to the URL', () => {
    const state: ComparisonToolUrlParams = {
      categories: ['RNA - DIFFERENTIAL EXPRESSION', 'Tissue - Hemibrain'],
      pinnedItems: ['ENSG1~APOE4~Female'],
      sortFields: ['4 months'],
      sortOrders: [-1],
      filterSelections: { models: ['APOE4/Trem2*R47H'] },
    };

    expect(deserializeComparisonToolUrlParams(serialize(state))).toEqual(state);
  });
});

describe('serializeComparisonToolUrlParams', () => {
  it('should URL-encode each value and join lists with commas', () => {
    const MODEL_WITH_COMMA = 'APOE4,3xTg-AD';
    // A comma inside a value is encoded so it is not read back as a list separator.
    const ENCODED_MODEL_WITH_COMMA = 'APOE4%2C3xTg-AD';

    expect(
      serialize({
        categories: ['RNA - DIFFERENTIAL EXPRESSION'],
        pinnedItems: ['ENSG1~APOE4~Female'],
        sortFields: ['4 months'],
        sortOrders: [1, -1],
        filterSelections: { models: [MODEL_WITH_COMMA] },
      }),
    ).toEqual({
      categories: 'RNA%20-%20DIFFERENTIAL%20EXPRESSION',
      pinned: 'ENSG1~APOE4~Female',
      sortFields: '4%20months',
      sortOrders: '1,-1',
      models: ENCODED_MODEL_WITH_COMMA,
    });
  });

  it('should only return query params for the values set in the state', () => {
    // Params not returned are left as they are in the URL.
    expect(serialize({ sortFields: ['gene_symbol'] })).toEqual({ sortFields: 'gene_symbol' });
  });

  it('should remove a param from the URL when its value in the state is null', () => {
    // The router removes a query param whose value is null.
    expect(serialize({ categories: null, pinnedItems: null, sortOrders: null })).toEqual({
      categories: null,
      pinned: null,
      sortOrders: null,
    });
  });

  it('should remove a param from the URL when its list in the state is empty', () => {
    // The router removes a query param whose value is null.
    expect(serialize({ categories: [], pinnedItems: [], sortFields: [], sortOrders: [] })).toEqual({
      categories: null,
      pinned: null,
      sortFields: null,
      sortOrders: null,
    });
  });

  describe('filter selections', () => {
    const currentQueryParams: Params = {
      categories: 'RNA - DIFFERENTIAL EXPRESSION',
      models: 'APOE4',
      sex: 'Male',
    };

    it('should leave the current filters in the URL unchanged when the state has no filter selections', () => {
      // No models or sex params are returned, so the URL keeps its current filters.
      expect(
        serialize({ categories: ['RNA - DIFFERENTIAL EXPRESSION'] }, currentQueryParams),
      ).toEqual({ categories: 'RNA%20-%20DIFFERENTIAL%20EXPRESSION' });
    });

    it('should set the selected filters and remove current filters that are no longer selected', () => {
      expect(serialize({ filterSelections: { models: ['3xTg-AD'] } }, currentQueryParams)).toEqual({
        models: '3xTg-AD',
        sex: null,
      });
    });

    it('should remove every current filter from the URL when the filter selections are null', () => {
      expect(serialize({ filterSelections: null }, currentQueryParams)).toEqual({
        models: null,
        sex: null,
      });
    });

    it('should remove a filter from the URL when its selection is empty', () => {
      // sex is removed too, since it is a current filter that is no longer selected.
      expect(serialize({ filterSelections: { models: [] } }, currentQueryParams)).toEqual({
        models: null,
        sex: null,
      });
    });
  });
});
