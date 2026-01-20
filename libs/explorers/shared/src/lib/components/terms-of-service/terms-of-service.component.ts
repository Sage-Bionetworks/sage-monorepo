import {
  Component,
  computed,
  DestroyRef,
  inject,
  input,
  OnInit,
  ViewEncapsulation,
} from '@angular/core';

import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RouterModule } from '@angular/router';
import { SynapseApiService } from '@sagebionetworks/explorers/services';
import { HeroComponent } from '@sagebionetworks/explorers/ui';
import { LoadingIconComponent } from '@sagebionetworks/explorers/util';
import { MarkdownModule } from 'ngx-markdown';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'explorers-terms-of-service',
  imports: [HeroComponent, MarkdownModule, LoadingIconComponent, RouterModule],
  templateUrl: './terms-of-service.component.html',
  styleUrls: ['./terms-of-service.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class TermsOfServiceComponent implements OnInit {
  synapseService = inject(SynapseApiService);
  private readonly destroyRef = inject(DestroyRef);

  content = '';
  isLoading = true;
  heroBackgroundImagePath = input<string | undefined>();

  heroBackgroundImagePathOrDefault = computed(
    () => this.heroBackgroundImagePath() ?? 'explorers-assets/images/background.svg',
  );

  ngOnInit() {
    this.loadTOS();
  }

  loadTOS() {
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
