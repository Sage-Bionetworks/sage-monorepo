import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NxWelcomeComponent } from './nx-welcome.component';
import { FooterComponent } from '@sagebionetworks/model-ad/ui';

@Component({
  standalone: true,
  imports: [NxWelcomeComponent, RouterModule, FooterComponent],
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  title = 'model-ad-app';
}
