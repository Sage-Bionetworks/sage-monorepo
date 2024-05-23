import { Directive } from '@angular/core';

@Directive({
  selector: '[sageBoxplot]',
  standalone: true,
})
export class BoxplotDirective {
  constructor() {
    // add comment so constructor is not empty
  }
}
