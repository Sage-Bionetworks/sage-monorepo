import { CommonModule } from '@angular/common';
import {
  Component,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
  ViewEncapsulation,
} from '@angular/core';
import { SafeHtml, DomSanitizer } from '@angular/platform-browser';
import { LoadingIconComponent } from '@sagebionetworks/agora/ui';
import { SynapseWiki } from '@sagebionetworks/agora/models';
import { SynapseApiService } from '@sagebionetworks/agora/services';

@Component({
  selector: 'agora-wiki',
  standalone: true,
  imports: [CommonModule, LoadingIconComponent],
  providers: [SynapseApiService],
  templateUrl: './wiki.component.html',
  styleUrls: ['./wiki.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class WikiComponent implements OnChanges, OnInit {
  @Input() ownerId: string | undefined;
  @Input() wikiId: string | undefined;
  @Input() className = '';

  loading = true;

  data: SynapseWiki = {} as SynapseWiki;
  safeHtml: SafeHtml | null = '<div class="wiki-no-data">No data found...</div>';

  constructor(
    private synapseApiService: SynapseApiService,
    private domSanitizer: DomSanitizer,
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['wikiId'] && !changes['wikiId'].firstChange) {
      this.getWikiData();
    }
  }

  ngOnInit() {
    this.loading = true;
    this.getWikiData();
  }

  getWikiData() {
    this.synapseApiService.getWiki(this.ownerId ?? 'syn25913473', this.wikiId ?? '').subscribe({
      next: (wiki: SynapseWiki) => {
        if (!wiki) {
          this.loading = false;
          return;
        }

        this.data = wiki;
        // Requires bypassSecurityTrustHtml to render iframes (e.g. videos)
        this.safeHtml = this.domSanitizer.bypassSecurityTrustHtml(
          this.synapseApiService.renderHtml(wiki.markdown),
        );
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching wiki: ', err);
        console.warn('Service unavailable. Please try again later.');
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      },
    });
  }

  getClassName() {
    const className = [this.className];
    if (this.loading) {
      className.push('loading');
    }
    return className;
  }
}
