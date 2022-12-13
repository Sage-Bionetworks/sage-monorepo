import { FilterValue } from './filter-value.model';

const thisYear = new Date().getFullYear();

const updateYear = (
  thisYear: number,
  startYearDiff: number,
  endYearDiff: number
) => {
  return {
    start: new Date(thisYear + startYearDiff, 0, 1),
    end: new Date(thisYear + endYearDiff, 11, 31),
  };
};

export const challengeStartYearRangeFilterValues: FilterValue[] = [
  {
    value: undefined,
    label: 'All',
    active: true,
  },
  {
    value: updateYear(thisYear, 1, 1),
    label: (thisYear + 1).toString(),
    active: false,
  },
  {
    value: updateYear(thisYear, 0, 0),
    label: thisYear.toString(),
    active: false,
  },
  {
    value: updateYear(thisYear, -1, -1),
    label: (thisYear - 1).toString(),
    active: false,
  },
  {
    value: updateYear(thisYear, -2, -2),
    label: (thisYear - 2).toString(),
    active: false,
  },
  {
    value: updateYear(thisYear, -6, -3),
    label: thisYear - 6 + ' - ' + (thisYear - 3),
    active: false,
  },
  {
    value: updateYear(thisYear, -11, -7),
    label: thisYear - 11 + ' - ' + (thisYear - 7),
    active: false,
  },
  {
    value: updateYear(thisYear, -17, -12),
    label: thisYear - 17 + ' - ' + (thisYear - 12),
    active: false,
  },
  {
    value: updateYear(thisYear, -28, -18),
    label: thisYear - 28 + ' - ' + (thisYear - 18),
    active: false,
  },
  {
    value: 'custom',
    label: 'Custom',
    active: false,
  },
];
