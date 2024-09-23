import { NgModule, ModuleWithProviders, SkipSelf, Optional } from '@angular/core';
import { Configuration } from './configuration';
import { HttpClient } from '@angular/common/http';

import { AccessApprovalServicesService } from './api/accessApprovalServices.service';
import { AccessRequirementServicesService } from './api/accessRequirementServices.service';
import { ActivityServicesService } from './api/activityServices.service';
import { AgentChatServicesService } from './api/agentChatServices.service';
import { AsynchronousJobServicesService } from './api/asynchronousJobServices.service';
import { AuthenticationServicesService } from './api/authenticationServices.service';
import { AuthenticationServices2FAService } from './api/authenticationServices2FA.service';
import { CertifiedUserServicesService } from './api/certifiedUserServices.service';
import { ChallengeServicesService } from './api/challengeServices.service';
import { DOIServicesService } from './api/dOIServices.service';
import { DataAccessServicesService } from './api/dataAccessServices.service';
import { DiscussionServicesService } from './api/discussionServices.service';
import { DockerAuthorizationServicesService } from './api/dockerAuthorizationServices.service';
import { DockerCommitServicesService } from './api/dockerCommitServices.service';
import { DockerRegistryEventServicesService } from './api/dockerRegistryEventServices.service';
import { DownloadListServicesService } from './api/downloadListServices.service';
import { DrsServicesService } from './api/drsServices.service';
import { EntityBundleServicesV2Service } from './api/entityBundleServicesV2.service';
import { EntityServicesService } from './api/entityServices.service';
import { EvaluationServicesService } from './api/evaluationServices.service';
import { FileServicesService } from './api/fileServices.service';
import { FormServicesService } from './api/formServices.service';
import { JSONSchemaServicesService } from './api/jSONSchemaServices.service';
import { LogServiceService } from './api/logService.service';
import { MembershipInvitationServicesService } from './api/membershipInvitationServices.service';
import { MembershipRequestServicesService } from './api/membershipRequestServices.service';
import { MessageServicesService } from './api/messageServices.service';
import { OpenIDConnectServicesService } from './api/openIDConnectServices.service';
import { PrincipalServicesService } from './api/principalServices.service';
import { ProjectSettingsServicesService } from './api/projectSettingsServices.service';
import { RecycleBinServicesService } from './api/recycleBinServices.service';
import { SearchServicesService } from './api/searchServices.service';
import { StatisticsServicesService } from './api/statisticsServices.service';
import { StorageReportServicesService } from './api/storageReportServices.service';
import { SubscriptionServicesService } from './api/subscriptionServices.service';
import { TableServicesService } from './api/tableServices.service';
import { TeamServicesService } from './api/teamServices.service';
import { UserGroupServicesService } from './api/userGroupServices.service';
import { UserProfileServicesService } from './api/userProfileServices.service';
import { VerificationServicesService } from './api/verificationServices.service';
import { WebhookServicesService } from './api/webhookServices.service';
import { WikiPageServicesService } from './api/wikiPageServices.service';
import { WikiPageServices2Service } from './api/wikiPageServices2.service';

@NgModule({
  imports: [],
  declarations: [],
  exports: [],
  providers: [],
})
export class ApiModule {
  public static forRoot(configurationFactory: () => Configuration): ModuleWithProviders<ApiModule> {
    return {
      ngModule: ApiModule,
      providers: [{ provide: Configuration, useFactory: configurationFactory }],
    };
  }

  constructor(@Optional() @SkipSelf() parentModule: ApiModule, @Optional() http: HttpClient) {
    if (parentModule) {
      throw new Error('ApiModule is already loaded. Import in your base AppModule only.');
    }
    if (!http) {
      throw new Error(
        'You need to import the HttpClientModule in your AppModule! \n' +
          'See also https://github.com/angular/angular/issues/20575',
      );
    }
  }
}
