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
package org.talend.components.marketo.runtime.client.rest.type;

public class FieldDescription {

    // {"id":2,"displayName":"Company Name","dataType":"string","length":255,"rest":{"name":"company",
    // "readOnly":false},"soap":{"name":"Company","readOnly":false}}

    private Integer id;

    private String displayName;

    private String dataType;

    private Integer length;

    private ApiFieldName rest;

    private ApiFieldName soap;

    public class ApiFieldName {

        private String name;

        private Boolean readOnly;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getReadOnly() {
            return readOnly;
        }

        public void setReadOnly(Boolean readOnly) {
            this.readOnly = readOnly;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("ApiFieldName{");
            sb.append("name='").append(name).append('\'');
            sb.append(", readOnly=").append(readOnly);
            sb.append('}');
            return sb.toString();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public ApiFieldName getRest() {
        return rest;
    }

    public void setRest(ApiFieldName rest) {
        this.rest = rest;
    }

    public ApiFieldName getSoap() {
        return soap;
    }

    public void setSoap(ApiFieldName soap) {
        this.soap = soap;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("FieldDescription{");
        sb.append("id=").append(id);
        sb.append(", displayName='").append(displayName).append('\'');
        sb.append(", dataType='").append(dataType).append('\'');
        sb.append(", length=").append(length);
        sb.append(", rest=").append(rest);
        sb.append(", soap=").append(soap);
        sb.append('}');
        return sb.toString();
    }
}
