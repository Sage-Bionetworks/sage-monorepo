import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'commaSeparate',
  standalone: true,
})
export class CommaSeparatePipe implements PipeTransform {
  transform(value: string[] | null | undefined): string {
    if (!Array.isArray(value)) return '';
    return value.join(', ');
  }
}
