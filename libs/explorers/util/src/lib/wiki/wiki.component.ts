import { CommonModule } from '@angular/common';
import {
  Component,
  DestroyRef,
  inject,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
  ViewEncapsulation,
} from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { SynapseWiki } from '@sagebionetworks/explorers/models';
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
export class WikiComponent implements OnChanges, OnInit {
  synapseApiService = inject(SynapseApiService);
  domSanitizer = inject(DomSanitizer);
  private destroyRef = inject(DestroyRef);

  @Input() ownerId: string | undefined;
  @Input() wikiId: string | undefined;
  @Input() className = '';

  isLoading = true;

  data: SynapseWiki = {} as SynapseWiki;
  safeHtml: SafeHtml | null = '<div class="wiki-no-data">No data found...</div>';

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['wikiId'] && !changes['wikiId'].firstChange) {
      this.getWikiData();
    }
  }

  ngOnInit() {
    this.isLoading = true;
    this.getWikiData();
  }

  getWikiData() {
    this.synapseApiService
      .getWiki(this.ownerId || 'syn25913473', this.wikiId || '')
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => {
          this.isLoading = false;
        }),
      )
      .subscribe({
        next: (wiki: SynapseWiki) => {
          this.data = wiki;
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
