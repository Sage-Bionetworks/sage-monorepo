import { CELL_CLASSES, CellClassId } from '@sagebionetworks/qtl/config';

export const CELL_CLASS_MUTED_COLOR_PERCENT = 50;

export function getCellClassMutedColor(cellClassId: CellClassId): string {
  return `color-mix(in srgb, ${CELL_CLASSES[cellClassId].color} ${CELL_CLASS_MUTED_COLOR_PERCENT}%, var(--color-white))`;
}
