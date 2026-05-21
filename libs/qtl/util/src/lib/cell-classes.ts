import { CELL_CLASSES } from '@sagebionetworks/qtl/config';

export function getCellTypeMutedColor(cellType: keyof typeof CELL_CLASSES): string {
  return CELL_CLASSES[cellType].mutedColor ?? 'var(--color-gray-300)';
}
