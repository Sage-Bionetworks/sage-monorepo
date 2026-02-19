import { CommonModule } from '@angular/common';
import { Component, DestroyRef, effect, inject, input, ViewEncapsulation } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { SynapseWikiMarkdown, SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { SynapseApiService } from '@sagebionetworks/explorers/services';
import { finalize } from 'rxjs/operators';
import { LoadingIconComponent } from '../loading-icon/loading-icon.component';
@Component({
  selector: 'explorers-wiki',
  imports: [CommonModule, LoadingIconComponent],
  templateUrl: './wiki.component.html',
  styleUrls: ['./wiki.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class WikiComponent {
  synapseApiService = inject(SynapseApiService);
  domSanitizer = inject(DomSanitizer);
  private readonly destroyRef = inject(DestroyRef);

  wikiParams = input<SynapseWikiParams>();

  className = '';
  isLoading = true;
  safeHtml: SafeHtml | null = '<div class="wiki-no-data">No data found...</div>';

  constructor() {
    effect(() => {
      const params = this.wikiParams();
      if (params) {
        this.getWikiMarkdown();
      }
    });
  }

  getWikiMarkdown() {
    const ownerId = this.wikiParams()?.ownerId;
    const wikiId = this.wikiParams()?.wikiId;
    if (!ownerId || !wikiId) {
      console.error('Wiki parameter(s) missing');
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
          console.error('Error fetching wiki: ', err);
        },
      });
  }

  getClassName() {
    const className = [this.className];
    if (this.isLoading) {
      className.push('loading');
    }
    return className;
  }
}
