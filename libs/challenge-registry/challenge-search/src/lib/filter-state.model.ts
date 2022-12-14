import { DateRange } from '@sagebionetworks/api-client-angular-deprecated';

export interface FilterState {
  /* The name of the filter. */
  name: string;
  /* */
  value:
    | number
    | string
    | DateRange
    | undefined
    | (number | string | DateRange | undefined)[];
}
