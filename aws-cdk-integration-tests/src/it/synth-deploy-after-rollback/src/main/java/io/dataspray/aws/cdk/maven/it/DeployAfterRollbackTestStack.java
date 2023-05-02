package io.dataspray.aws.cdk.maven.it;

import software.amazon.awscdk.Construct;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;


public class DeployAfterRollbackTestStack extends Stack {

    public DeployAfterRollbackTestStack(final Construct scope, final String id) {
        super(scope, id);

        Table.Builder.create(this, "UserTable")
                .removalPolicy(RemovalPolicy.DESTROY)
                .tableName("deploy_after_rollback_it_user")
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .partitionKey(Attribute.builder()
                        .name("id")
                        .type(AttributeType.STRING)
                        .build())
                .build();
    }
}
