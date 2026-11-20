import {
  CELL_CLASSES,
  CELL_SUBCLASSES,
  CellClassId,
  CellSubclassId,
  UNKNOWN_CELL_TYPE_COLOR,
} from '@sagebionetworks/qtl/config';
import {
  CELL_CLASS_MUTED_COLOR_PERCENT,
  findCellClass,
  findCellSubclass,
  getCellClassMutedColor,
  resolveCellSubclass,
  unknownCellClass,
  unknownCellSubclass,
} from './cell-classes';

const astrocyte: CellClassId = 'class_astro';
const adaptiveImmuneSubclass: CellSubclassId = 'adaptiveimmune';
const unlistedCellClassId = 'class_ependymal';
const unlistedCellSubclassId = 'ependymal';

describe('findCellClass', () => {
  it('finds a cell class in the color guide', () => {
    expect(findCellClass(astrocyte)).toBe(CELL_CLASSES[astrocyte]);
  });

  it('finds nothing for an ID missing from the color guide', () => {
    expect(findCellClass(unlistedCellClassId)).toBeUndefined();
  });

  it('finds nothing for an object prototype key', () => {
    expect(findCellClass('toString')).toBeUndefined();
  });
});

describe('findCellSubclass', () => {
  it('finds a cell subclass in the color guide, with its cell class', () => {
    const { name, color, cellClassId } = CELL_SUBCLASSES[adaptiveImmuneSubclass];
    expect(findCellSubclass(adaptiveImmuneSubclass)).toEqual({
      name,
      color,
      cellClass: CELL_CLASSES[cellClassId],
    });
  });

  it('finds nothing for an ID missing from the color guide', () => {
    expect(findCellSubclass(unlistedCellSubclassId)).toBeUndefined();
  });
});

describe('resolveCellSubclass', () => {
  it('pairs a cell subclass with its cell class', () => {
    expect(resolveCellSubclass(adaptiveImmuneSubclass).cellClass).toBe(
      CELL_CLASSES[CELL_SUBCLASSES[adaptiveImmuneSubclass].cellClassId],
    );
  });
});

describe('unknownCellClass', () => {
  it('falls back to gray, named by the ID', () => {
    expect(unknownCellClass(unlistedCellClassId)).toEqual({
      name: unlistedCellClassId,
      short: unlistedCellClassId,
      color: UNKNOWN_CELL_TYPE_COLOR,
    });
  });
});

describe('unknownCellSubclass', () => {
  it('falls back to gray for the subclass and its cell class, named by the ID', () => {
    const unknownSubclass = unknownCellSubclass(unlistedCellSubclassId);
    expect(unknownSubclass.name).toBe(unlistedCellSubclassId);
    expect(unknownSubclass.color).toBe(UNKNOWN_CELL_TYPE_COLOR);
    expect(unknownSubclass.cellClass.color).toBe(UNKNOWN_CELL_TYPE_COLOR);
  });
});

describe('getCellClassMutedColor', () => {
  it('lightens the cell class color by mixing it with white', () => {
    expect(getCellClassMutedColor(CELL_CLASSES[astrocyte])).toBe(
      `color-mix(in srgb, ${CELL_CLASSES[astrocyte].color} ${CELL_CLASS_MUTED_COLOR_PERCENT}%, var(--color-white))`,
    );
  });
});
