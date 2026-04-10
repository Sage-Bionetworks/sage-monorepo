import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { GCTGene } from '@sagebionetworks/agora/api-client';
import { AGORA_LOADING_ICON_COLORS } from '@sagebionetworks/agora/config';
import { provideExplorersConfig, SvgIconService } from '@sagebionetworks/explorers/services';
import { provideLoadingIconColors, SvgIconServiceStub } from '@sagebionetworks/explorers/testing';
import { MessageService } from 'primeng/api';
import { GeneComparisonToolComponent } from './gene-comparison-tool.component';

function makeGene(ensemblGeneId: string, uniprotid?: string): GCTGene {
  const uid = uniprotid ? ensemblGeneId + uniprotid : ensemblGeneId;
  return {
    ensembl_gene_id: ensemblGeneId,
    hgnc_symbol: 'GENE',
    uniprotid: uniprotid ?? null,
    uid,
    tissues: [],
    target_risk_score: null,
    genetics_score: null,
    multi_omics_score: null,
  };
}

describe('GeneComparisonToolComponent', () => {
  let component: GeneComparisonToolComponent;
  let fixture: ComponentFixture<GeneComparisonToolComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoopAnimationsModule],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        MessageService,
        { provide: SvgIconService, useClass: SvgIconServiceStub },
        ...provideLoadingIconColors(AGORA_LOADING_ICON_COLORS),
        provideExplorersConfig({ visualizationOverviewPanes: [] }),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(GeneComparisonToolComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('pinning state', () => {
    beforeEach(() => {
      component.resetPinnedItemsState();
    });

    describe('resetPinnedItemsState', () => {
      it('resets pinnedItems, pinnedItemsPerGene, and uniquePinnedGenesCount', () => {
        component.category = 'RNA - Differential Expression';
        component.pinItem(makeGene('ENSG001'), false);

        component.resetPinnedItemsState();

        expect(component.pinnedItems).toEqual([]);
        expect(component.pinnedItemsPerGene.size).toBe(0);
        expect(component.uniquePinnedGenesCount).toBe(0);
      });
    });

    describe('pinItem — RNA - Differential Expression', () => {
      beforeEach(() => {
        component.category = 'RNA - Differential Expression';
      });

      it('adds a gene and syncs all three fields', () => {
        const gene = makeGene('ENSG001');
        component.pinItem(gene, false);

        expect(component.pinnedItems).toContain(gene);
        expect(component.pinnedItemsPerGene.get('ENSG001')).toBe(1);
        expect(component.uniquePinnedGenesCount).toBe(1);
      });

      it('does not add a duplicate', () => {
        const gene = makeGene('ENSG001');
        component.pinItem(gene, false);
        component.pinItem(gene, false);

        expect(component.pinnedItems.length).toBe(1);
        expect(component.uniquePinnedGenesCount).toBe(1);
      });

      it('does not exceed maxPinnedGenes', () => {
        component.maxPinnedGenes = 2;
        component.pinItem(makeGene('ENSG001'), false);
        component.pinItem(makeGene('ENSG002'), false);
        component.pinItem(makeGene('ENSG003'), false);

        expect(component.pinnedItems.length).toBe(2);
        expect(component.uniquePinnedGenesCount).toBe(2);
      });
    });

    describe('pinItem — Protein - Differential Expression', () => {
      beforeEach(() => {
        component.category = 'Protein - Differential Expression';
      });

      it('adds a protein and syncs all three fields', () => {
        const protein = makeGene('ENSG001', 'P00001');
        component.pinItem(protein, false);

        expect(component.pinnedItems).toContain(protein);
        expect(component.pinnedItemsPerGene.get('ENSG001')).toBe(1);
        expect(component.uniquePinnedGenesCount).toBe(1);
      });

      it('allows multiple proteins from the same gene, incrementing the map count', () => {
        const p1 = makeGene('ENSG001', 'P00001');
        const p2 = makeGene('ENSG001', 'P00002');
        component.pinItem(p1, false);
        component.pinItem(p2, false);

        expect(component.pinnedItems.length).toBe(2);
        expect(component.pinnedItemsPerGene.get('ENSG001')).toBe(2);
        expect(component.uniquePinnedGenesCount).toBe(1);
      });

      it('at the gene limit, allows a protein whose gene is already pinned', () => {
        component.maxPinnedGenes = 1;
        component.pinItem(makeGene('ENSG001', 'P00001'), false);
        component.pinItem(makeGene('ENSG001', 'P00002'), false);

        expect(component.pinnedItems.length).toBe(2);
        expect(component.uniquePinnedGenesCount).toBe(1);
      });

      it('at the gene limit, blocks a protein whose gene is not already pinned', () => {
        component.maxPinnedGenes = 1;
        component.pinItem(makeGene('ENSG001', 'P00001'), false);
        component.pinItem(makeGene('ENSG002', 'P00002'), false);

        expect(component.pinnedItems.length).toBe(1);
        expect(component.uniquePinnedGenesCount).toBe(1);
      });
    });

    describe('onUnpinItemClick', () => {
      it('removes a gene and syncs all three fields', () => {
        component.category = 'RNA - Differential Expression';
        const gene = makeGene('ENSG001');
        component.pinItem(gene, false);
        component.onUnpinItemClick(gene, false);

        expect(component.pinnedItems).not.toContain(gene);
        expect(component.pinnedItemsPerGene.has('ENSG001')).toBe(false);
        expect(component.uniquePinnedGenesCount).toBe(0);
      });

      it('with multiple proteins of the same gene, decrements the map count without removing the key', () => {
        component.category = 'Protein - Differential Expression';
        const p1 = makeGene('ENSG001', 'P00001');
        const p2 = makeGene('ENSG001', 'P00002');
        component.pinItem(p1, false);
        component.pinItem(p2, false);
        component.onUnpinItemClick(p1, false);

        expect(component.pinnedItemsPerGene.get('ENSG001')).toBe(1);
        expect(component.uniquePinnedGenesCount).toBe(1);
      });

      it('removing the last protein of a gene removes the key from the map', () => {
        component.category = 'Protein - Differential Expression';
        const p1 = makeGene('ENSG001', 'P00001');
        component.pinItem(p1, false);
        component.onUnpinItemClick(p1, false);

        expect(component.pinnedItemsPerGene.has('ENSG001')).toBe(false);
        expect(component.uniquePinnedGenesCount).toBe(0);
      });

      it('is a no-op for an item not in the pinned list', () => {
        component.category = 'RNA - Differential Expression';
        component.onUnpinItemClick(makeGene('ENSG999'), false);

        expect(component.pinnedItems.length).toBe(0);
      });
    });

    describe('isPinDisabled — RNA - Differential Expression', () => {
      beforeEach(() => {
        component.category = 'RNA - Differential Expression';
      });

      it('returns false when under the limit', () => {
        expect(component.isPinDisabled()).toBe(false);
      });

      it('returns true when at the limit', () => {
        component.maxPinnedGenes = 1;
        component.pinItem(makeGene('ENSG001'), false);

        expect(component.isPinDisabled()).toBe(true);
      });
    });

    describe('isPinDisabled — Protein - Differential Expression', () => {
      beforeEach(() => {
        component.category = 'Protein - Differential Expression';
      });

      it('returns false when under the limit', () => {
        expect(component.isPinDisabled(makeGene('ENSG001', 'P00001'))).toBe(false);
      });

      it('at the gene limit, returns false for a protein whose gene is already pinned', () => {
        component.maxPinnedGenes = 1;
        component.pinItem(makeGene('ENSG001', 'P00001'), false);

        expect(component.isPinDisabled(makeGene('ENSG001', 'P00002'))).toBe(false);
      });

      it('at the gene limit, returns true for a protein whose gene is not pinned', () => {
        component.maxPinnedGenes = 1;
        component.pinItem(makeGene('ENSG001', 'P00001'), false);

        expect(component.isPinDisabled(makeGene('ENSG002', 'P00002'))).toBe(true);
      });

      it('at the gene limit, returns true when no gene argument is provided', () => {
        component.maxPinnedGenes = 1;
        component.pinItem(makeGene('ENSG001', 'P00001'), false);

        expect(component.isPinDisabled()).toBe(true);
      });
    });

    describe('isPinAllDisabled — RNA - Differential Expression', () => {
      beforeEach(() => {
        component.category = 'RNA - Differential Expression';
      });

      it('returns false when under the limit', () => {
        expect(component.isPinAllDisabled()).toBe(false);
      });

      it('returns true when at the limit', () => {
        component.maxPinnedGenes = 1;
        component.pinItem(makeGene('ENSG001'), false);

        expect(component.isPinAllDisabled()).toBe(true);
      });
    });

    describe('isPinAllDisabled — Protein - Differential Expression', () => {
      beforeEach(() => {
        component.category = 'Protein - Differential Expression';
      });

      it('returns false when under the limit', () => {
        expect(component.isPinAllDisabled()).toBe(false);
      });

      it('at the gene limit, returns false when a filtered protein belongs to an already-pinned gene', () => {
        component.maxPinnedGenes = 1;
        component.pinItem(makeGene('ENSG001', 'P00001'), false);
        component.genesTable.filteredValue = [makeGene('ENSG001', 'P00002')];

        expect(component.isPinAllDisabled()).toBe(false);
      });

      it('at the gene limit, returns true when no filtered protein belongs to an already-pinned gene', () => {
        component.maxPinnedGenes = 1;
        component.pinItem(makeGene('ENSG001', 'P00001'), false);
        component.genesTable.filteredValue = [makeGene('ENSG002', 'P00002')];

        expect(component.isPinAllDisabled()).toBe(true);
      });

      it('at the gene limit, returns true when filteredValue is null (nothing to pin)', () => {
        component.maxPinnedGenes = 1;
        component.pinItem(makeGene('ENSG001', 'P00001'), false);
        component.genesTable.filteredValue = null;

        expect(component.isPinAllDisabled()).toBe(true);
      });
    });

    describe('pinProteinsBatched', () => {
      beforeEach(() => {
        component.category = 'Protein - Differential Expression';
      });

      it('pins all proteins when under the gene limit', () => {
        component.pinProteinsBatched([
          makeGene('ENSG001', 'P00001'),
          makeGene('ENSG002', 'P00002'),
        ]);

        expect(component.pinnedItems.length).toBe(2);
        expect(component.uniquePinnedGenesCount).toBe(2);
      });

      it('at the gene limit, allows proteins whose gene is already pinned', () => {
        component.maxPinnedGenes = 1;
        component.pinItem(makeGene('ENSG001', 'P00001'), false);
        component.pinProteinsBatched([makeGene('ENSG001', 'P00002')]);

        expect(component.pinnedItems.length).toBe(2);
        expect(component.uniquePinnedGenesCount).toBe(1);
      });

      it('at the gene limit, skips proteins whose gene is not already pinned', () => {
        component.maxPinnedGenes = 1;
        component.pinItem(makeGene('ENSG001', 'P00001'), false);
        component.pinProteinsBatched([makeGene('ENSG002', 'P00002')]);

        expect(component.pinnedItems.length).toBe(1);
        expect(component.uniquePinnedGenesCount).toBe(1);
      });
    });

    describe('toast behaviour', () => {
      let addSpy: jest.SpyInstance;

      beforeEach(() => {
        addSpy = jest.spyOn(component.messageService, 'add');
      });

      afterEach(() => {
        addSpy.mockRestore();
      });

      it('pinItem shows a toast for each individually blocked protein', () => {
        component.category = 'Protein - Differential Expression';
        component.maxPinnedGenes = 1;
        component.pinItem(makeGene('ENSG001', 'P00001'), false);

        // two separate blocked calls → two separate toasts
        component.pinItem(makeGene('ENSG002', 'P00002'), false);
        component.pinItem(makeGene('ENSG003', 'P00003'), false);

        expect(addSpy).toHaveBeenCalledTimes(2);
      });

      it('pinProteinsBatched shows a single toast regardless of how many proteins are blocked', () => {
        component.category = 'Protein - Differential Expression';
        component.maxPinnedGenes = 1;
        component.pinItem(makeGene('ENSG001', 'P00001'), false);

        // three blocked proteins in one batch → one toast at the end
        component.pinProteinsBatched([
          makeGene('ENSG002', 'P00002'),
          makeGene('ENSG003', 'P00003'),
          makeGene('ENSG004', 'P00004'),
        ]);

        expect(addSpy).toHaveBeenCalledTimes(1);
      });

      it('pinProteinsBatched reports the correct count of pinned (not blocked) proteins in the toast', () => {
        component.category = 'Protein - Differential Expression';
        component.maxPinnedGenes = 2;
        component.pinItem(makeGene('ENSG001', 'P00001'), false);
        component.pinItem(makeGene('ENSG002', 'P00002'), false);

        // ENSG001 already pinned → its extra protein allowed; ENSG003 blocked
        component.pinProteinsBatched([
          makeGene('ENSG001', 'P00003'),
          makeGene('ENSG003', 'P00004'),
        ]);

        expect(addSpy).toHaveBeenCalledTimes(1);
        const detail: string = addSpy.mock.calls[0][0].detail;
        expect(detail).toContain('Only 1 row was pinned');
      });

      it('pinProteinsBatched shows no toast when no proteins are blocked', () => {
        component.category = 'Protein - Differential Expression';
        component.pinProteinsBatched([
          makeGene('ENSG001', 'P00001'),
          makeGene('ENSG002', 'P00002'),
        ]);

        expect(addSpy).not.toHaveBeenCalled();
      });
    });

    describe('pinItems — RNA - Differential Expression', () => {
      beforeEach(() => {
        component.category = 'RNA - Differential Expression';
      });

      it('pins all genes when under the limit', () => {
        component.pinItems([makeGene('ENSG001'), makeGene('ENSG002')]);

        expect(component.pinnedItems.length).toBe(2);
        expect(component.uniquePinnedGenesCount).toBe(2);
      });

      it('pins only up to maxPinnedGenes when the batch exceeds the limit', () => {
        component.maxPinnedGenes = 2;
        component.pinItems([makeGene('ENSG001'), makeGene('ENSG002'), makeGene('ENSG003')]);

        expect(component.pinnedItems.length).toBe(2);
        expect(component.uniquePinnedGenesCount).toBe(2);
      });

      it('is a no-op when already at the limit', () => {
        component.maxPinnedGenes = 1;
        component.pinItem(makeGene('ENSG001'), false);
        component.pinItems([makeGene('ENSG002')]);

        expect(component.pinnedItems.length).toBe(1);
      });

      it('shows a toast when the batch is truncated', () => {
        const addSpy = jest.spyOn(component.messageService, 'add');
        component.maxPinnedGenes = 2;
        component.pinItems([makeGene('ENSG001'), makeGene('ENSG002'), makeGene('ENSG003')]);

        expect(addSpy).toHaveBeenCalledTimes(1);
        addSpy.mockRestore();
      });
    });

    describe('pinItems — Protein - Differential Expression', () => {
      beforeEach(() => {
        component.category = 'Protein - Differential Expression';
      });

      it('pins all proteins when under the gene limit', () => {
        component.pinItems([makeGene('ENSG001', 'P00001'), makeGene('ENSG002', 'P00002')]);

        expect(component.pinnedItems.length).toBe(2);
        expect(component.uniquePinnedGenesCount).toBe(2);
      });

      it('at gene limit, pins only proteins whose genes are already pinned', () => {
        component.maxPinnedGenes = 2;
        component.pinItem(makeGene('ENSG001', 'P00001'), false);
        component.pinItem(makeGene('ENSG002', 'P00002'), false);

        component.pinItems([
          makeGene('ENSG001', 'P00003'), // Should be pinned (ENSG001 already pinned)
          makeGene('ENSG002', 'P00004'), // Should be pinned (ENSG002 already pinned)
          makeGene('ENSG003', 'P00005'), // Should be skipped (ENSG003 not pinned)
        ]);

        expect(component.pinnedItems.length).toBe(4);
        expect(component.uniquePinnedGenesCount).toBe(2);
        expect(component.pinnedItems.map((p) => p.uniprotid)).toEqual([
          'P00001',
          'P00002',
          'P00003',
          'P00004',
        ]);
      });

      it('at gene limit, skips all proteins when no genes are already pinned', () => {
        component.maxPinnedGenes = 1;
        component.pinItem(makeGene('ENSG001', 'P00001'), false);

        component.pinItems([makeGene('ENSG002', 'P00002'), makeGene('ENSG003', 'P00003')]);

        expect(component.pinnedItems.length).toBe(1);
        expect(component.uniquePinnedGenesCount).toBe(1);
      });
    });

    describe('cache behavior on initial load', () => {
      it('caches all pinned items when under the limit', () => {
        component.category = 'RNA - Differential Expression';
        component.maxPinnedGenes = 50;
        component.initialLoad = true;

        const itemsToPin = [makeGene('ENSG001'), makeGene('ENSG002'), makeGene('ENSG003')];

        // Simulate what initData does
        component.resetPinnedItemsState();
        component.pinItems(itemsToPin);
        if (component.initialLoad) {
          component.initialLoad = false;
          component.setPinnedItemsCache(component.pinnedItems);
        }

        expect(component.pinnedItemsCache.length).toBe(3);
        expect(component.pinnedItems.length).toBe(3);
      });

      it('only caches items up to the limit when exceeding maxPinnedGenes', () => {
        component.category = 'RNA - Differential Expression';
        component.maxPinnedGenes = 2;
        component.initialLoad = true;

        const itemsToPin = [
          makeGene('ENSG001'),
          makeGene('ENSG002'),
          makeGene('ENSG003'),
          makeGene('ENSG004'),
          makeGene('ENSG005'),
        ];

        // Simulate what initData does
        component.resetPinnedItemsState();
        component.pinItems(itemsToPin);
        if (component.initialLoad) {
          component.initialLoad = false;
          component.setPinnedItemsCache(component.pinnedItems);
        }

        // Only 2 items should be cached (matching the limit), not all 5
        expect(component.pinnedItemsCache.length).toBe(2);
        expect(component.pinnedItems.length).toBe(2);
        expect(component.pinnedItemsCache).toEqual(component.pinnedItems);
      });

      it('caches only pinned proteins when exceeding gene limit', () => {
        component.category = 'Protein - Differential Expression';
        component.maxPinnedGenes = 2; // 2 genes max
        component.initialLoad = true;

        const itemsToPin = [
          makeGene('ENSG001', 'P00001'),
          makeGene('ENSG002', 'P00002'),
          makeGene('ENSG003', 'P00003'), // This should not be pinned
          makeGene('ENSG003', 'P00004'), // This should not be pinned
        ];

        // Simulate what initData does
        component.resetPinnedItemsState();
        component.pinItems(itemsToPin);
        if (component.initialLoad) {
          component.initialLoad = false;
          component.setPinnedItemsCache(component.pinnedItems);
        }

        // Only 2 proteins should be cached (genes 1 and 2), not all 4
        expect(component.pinnedItemsCache.length).toBe(2);
        expect(component.pinnedItems.length).toBe(2);
        expect(component.uniquePinnedGenesCount).toBe(2);
        expect(component.pinnedItemsCache[0].ensembl_gene_id).toBe('ENSG001');
        expect(component.pinnedItemsCache[1].ensembl_gene_id).toBe('ENSG002');
      });

      it('does not update cache when not on initial load', () => {
        component.category = 'RNA - Differential Expression';
        component.maxPinnedGenes = 50;
        component.initialLoad = false; // Not initial load
        component.pinnedItemsCache = [makeGene('ENSG999')]; // Pre-existing cache

        const itemsToPin = [makeGene('ENSG001'), makeGene('ENSG002')];

        // Simulate what initData does
        component.resetPinnedItemsState();
        component.pinItems(itemsToPin);
        if (component.initialLoad) {
          component.initialLoad = false;
          component.setPinnedItemsCache(component.pinnedItems);
        }

        // Cache should not be updated since initialLoad was false
        expect(component.pinnedItemsCache.length).toBe(1);
        expect(component.pinnedItemsCache[0].ensembl_gene_id).toBe('ENSG999');
        expect(component.pinnedItems.length).toBe(2);
      });
    });
  });
});
