import { CELL_CLASSES, CELL_SUBCLASSES } from '@sagebionetworks/qtl/config';

export function getCellTypeMutedColor(cellType: keyof typeof CELL_CLASSES): string {
  return CELL_CLASSES[cellType].mutedColor ?? 'var(--color-gray-300)';
}

export function getParentCellClassColor(cellSubclass: keyof typeof CELL_SUBCLASSES): string {
  return CELL_CLASSES[CELL_SUBCLASSES[cellSubclass].cellClass].color;
}
