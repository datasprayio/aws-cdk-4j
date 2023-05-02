package io.dataspray.aws.cdk.maven.it;

import software.amazon.awscdk.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;


public class SynthContextTestStack extends Stack {

    public SynthContextTestStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public SynthContextTestStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        getAvailabilityZones();
    }
}
