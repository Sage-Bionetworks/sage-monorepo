import { CELL_CLASSES, CellClassId } from '@sagebionetworks/qtl/config';
import { CELL_CLASS_MUTED_COLOR_PERCENT, getCellClassMutedColor } from './cell-classes';

describe('getCellClassMutedColor', () => {
  it('lightens the cell class color by mixing it with white', () => {
    const astrocyte: CellClassId = 'class_astro';
    expect(getCellClassMutedColor(astrocyte)).toBe(
      `color-mix(in srgb, ${CELL_CLASSES[astrocyte].color} ${CELL_CLASS_MUTED_COLOR_PERCENT}%, var(--color-white))`,
    );
  });
});
