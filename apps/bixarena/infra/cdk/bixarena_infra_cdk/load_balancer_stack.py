import aws_cdk as cdk
from aws_cdk import aws_ec2 as ec2
from aws_cdk import aws_elasticloadbalancingv2 as elbv2
from aws_cdk import aws_wafv2 as wafv2
from constructs import Construct


class LoadBalancerStack(cdk.Stack):
    """Application load balancer and WAF."""

    def __init__(
        self, scope: Construct, construct_id: str, vpc: ec2.Vpc, **kwargs
    ) -> None:
        super().__init__(scope, construct_id, **kwargs)

        self.alb = elbv2.ApplicationLoadBalancer(
            self, "AppLoadBalancer", vpc=vpc, internet_facing=True
        )

        # WAF to protect against common web attacks
        web_acl = wafv2.CfnWebACL(
            self,
            "WebAcl",
            scope="REGIONAL",
            default_action=wafv2.CfnWebACL.DefaultActionProperty(allow={}),
            visibility_config=wafv2.CfnWebACL.VisibilityConfigProperty(
                cloud_watch_metrics_enabled=True,
                metric_name="WebAclMetrics",
                sampled_requests_enabled=True,
            ),
            rules=[
                # Managed rule group set
                wafv2.CfnWebACL.RuleProperty(
                    name="AWSManagedRulesCommonRuleSet",
                    priority=0,
                    statement=wafv2.CfnWebACL.StatementProperty(
                        managed_rule_group_statement=wafv2.CfnWebACL.ManagedRuleGroupStatementProperty(
                            name="AWSManagedRulesCommonRuleSet",
                            vendor_name="AWS",
                        )
                    ),
                    override_action=wafv2.CfnWebACL.OverrideActionProperty(none={}),
                    visibility_config=wafv2.CfnWebACL.VisibilityConfigProperty(
                        cloud_watch_metrics_enabled=True,
                        metric_name="AWSManagedRulesCommonRuleSet",
                        sampled_requests_enabled=True,
                    ),
                ),
                # Known bad inputs
                wafv2.CfnWebACL.RuleProperty(
                    name="AWSManagedRulesKnownBadInputsRuleSet",
                    priority=1,
                    statement=wafv2.CfnWebACL.StatementProperty(
                        managed_rule_group_statement=wafv2.CfnWebACL.ManagedRuleGroupStatementProperty(
                            vendor_name="AWS",
                            name="AWSManagedRulesKnownBadInputsRuleSet",
                        )
                    ),
                    override_action=wafv2.CfnWebACL.OverrideActionProperty(none={}),
                    visibility_config=wafv2.CfnWebACL.VisibilityConfigProperty(
                        sampled_requests_enabled=True,
                        cloud_watch_metrics_enabled=True,
                        metric_name="AWSManagedRulesKnownBadInputsRuleSet",
                    ),
                ),
            ],
        )

        wafv2.CfnWebACLAssociation(
            self,
            "WeAclAssociation",
            resource_arn=self.alb.load_balancer_arn,
            web_acl_arn=web_acl.attr_arn,
        )

        cdk.CfnOutput(
            self,
            "LoadBalancerDns",
            value=self.alb.load_balancer_dns_name,
            export_name=f"{construct_id}-dns",
        )
