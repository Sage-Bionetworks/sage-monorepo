import {
  CELL_CLASSES,
  CELL_SUBCLASSES,
  CellClass,
  CellClassId,
  CellSubclassId,
  UNKNOWN_CELL_TYPE_COLOR,
} from '@sagebionetworks/qtl/config';

export const CELL_CLASS_MUTED_COLOR_PERCENT = 50;

export interface ResolvedCellSubclass {
  name: string;
  color: string;
  cellClass: CellClass;
}

function isCellClassId(id: string): id is CellClassId {
  return Object.hasOwn(CELL_CLASSES, id);
}

function isCellSubclassId(id: string): id is CellSubclassId {
  return Object.hasOwn(CELL_SUBCLASSES, id);
}

export function resolveCellSubclass(cellSubclassId: CellSubclassId): ResolvedCellSubclass {
  const { name, color, cellClassId } = CELL_SUBCLASSES[cellSubclassId];
  return { name, color, cellClass: CELL_CLASSES[cellClassId] };
}

/** Returns `undefined` when the ID isn't in the cell type color guide. */
export function findCellClass(cellClassId: string): CellClass | undefined {
  return isCellClassId(cellClassId) ? CELL_CLASSES[cellClassId] : undefined;
}

/** Returns `undefined` when the ID isn't in the cell type color guide. */
export function findCellSubclass(cellSubclassId: string): ResolvedCellSubclass | undefined {
  return isCellSubclassId(cellSubclassId) ? resolveCellSubclass(cellSubclassId) : undefined;
}

/** A gray fallback, named by the ID, for a cell class missing from the cell type color guide. */
export function unknownCellClass(cellClassId: string): CellClass {
  return { name: cellClassId, short: cellClassId, color: UNKNOWN_CELL_TYPE_COLOR };
}

/** A gray fallback, named by the ID, for a cell subclass missing from the cell type color guide. */
export function unknownCellSubclass(cellSubclassId: string): ResolvedCellSubclass {
  return {
    name: cellSubclassId,
    color: UNKNOWN_CELL_TYPE_COLOR,
    cellClass: unknownCellClass(cellSubclassId),
  };
}

export function getCellClassMutedColor(cellClass: CellClass): string {
  return `color-mix(in srgb, ${cellClass.color} ${CELL_CLASS_MUTED_COLOR_PERCENT}%, var(--color-white))`;
}
