import { PreviewInstanceConfigData } from './preview-instance-config-data';

export class PreviewInstanceConfig implements PreviewInstanceConfigData {
  ami: string;
  defaultRegion: string;
  instanceType: string;
  keyName: string;
  securityGroupIds: string[];
  subnetId: string;
  tagPrefix: string;

  constructor(config: PreviewInstanceConfigData) {
    this.ami = config.ami;
    this.defaultRegion = config.defaultRegion;
    this.instanceType = config.instanceType;
    this.keyName = config.keyName;
    this.securityGroupIds = config.securityGroupIds;
    this.subnetId = config.subnetId;
    this.tagPrefix = config.tagPrefix;
  }
}
