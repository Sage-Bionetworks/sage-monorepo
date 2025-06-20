import { Component, input } from '@angular/core';

import { RouterModule } from '@angular/router';
import { NavigationLink } from '@sagebionetworks/explorers/models';

@Component({
  selector: 'explorers-footer',
  imports: [RouterModule],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent {
  footerLogoPath = input('');
  footerLinks = input<NavigationLink[]>([]);
  siteVersion = input('');
  dataVersion = input('');
}
