package org.talend.components.marketo.runtime.client.rest.type;

import java.util.List;
import java.util.Map;

public class SyncStatus {

    private Integer id;

    private String status;

    private List<Map<String, String>> reasons;

    private String errorMessage;

    public SyncStatus() {
    }

    public SyncStatus(int id, String status) {
        this.id = id;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Map<String, String>> getReasons() {
        return reasons;
    }

    public void setReasons(List<Map<String, String>> reasons) {
        this.reasons = reasons;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SyncStatus{");
        sb.append("id=").append(id);
        sb.append(", status='").append(status).append('\'');
        sb.append(", errorMessage='").append(errorMessage).append('\'');
        sb.append(", reasons=").append(reasons);
        sb.append('}');
        return sb.toString();
    }
}
