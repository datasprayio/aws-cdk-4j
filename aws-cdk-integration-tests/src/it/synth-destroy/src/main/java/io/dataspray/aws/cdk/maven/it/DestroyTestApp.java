package io.dataspray.aws.cdk.maven.it;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Stack;


public class DestroyTestApp {

    public static void main(String[] args) {
        App app = new App();
        new DestroyTestStack(app, "destroy-test-stack");
        app.synth();
    }

}
