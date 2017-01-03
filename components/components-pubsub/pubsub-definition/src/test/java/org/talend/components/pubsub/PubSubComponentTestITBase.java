package org.talend.components.pubsub;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.junit.Test;
import org.talend.components.api.service.ComponentService;
import org.talend.components.api.test.AbstractComponentTest;
import org.talend.components.pubsub.input.PubSubInputDefinition;
import org.talend.components.pubsub.output.PubSubOutputDefinition;

public abstract class PubSubComponentTestITBase extends AbstractComponentTest {

    @Inject
    ComponentService componentService;

    @Override
    public ComponentService getComponentService() {
        return componentService;
    }

    @Test
    public void assertComponentsAreRegistered() {
        assertThat(getComponentService().getComponentDefinition(PubSubInputDefinition.NAME), notNullValue());
        assertThat(getComponentService().getComponentDefinition(PubSubOutputDefinition.NAME), notNullValue());
    }
}
