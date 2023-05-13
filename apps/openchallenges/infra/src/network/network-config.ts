import { NetworkConfigData } from './network-config-data';

export class NetworkConfig implements NetworkConfigData {
  defaultRegion: string;
  tagPrefix: string;
  vpcCirdBlock: string;

  constructor(config: NetworkConfigData) {
    this.defaultRegion = config.defaultRegion;
    this.tagPrefix = config.tagPrefix;
    this.vpcCirdBlock = config.vpcCirdBlock;
  }
}
