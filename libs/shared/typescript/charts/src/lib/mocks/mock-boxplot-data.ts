import { CategoryBoxplotSummary, CategoryPoint } from '../models';

export const staticBoxplotPoints: CategoryPoint[] = [
  { xAxisCategory: 'CAT1', value: 25 },
  { xAxisCategory: 'CAT2', value: 35 },
  { xAxisCategory: 'CAT3', value: 45 },
  { xAxisCategory: 'CAT4', value: 55 },
];

export const staticBoxplotSummaries: CategoryBoxplotSummary[] = [
  {
    xAxisCategory: 'CAT1',
    min: 10,
    firstQuartile: 20,
    median: 30,
    thirdQuartile: 40,
    max: 50,
  },
  {
    xAxisCategory: 'CAT2',
    min: 15,
    firstQuartile: 25,
    median: 35,
    thirdQuartile: 45,
    max: 55,
  },
  {
    xAxisCategory: 'CAT3',
    min: 20,
    firstQuartile: 30,
    median: 40,
    thirdQuartile: 50,
    max: 60,
  },
  {
    xAxisCategory: 'CAT4',
    min: 20,
    firstQuartile: 30,
    median: 40,
    thirdQuartile: 50,
    max: 60,
  },
];

export const dynamicBoxplotPoints: CategoryPoint[] = [
  { xAxisCategory: 'Control1', value: 573, pointCategory: 'Female' },
  { xAxisCategory: 'Control1', value: 317, pointCategory: 'Female' },
  { xAxisCategory: 'Control1', value: 759, pointCategory: 'Female' },
  { xAxisCategory: 'Control2', value: 809, pointCategory: 'Female' },
  { xAxisCategory: 'Control2', value: 537, pointCategory: 'Female' },
  { xAxisCategory: 'Control2', value: 590, pointCategory: 'Female' },
  { xAxisCategory: 'Experimental', value: 596, pointCategory: 'Female' },
  { xAxisCategory: 'Experimental', value: 416, pointCategory: 'Female' },
  { xAxisCategory: 'Experimental', value: 626, pointCategory: 'Female' },
  { xAxisCategory: 'Control1', value: 877, pointCategory: 'Male' },
  { xAxisCategory: 'Control1', value: 699, pointCategory: 'Male' },
  { xAxisCategory: 'Control1', value: 854, pointCategory: 'Male' },
  { xAxisCategory: 'Control2', value: 550, pointCategory: 'Male' },
  { xAxisCategory: 'Control2', value: 919, pointCategory: 'Male' },
  { xAxisCategory: 'Control2', value: 407, pointCategory: 'Male' },
  { xAxisCategory: 'Experimental', value: 982, pointCategory: 'Male' },
  { xAxisCategory: 'Experimental', value: 336, pointCategory: 'Male' },
  { xAxisCategory: 'Experimental', value: 856, pointCategory: 'Male' },
];
