package io.dataspray.aws.cdk.maven.it;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;


public class DeployBasicTestApp {

    public static void main(String[] args) {
        String stage = args.length > 0 ? args[0] : "dev";
        App app = new App();
        new DeployBasicTestStack(app, "synth-deploy-basic-test-stack", stage);
        app.synth();
    }

}
