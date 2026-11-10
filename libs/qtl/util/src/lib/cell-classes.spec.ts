import { CELL_CLASSES } from '@sagebionetworks/qtl/config';
import { getCellTypeMutedColor } from './cell-classes';

describe('getCellTypeMutedColor', () => {
  it('returns the cell type muted color when defined', () => {
    expect(getCellTypeMutedColor('Astrocyte')).toBe(CELL_CLASSES.Astrocyte.mutedColor);
  });

  it('falls back to the gray CSS variable when mutedColor is undefined', () => {
    const original = CELL_CLASSES['Immune Cell'].mutedColor;
    (CELL_CLASSES['Immune Cell'] as { mutedColor?: string }).mutedColor = undefined;
    try {
      expect(getCellTypeMutedColor('Immune Cell')).toBe('var(--color-gray-300)');
    } finally {
      (CELL_CLASSES['Immune Cell'] as { mutedColor?: string }).mutedColor = original;
    }
  });
});
