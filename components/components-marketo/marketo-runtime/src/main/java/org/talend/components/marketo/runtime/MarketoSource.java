package org.talend.components.marketo.runtime;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.BoundedReader;
import org.talend.components.api.component.runtime.BoundedSource;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.marketo.tmarketoinput.TMarketoInputProperties;
import org.talend.daikon.properties.ValidationResult;

public class MarketoSource extends MarketoSourceOrSink implements BoundedSource {

    private transient static final Logger LOG = LoggerFactory.getLogger(MarketoSource.class);

    public MarketoSource() {
    }

    @Override
    public List<? extends BoundedSource> splitIntoBundles(long desiredBundleSizeBytes, RuntimeContainer adaptor)
            throws Exception {
        List<BoundedSource> list = new ArrayList<>();
        list.add(this);
        return list;
    }

    @Override
    public long getEstimatedSizeBytes(RuntimeContainer adaptor) {
        return 0;
    }

    @Override
    public boolean producesSortedKeys(RuntimeContainer adaptor) {
        return false;
    }

    @Override
    public ValidationResult validate(RuntimeContainer container) {
        // TODO validate TMarketoInputProperties
        return super.validate(container);
    }

    @Override
    public BoundedReader createReader(RuntimeContainer adaptor) {
        return new MarketoInputReader(adaptor, this, (TMarketoInputProperties) properties);
    }

}
