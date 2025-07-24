import {
  CategoryAsValueBoxplotSummary,
  CategoryAsValuePoint,
  CategoryBoxplotSummary,
  CategoryPoint,
} from '../models';
import {
  addXAxisValueToBoxplotSummaries,
  addXAxisValueToCategoryPoint,
  formatCategoryPointsForBoxplotTransform,
  getCategoryPointColor,
  getCategoryPointShape,
  getCategoryPointStyle,
  getOffsetAndJitteredXValue,
  getPointStyleFromArray,
  getRandomNumber,
  getUniqueValues,
} from './boxplot-utils';

const consoleWarnSpy = jest.spyOn(console, 'warn').mockImplementation(() => {
  return undefined;
});

const defaultPtOffset = 0.2;

const pointsWithPointCategories: CategoryPoint[] = [
  { xAxisCategory: 'c', pointCategory: 'Y', value: 1 },
  { xAxisCategory: 'c', pointCategory: 'X', value: 2 },
  { xAxisCategory: 'c', pointCategory: 'Z', value: 3 },
  { xAxisCategory: 'a', pointCategory: 'Y', value: 4 },
  { xAxisCategory: 'a', pointCategory: 'X', value: 5 },
  { xAxisCategory: 'a', pointCategory: 'Z', value: 6 },
  { xAxisCategory: 'b', pointCategory: 'Y', value: 7 },
  { xAxisCategory: 'b', pointCategory: 'X', value: 8 },
  { xAxisCategory: 'b', pointCategory: 'Z', value: 9 },
];

const pointsWithoutPointCategories: CategoryPoint[] = [
  { xAxisCategory: 'c', value: 3 },
  { xAxisCategory: 'a', value: 1 },
  { xAxisCategory: 'b', value: 2 },
];

