export interface Panel {
  name: string; // Unique identifier for the panel
  label: string; // Display name shown in the UI
  disabled: boolean; // Flag to enable/disable panel
  children?: Panel[]; // Optional sub-panels for hierarchical navigation
}
