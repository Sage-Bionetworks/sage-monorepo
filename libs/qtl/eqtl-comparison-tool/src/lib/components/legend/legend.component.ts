import { Component, ViewEncapsulation } from '@angular/core';
import { SimpleTableCell, SimpleTableColumn } from '@sagebionetworks/explorers/models';
import { SimpleTableComponent } from '@sagebionetworks/explorers/ui';
import { CELL_CLASSES } from '@sagebionetworks/qtl/config';
import {
  LEGEND_CELL_CLASS_COLUMNS,
  LEGEND_CELL_CLASSES_TITLE,
  LEGEND_LOCUS_BROWSER_TITLE,
  LEGEND_MULTIPLE_VARIANTS_EXAMPLE_COUNT,
  LEGEND_SECTIONS,
} from './legend.constants';

const [ABBREVIATION_COLUMN, CELL_CLASS_COLUMN] = LEGEND_CELL_CLASS_COLUMNS;

@Component({
  selector: 'qtl-eqtl-comparison-tool-legend',
  imports: [SimpleTableComponent],
  templateUrl: './legend.component.html',
  styleUrls: ['./legend.component.scss'],
  // The cell class column classes style cells inside SimpleTableComponent's view.
  encapsulation: ViewEncapsulation.None,
})
export class LegendComponent {
  protected readonly locusBrowserTitle = LEGEND_LOCUS_BROWSER_TITLE;
  protected readonly cellClassesTitle = LEGEND_CELL_CLASSES_TITLE;
  protected readonly sections = LEGEND_SECTIONS;
  protected readonly multipleVariantsExampleCount = LEGEND_MULTIPLE_VARIANTS_EXAMPLE_COUNT;

  protected readonly cellClassColumns: SimpleTableColumn[] = [
    { name: ABBREVIATION_COLUMN, className: 'legend-abbreviation-col' },
    { name: CELL_CLASS_COLUMN, className: 'legend-cell-class-col' },
  ];

  protected readonly cellClassRows: SimpleTableCell[][] = Object.values(CELL_CLASSES)
    .toSorted((a, b) => a.short.localeCompare(b.short))
    .map(({ name, short, color }) => [
      { type: 'swatch', color, text: short },
      { type: 'text', value: name },
    ]);
}
