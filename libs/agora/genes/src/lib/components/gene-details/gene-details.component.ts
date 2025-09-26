/* eslint-disable @typescript-eslint/no-this-alias */
import { CommonModule, isPlatformBrowser, Location } from '@angular/common';
import {
  AfterViewChecked,
  AfterViewInit,
  Component,
  HostListener,
  inject,
  OnInit,
  PLATFORM_ID,
} from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faAngleLeft, faAngleRight } from '@fortawesome/free-solid-svg-icons';
import { Gene, GeneService } from '@sagebionetworks/agora/api-client';
import { HelperService } from '@sagebionetworks/agora/services';
import { GeneEvidenceMetabolomicsComponent } from '../gene-evidence-metabolomics/gene-evidence-metabolomics.component';
import { GeneEvidenceProteomicsComponent } from '../gene-evidence-proteomics/gene-evidence-proteomics.component';
import { GeneEvidenceRnaComponent } from '../gene-evidence-rna/gene-evidence-rna.component';
import { ExperimentalValidationComponent } from '../gene-experimental-validation/gene-experimental-validation.component';
import { GeneHeroComponent } from '../gene-hero/gene-hero.component';
import { GeneNominationsComponent } from '../gene-nominations/gene-nominations.component';
import { GeneResourcesComponent } from '../gene-resources/gene-resources.component';
import { GeneSoeComponent } from '../gene-soe/gene-soe.component';

interface Panel {
  name: string;
  label: string;
  disabled: boolean;
  children?: Panel[];
}

@Component({
  selector: 'agora-gene-details',
  imports: [
    CommonModule,
    GeneHeroComponent,
    GeneSoeComponent,
    GeneEvidenceMetabolomicsComponent,
    GeneEvidenceProteomicsComponent,
    GeneEvidenceRnaComponent,
    ExperimentalValidationComponent,
    GeneNominationsComponent,
    GeneResourcesComponent,
    FontAwesomeModule,
  ],
  templateUrl: './gene-details.component.html',
  styleUrls: ['./gene-details.component.scss'],
})
export class GeneDetailsComponent implements OnInit, AfterViewInit, AfterViewChecked {
  route = inject(ActivatedRoute);
  router = inject(Router);
  location = inject(Location);
  helperService = inject(HelperService);
  geneService = inject(GeneService);
  private readonly platformId: Record<string, any> = inject(PLATFORM_ID);

  faAngleRight = faAngleRight;
  faAngleLeft = faAngleLeft;

  gene: Gene | undefined;

  panels: Panel[] = [
    {
      name: 'summary',
      label: 'Summary',
      disabled: false,
    },
    {
      name: 'evidence',
      label: 'Evidence',
      disabled: false,
      children: [
        {
          name: 'rna',
          label: 'RNA',
          disabled: false,
        },
        {
          name: 'protein',
          label: 'Protein',
          disabled: false,
        },
        {
          name: 'metabolomics',
          label: 'Metabolomics',
          disabled: false,
        },
      ],
    },
    {
      name: 'resources',
      label: 'Resources',
      disabled: false,
    },
    {
      name: 'nominations',
      label: 'Nomination Details',
      disabled: false,
    },
    {
      name: 'experimental-validation',
      label: 'Experimental Validation',
      disabled: false,
    },
  ];

  activePanel = 'summary';
  activeParent = '';
  navSlideIndex = 0;

  @HostListener('window:scroll', ['$event'])
  onWindowScroll() {
    if (isPlatformBrowser(this.platformId)) {
      const nav = document.querySelector<HTMLElement>('.gene-details-nav');
      const rect = nav?.getBoundingClientRect();

      if (rect && rect.y <= 0) {
        nav?.classList.add('sticky');
      } else {
        nav?.classList.remove('sticky');
      }
    }
  }

  @HostListener('window:resize', ['$event'])
  onWindowResize() {
    if (isPlatformBrowser(this.platformId)) {
      const nav = document.querySelector<HTMLElement>('.gene-details-nav');
      const navContainer = nav?.querySelector<HTMLElement>('.gene-details-nav-container');
      const navList = nav?.querySelector<HTMLElement>('.gene-details-nav-container > ul');
      const navItems = nav?.querySelectorAll<HTMLElement>('.gene-details-nav-container > ul > li');
      let navItemsWidth = 0;
      if (navItems) {
        for (let i = 0; i < navItems.length; ++i) {
          navItemsWidth += navItems[i].offsetWidth;
        }
      }

      if (navContainer && navList && navItemsWidth) {
        if (navItemsWidth > navContainer.offsetWidth) {
          nav?.classList.add('scrollable');
        } else {
          nav?.classList.remove('scrollable');
          this.navSlideIndex = 0;
          navList.style.marginLeft = '0px';
        }
      }
    }
  }

