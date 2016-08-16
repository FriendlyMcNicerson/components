// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.datastewardship.runtime;

import org.apache.avro.Schema;
import org.talend.components.api.component.runtime.Sink;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.datastewardship.CampaignType;
import org.talend.components.datastewardship.tdatastewardshiptaskoutput.TDataStewardshipTaskOutputProperties;

/**
 * TDS Task {@link Sink}
 */
public class TdsTaskSink extends TdsSink {

    private static final long serialVersionUID = -7153395345785814016L;

    /**
     * Data schema
     */
    private Schema schema;

    /**
     * Campaign name
     */
    private String campaignName;

    /**
     * Campaign type
     */
    private String campaignType;

    /**
     * Task priority
     */
    private Integer taskPriority;

    /**
     * Task tags
     */
    private String taskTags;

    /**
     * Task state
     */
    private String taskState;

    /**
     * Task assignee
     */
    private String taskAssignee;

    /**
     * Task comment
     */
    private String taskComment;
    
    /**
     * Batch size
     */
    private int batchSize = -1;
    
    /**
     * Group ID
     */
    private String groupIdColumn;
    
    /**
     * Source
     */
    private String sourceColumn;

    /**
     * Master Indicator
     */
    private String masterColumn;

    /**
     * Score
     */
    private String scoreColumn;

    /**
     * Initializes this {@link Sink} with user specified properties
     * 
     * @param container {@link RuntimeContainer} instance
     * @param properties user specified properties
     */
    @Override
    public void initialize(RuntimeContainer container, ComponentProperties properties) {
        super.initialize(container, properties);
        TDataStewardshipTaskOutputProperties outputProperties = (TDataStewardshipTaskOutputProperties) properties;
        schema = outputProperties.schema.schema.getValue();
        campaignName = outputProperties.campaign.campaignName.getValue();
        campaignType = outputProperties.campaign.campaignType.getValue().toString();
        /*
        taskPriority = outputProperties.tasksMetadata.taskPriority.getValue().getValue();
        taskTags = outputProperties.tasksMetadata.taskTags.getValue();
        taskState = outputProperties.tasksMetadata.taskState.getValue();
        taskAssignee = outputProperties.tasksMetadata.taskAssignee.getValue();
        taskComment = outputProperties.tasksMetadata.taskComment.getValue();
        */
        if (outputProperties.batchSize.getValue() != null) {
            batchSize = outputProperties.batchSize.getValue();
        }
               
        if (campaignType.equals(CampaignType.MERGING.toString())) {
            groupIdColumn = outputProperties.advancedMappings.groupIdColumn.getValue();
            sourceColumn = outputProperties.advancedMappings.sourceColumn.getValue();
            masterColumn = outputProperties.advancedMappings.masterColumn.getValue();
            scoreColumn = outputProperties.advancedMappings.scoreColumn.getValue();
        }               
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WriteOperation<?> createWriteOperation() {
        return new TdsTaskWriteOperation(this);
    }

    /**
     * Returns data schema
     * 
     * @return data schema
     */
    public Schema getSchema() {
        return schema;
    }

    /**
     * Getter for campaignName.
     * 
     * @return the campaignName
     */
    public String getCampaignName() {
        return campaignName;
    }

    /**
     * Getter for campaignType.
     * 
     * @return the campaignType
     */
    public String getCampaignType() {
        return campaignType;
    }

    /**
     * Getter for taskPriority.
     * 
     * @return the taskPriority
     */
    public Integer getTaskPriority() {
        return taskPriority;
    }

    /**
     * Getter for taskTags.
     * 
     * @return the taskTags
     */
    public String getTaskTags() {
        return taskTags;
    }

    /**
     * Getter for taskState.
     * 
     * @return the taskState
     */
    public String getTaskState() {
        return taskState;
    }

    /**
     * Getter for taskAssignee.
     * 
     * @return the taskAssignee
     */
    public String getTaskAssignee() {
        return taskAssignee;
    }

    /**
     * Getter for taskComment.
     * 
     * @return the taskComment
     */
    public String getTaskComment() {
        return taskComment;
    }
    
    /**
     * Getter for batchSize.
     * 
     * @return the batchSize
     */
    public Integer getBatchSize() {
        return batchSize;
    }
    
    /**
     * Getter for groupId.
     * 
     * @return the groupId
     */
    public String getGroupIdColumn() {
        return groupIdColumn;
    }
    
    /**
     * Getter for source.
     * 
     * @return the source
     */
    public String getSourceColumn() {
        return sourceColumn;
    }
    
    /**
     * Getter for masterIndicator.
     * 
     * @return the masterIndicator
     */
    public String getMasterColumn() {
        return masterColumn;
    }
    
    /**
     * Getter for score.
     * 
     * @return the score
     */
    public String getScoreColumn() {
        return scoreColumn;
    }

}
