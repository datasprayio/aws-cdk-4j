package io.dataspray.aws.cdk.maven.it;

import software.amazon.awscdk.App;


public class DeployLambdaFunctionTestApp {

    public static void main(String[] args) {
        App app = new App();
        new DeployLambdaFunctionTestStack(app, "deploy-lambda-function-test-stack");
        app.synth();
    }

}
