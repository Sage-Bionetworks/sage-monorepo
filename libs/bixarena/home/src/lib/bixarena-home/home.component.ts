import { isPlatformServer } from '@angular/common';
import { Component, inject, PLATFORM_ID, signal } from '@angular/core';
import { PublicStats, StatsService } from '@sagebionetworks/bixarena/api-client';

@Component({
  selector: 'bixarena-home',
  imports: [],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent {
  readonly isPlatformServer = isPlatformServer(inject(PLATFORM_ID));
  readonly stats = signal<PublicStats | null>(null);

  constructor() {
    if (this.isPlatformServer) return;
    inject(StatsService)
      .getPublicStats()
      .subscribe((s) => this.stats.set(s));
  }
}
