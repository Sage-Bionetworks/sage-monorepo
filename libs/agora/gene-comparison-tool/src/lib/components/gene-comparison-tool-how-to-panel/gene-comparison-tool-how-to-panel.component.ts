// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { Component, inject, OnInit, ViewEncapsulation } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { CheckboxModule } from 'primeng/checkbox';
import { DialogModule } from 'primeng/dialog';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LoadingIconComponent } from '@sagebionetworks/agora/shared';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
// import { SynapseApiService } from '../../../../../../core/services';

interface Pane {
  heading: string;
  content: SafeHtml;
}

// -------------------------------------------------------------------------- //
// Component
// -------------------------------------------------------------------------- //
@Component({
  selector: 'agora-gene-comparison-tool-how-to-panel',
  standalone: true,
  imports: [CommonModule, FormsModule, CheckboxModule, DialogModule, LoadingIconComponent],
  providers: [CookieService],
  templateUrl: './gene-comparison-tool-how-to-panel.component.html',
  styleUrls: ['./gene-comparison-tool-how-to-panel.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class GeneComparisonToolHowToPanelComponent implements OnInit {
  cookieService = inject(CookieService);
  sanitizer = inject(DomSanitizer);

  isActive = false;
  willHide = false;
  willHideCookieName = 'gct_hide_how_to';

  panes: Pane[] = [
    {
      heading: 'Error',
      content: '<div class="wiki-no-data">No data found...</div>',
    },
  ];
  activePane = 0;

  loading = false;

  ngOnInit() {
    if (this.cookieService.get(this.willHideCookieName) !== '1') {
      this.isActive = true;
    } else {
      this.willHide = true;
    }

    this.loadContent();
  }

  loadContent() {
    this.panes = [
      {
        heading: 'Gene Comparison Overview',
        content: this.sanitizer.bypassSecurityTrustHtml(
          `<p>Welcome to Agora’s Gene Comparison Tool. This overview demonstrates how to use the tool to explore results about genes related to AD. You can revisit this walkthrough by clicking the Visualization Overview link at the bottom of the page.</p>
          <p>Click on the Legend link at the bottom of the page to view the legend for the current visualization.</p>
          <img src="/agora-assets/images/gct-how-to-0.svg" />`,
        ),
      },
      {
        heading: 'View Detailed Expression Info',
        content: this.sanitizer.bypassSecurityTrustHtml(
          `<p>Click on a circle to show detailed information about a result for a specific brain region.</p>
          <img src="/agora-assets/images/gct-how-to-1.gif" />`,
        ),
      },
      {
        heading: 'Compare Multiple Genes',
        content: this.sanitizer.bypassSecurityTrustHtml(
          `<p>You can pin several genes to visually compare them together. Then export the data about your pinned genes as a CSV file for further analysis.</p>
          <img src="/agora-assets/images/gct-how-to-2.gif" />`,
        ),
      },
      {
        heading: 'Filter Gene Selection',
        content: this.sanitizer.bypassSecurityTrustHtml(
          `<p>Filter genes by Nomination, Association with AD, Study and more. Or simply use the search bar to quickly find the genes you are interested in.</p>
          <img src="/agora-assets/images/gct-how-to-3.gif" />`,
        ),
      },
    ];

    // Uncomment to use wiki page
    // this.loading = true;

    // this.synapseApiService.getWiki('syn25913473', '618351').subscribe(
    //   (wiki: any) => {
    //     if (!wiki) {
    //       this.loading = false;
    //       return;
    //     }

    //     const sanitized = this.synapseApiService.renderHtml(wiki.markdown);
    //     const panes = sanitized.split('<hr />');

    //     this.panes = panes.map((html: string) => {
    //       const headings = html.match('<h4>(.*?)</h4>');
    //       const content = html.replace(/<h4>(.*?)<\/h4>/, '');

    //       return {
    //         heading: headings?.length ? headings[1] : '',
    //         content: this.sanitizer.bypassSecurityTrustHtml(content),
    //       };
    //     });

    //     this.loading = false;
    //   },
    //   () => {
    //     this.loading = false;
    //   }
    // );
  }

  previous() {
    if (this.activePane > 0) {
      this.activePane--;
    }
  }

  next() {
    if (this.activePane < this.panes.length - 1) {
      this.activePane++;
    }
  }

  onHide() {
    this.cookieService.set(this.willHideCookieName, this.willHide ? '1' : '0');
    this.activePane = 0;
  }

  toggle() {
    this.isActive = !this.isActive;
  }
}
