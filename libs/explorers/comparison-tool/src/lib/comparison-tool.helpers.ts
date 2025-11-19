import { TableLazyLoadEvent } from 'primeng/table';
import { NUMBER_OF_ROWS_TO_DISPLAY, PaginationParams } from './comparison-tool.variables';

/**
 * Calculates pagination parameters from a PrimeNG TableLazyLoadEvent
 * @param event The lazy load event from PrimeNG table
 * @returns Object containing pageNumber (zero-based) and pageSize
 */
export function getPaginationParams(event: TableLazyLoadEvent): PaginationParams {
  const pageNumber = Math.floor((event.first ?? 0) / (event.rows ?? NUMBER_OF_ROWS_TO_DISPLAY));
  const pageSize = event.rows ?? NUMBER_OF_ROWS_TO_DISPLAY;
  return { pageNumber, pageSize };
}
