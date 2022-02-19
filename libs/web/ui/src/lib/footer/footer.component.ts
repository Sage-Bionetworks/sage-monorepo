import { Component, Input } from '@angular/core';

@Component({
  selector: 'challenge-registry-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent {
  @Input()
  version = '1.0.0';
}
