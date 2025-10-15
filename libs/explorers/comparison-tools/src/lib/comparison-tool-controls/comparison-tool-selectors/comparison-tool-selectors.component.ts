import { Component, computed, effect, input, model, ViewEncapsulation } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ComparisonToolConfig, SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { isEqual } from 'lodash';
import { SelectItem } from 'primeng/api';
import { Select } from 'primeng/select';

interface DropdownTree {
  [key: string]: DropdownTree;
}

@Component({
  selector: 'explorers-comparison-tool-selectors',
  imports: [FormsModule, Select],
  templateUrl: './comparison-tool-selectors.component.html',
  styleUrls: ['./comparison-tool-selectors.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ComparisonToolSelectorsComponent {
  pageConfigs = input<ComparisonToolConfig[]>([]);
  popoverWikis = input<{ [key: string]: SynapseWikiParams }>({});
  selection = model<string[]>([], { alias: 'dropdownsSelection' });

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

  constructor() {
    effect(() => {
      this.validateSelection();
    });
  }

  private buildDropdownTree(configs: ComparisonToolConfig[]): DropdownTree {
    const tree: DropdownTree = {};

    configs.forEach((config) => {
      let currentLevel = tree;
      config.dropdowns.forEach((value) => {
        if (!currentLevel[value]) {
          currentLevel[value] = {};
        }
        currentLevel = currentLevel[value];
      });
    });

    return tree;
  }

  private getDropdownOptionsFromTree(tree: DropdownTree): SelectItem[][] {
    const levels: SelectItem[][] = [];
    let currentTree = tree;

    let levelIndex = 0;
    while (this.hasKeys(currentTree)) {
      const options = Object.keys(currentTree).map((key) => ({
        label: key,
        value: key,
      }));

      levels.push(options);

      if (levelIndex < this.selection().length) {
        const selectedValue = this.selection()[levelIndex];
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

  private validateSelection(): void {
    const tree = this.dropdownTree();
    const selection = this.selection();

    if (!this.hasKeys(tree)) {
      if (selection.length > 0) {
        this.selection.set([]);
      }
      return;
    }

    const normalizedSelection = this.normalizeSelection(selection, tree);
    if (!isEqual(selection, normalizedSelection)) {
      this.selection.set(normalizedSelection);
    }
  }

  private autoCompleteSelection(currentLevel: DropdownTree, selection: string[]): void {
    while (this.hasKeys(currentLevel)) {
      const firstKey = Object.keys(currentLevel)[0];
      selection.push(firstKey);
      currentLevel = currentLevel[firstKey];
    }
  }

  private normalizeSelection(selection: string[], tree: DropdownTree): string[] {
    if (!this.hasKeys(tree)) {
      return [];
    }

    const normalized: string[] = [];
    let currentLevel = tree;

    for (const value of selection) {
      const nextLevel = currentLevel[value];
      if (!nextLevel) {
        break;
      }

      normalized.push(value);
      currentLevel = nextLevel;
    }

    if (normalized.length === 0) {
      this.autoCompleteSelection(tree, normalized);
      return normalized;
    }

    this.autoCompleteSelection(currentLevel, normalized);
    return normalized;
  }

  onDropdownChange(levelIndex: number, selectedValue: string): void {
    const newSelection = this.selection().slice(0, levelIndex + 1);
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

    this.selection.set(newSelection);
  }

  getSelectedValueForLevel(levelIndex: number): string | undefined {
    return this.selection()[levelIndex];
  }

  private hasKeys(tree: DropdownTree): boolean {
    return Object.keys(tree).length > 0;
  }
}