describe('boxplot-utils', () => {
  beforeEach(() => jest.clearAllMocks());
  afterAll(() => {
    consoleWarnSpy.mockRestore();
    jest.restoreAllMocks();
  });

  describe('getPointStyleFromArray', () => {
    const pointCategories = ['a', 'b', 'c'];
    const shapes = ['circle', 'triangle'];
    const styleType = 'shape';
    const notFoundValue = 'none';

    it('should handle valid point categories', () => {
      expect(
        getPointStyleFromArray('a', pointCategories, shapes, styleType, notFoundValue),
      ).toEqual('circle');
      expect(
        getPointStyleFromArray('b', pointCategories, shapes, styleType, notFoundValue),
      ).toEqual('triangle');
      expect(consoleWarnSpy).not.toHaveBeenCalled();
    });

    it('should reuse values if there are more categories than values', () => {
      expect(getPointStyleFromArray('c', pointCategories, shapes, 'shape', 'none')).toEqual(
        'circle',
      );
      expect(consoleWarnSpy).toHaveBeenCalledTimes(1);
    });

    it('should return notFoundValue if category is not found', () => {
      expect(
        getPointStyleFromArray('d', pointCategories, shapes, styleType, notFoundValue),
      ).toEqual(notFoundValue);
      expect(consoleWarnSpy).toHaveBeenCalledTimes(1);
    });

    it('should return notFoundValue if category is undefined', () => {
      expect(
        getPointStyleFromArray(undefined, pointCategories, shapes, styleType, notFoundValue),
      ).toEqual(notFoundValue);
      expect(consoleWarnSpy).toHaveBeenCalledTimes(1);
    });
  });

  describe('getCategoryPointShape', () => {
    it('should return first shape for the first point category', () => {
      expect(getCategoryPointShape('X', ['X', 'Y', 'Z'])).toEqual('circle');
      expect(consoleWarnSpy).not.toHaveBeenCalled();
    });

    it('should return second shape for the second point category', () => {
      expect(getCategoryPointShape('Y', ['X', 'Y', 'Z'])).toEqual('triangle');
      expect(consoleWarnSpy).not.toHaveBeenCalled();
    });

    it('should return default when point category is not found', () => {
      expect(getCategoryPointShape('WW', ['X', 'Y', 'Z'])).toEqual('none');
      expect(consoleWarnSpy).toHaveBeenCalledTimes(1);
    });
  });

  describe('getCategoryPointColor', () => {
    it('should return first color for the first point category', () => {
      expect(getCategoryPointColor('X', ['X', 'Y', 'Z'], ['blue', 'red', 'yellow'])).toEqual(
        'blue',
      );
      expect(consoleWarnSpy).not.toHaveBeenCalled();
    });

    it('should return second color for the second point category', () => {
      expect(getCategoryPointColor('Y', ['X', 'Y', 'Z'], ['blue', 'red', 'yellow'])).toEqual('red');
      expect(consoleWarnSpy).not.toHaveBeenCalled();
    });

    it('should return default when point category is not found', () => {
      expect(getCategoryPointColor('WW', ['X', 'Y', 'Z'])).toEqual('transparent');
      expect(consoleWarnSpy).toHaveBeenCalledTimes(1);
    });
  });

  describe('getCategoryPointStyle', () => {
    const pointCategories = ['X', 'Y', 'Z'];

    it('should return default value when hasPointCategories is false', () => {
      const result = getCategoryPointStyle(
        'X',
        false,
        { X: 'triangle' },
        getCategoryPointShape,
        pointCategories,
        'circle',
      );
      expect(result).toEqual('circle');
    });

    it('should return custom style when point category in custom mapping', () => {
      const customShapes = { X: 'triangle', Y: 'rect' };
      const result = getCategoryPointStyle(
        'X',
        true,
        customShapes,
        getCategoryPointShape,
        pointCategories,
        'circle',
      );
      expect(result).toEqual('triangle');
    });

    it('should return default when point category not in custom mapping', () => {
      const customShapes = { X: 'triangle', Y: 'rect' };
      const result = getCategoryPointStyle(
        'Z',
        true,
        customShapes,
        getCategoryPointShape,
        pointCategories,
        'circle',
      );
      expect(result).toEqual('circle');
    });

    it('should use default style fucntion when no custom mapping is provided', () => {
      const result = getCategoryPointStyle(
        'X',
        true,
        undefined,
        getCategoryPointShape,
        pointCategories,
        'triangle',
      );
      expect(result).toEqual('circle');
    });
  });

  describe('getUniqueValues', () => {
    it('should return sorted, unique values when specified', () => {
      expect(getUniqueValues(pointsWithPointCategories, 'xAxisCategory', true)).toEqual([
        'a',
        'b',
        'c',
      ]);

      expect(getUniqueValues(pointsWithPointCategories, 'pointCategory', true)).toEqual([
        'X',
        'Y',
        'Z',
      ]);
    });

    it('should return unsorted, unique values by default', () => {
      expect(getUniqueValues(pointsWithPointCategories, 'xAxisCategory')).toEqual(['c', 'a', 'b']);

      expect(getUniqueValues(pointsWithPointCategories, 'pointCategory')).toEqual(['Y', 'X', 'Z']);
    });

    it('should handle all undefined values', () => {
      expect(getUniqueValues(pointsWithoutPointCategories, 'pointCategory')).toEqual([]);
    });
  });

  describe('getRandomNumber', () => {
    it('returns point within expected range', () => {
      const testPairs = [
        { min: 0, max: 1 },
        { min: -0.02, max: 0.02 },
        { min: 100, max: 441 },
        { min: -230, max: -5 },
      ];

      testPairs.forEach((testPair) => {
        const min = testPair.min;
        const max = testPair.max;

        const num = getRandomNumber(min, max);
        expect(num).toBeGreaterThan(min);
        expect(num).toBeLessThan(max);
      });
    });
  });

  describe('getOffsetAndJitteredXValue', () => {
    it('1 point', () => {
      expect(getOffsetAndJitteredXValue(2, 0, 1)).toEqual(2);
    });

    it('2 points', () => {
      expect(getOffsetAndJitteredXValue(2, 0, 2)).toEqual(1.9);
      expect(getOffsetAndJitteredXValue(2, 1, 2)).toEqual(2.1);
    });

    it('3 points', () => {
      expect(getOffsetAndJitteredXValue(2, 0, 3)).toEqual(1.8);
      expect(getOffsetAndJitteredXValue(2, 1, 3)).toEqual(2);
      expect(getOffsetAndJitteredXValue(2, 2, 3)).toEqual(2.2);
    });

    it('4 points', () => {
      expect(getOffsetAndJitteredXValue(2, 0, 4)).toEqual(1.7);
      expect(getOffsetAndJitteredXValue(2, 1, 4)).toEqual(1.9);
      expect(getOffsetAndJitteredXValue(2, 2, 4)).toEqual(2.1);
      expect(getOffsetAndJitteredXValue(2, 3, 4)).toEqual(2.3);
    });

    it('prevents overlap with next category when there are more subcategories than can be offset using default offset', () => {
      expect(getOffsetAndJitteredXValue(2, 0, 6)).toEqual(1.55);
      expect(getOffsetAndJitteredXValue(2, 5, 6)).toEqual(2.45);
    });

    it('jitters xValues when jitterMax is greater than 0', () => {
      const jitterMax = 0.02;
      const offsetXValue = 1.9;
      for (let i = 0; i < 10; i++) {
        const xValue = getOffsetAndJitteredXValue(2, 0, 2, defaultPtOffset, jitterMax);
        expect(xValue).toBeLessThan(offsetXValue + jitterMax);
        expect(xValue).toBeGreaterThan(offsetXValue - jitterMax);
      }
    });

    it('does not jitter xValues when there is only one category', () => {
      expect(getOffsetAndJitteredXValue(2, 0, 1, defaultPtOffset, 0.1)).toEqual(2);
    });
  });

  describe('addXAxisValueToCategoryPoint', () => {
    it('handles points without pointCategory values', () => {
      const valuePoints: CategoryAsValuePoint[] = [
        { xAxisCategory: 'c', value: 3, xAxisValue: 3 },
        { xAxisCategory: 'a', value: 1, xAxisValue: 1 },
        { xAxisCategory: 'b', value: 2, xAxisValue: 2 },
      ];

      const updatedPoints = addXAxisValueToCategoryPoint(
        pointsWithoutPointCategories,
        ['a', 'b', 'c'],
        [],
      );
      valuePoints.forEach((valuePt, index) => {
        expect(valuePt.xAxisValue).toBeCloseTo(updatedPoints[index].xAxisValue);
      });
    });

    it('handles multiple points per pointCategory', () => {
      const valuePoints: CategoryAsValuePoint[] = [
        { xAxisCategory: 'c', pointCategory: 'Y', value: 1, xAxisValue: 3 },
        { xAxisCategory: 'c', pointCategory: 'X', value: 2, xAxisValue: 2.8 },
        { xAxisCategory: 'c', pointCategory: 'Z', value: 3, xAxisValue: 3.2 },
        { xAxisCategory: 'a', pointCategory: 'Y', value: 4, xAxisValue: 1 },
        { xAxisCategory: 'a', pointCategory: 'X', value: 5, xAxisValue: 0.8 },
        { xAxisCategory: 'a', pointCategory: 'Z', value: 6, xAxisValue: 1.2 },
        { xAxisCategory: 'b', pointCategory: 'Y', value: 7, xAxisValue: 2 },
        { xAxisCategory: 'b', pointCategory: 'X', value: 8, xAxisValue: 1.8 },
        { xAxisCategory: 'b', pointCategory: 'Z', value: 9, xAxisValue: 2.2 },
      ];

      const updatedPoints = addXAxisValueToCategoryPoint(
        pointsWithPointCategories,
        ['a', 'b', 'c'],
        ['X', 'Y', 'Z'],
      );
      valuePoints.forEach((valuePt, index) => {
        expect(valuePt.xAxisValue).toBeCloseTo(updatedPoints[index].xAxisValue);
      });
    });
  });

  describe('addXAxisValueToBoxplotSummaries', () => {
    it('correctly maps xAxisCategory to xAxisValue', () => {
      const input: CategoryBoxplotSummary[] = [
        {
          xAxisCategory: 'c',
          min: 10,
          firstQuartile: 20,
          median: 30,
          thirdQuartile: 40,
          max: 50,
        },
        {
          xAxisCategory: 'a',
          min: 15,
          firstQuartile: 25,
          median: 35,
          thirdQuartile: 45,
          max: 55,
        },
        {
          xAxisCategory: 'b',
          min: 20,
          firstQuartile: 30,
          median: 40,
          thirdQuartile: 50,
          max: 60,
        },
      ];
      const output: CategoryAsValueBoxplotSummary[] = [
        {
          xAxisCategory: 'c',
          min: 10,
          firstQuartile: 20,
          median: 30,
          thirdQuartile: 40,
          max: 50,
          xAxisValue: 3,
        },
        {
          xAxisCategory: 'a',
          min: 15,
          firstQuartile: 25,
          median: 35,
          thirdQuartile: 45,
          max: 55,
          xAxisValue: 1,
        },
        {
          xAxisCategory: 'b',
          min: 20,
          firstQuartile: 30,
          median: 40,
          thirdQuartile: 50,
          max: 60,
          xAxisValue: 2,
        },
      ];

      expect(addXAxisValueToBoxplotSummaries(input, ['a', 'b', 'c'])).toEqual(output);
    });
  });

  describe('formatCategoryPointsForBoxplotTransform', () => {
    it('correctly restructures data', () => {
      const ptsBoxplotTransform = [[], [1], [2], [3]];
      expect(
        formatCategoryPointsForBoxplotTransform(pointsWithoutPointCategories, ['a', 'b', 'c']),
      ).toEqual(ptsBoxplotTransform);
    });

    it('ignores point categories', () => {
      const ptsBoxplotTransform = [[], [4, 5, 6], [7, 8, 9], [1, 2, 3]];
      expect(
        formatCategoryPointsForBoxplotTransform(pointsWithPointCategories, ['a', 'b', 'c']),
      ).toEqual(ptsBoxplotTransform);
    });
  });
});
