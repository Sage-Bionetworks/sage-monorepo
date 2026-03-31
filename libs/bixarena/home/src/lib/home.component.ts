import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { StatsService } from '@sagebionetworks/bixarena/api-client';

@Component({
  selector: 'bixarena-home',
  imports: [AsyncPipe],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent {
  readonly stats$ = inject(StatsService).getPublicStats();
}
