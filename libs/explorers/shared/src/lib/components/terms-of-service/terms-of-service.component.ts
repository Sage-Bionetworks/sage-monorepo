import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SynapseApiService } from '@sagebionetworks/explorers/services';
import { MarkdownModule } from 'ngx-markdown';
import { HeroComponent } from '@sagebionetworks/explorers/ui';
import { LoadingIconComponent } from '@sagebionetworks/explorers/util';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'explorers-terms-of-service',
  imports: [CommonModule, HeroComponent, MarkdownModule, LoadingIconComponent, RouterModule],
  templateUrl: './terms-of-service.component.html',
  styleUrls: ['./terms-of-service.component.scss'],
})
export class TermsOfServiceComponent implements OnInit {
  synapseService = inject(SynapseApiService);
  private destroyRef = inject(DestroyRef);

  content = '';
  isLoading = true;

  ngOnInit() {
    this.loadTOS();
  }

  loadTOS() {
    this.isLoading = true;
    this.synapseService
      .getTermsOfService()
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => {
          this.isLoading = false;
        }),
      )
      .subscribe({
        next: (markdown) => {
          this.content = markdown;
        },
        error: (error) => {
          console.error('Error loading terms of service:', error);
        },
      });
  }
}
