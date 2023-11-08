package io.dataspray.aws.cdk.maven.it;

import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;


public class SynthAppExtensionTestStack extends Stack {

    public SynthAppExtensionTestStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public SynthAppExtensionTestStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);
    }
}
