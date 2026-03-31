import { Component, inject } from '@angular/core';
import { ConfigService } from '@sagebionetworks/bixarena/config';

@Component({
  selector: 'bixarena-footer',
  imports: [],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.scss',
})
export class FooterComponent {
  protected readonly version = inject(ConfigService).config.app.version;
}
