export interface PreviewInstanceConfigData {
  ami: string;
  defaultRegion: string;
  instanceType: string;
  keyName: string;
  securityGroupIds: string[];
  subnetId: string;
  tagPrefix: string;
}
