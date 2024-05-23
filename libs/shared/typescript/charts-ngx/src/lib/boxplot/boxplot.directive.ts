import { Directive, ElementRef, Input, OnInit } from '@angular/core';

@Directive({
  selector: '[sageBoxplot]',
  standalone: true,
})
export class BoxplotDirective implements OnInit {
  @Input() text = '';

  constructor(private el: ElementRef) {
    // add comment so constructor is not empty
  }

  ngOnInit() {
    this.el.nativeElement.textContent = this.text;
  }
}
