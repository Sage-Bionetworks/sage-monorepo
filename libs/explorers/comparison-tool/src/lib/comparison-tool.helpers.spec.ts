import { TableLazyLoadEvent } from 'primeng/table';
import { getPaginationParams } from './comparison-tool.helpers';
import { NUMBER_OF_ROWS_TO_DISPLAY } from './comparison-tool.variables';

describe('comparison-tool.helpers', () => {
  describe('getPaginationParams', () => {
    it('should calculate correct pagination for first page', () => {
      const event: TableLazyLoadEvent = {
        first: 0,
        rows: 10,
      };

      const result = getPaginationParams(event);

      expect(result).toEqual({
        pageNumber: 0,
        pageSize: 10,
      });
    });

    it('should calculate correct pagination for second page', () => {
      const event: TableLazyLoadEvent = {
        first: 10,
        rows: 10,
      };

      const result = getPaginationParams(event);

      expect(result).toEqual({
        pageNumber: 1,
        pageSize: 10,
      });
    });

    it('should calculate correct pagination for third page', () => {
      const event: TableLazyLoadEvent = {
        first: 20,
        rows: 10,
      };

      const result = getPaginationParams(event);

      expect(result).toEqual({
        pageNumber: 2,
        pageSize: 10,
      });
    });

    it('should handle different page sizes', () => {
      const event: TableLazyLoadEvent = {
        first: 25,
        rows: 25,
      };

      const result = getPaginationParams(event);

      expect(result).toEqual({
        pageNumber: 1,
        pageSize: 25,
      });
    });

    it('should use default page size when rows is undefined', () => {
      const event: TableLazyLoadEvent = {
        first: 0,
      };

      const result = getPaginationParams(event);

      expect(result).toEqual({
        pageNumber: 0,
        pageSize: NUMBER_OF_ROWS_TO_DISPLAY,
      });
    });

    it('should use default page size when rows is null', () => {
      const event: TableLazyLoadEvent = {
        first: 0,
        rows: null as unknown as number,
      };

      const result = getPaginationParams(event);

      expect(result).toEqual({
        pageNumber: 0,
        pageSize: NUMBER_OF_ROWS_TO_DISPLAY,
      });
    });

    it('should default to page 0 when first is undefined', () => {
      const event: TableLazyLoadEvent = {
        rows: 10,
      };

      const result = getPaginationParams(event);

      expect(result).toEqual({
        pageNumber: 0,
        pageSize: 10,
      });
    });

    it('should default to page 0 when first is null', () => {
      const event: TableLazyLoadEvent = {
        first: null as unknown as number,
        rows: 10,
      };

      const result = getPaginationParams(event);

      expect(result).toEqual({
        pageNumber: 0,
        pageSize: 10,
      });
    });

    it('should handle both first and rows being undefined', () => {
      const event: TableLazyLoadEvent = {};

      const result = getPaginationParams(event);

      expect(result).toEqual({
        pageNumber: 0,
        pageSize: NUMBER_OF_ROWS_TO_DISPLAY,
      });
    });

    it('should correctly calculate page number with non-standard offsets', () => {
      const event: TableLazyLoadEvent = {
        first: 15,
        rows: 10,
      };

      const result = getPaginationParams(event);

      expect(result).toEqual({
        pageNumber: 1,
        pageSize: 10,
      });
    });

    it('should handle large page numbers', () => {
      const event: TableLazyLoadEvent = {
        first: 1000,
        rows: 50,
      };

      const result = getPaginationParams(event);

      expect(result).toEqual({
        pageNumber: 20,
        pageSize: 50,
      });
    });

    it('should handle zero rows per page (edge case)', () => {
      const event: TableLazyLoadEvent = {
        first: 0,
        rows: 0,
      };

      const result = getPaginationParams(event);

      // When rows is 0, Math.floor(0/0) = NaN, but the nullish coalescing
      // operator doesn't catch 0, so we get NaN
      expect(result.pageNumber).toBeNaN();
      expect(result.pageSize).toBe(0);
    });
  });
});
