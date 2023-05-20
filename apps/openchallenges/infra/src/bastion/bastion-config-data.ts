export interface BastionConfigData {
  ami: string;
  defaultRegion: string;
  instanceType: string;
  keyName: string;
  securityGroupId: string;
  subnetId: string;
  tagPrefix: string;
}
