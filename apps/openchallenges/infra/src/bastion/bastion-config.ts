import { BastionConfigData } from './bastion-config-data';

export class BastionConfig implements BastionConfigData {
  ami: string;
  defaultRegion: string;
  hello: string;
  instanceType: string;
  keyName: string;
  privateIp: string | undefined;
  securityGroupId: string;
  subnetId: string;
  tagPrefix: string;

  constructor(config: BastionConfigData) {
    this.ami = config.ami;
    this.defaultRegion = config.defaultRegion;
    this.hello = config.hello;
    this.instanceType = config.instanceType;
    this.keyName = config.keyName;
    this.privateIp = config.privateIp;
    this.securityGroupId = config.securityGroupId;
    this.subnetId = config.subnetId;
    this.tagPrefix = config.tagPrefix;
  }
}
