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
import {
  ErrorOverlayService,
  LoggerService,
  PlatformService,
  SynapseApiService,
} from '@sagebionetworks/explorers/services';
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
  private readonly logger = inject(LoggerService);
  private readonly errorOverlayService = inject(ErrorOverlayService);
  synapseApiService = inject(SynapseApiService);
  domSanitizer = inject(DomSanitizer);
  private readonly destroyRef = inject(DestroyRef);

  wikiParams = input.required<SynapseWikiParams>();
  className = input<string>('');

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
      this.logger.log('WikiComponent: Loading wiki content', { ownerId, wikiId });

      this.synapseApiService
        .getWikiMarkdown(ownerId, wikiId)
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
            this.errorOverlayService.showError(
              'Failed to load wiki content. Please try again later.',
            );
          },
        });
    }
  }
}
