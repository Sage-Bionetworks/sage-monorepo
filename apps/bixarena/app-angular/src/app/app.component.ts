import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NavComponent, FooterComponent } from '@sagebionetworks/bixarena/ui';

@Component({
  imports: [RouterModule, NavComponent, FooterComponent],
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {}
