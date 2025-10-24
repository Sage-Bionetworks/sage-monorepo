import { Component, computed, inject, ViewEncapsulation } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ComparisonToolConfig } from '@sagebionetworks/explorers/models';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { PopoverLinkComponent } from '@sagebionetworks/explorers/util';
import { SelectItem } from 'primeng/api';
import { Select } from 'primeng/select';

interface DropdownTree {
  [key: string]: DropdownTree;
}

@Component({
  selector: 'explorers-comparison-tool-category-selectors',
  imports: [FormsModule, Select, PopoverLinkComponent],
  templateUrl: './comparison-tool-category-selectors.component.html',
  styleUrls: ['./comparison-tool-category-selectors.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ComparisonToolCategorySelectorsComponent {
  private readonly comparisonToolService = inject(ComparisonToolService);

  pageConfigs = computed(() => this.comparisonToolService.configs());
  selectorsWikiParams = computed(() => this.comparisonToolService.selectorsWikiParams());

  dropdownTree = computed(() => {
    const configs = this.pageConfigs();
    if (!configs || configs.length === 0) {
      return {};
    }
    return this.buildDropdownTree(configs);
  });
  dropdownLevels = computed(() => {
    const tree = this.dropdownTree();
    return this.getDropdownOptionsFromTree(tree);
  });

  private buildDropdownTree(configs: ComparisonToolConfig[]): DropdownTree {
    const tree: DropdownTree = {};

    for (const config of configs) {
      let currentLevel = tree;
      for (const value of config.dropdowns ?? []) {
        if (!currentLevel[value]) {
          currentLevel[value] = {};
        }
        currentLevel = currentLevel[value];
      }
    }

    return tree;
  }

  private getDropdownOptionsFromTree(tree: DropdownTree): SelectItem[][] {
    const levels: SelectItem[][] = [];
    let currentTree = tree;

    const selection = this.getSelection();

    let levelIndex = 0;
    while (this.hasKeys(currentTree)) {
      const options = Object.keys(currentTree).map((key) => ({
        label: key,
        value: key,
      }));

      levels.push(options);

      if (levelIndex < selection.length) {
        const selectedValue = selection[levelIndex];
        if (currentTree[selectedValue]) {
          currentTree = currentTree[selectedValue];
        } else {
          // Selection is invalid
          break;
        }
      } else {
        // No more selections
        break;
      }

      levelIndex++;
    }

    return levels;
  }

  private autoCompleteSelection(currentLevel: DropdownTree, selection: string[]): void {
    while (this.hasKeys(currentLevel)) {
      const firstKey = Object.keys(currentLevel)[0];
      selection.push(firstKey);
      currentLevel = currentLevel[firstKey];
    }
  }

  onDropdownChange(levelIndex: number, selectedValue: string): void {
    const currentSelection = this.getSelection();
    const newSelection = currentSelection.slice(0, levelIndex + 1);
    newSelection[levelIndex] = selectedValue;

    const tree = this.dropdownTree();
    let currentLevel = tree;

    for (const selection of newSelection) {
      if (currentLevel[selection]) {
        currentLevel = currentLevel[selection];
      } else {
        break;
      }
    }

    this.autoCompleteSelection(currentLevel, newSelection);

    this.comparisonToolService.setDropdownSelection(newSelection);
  }

  getSelectedValueForLevel(levelIndex: number): string | undefined {
    return this.getSelection()[levelIndex];
  }

  private hasKeys(tree: DropdownTree): boolean {
    return Object.keys(tree).length > 0;
  }

  private getSelection(): string[] {
    return this.comparisonToolService.dropdownSelection();
  }
}