  reset() {
    this.gene = undefined;
    this.activePanel = 'summary';
    this.activeParent = '';
    this.navSlideIndex = 0;
  }

  ngOnInit() {
    this.route.paramMap.subscribe((params: ParamMap) => {
      this.reset();
      this.helperService.setLoading(true);

      if (params.get('id')) {
        this.geneService.getGene(params.get('id') as string).subscribe((gene) => {
          if (!gene) {
            this.helperService.setLoading(false);
            // https://github.com/angular/angular/issues/45202
            // eslint-disable-next-line @typescript-eslint/no-floating-promises
            this.router.navigateByUrl('/404-not-found', { skipLocationChange: true });
          } else {
            this.gene = gene;

            this.panels.forEach((p: Panel) => {
              if (p.name == 'nominations' && !this.gene?.total_nominations) {
                p.disabled = true;
              } else if (
                p.name == 'experimental-validation' &&
                !this.gene?.experimental_validation?.length
              ) {
                p.disabled = true;
              } else {
                p.disabled = false;
              }
            });

            const nominationsPanel = this.panels.find((p) => p.name == 'nominations');
            if (nominationsPanel) {
              nominationsPanel.disabled = !this.gene.total_nominations ? true : false;
            }

            const experimentalValidationPanel = this.panels.find(
              (p) => p.name == 'experimental-validation',
            );
            if (experimentalValidationPanel) {
              experimentalValidationPanel.disabled = !this.gene.experimental_validation?.length
                ? true
                : false;
            }

            this.helperService.setLoading(false);
          }
        });
      }

      if (params.get('subtab')) {
        this.activePanel = params.get('subtab') as string;
        this.activeParent = params.get('tab') as string;
      } else if (params.get('tab')) {
        const panel = this.panels.find((p: Panel) => p.name === params.get('tab'));
        if (panel?.children) {
          this.activePanel = panel.children[0].name;
          this.activeParent = panel.name;
        } else if (panel) {
          this.activePanel = panel.name;
          this.activeParent = '';
        }
      }
    });
  }

  ngAfterViewInit() {
    if (!this.gene?.ensembl_gene_id) {
      this.helperService.setLoading(true);
    }
    const self = this;
    setTimeout(function () {
      self.onWindowResize();
    }, 100);
  }

  ngAfterViewChecked() {
    this.onWindowResize();
  }

  activatePanel(panel: Panel) {
    if (panel.disabled) {
      return;
    }

    let url = '/genes/' + this.gene?.ensembl_gene_id + '/';

    if (panel.children) {
      this.activePanel = panel.children[0].name;
      this.activeParent = panel.name;
      url += panel.name + '/' + panel.children[0].name;
    } else if (!this.panels.find((p: Panel) => p.name === panel.name)) {
      const parent = this.panels.find((p: Panel) =>
        p.children?.find((c: Panel) => c.name === panel.name),
      );
      this.activePanel = panel.name;
      this.activeParent = parent?.name || '';
      url += parent?.name + '/' + panel.name;
    } else {
      this.activePanel = panel.name;
      this.activeParent = '';
      url += panel.name;
    }

    // added logic to support dropdown state when page is refreshed
    const modelUrlParam = this.helperService.getUrlParam('model');
    if (modelUrlParam) {
      url = this.helperService.addSingleUrlParam(url, 'model', modelUrlParam);
    }

    if (isPlatformBrowser(this.platformId)) {
      const nav = document.querySelector('.gene-details-nav');
      if (nav) {
        window.scrollTo(0, this.helperService.getOffset(nav).top);
      }
    }

    this.location.replaceState(url);
  }

  getPanelCount() {
    return this.panels.map((p: Panel) => !p.disabled).length;
  }

  onNavigationItemClick(panel: Panel) {
    this.activatePanel(panel);
  }

  slideNavigation(direction: number) {
    if (isPlatformBrowser(this.platformId)) {
      this.navSlideIndex += direction;

      if (this.navSlideIndex < 0) {
        this.navSlideIndex = 0;
      } else if (this.navSlideIndex > this.getPanelCount() - 1) {
        this.navSlideIndex = this.panels.length - 1;
      }

      const nav = document.querySelector<HTMLElement>('.gene-details-nav');
      const navList = nav?.querySelector<HTMLElement>('.gene-details-nav-container > ul');
      const navItems = nav?.querySelectorAll<HTMLElement>('.gene-details-nav-container > ul > li');

      if (navList && navItems) {
        let navItemsWidth = 0;

        for (let i = 0; i < this.navSlideIndex; ++i) {
          navItemsWidth += navItems[i].offsetWidth;
        }

        if (this.navSlideIndex > 0) {
          navItemsWidth += 20;
        }

        navList.style.marginLeft = navItemsWidth * -1 + 'px';
      }
    }
  }
}
