package io.dataspray.aws.cdk.maven.it;

import io.dataspray.aws.cdk.AwsCdk;
import software.amazon.awscdk.App;
import software.amazon.awscdk.cxapi.CloudAssembly;


public class DestroyTestApp {

    public static void main(String[] args) {
        App app = new App();
        new DestroyTestStack(app, "destroy-test-stack-as-library");
        CloudAssembly assembly = app.synth();
        AwsCdk.destroy().execute(assembly);
    }

}
