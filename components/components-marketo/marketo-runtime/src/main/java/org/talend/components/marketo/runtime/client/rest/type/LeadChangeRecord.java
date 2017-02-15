package org.talend.components.marketo.runtime.client.rest.type;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class LeadChangeRecord {
    private int id;

    private int leadId;

    private Date activityDate;

    private int activityTypeId;

    private String activityTypeValue;

    private List<Map<String, String>> fields;

    private List<Map<String, String>> attributes;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setLeadId(int leadId) {
        this.leadId = leadId;
    }

    public int getLeadId() {
        return this.leadId;
    }

    public void setActivityDate(Date activityDate) {
        this.activityDate = activityDate;
    }

    public Date getActivityDate() {
        return this.activityDate;
    }

    public void setActivityTypeId(int activityTypeId) {
        this.activityTypeId = activityTypeId;
    }

    public String getActivityTypeValue() {
        return activityTypeValue;
    }

    public void setActivityTypeValue(String activityTypeValue) {
        this.activityTypeValue = activityTypeValue;
    }

    public int getActivityTypeId() {
        return this.activityTypeId;
    }

    public void setFields(List<Map<String, String>> fields) {
        this.fields = fields;
    }

    public List<Map<String, String>> getFields() {
        return this.fields;
    }

    public void setAttributes(List<Map<String, String>> attributes) {
        this.attributes = attributes;
    }

    public List<Map<String, String>> getAttributes() {
        return this.attributes;
    }

    @Override
    public String toString() {
        return "LeadChangeRecord [id=" + id + ", leadId=" + leadId
                + ", activityDate=" + activityDate + ", activityTypeId="
                + activityTypeId + ", activityTypeValue=" + activityTypeValue
                + ", fields=" + fields + ", attributes=" + attributes + "]";
    }

}
