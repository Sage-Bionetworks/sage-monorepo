import { CandlestickItem } from '../models';

export const candlestickItems: CandlestickItem[] = [
  { xAxisCategory: 'BRAAK', value: 1.05, ciLower: 0.82, ciUpper: 1.34 },
  { xAxisCategory: 'CERAD', value: 0.93, ciLower: 0.71, ciUpper: 1.21 },
  { xAxisCategory: 'COGDX', value: 1.18, ciLower: 0.94, ciUpper: 1.48 },
];

export const candlestickItemsColored: CandlestickItem[] = candlestickItems.map((item, i) => ({
  ...item,
  color: ['#8b8ad1', '#56B4E9', '#009E73'][i],
}));
