import { CommonModule, isPlatformBrowser } from '@angular/common';
import {
  Component,
  DestroyRef,
  inject,
  input,
  OnInit,
  PLATFORM_ID,
  ViewEncapsulation,
} from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { SynapseWikiMarkdown, SynapseWikiParams } from '@sagebionetworks/explorers/models';
import {
  ErrorOverlayService,
  LoggerService,
  SynapseApiService,
} from '@sagebionetworks/explorers/services';
import { LoadingIconComponent } from '../loading-icon/loading-icon.component';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs/operators';
@Component({
  selector: 'explorers-wiki',
  imports: [CommonModule, LoadingIconComponent],
  templateUrl: './wiki.component.html',
  styleUrls: ['./wiki.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class WikiComponent implements OnInit {
  private readonly platformId = inject(PLATFORM_ID);
  private readonly logger = inject(LoggerService);
  private readonly errorOverlayService = inject(ErrorOverlayService);
  synapseApiService = inject(SynapseApiService);
  domSanitizer = inject(DomSanitizer);
  private destroyRef = inject(DestroyRef);

  wikiParams = input<SynapseWikiParams>();

  className = '';
  isLoading = true;
  safeHtml: SafeHtml | null = '<div class="wiki-no-data">No data found...</div>';

  ngOnInit() {
    this.getWikiMarkdown();
  }

  getWikiMarkdown() {
    if (isPlatformBrowser(this.platformId)) {
      const ownerId = this.wikiParams()?.ownerId;
      const wikiId = this.wikiParams()?.wikiId;
      if (!ownerId || !wikiId) {
        this.logger.warn('WikiComponent: Wiki parameter(s) missing');
        return;
      }

      this.isLoading = true;

      this.synapseApiService
        .getWikiMarkdown(ownerId, wikiId)
        .pipe(
          takeUntilDestroyed(this.destroyRef),
          finalize(() => {
            this.isLoading = false;
          }),
        )
        .subscribe({
          next: (wiki: SynapseWikiMarkdown) => {
            // Requires bypassSecurityTrustHtml to render iframes (e.g. videos)
            this.safeHtml = this.domSanitizer.bypassSecurityTrustHtml(
              this.synapseApiService.renderHtml(wiki.markdown),
            );
          },
          error: (err) => {
            this.logger.error('WikiComponent: Failed to load wiki content', err);
            this.logger.trackError(err);
            this.errorOverlayService.showError(
              'Failed to load wiki content. Please try again later.',
            );
          },
        });
    }
  }

  getClassName() {
    const className = [this.className];
    if (this.isLoading) {
      className.push('loading');
    }
    return className;
  }
}
