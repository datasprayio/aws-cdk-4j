package io.dataspray.aws.cdk.maven.it;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;


public class SynthContextTestApp {

    public static void main(String[] args) {
        App app = new App();
        Environment environment = Environment.builder()
                .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
                .region(System.getenv("CDK_DEFAULT_REGION"))
                .build();
        StackProps stackProps = StackProps.builder()
                .env(environment)
                .build();
        new SynthContextTestStack(app, "synth-context-test-stack", stackProps);
        app.synth();
    }

}
