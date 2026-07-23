import { Pipe, PipeTransform } from '@angular/core';
import { capitalizeFirstLetter } from '../string/capitalize-first-letter';

@Pipe({
  name: 'capitalizeFirstLetter',
  standalone: true,
})
export class CapitalizeFirstLetterPipe implements PipeTransform {
  transform(value: string | null | undefined): string {
    return capitalizeFirstLetter(value);
  }
}
