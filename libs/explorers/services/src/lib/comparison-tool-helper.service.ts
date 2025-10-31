import { Injectable } from '@angular/core';
import {
  ComparisonToolConfig,
  ComparisonToolLink,
  HeatmapCircleData,
} from '@sagebionetworks/explorers/models';

@Injectable({
  providedIn: 'root',
})
export class ComparisonToolHelperService {
  reshapeComparisonToolDataToStringArray(
    data: Record<string, unknown>[],
    config: ComparisonToolConfig,
    siteUrl: string,
  ): string[][] {
    const columns = config.columns;
    const heatmapCols = this.getHeatmapColumns(columns, data);
    const headerRow = this.getHeaderRow(columns, heatmapCols);
    const rows = this.getDataRows(data, columns, heatmapCols, siteUrl);
    return [headerRow, ...rows];
  }

  protected getHeatmapColumns(
    columns: ComparisonToolConfig['columns'],
    data: Record<string, unknown>[],
  ): string[] {
    let hasHeatmapCol = false;
    const heatmapCols: string[] = [];
    for (const col of columns) {
      if (col.type === 'heat_map' && !hasHeatmapCol) {
        heatmapCols.push('tissue');
        const heatmapData = data[0][col.data_key] as HeatmapCircleData;
        const heatmapKeys = Object.keys(heatmapData);
        heatmapCols.push(...heatmapKeys);
        hasHeatmapCol = true;
      }
    }
    return heatmapCols;
  }

  protected getHeaderRow(
    columns: ComparisonToolConfig['columns'],
    heatmapCols: string[],
  ): string[] {
    const headerRow: string[] = [];
    for (const col of columns) {
      if (col.type === 'heat_map') {
        continue;
      } else {
        headerRow.push(col.data_key);
      }
    }
    headerRow.push(...heatmapCols); // ensure heatmap columns are at the end
    return headerRow;
  }

  protected getDataRows(
    data: Record<string, unknown>[],
    columns: ComparisonToolConfig['columns'],
    heatmapCols: string[],
    siteUrl: string,
  ): string[][] {
    return data.flatMap((item) => {
      const nonHeatmapValues = columns
        .filter((col) => col.type !== 'heat_map')
        .map((col) => {
          const itemData = item[col.data_key];
          return itemData === null || itemData === undefined
            ? ''
            : this.getNonHeatmapValue(col.type, itemData, col, siteUrl);
        });
      const allRows = this.getHeatmapRows(item, columns, heatmapCols, nonHeatmapValues);
      return allRows.length > 0 ? allRows : [nonHeatmapValues];
    });
  }

  protected getHeatmapRows(
    item: Record<string, unknown>,
    columns: ComparisonToolConfig['columns'],
    heatmapCols: string[],
    nonHeatmapValues: string[],
  ): string[][] {
    return columns
      .filter((col) => col.type === 'heat_map')
      .flatMap((col) => {
        const heatmapData = item[col.data_key] as HeatmapCircleData;
        if (!heatmapData) return [];
        return [
          [
            ...nonHeatmapValues,
            col.data_key,
            ...heatmapCols.slice(1).map((key) => String(heatmapData[key])),
          ],
        ];
      });
  }

  protected getNonHeatmapValue(
    colType: string,
    itemData: unknown,
    col: any,
    siteUrl: string,
  ): string {
    switch (colType) {
      case 'text':
        if (Array.isArray(itemData)) {
          return (itemData as string[]).join('; ');
        } else {
          return itemData as string;
        }
      case 'link_external':
      case 'link_internal': {
        const linkUrlData = (itemData as ComparisonToolLink).link_url || col.link_url;
        const linkUrl = colType === 'link_internal' ? `${siteUrl}/${linkUrlData}` : linkUrlData;
        return linkUrl;
      }
      default:
        return itemData as string;
    }
  }
}
