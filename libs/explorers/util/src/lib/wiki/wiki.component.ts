import {
  Component,
  DestroyRef,
  effect,
  inject,
  input,
  signal,
  ViewEncapsulation,
} from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { SynapseWikiMarkdown, SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { PlatformService, SynapseApiService } from '@sagebionetworks/explorers/services';
import { LoadingIconComponent } from '../loading-icon/loading-icon.component';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs';
@Component({
  selector: 'explorers-wiki',
  imports: [LoadingIconComponent],
  templateUrl: './wiki.component.html',
  styleUrls: ['./wiki.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class WikiComponent {
  private readonly platformService = inject(PlatformService);
  synapseApiService = inject(SynapseApiService);
  domSanitizer = inject(DomSanitizer);
  private readonly destroyRef = inject(DestroyRef);

  wikiParams = input.required<SynapseWikiParams>();
  className = input<string>('');
  suppressErrorOverlay = input(false);

  isLoading = signal(true);
  safeHtml: SafeHtml | null = '<div class="wiki-no-data">No data found...</div>';

  constructor() {
    effect(() => {
      const wikiParams = this.wikiParams();
      this.getWikiMarkdown(wikiParams);
    });
  }

  getWikiMarkdown(wikiParams: SynapseWikiParams) {
    if (this.platformService.isBrowser) {
      const { ownerId, wikiId } = wikiParams;

      this.isLoading.set(true);

      this.synapseApiService
        .getWikiMarkdown(ownerId, wikiId, this.suppressErrorOverlay())
        .pipe(
          takeUntilDestroyed(this.destroyRef),
          finalize(() => {
            this.isLoading.set(false);
          }),
        )
        .subscribe({
          next: (wiki: SynapseWikiMarkdown) => {
            // Requires bypassSecurityTrustHtml to render iframes (e.g. videos)
            this.safeHtml = this.domSanitizer.bypassSecurityTrustHtml(
              this.synapseApiService.renderHtml(wiki.markdown),
            );
          },
          error: () => {
            this.safeHtml = 'No data found, please try again later.';
          },
        });
    }
  }
}
