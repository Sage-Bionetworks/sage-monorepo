import {
  CategoryAsValuePoint,
  CategoryBoxplotSummary,
  CategoryPoint,
} from '../models';
import { CategoryAsValueBoxplotSummary } from '../models/boxplot';

export const getPointStyleFromArray = (
  pt: CategoryPoint,
  pointCategories: string[],
  styleValues: string[],
  styleType: string,
  notFoundValue: string
) => {
  const pointIndex = pointCategories.indexOf(pt.pointCategory || '');
  if (pointIndex < 0) {
    console.warn(`Point category ${pt.pointCategory} not found.`);
    return notFoundValue;
  }
  if (pointIndex >= styleValues.length) {
    console.warn(
      `More point category values provided than possible ${styleType}s. Will re-use an existing ${styleType}.`
    );
    return styleValues[pointIndex % styleValues.length];
  }
  return styleValues[pointIndex];
};

export const getCategoryPointShape = (
  pt: CategoryPoint,
  pointCategories: string[],
  shapes: string[] = [
    'circle',
    'triangle',
    'rect',
    'roundRect',
    'diamond',
    'pin',
    'arrow',
  ]
) => {
  return getPointStyleFromArray(pt, pointCategories, shapes, 'shape', 'none');
};

export const getCategoryPointColor = (
  pt: CategoryPoint,
  pointCategories: string[],
  colors: string[] = [
    '#000000',
    '#E69F00',
    '#56B4E9',
    '#009E73',
    '#F0E442',
    '#0072B2',
    '#D55E00',
    '#CC79A7',
  ]
) => {
  return getPointStyleFromArray(
    pt,
    pointCategories,
    colors,
    'color',
    'transparent'
  );
};

export function getSortedUniqueValues(
  values: Record<string, unknown>[],
  key: string
): unknown[] {
  const outputVals: unknown[] = [];
  values.forEach((val) => {
    const value = val[key];
    if (value && outputVals.indexOf(value) === -1) outputVals.push(value);
  });
  outputVals.sort();
  return outputVals;
}

// Will calculate a new x-axis value for a point, such that the nPoints
// will be equally spaced in the x direction around an initial xValue and this
// point will be offset based on its index among those nPoints.
export function getJitteredXValue(
  xValue: number,
  pointIndex: number,
  nPoints: number,
  spacing = 0.2
) {
  if (nPoints === 1) return xValue;
  const totalDistance = Math.min(spacing * (nPoints - 1), 0.9);
  const startingXValue = xValue - totalDistance / 2;
  const pointOffset = (pointIndex * totalDistance) / (nPoints - 1);
  const newXValue = startingXValue + pointOffset;

  // round to nearest hundredth decimal
  return Math.round(newXValue * 100) / 100;
}

function getXAxisValueOfXAxisCategory(
  xAxisCategories: string[],
  xAxisCategory: string
) {
  const xAxisCategoryIndex = xAxisCategories.indexOf(xAxisCategory);
  return xAxisCategoryIndex + 1;
}

// Translates the xAxisCategory into an xAxisValue and will evenly space points
// in the x direction around that xAxisValue if there are 2+ pointCategories.
export function addXAxisValueToCategoryPoint(
  points: CategoryPoint[],
  xAxisCategories: string[],
  pointCategories: string[]
): CategoryAsValuePoint[] {
  return points.map((pt) => {
    let xAxisValue = getXAxisValueOfXAxisCategory(
      xAxisCategories,
      pt.xAxisCategory
    );

    // calculate the underlying points offset, if there are point categories
    if (pointCategories.length > 0 && pt.pointCategory) {
      const pointCategoryIndex = pointCategories.indexOf(pt.pointCategory);
      const nPoints = pointCategories.length;
      xAxisValue = getJitteredXValue(xAxisValue, pointCategoryIndex, nPoints);
    }

    const valuePoint: CategoryAsValuePoint = { ...pt, xAxisValue };
    return valuePoint;
  });
}

export function addXAxisValueToBoxplotSummaries(
  summaries: CategoryBoxplotSummary[],
  xAxisCategories: string[]
): CategoryAsValueBoxplotSummary[] {
  return summaries.map((summary) => {
    const xAxisValue = getXAxisValueOfXAxisCategory(
      xAxisCategories,
      summary.xAxisCategory
    );
    const updatedSummary: CategoryAsValueBoxplotSummary = {
      ...summary,
      xAxisValue,
    };
    return updatedSummary;
  });
}

// Translates points into arrays that can be transformed into boxplot summaries
// per xAxisCategory using Apache Echarts. The array will map to 1-based index
// values of xAxisCategories, so the first array will always be empty.
export function formatCategoryPointsForBoxplotTransform(
  points: CategoryPoint[],
  xAxisCategories: string[]
) {
  // add a placeholder array, so xAxisCategories mapped to xAxisValues can start from index 1
  const dataForBoxplotTransform: number[][] = [];
  for (let i = 0; i <= xAxisCategories.length; i++)
    dataForBoxplotTransform.push([]);

  points.forEach((pt) => {
    const xAxisCategoryValue = getXAxisValueOfXAxisCategory(
      xAxisCategories,
      pt.xAxisCategory
    );
    dataForBoxplotTransform[xAxisCategoryValue].push(pt.value);
  });

  return dataForBoxplotTransform;
}
