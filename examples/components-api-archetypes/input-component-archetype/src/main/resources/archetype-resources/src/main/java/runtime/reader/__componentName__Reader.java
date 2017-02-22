#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package ${package}.runtime.reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.AbstractBoundedReader;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.common.avro.RootSchemaUtils;
import ${package}.${componentPackage}.${componentName}Properties;

/**
 * Simple implementation of a reader.
 */
public class ${componentName}Reader extends AbstractBoundedReader<IndexedRecord> {

    private static final Logger LOGGER = LoggerFactory.getLogger(${componentName}Reader.class);

    private final String filePath;
    
    private final Schema schema;
    
    private final Schema outOfBandSchema;
    
    /**
     * Runtime Root schema
     */
    private final Schema rootSchema;

    private boolean started = false;
    
    private boolean hasMore = false;

    private BufferedReader reader = null;

    private IndexedRecord current;
    
    /**
     * Holds values for return properties
     */
    private Result result;

    public ${componentName}Reader(${componentName}Source source) {
        super(source);
        this.filePath = source.getFilePath();
        this.schema = source.getDesignSchema();
        
        outOfBandSchema = ${componentName}Properties.outOfBandSchema;
        
        rootSchema = RootSchemaUtils.createRootSchema(schema, outOfBandSchema);
    }

    @Override
    public boolean start() throws IOException {
        reader = new BufferedReader(new FileReader(filePath));
        result = new Result();
        LOGGER.debug("open: " + filePath); //$NON-NLS-1$
        started = true;
        return advance();
    }

    @Override
    public boolean advance() throws IOException {
        if (!started) {
            throw new IllegalStateException("Reader wasn't started");
        }
        String line = reader.readLine();
        hasMore = line != null;
        if (hasMore) {
        	IndexedRecord dataRecord = new GenericData.Record(schema);
        	dataRecord.put(0, line);
        	IndexedRecord outOfBandRecord = new GenericData.Record(outOfBandSchema);
        	outOfBandRecord.put(0, result.totalCount);
        	current = new GenericData.Record(rootSchema);
        	current.put(0, dataRecord);
        	current.put(1, outOfBandRecord);
        	result.totalCount++;
        }
        return hasMore;
    }

    @Override
    public IndexedRecord getCurrent() throws NoSuchElementException {
        if (!started) {
            throw new NoSuchElementException("Reader wasn't started");
        }
        if (!hasMore) {
        	throw new NoSuchElementException("Has no more elements");
        }
        return current;
    }

    @Override
    public void close() throws IOException {
        if (!started) {
            throw new IllegalStateException("Reader wasn't started");
        }
        reader.close();
        LOGGER.debug("close: " + filePath); //$NON-NLS-1$
        reader = null;
        started = false;
        hasMore = false;
    }

    /**
     * Returns values of Return properties. It is called after component finished his work (after {@link this#close()} method)
     */
    @Override
    public Map<String, Object> getReturnValues() {
        return result.toMap();
    }

}
