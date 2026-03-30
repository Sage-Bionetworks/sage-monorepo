import { Component, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ThemeService } from '@sagebionetworks/bixarena/services';
import { NavComponent, FooterComponent } from '@sagebionetworks/bixarena/ui';

@Component({
  imports: [RouterModule, NavComponent, FooterComponent],
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  constructor() {
    inject(ThemeService).init();
  }
}
