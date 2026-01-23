import { CommonModule } from '@angular/common';
import { AfterViewChecked, Component, ElementRef, ViewEncapsulation, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { VisualizationOverviewPane } from '@sagebionetworks/explorers/models';
import { ComparisonToolService, PlatformService } from '@sagebionetworks/explorers/services';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { DialogModule } from 'primeng/dialog';
import { TooltipModule } from 'primeng/tooltip';

@Component({
  selector: 'explorers-visualization-overview-panel',
  imports: [CommonModule, ButtonModule, CheckboxModule, DialogModule, FormsModule, TooltipModule],
  templateUrl: './visualization-overview-panel.component.html',
  styleUrls: ['./visualization-overview-panel.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class VisualizationOverviewPanelComponent implements AfterViewChecked {
  comparisonToolService = inject(ComparisonToolService);
  platformService = inject(PlatformService);
  viewConfig = this.comparisonToolService.viewConfig;
  private elementRef = inject(ElementRef);

  get willHide(): boolean {
    return this.comparisonToolService.isVisualizationOverviewHiddenByUser();
  }

  setWillHide(value: boolean) {
    this.comparisonToolService.setVisualizationOverviewHiddenByUser(value);
  }

  activePane = 0;
  private lastPlayedPane = -1;

  readonly defaultPanes: VisualizationOverviewPane[] = [
    {
      heading: 'Comparison Tool Tutorial',
      content: `<p>Comparison Tools allow you to discover, compare and share results and related information. While each Comparison Tool presents different kinds of information, they all support similar exploration features. This tutorial demonstrates how you can use these features to find, download, and share results.</p>
        <p>You can always revisit this tutorial by clicking the Visualization Overview link at the bottom of the page.</p>
        <p>If the tool you're using has a legend, you can view it by clicking on the Legend link at the bottom of the page.</p>
        <div class="image-container"><img src="/explorers-assets/images/visualization-overview-1.png" /></div>`,
    },
    {
      heading: 'Search & Filter',
      content: `<p>Quickly find results of interest using the tool's embedded search box. You can enter a partial value to get a list of matching results, a full value to find an exact match, or perform a bulk search using a list of comma-separated full values.</p>
        <p>You can also use the tool's supported filters to find results of interest. The filter region near the top of the page shows which filters are currently applied. You can clear specific filters, or all of the filters at once.</p>
        <div class="video-container"><video autoplay muted loop playsinline preload="none" controls><source src="/explorers-assets/videos/visualization-overview-2.mp4" type="video/mp4"></video></div>`,
    },
    {
      heading: 'Build, share and export custom comparisons',
      content: `<p>Pin up to 50 rows of results so that you can easily compare them. Once you've built your custom result set, you can share it with your colleagues by Share URL, download an image of the pinned results, and export the underlying data for further analysis.</p>
        <div class="video-container"><video autoplay muted loop playsinline preload="none" controls><source src="/explorers-assets/videos/visualization-overview-3.mp4" type="video/mp4"></video></div>`,
    },
    {
      heading: 'Customize your view',
      content: `<p>Sort results on any column by clicking on the sort control for that column. You can sort by multiple columns by holding down the command (⌘ Mac) or control (Windows) key while clicking on additional sort controls.</p>
        <p>Use the p-value filter control to mask insignificant heatmap results based on a configurable p-value threshold.</p>
        <p>You can also control which columns are visible using the column picker.</p>
        <div class="image-container"><img src="/explorers-assets/images/gct-how-to-3.gif" /></div>`,
    },
  ];

  get panes(): VisualizationOverviewPane[] {
    return this.defaultPanes;
  }

  get activePaneHasVideo(): boolean {
    if (this.panes.length === 0) return false;
    return this.panes[this.activePane]?.content?.includes('<video') ?? false;
  }

  get dialogStyle(): Record<string, string> {
    return this.activePaneHasVideo
      ? { width: '95vw', maxWidth: '1200px' }
      : { width: '100%', maxWidth: '580px' };
  }

  ngAfterViewChecked() {
    if (
      this.platformService.isBrowser &&
      this.comparisonToolService.isVisualizationOverviewVisible() &&
      this.activePane !== this.lastPlayedPane
    ) {
      this.initializeMediaInActivePane();
      this.lastPlayedPane = this.activePane;
    }
  }

  private initializeMediaInActivePane() {
    const activePane = this.elementRef.nativeElement.querySelector(
      '.visualization-overview-panel-pane.active',
    );
    if (!activePane) return;

    // Handle videos
    const videos = activePane.querySelectorAll('video');
    videos.forEach((video: HTMLVideoElement) => {
      video.classList.add('loading');

      const onCanPlay = () => {
        video.classList.remove('loading');
        video.removeEventListener('canplay', onCanPlay);
      };
      video.addEventListener('canplay', onCanPlay);

      const playPromise = video.play();
      if (playPromise !== undefined) {
        playPromise.catch(() => {
          video.classList.remove('loading');
        });
      }
    });

    // Handle images
    const images = activePane.querySelectorAll('.image-container img');
    images.forEach((img: HTMLImageElement) => {
      if (img.complete) {
        img.classList.remove('loading');
      } else {
        img.classList.add('loading');
        const onLoad = () => {
          img.classList.remove('loading');
          img.removeEventListener('load', onLoad);
        };
        img.addEventListener('load', onLoad);
      }
    });
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
    this.activePane = 0;
    this.lastPlayedPane = -1;
  }

  close() {
    this.comparisonToolService.setVisualizationOverviewVisibility(false);
  }
}
