import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SynapseApiService } from '@sagebionetworks/explorers/services';
import { MarkdownModule } from 'ngx-markdown';
import { HeroComponent } from '@sagebionetworks/explorers/ui';
import { LoadingIconComponent } from '@sagebionetworks/explorers/util';

@Component({
  selector: 'explorers-terms-of-service',
  imports: [CommonModule, HeroComponent, MarkdownModule, LoadingIconComponent, RouterModule],
  templateUrl: './terms-of-service.component.html',
  styleUrls: ['./terms-of-service.component.scss'],
})
export class TermsOfServiceComponent implements OnInit {
  synapseService = inject(SynapseApiService);

  content = '';
  loading = true;

  ngOnInit() {
    this.loadTOS();
  }

  loadTOS() {
    this.loading = true;

    this.synapseService.getTermsOfService().subscribe({
      next: (markdown) => {
        this.content = markdown;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading terms of service:', error);
        this.loading = false;
      },
    });
  }
}
