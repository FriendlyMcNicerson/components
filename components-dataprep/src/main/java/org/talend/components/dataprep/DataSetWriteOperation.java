package org.talend.components.dataprep;

import org.talend.components.api.component.runtime.Sink;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.Writer;
import org.talend.components.api.component.runtime.WriterResult;
import org.talend.components.api.container.RuntimeContainer;

/**
 * Created by stavytskyi on 4/19/16.
 */
public class DataSetWriteOperation implements WriteOperation<WriterResult> {

    private String url;
    private String dataSetName;
    private String mode;
    private Sink sink;

    public DataSetWriteOperation(Sink sink) {
        this.sink = sink;
    }

    @Override
    public void initialize(RuntimeContainer runtimeContainer) {

    }

    @Override
    public void finalize(Iterable<WriterResult> iterable, RuntimeContainer runtimeContainer) {

    }

    @Override
    public Writer<WriterResult> createWriter(RuntimeContainer runtimeContainer) {
        return new DataSetOutputWriter();
    }

    @Override
    public Sink getSink() {
        return sink;
    }

    public DataSetWriteOperation setUrl(String url) {
        this.url = url;
        return this;
    }

    public DataSetWriteOperation setDataSetName (String dataSetName){
        this.dataSetName = dataSetName;
        return this;
    }

    public DataSetWriteOperation setMode(String mode) {
        this.mode = mode;
        return this;
    }
}
