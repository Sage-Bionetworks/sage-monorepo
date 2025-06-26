import { Component, Input } from '@angular/core';

@Component({
  selector: 'explorers-wiki',
  standalone: true,
  template: '<div>wiki</div>',
})
export class MockWikiComponent {
  @Input() params: any;
}
