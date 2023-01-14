import { Component, Input } from '@angular/core';

@Component({
  selector: 'openchallenges-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent {
  @Input() version = 'x.y.z';
}
