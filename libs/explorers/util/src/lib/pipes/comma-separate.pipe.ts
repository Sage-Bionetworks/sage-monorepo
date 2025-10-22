import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'commaSeparate',
  standalone: true,
})
export class CommaSeparatePipe implements PipeTransform {
  transform(value: string | string[] | null | undefined): string {
    if (!value) return '';
    if (!Array.isArray(value)) return value;
    return value.join(', ');
  }
}
