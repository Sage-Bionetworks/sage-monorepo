import { BoxPlotChartItem, RowChartItem } from '@sagebionetworks/agora/models';

export const boxPlotChartItemMock: BoxPlotChartItem = {
  key: 'ACC',
  value: [-0.256, -0.0036, 0.2503],
  circle: {
    value: -0.0144678061734664,
    tooltip:
      'MSN is not significantly differentially expressed in ACC with a log fold change value of -0.0145 and an adjusted p-value of 0.893.',
  },
  quartiles: [-0.0661, -0.0036, 0.0604],
};

export const rowChartItemMock: RowChartItem = {
  key: ['ACC', 'ENSG00000147065', 'AD Diagnosis (males and females)'],
  value: {
    adj_p_val: 0.893263674388766,
    fc: 0.9900217968674684,
    logfc: -0.0144678061734664,
  },
  tissue: 'ACC',
  ci_l: -0.133332670728704,
  ci_r: 0.104397058381771,
};
