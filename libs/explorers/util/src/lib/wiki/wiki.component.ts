import { CommonModule } from '@angular/common';
import { Component, DestroyRef, inject, input, OnInit, ViewEncapsulation } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { SynapseWikiMarkdown, SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { SynapseApiService } from '@sagebionetworks/explorers/services';
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
  synapseApiService = inject(SynapseApiService);
  domSanitizer = inject(DomSanitizer);
  private readonly destroyRef = inject(DestroyRef);

  wikiParams = input<SynapseWikiParams>();
  className = input<string>('');
  isLoading = true;
  safeHtml: SafeHtml | null = '<div class="wiki-no-data">No data found...</div>';

  ngOnInit() {
    this.getWikiMarkdown();
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
    const classNames = [this.className()];
    if (this.isLoading) {
      classNames.push('loading');
    }
    return classNames;
  }
}
