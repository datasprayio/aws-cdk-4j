package io.dataspray.aws.cdk.maven.it;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.CfnParameter;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.s3.BlockPublicAccess;
import software.amazon.awscdk.services.s3.Bucket;
import software.constructs.Construct;


public class DeployBasicTestStack extends Stack {

    public DeployBasicTestStack(final Construct scope, final String id, String stage) {
        super(scope, id);

        CfnParameter parameter = CfnParameter.Builder.create(this, "Parameter")
                .defaultValue("DefaultValue")
                .build();

        Bucket bucket = Bucket.Builder.create(this, "Bucket")
                .removalPolicy(RemovalPolicy.DESTROY)
                .bucketName(String.join("-", id, stage))
                .blockPublicAccess(BlockPublicAccess.BLOCK_ALL)
                .build();

        CfnOutput.Builder.create(this, "BucketName")
                .description("The name of the bucket")
                .value(bucket.getBucketName())
                .build();

        CfnOutput.Builder.create(this, "ParameterValue")
                .value(parameter.getValueAsString())
                .build();

        CfnOutput.Builder.create(this, "Stage")
                .value(stage)
                .build();
    }
}
