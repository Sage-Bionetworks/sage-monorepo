export interface BastionConfigData {
  ami: string;
  defaultRegion: string;
  instanceType: string;
  keyName: string;
  privateIp: string | undefined;
  securityGroupId: string;
  subnetId: string;
  tagPrefix: string;
}
