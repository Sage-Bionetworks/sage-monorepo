import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'openchallenges-footer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent {
  @Input({ required: true }) appVersion = '';
  @Input({ required: true }) dataUpdatedOn = '';
  @Input({ required: true }) privacyPolicyUrl = '';
  @Input({ required: true }) termsOfUseUrl = '';
}
