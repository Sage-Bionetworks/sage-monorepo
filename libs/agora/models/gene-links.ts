import { SimulationLinkDatum, SimulationNodeDatum } from 'd3';

export interface NetworkChartNode extends SimulationNodeDatum {
  id: string;
  label?: string;
  value?: number;
  class?: string;
}

export interface NetworkChartLink extends SimulationLinkDatum<SimulationNodeDatum> {
  source: NetworkChartNode;
  target: NetworkChartNode;
  value?: number;
  class?: string;
}

export interface NetworkChartData {
  nodes: NetworkChartNode[];
  links: NetworkChartLink[];
}
