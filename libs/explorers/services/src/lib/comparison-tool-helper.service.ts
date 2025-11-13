import { Injectable } from '@angular/core';
import {
  ComparisonToolConfig,
  ComparisonToolConfigColumn,
  ComparisonToolLink,
  HeatmapCircleData,
  VisualizationOverviewPane,
} from '@sagebionetworks/explorers/models';

@Injectable({
  providedIn: 'root',
})
export class ComparisonToolHelperService {
  createVisualizationOverviewPane(heading: string, htmlContent: string): VisualizationOverviewPane {
    return {
      heading,
      content: htmlContent,
    };
  }

  getComparisonToolDataFilename(config: ComparisonToolConfig): string {
    const pageName = config.page.toLowerCase().replaceAll(' ', '_');
    const primaryCategory = config.dropdowns?.[0];
    if (primaryCategory) {
      const categoryName = primaryCategory
        .toLowerCase()
        .replaceAll('-', '')
        .replaceAll(/\s+/g, ' ')
        .replaceAll(' ', '_');
      return `${pageName}_${categoryName}`;
    }
    return pageName;
  }

  buildComparisonToolCsvRows(
    data: Record<string, unknown>[],
    config: ComparisonToolConfig,
    siteUrl: string,
    heatmapCategoryColumnName = 'heatmap',
  ): string[][] {
    const exportedColumns = config.columns.filter((col) => col.is_exported !== false);
    const heatmapColumns = exportedColumns.filter((col) => col.type === 'heat_map');
    const baseColumns = exportedColumns.filter((col) => col.type !== 'heat_map');

    const heatmapMetrics = heatmapColumns.length
      ? this.collectHeatmapMetrics(data, heatmapColumns)
      : [];

    const headerRow = this.buildHeaderRow(
      exportedColumns,
      heatmapMetrics,
      heatmapCategoryColumnName,
    );

    const rows = data.flatMap((item) =>
      this.buildRows(
        item,
        baseColumns,
        heatmapColumns,
        heatmapMetrics,
        headerRow,
        siteUrl,
        heatmapCategoryColumnName,
      ),
    );

    return [headerRow, ...rows];
  }

  protected buildHeaderRow(
    columns: ComparisonToolConfigColumn[],
    heatmapMetrics: string[],
    heatmapCategoryColumnName: string,
  ): string[] {
    const headerRow: string[] = [];
    let heatmapInserted = false;

    for (const column of columns) {
      if (column.type === 'heat_map') {
        if (!heatmapInserted) {
          headerRow.push(heatmapCategoryColumnName, ...heatmapMetrics);
          heatmapInserted = true;
        }
      } else {
        headerRow.push(column.data_key);
      }
    }

    return headerRow;
  }

  protected collectHeatmapMetrics(
    data: Record<string, unknown>[],
    heatmapColumns: ComparisonToolConfigColumn[],
  ): string[] {
    const firstRecord = data[0];
    if (!firstRecord) {
      return [];
    }

    for (const column of heatmapColumns) {
      const heatmapData = firstRecord[column.data_key] as HeatmapCircleData | undefined;
      if (heatmapData) {
        return Object.keys(heatmapData);
      }
    }

    return [];
  }

  protected buildRows(
    item: Record<string, unknown>,
    baseColumns: ComparisonToolConfigColumn[],
    heatmapColumns: ComparisonToolConfigColumn[],
    heatmapMetrics: string[],
    headerRow: string[],
    siteUrl: string,
    heatmapCategoryColumnName: string,
  ): string[][] {
    const baseValues: Record<string, string> = {};

    for (const column of baseColumns) {
      const itemData = item[column.data_key];
      baseValues[column.data_key] =
        itemData === null || itemData === undefined
          ? ''
          : this.getNonHeatmapValue(column.type, itemData, column, siteUrl);
    }

    if (heatmapColumns.length === 0) {
      return [headerRow.map((key) => baseValues[key] ?? '')];
    }

    return heatmapColumns.map((column) => {
      const rowValues: Record<string, string> = { ...baseValues };
      const heatmapData = item[column.data_key] as
        | Record<string, number | null | undefined>
        | undefined;

      rowValues[heatmapCategoryColumnName] = column.data_key;

      for (const metric of heatmapMetrics) {
        const metricValue = heatmapData?.[metric];
        rowValues[metric] =
          metricValue === null || metricValue === undefined ? '' : String(metricValue);
      }

      return headerRow.map((key) => rowValues[key] ?? '');
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
          return (itemData as string[]).join(',');
        }
        return String(itemData);
      case 'link_external':
      case 'link_internal': {
        const linkUrlData = (itemData as ComparisonToolLink).link_url || col.link_url;
        const linkUrl = colType === 'link_internal' ? `${siteUrl}/${linkUrlData}` : linkUrlData;
        return linkUrl;
      }
      default:
        return String(itemData);
    }
  }
}
