import { CategoryAsValuePoint, CategoryBoxplotSummary, CategoryPoint } from '../models';
import { CategoryAsValueBoxplotSummary } from '../models/boxplot';

export const getPointStyleFromArray = (
  pointCategory: string | undefined,
  pointCategories: string[],
  styleValues: string[],
  styleType: string,
  notFoundValue: string,
) => {
  const pointIndex = pointCategories.indexOf(pointCategory || '');
  if (pointIndex < 0) {
    console.warn(`Point category ${pointCategory} not found.`);
    return notFoundValue;
  }
  if (pointIndex >= styleValues.length) {
    console.warn(
      `More point category values provided than possible ${styleType}s. Will re-use an existing ${styleType}.`,
    );
    return styleValues[pointIndex % styleValues.length];
  }
  return styleValues[pointIndex];
};

export const getCategoryPointShape = (
  pointCategory: string | undefined,
  pointCategories: string[],
  shapes: string[] = ['circle', 'triangle', 'rect', 'roundRect', 'diamond', 'pin', 'arrow'],
) => {
  return getPointStyleFromArray(pointCategory, pointCategories, shapes, 'shape', 'none');
};

export const getCategoryPointColor = (
  pointCategory: string | undefined,
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
  ],
) => {
  return getPointStyleFromArray(pointCategory, pointCategories, colors, 'color', 'transparent');
};

export const getCategoryPointStyle = <T>(
  pointCategory: string | undefined,
  hasPointCategories: boolean,
  customMapping: Record<string, T> | undefined,
  defaultStyleFunction: (pointCategory: string | undefined, categories: string[]) => T,
  pointCategories: string[],
  defaultValue: T,
): T => {
  if (!hasPointCategories) {
    return defaultValue;
  }

  if (customMapping) {
    const customStyle = pointCategory && customMapping[pointCategory];
    return customStyle || defaultValue;
  } else {
    return defaultStyleFunction(pointCategory, pointCategories);
  }
};

export function getUniqueValues(
  values: Record<string, unknown>[],
  key: string,
  sortValues = false,
): unknown[] {
  const outputVals: unknown[] = [];
  values.forEach((val) => {
    const value = val[key];
    if (value && outputVals.indexOf(value) === -1) outputVals.push(value);
  });
  if (sortValues) outputVals.sort();
  return outputVals;
}

export function getRandomNumber(min: number, max: number) {
  const range = max - min;
  return Math.random() * range + min;
}

// Will calculate a new x-axis value for a point, such that the nPoints
// will be equally spaced in the x direction around an initial xValue and this
// point will be offset based on its index among those nPoints. Optionally, the points within
// each category will also be jittered when there is >1 category.
export function getOffsetAndJitteredXValue(
  xValue: number,
  pointIndex: number,
  nPoints: number,
  offset = 0.2,
  jitterMax = 0, // set to positive value to jitter points
) {
  if (nPoints === 1) return xValue;
  const totalDistance = Math.min(offset * (nPoints - 1), 0.9);
  const startingXValue = xValue - totalDistance / 2;
  const pointOffset = (pointIndex * totalDistance) / (nPoints - 1);
  const offsetXValue = startingXValue + pointOffset;
  const jitter = getRandomNumber(jitterMax * -1, jitterMax);

  return offsetXValue + jitter;
}

function getXAxisValueOfXAxisCategory(xAxisCategories: string[], xAxisCategory: string) {
  const xAxisCategoryIndex = xAxisCategories.indexOf(xAxisCategory);
  return xAxisCategoryIndex + 1;
}

// Translates the xAxisCategory into an xAxisValue and will evenly space points
// in the x direction around that xAxisValue if there are 2+ pointCategories.
export function addXAxisValueToCategoryPoint(
  points: CategoryPoint[],
  xAxisCategories: string[],
  pointCategories: string[],
  offset = 0.2,
  jitterMax = 0, // set to positive value to jitter points
): CategoryAsValuePoint[] {
  return points.map((pt) => {
    let xAxisValue = getXAxisValueOfXAxisCategory(xAxisCategories, pt.xAxisCategory);

    // calculate the underlying points offset, if there are point categories
    if (pointCategories.length > 0 && pt.pointCategory) {
      const pointCategoryIndex = pointCategories.indexOf(pt.pointCategory);
      const nPoints = pointCategories.length;
      xAxisValue = getOffsetAndJitteredXValue(
        xAxisValue,
        pointCategoryIndex,
        nPoints,
        offset,
        jitterMax,
      );
    }

    const valuePoint: CategoryAsValuePoint = { ...pt, xAxisValue };
    return valuePoint;
  });
}

export function addXAxisValueToBoxplotSummaries(
  summaries: CategoryBoxplotSummary[],
  xAxisCategories: string[],
): CategoryAsValueBoxplotSummary[] {
  return summaries.map((summary) => {
    const xAxisValue = getXAxisValueOfXAxisCategory(xAxisCategories, summary.xAxisCategory);
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
  xAxisCategories: string[],
) {
  // add a placeholder array, so xAxisCategories mapped to xAxisValues can start from index 1
  const dataForBoxplotTransform: number[][] = [];
  for (let i = 0; i <= xAxisCategories.length; i++) dataForBoxplotTransform.push([]);

  points.forEach((pt) => {
    const xAxisCategoryValue = getXAxisValueOfXAxisCategory(xAxisCategories, pt.xAxisCategory);
    dataForBoxplotTransform[xAxisCategoryValue].push(pt.value);
  });

  return dataForBoxplotTransform;
}
