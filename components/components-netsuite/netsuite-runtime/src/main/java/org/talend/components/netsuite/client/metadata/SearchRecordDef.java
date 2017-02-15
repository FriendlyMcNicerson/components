package org.talend.components.netsuite.client.metadata;

import org.talend.components.netsuite.client.NetSuiteClientService;

/**
 *
 */
public class SearchRecordDef {
    private String name;
    private String searchRecordType;
    private Class<?> searchClass;
    private Class<?> searchBasicClass;
    private Class<?> searchAdvancedClass;

    public SearchRecordDef(String searchRecordType,
            Class<?> searchClass, Class<?> searchBasicClass, Class<?> searchAdvancedClass) {
        this.name = NetSuiteClientService.toInitialUpper(searchRecordType);
        this.searchRecordType = searchRecordType;
        this.searchClass = searchClass;
        this.searchBasicClass = searchBasicClass;
        this.searchAdvancedClass = searchAdvancedClass;
    }

    public String getName() {
        return name;
    }

    public String getSearchRecordType() {
        return searchRecordType;
    }

    public Class<?> getSearchClass() {
        return searchClass;
    }

    public Class<?> getSearchBasicClass() {
        return searchBasicClass;
    }

    public Class<?> getSearchAdvancedClass() {
        return searchAdvancedClass;
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder("SearchRecordDef{");
        sb.append("name='").append(name).append('\'');
        sb.append(", searchRecordType=").append(searchRecordType);
        sb.append(", searchClass=").append(searchClass);
        sb.append(", searchBasicClass=").append(searchBasicClass);
        sb.append(", searchAdvancedClass=").append(searchAdvancedClass);
        sb.append('}');
        return sb.toString();
    }
}
