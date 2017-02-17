package org.talend.components.marketo.runtime.client.rest.type;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class LeadActivityRecord {

    private Long id;
    private Integer leadId;
    private Date activityDate;
    private Integer activityTypeId;
    private String activityTypeValue;
    private String primaryAttributeValueId;
    private String primaryAttributeValue;
    private List<Map<String, String>> attributes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLeadId() {
        return leadId;
    }

    public void setLeadId(Integer leadId) {
        this.leadId = leadId;
    }

    public Date getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(Date activityDate) {
        this.activityDate = activityDate;
    }

    public Integer getActivityTypeId() {
        return activityTypeId;
    }

    public void setActivityTypeId(Integer activityTypeId) {
        this.activityTypeId = activityTypeId;
    }

    public String getActivityTypeValue() {
        return activityTypeValue;
    }

    public void setActivityTypeValue(String activityTypeValue) {
        this.activityTypeValue = activityTypeValue;
    }

    public String getPrimaryAttributeValueId() {
        return primaryAttributeValueId;
    }

    public void setPrimaryAttributeValueId(String primaryAttributeValueId) {
        this.primaryAttributeValueId = primaryAttributeValueId;
    }

    public String getPrimaryAttributeValue() {
        return primaryAttributeValue;
    }

    public void setPrimaryAttributeValue(String primaryAttributeValue) {
        this.primaryAttributeValue = primaryAttributeValue;
    }

    public List<Map<String, String>> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Map<String, String>> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "LeadActivityRecord [id=" + id + ", leadId=" + leadId
                + ", activityDate=" + activityDate + ", activityTypeId="
                + activityTypeId + ", activityTypeValue=" + activityTypeValue
                + ", primaryAttributeValueId=" + primaryAttributeValueId
                + ", primaryAttributeValue=" + primaryAttributeValue
                + ", attributes=" + attributes + "]";
    }

}
