import { Component, Input } from '@angular/core';

@Component({
  selector: 'openchallenges-footer',
  standalone: true,
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent {
  @Input() version = 'x.y.z';
}
