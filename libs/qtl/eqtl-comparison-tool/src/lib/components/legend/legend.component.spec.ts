import { CELL_CLASSES, CellClassId } from '@sagebionetworks/qtl/config';
import { render, screen, within } from '@testing-library/angular';
import { LegendComponent } from './legend.component';
import {
  LEGEND_CELL_CLASS_COLUMNS,
  LEGEND_CELL_CLASSES_TITLE,
  LEGEND_LOCUS_BROWSER_TITLE,
  LEGEND_SECTIONS,
} from './legend.constants';

const CELL_CLASS_IDS_BY_ABBREVIATION: CellClassId[] = [
  'class_astro',
  'class_en',
  'class_immune',
  'class_in',
  'class_oligo',
  'class_opc',
  'class_vascular',
];

function getCellClassBodyRows(): HTMLElement[] {
  const table = screen.getByRole('table', { name: LEGEND_CELL_CLASSES_TITLE });
  const [, ...bodyRows] = within(table).getAllByRole('row');
  return bodyRows;
}

describe('LegendComponent', () => {
  it('titles the locus browser and cell class parts of the legend as headings', async () => {
    await render(LegendComponent);
    expect(screen.getByRole('heading', { name: LEGEND_LOCUS_BROWSER_TITLE })).toBeVisible();
    expect(screen.getByRole('heading', { name: LEGEND_CELL_CLASSES_TITLE })).toBeVisible();
  });

  it.each(LEGEND_SECTIONS)('lists what each "$title" symbol means, in order', async (section) => {
    await render(LegendComponent);
    const listItems = within(screen.getByRole('list', { name: section.title })).getAllByRole(
      'listitem',
    );
    expect(listItems).toHaveLength(section.items.length);
    section.items.forEach(({ label }, index) => {
      expect(within(listItems[index]).getByText(label)).toBeVisible();
    });
  });

  it('labels the cell class table columns', async () => {
    await render(LegendComponent);
    const columnHeaders = screen.getAllByRole('columnheader').map((header) => header.textContent);
    expect(columnHeaders.map((header) => header?.trim())).toEqual([...LEGEND_CELL_CLASS_COLUMNS]);
  });

  it('lists every cell class sorted alphabetically by abbreviation', async () => {
    await render(LegendComponent);
    const rowCells = getCellClassBodyRows().map((row) =>
      within(row)
        .getAllByRole('cell')
        .map((cell) => cell.textContent?.trim()),
    );
    expect(rowCells).toEqual(
      CELL_CLASS_IDS_BY_ABBREVIATION.map((id) => [CELL_CLASSES[id].short, CELL_CLASSES[id].name]),
    );
  });

  it('shows each cell class in its color', async () => {
    await render(LegendComponent);
    const [astrocyteRow] = getCellClassBodyRows();
    const swatch = within(astrocyteRow).getByText(CELL_CLASSES.class_astro.short).firstElementChild;
    expect(swatch).toHaveStyle({ backgroundColor: CELL_CLASSES.class_astro.color });
  });
});
