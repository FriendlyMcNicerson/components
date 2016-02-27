// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.salesforce.runtime;

import org.talend.components.api.adaptor.Adaptor;
import org.talend.components.api.component.runtime.Sink;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.Writer;
import org.talend.components.api.component.runtime.WriterResult;

final class SalesforceWriteOperation implements WriteOperation<WriterResult> {

    private SalesforceSink ssink;

    public SalesforceWriteOperation(SalesforceSink ssink) {
        this.ssink = ssink;
    }

    @Override
    public void initialize(Adaptor adaptor) {
    }

    @Override
    public Sink getSink() {
        return ssink;
    }

    @Override
    public void finalize(Iterable<WriterResult> writerResults, Adaptor adaptor) {
    }

    @Override
    public Writer<WriterResult> createWriter(Adaptor adaptor) {
        return new SalesforceWriter(this, adaptor);
    }

}