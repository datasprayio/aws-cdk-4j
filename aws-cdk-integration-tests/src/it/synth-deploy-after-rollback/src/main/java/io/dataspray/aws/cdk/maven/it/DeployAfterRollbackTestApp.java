package io.dataspray.aws.cdk.maven.it;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;


public class DeployAfterRollbackTestApp {

    public static void main(String[] args) {
        App app = new App();
        new DeployAfterRollbackTestStack(app, "synth-deploy-after-rollback-test-stack");
        app.synth();
    }

}
