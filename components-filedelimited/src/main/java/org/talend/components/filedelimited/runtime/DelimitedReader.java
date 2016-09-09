package org.talend.components.filedelimited.runtime;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.BoundedSource;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.filedelimited.tFileInputDelimited.TFileInputDelimitedProperties;
import org.talend.fileprocess.FileInputDelimited;

public class DelimitedReader extends FileDelimitedReader {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(DelimitedReader.class);

    private FileInputDelimited fid;

    public DelimitedReader(RuntimeContainer container, BoundedSource source, TFileInputDelimitedProperties properties) {
        super(container, source, properties);
    }

    @Override
    public boolean start() throws IOException {
        fileDelimitedRuntime.init();
        LOGGER.debug("open: " + properties.fileName.getStringValue());
        boolean startAble = false;
        fid = fileDelimitedRuntime.getFileDelimited();
        startAble = fid != null && fid.nextRecord();
        if (startAble) {
            getCurrentRecord();
        }
        return startAble;
    }

    @Override
    public boolean advance() throws IOException {
        boolean isContinue = false;
        isContinue = fid.nextRecord();
        if (!isContinue) {
            if (properties.uncompress.getValue()) {
                fid = fileDelimitedRuntime.getFileDelimited();
                isContinue = fid != null && fid.nextRecord();
            }
        }
        if (isContinue) {
            getCurrentRecord();
        }
        return isContinue;
    }

    @Override
    public void close() throws IOException {
        if (!(fileDelimitedRuntime.fileNameOrStream instanceof InputStream)) {
            if (fid != null) {
                fid.close();
            }
        }
        LOGGER.debug("close: " + properties.fileName.getStringValue());
    }

    protected void getCurrentRecord() throws IOException {
        values = new String[schema.getFields().size()];
        // TODO consider dynamic
        for (int i = 0; i < schema.getFields().size(); i++) {
            values[i] = (fid.get(i));
        }
        currentIndexRecord = ((DelimitedAdaptorFactory) factory).convertToAvro(values);
    }

}
