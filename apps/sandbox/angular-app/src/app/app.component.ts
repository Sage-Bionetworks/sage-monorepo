import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NxWelcomeComponent } from './nx-welcome.component';
import { SandboxAngularLibComponent } from '@sagebionetworks/sandbox-angular-lib';

@Component({
  imports: [NxWelcomeComponent, RouterModule, SandboxAngularLibComponent],
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  title = 'sandbox-angular-app';
}
