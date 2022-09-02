package io.dataspray.aws.cdk.maven.it;

import io.dataspray.aws.cdk.AwsCdk;
import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.Environment;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.cxapi.CloudAssembly;


public class DeployBasicTestApp {

    public static void main(String[] args) {
        String stage = args.length > 0 ? args[0] : "dev";
        App app = new App();
        new DeployBasicTestStack(app, "synth-deploy-basic-test-stack-as-library", stage);
        CloudAssembly assembly = app.synth();
        AwsCdk.bootstrap().execute(assembly);
        AwsCdk.deploy().execute(assembly);
    }

}
