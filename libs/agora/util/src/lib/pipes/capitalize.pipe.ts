import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'capitalize',
})
export class CapitalizePipe implements PipeTransform {
  transform(value: any): string {
    if (typeof value === 'boolean') {
      return value.toString().charAt(0).toUpperCase() + value.toString().slice(1);
    }
    if (typeof value === 'string') {
      if (value === 'true') return 'True';
      if (value === 'false') return 'False';
    }
    return value;
  }
}
