import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FooterComponent } from '@sagebionetworks/model-ad/ui';

@Component({
  standalone: true,
  imports: [RouterModule, FooterComponent],
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  title = 'model-ad-app';
}
