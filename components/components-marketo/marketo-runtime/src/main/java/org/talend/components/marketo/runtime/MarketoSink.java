package org.talend.components.marketo.runtime;

import org.talend.components.api.component.runtime.Sink;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.marketo.tmarketolistoperation.TMarketoListOperationProperties;
import org.talend.components.marketo.tmarketooutput.TMarketoOutputProperties;
import org.talend.daikon.properties.ValidationResult;

public class MarketoSink extends MarketoSourceOrSink implements Sink {

    @Override
    public WriteOperation<?> createWriteOperation() {
        if (properties instanceof TMarketoListOperationProperties) {
            return new MarketoListOperationWriteOperation(this);
        }
        if (properties instanceof TMarketoOutputProperties) {
            return new MarketoOutputWriteOperation(this);
        }
        return null;
    }

    @Override
    public ValidationResult validate(RuntimeContainer container) {
        // TODO TMarketoListOperationProperties || TMarketoOutputProperties
        return super.validate(container);
    }

}
