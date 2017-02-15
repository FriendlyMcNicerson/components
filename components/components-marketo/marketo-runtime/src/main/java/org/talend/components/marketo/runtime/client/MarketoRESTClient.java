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
package org.talend.components.marketo.runtime.client;

import static java.lang.String.format;
import static org.talend.components.marketo.tmarketoinput.TMarketoInputProperties.LeadSelector.LeadKeySelector;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.IndexedRecord;
import org.apache.cxf.jaxrs.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.marketo.MarketoComponentProperties;
import org.talend.components.marketo.runtime.client.rest.response.*;
import org.talend.components.marketo.runtime.client.rest.type.LeadActivityRecord;
import org.talend.components.marketo.runtime.client.rest.type.LeadChangeRecord;
import org.talend.components.marketo.runtime.client.rest.type.ListRecord;
import org.talend.components.marketo.runtime.client.type.*;
import org.talend.components.marketo.tmarketoinput.TMarketoInputProperties;
import org.talend.components.marketo.tmarketoinput.TMarketoInputProperties.IncludeExcludeFieldsREST;
import org.talend.components.marketo.tmarketoinput.TMarketoInputProperties.ListParam;
import org.talend.components.marketo.tmarketooutput.TMarketoOutputProperties;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class MarketoRESTClient extends MarketoClient {

    private transient static final Logger LOG = LoggerFactory.getLogger(MarketoRESTClient.class);

    private String accessToken;

    private WebClient webClient;

    private String basicPath = "/rest";

    public static final String API_PATH_ACTIVITIES = "/v1/activities.json";

    public static final String API_PATH_ACTIVITIES_LEADCHANGES = "/v1/activities/leadchanges.json";

    public static final String API_PATH_ACTIVITIES_TYPES = "/v1/activities/types.json";

    public static final String API_PATH_IDENTITY_OAUTH_TOKEN = "/identity/oauth/token";

    public static final String API_PATH_LEADS = "/v1/leads.json";

    public static final String API_PATH_LEADS_DELETE = "/v1/leads/delete.json";

    public static final String API_PATH_LEADS_ISMEMBER = "/leads/ismember.json";

    public static final String API_PATH_LEADS_JSON = "/leads.json";

    public static final String API_PATH_LISTS = "/v1/lists/";

    public static final String API_PATH_LISTS_JSON = "/v1/lists.json";

    public static final String API_PATH_PAGINGTOKEN = "/v1/activities/pagingtoken.json";

    public static final String FIELD_ACCESS_TOKEN = "access_token";

    public static final String FIELD_ACTION = "action";

    public static final String FIELD_ACTIVITY_DATE = "activityDate";

    public static final String FIELD_ACTIVITY_TYPE_ID = "activityTypeId";

    public static final String FIELD_ACTIVITY_TYPE_IDS = "activityTypeIds";

    public static final String FIELD_ACTIVITY_TYPE_VALUE = "activityTypeValue";

    public static final String FIELD_BATCH_SIZE = "batchSize";

    public static final String FIELD_FIELDS = "fields";

    public static final String FIELD_FILTER_TYPE = "filterType";

    public static final String FIELD_FILTER_VALUES = "filterValues";

    public static final String FIELD_ID = "id";

    public static final String FIELD_INPUT = "input";

    public static final String FIELD_LEAD_ID = "leadId";

    public static final String FIELD_LEAD_IDS = "leadIds";

    public static final String FIELD_LIST_ID = "listId";

    public static final String FIELD_LOOKUP_FIELD = "lookupField";

    public static final String FIELD_NAME = "name";

    public static final String FIELD_NEXT_PAGE_TOKEN = "nextPageToken";

    public static final String FIELD_PRIMARY_ATTRIBUTE_VALUE = "primaryAttributeValue";

    public static final String FIELD_PRIMARY_ATTRIBUTE_VALUE_ID = "primaryAttributeValueId";

    public static final String FIELD_SINCE_DATETIME = "sinceDatetime";

    public static final String QUERY_METHOD = "_method";

    public static final String QUERY_METHOD_DELETE = "DELETE";

    public static final String QUERY_METHOD_GET = "GET";

    public static final String QUERY_METHOD_POST = "POST";

    public static final String REST = "REST";

    public static final int REST_API_ACTIVITY_TYPE_IDS_LIMIT = 10;

    public static final int REST_API_BATCH_LIMIT = 300;

    public static final int REST_API_LEAD_IDS_LIMIT = 30;

    private Map<Integer, String> supportedActivities;

    public MarketoRESTClient(MarketoComponentProperties properties) throws MarketoException {
        endpoint = properties.connection.endpoint.getValue();
        userId = properties.connection.clientAccessId.getValue();
        secretKey = properties.connection.secretKey.getValue();
        int timeout = properties.connection.timeout.getValue();

        try {
            if (endpoint == null)
                throw new MarketoException(REST, "The endpoint is null!");
            URI basicURI = new URI(endpoint);
            if (basicURI.getPath() != null) {
                basicPath = basicURI.getPath();
            }
            webClient = WebClient.create(new URI(basicURI.getScheme(), basicURI.getHost(), null, null))
                    .type(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON_TYPE);
            WebClient.getConfig(webClient).getHttpConduit().getClient().setConnectionTimeout(timeout);
            WebClient.getConfig(webClient).getHttpConduit().getClient().setReceiveTimeout(timeout);
            refreshToken();
        } catch (URISyntaxException e) {
            LOG.error(e.toString());
            throw new MarketoException(REST, e.getMessage());
        } catch (MarketoException e) {
            LOG.error(e.toString());
            throw e;
        }
    }

    @Override
    public String getApi() {
        return REST;
    }

    @Override
    public String toString() {
        return format("Marketo REST API Client [%s].", endpoint);
    }

    private AuthenticationResponse getAccessToken() throws MarketoException {
        LOG.debug("getAccessToken getting token");

        webClient.resetQuery();
        webClient.replacePath(API_PATH_IDENTITY_OAUTH_TOKEN);
        webClient.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE);

        Response response = null;
        try {
            response = webClient.post("grant_type=client_credentials&client_secret=" + secretKey + "&client_id=" + userId);
        } catch (Exception e) {
            // TODO mangage SocketTimeoutException with timeout and retry properties
            LOG.error("AccessToken error: {}.", e.getMessage());
            throw new MarketoException(REST, "Marketo Authentication failed : " + e.getMessage());
        }
        if (response.getStatus() == 200 && response.hasEntity()) {
            webClient.type(MediaType.APPLICATION_JSON_TYPE);
            InputStream inStream = response.readEntity(InputStream.class);
            Reader reader = new InputStreamReader(inStream);
            Gson gson = new Gson();
            LOG.debug("MarketoRestExecutor.getAccessToken GOT token");
            return gson.fromJson(reader, AuthenticationResponse.class);
        } else {
            throw new MarketoException(REST, "Marketo Authentication failed! Please check your setting!");
        }
    }

    public void refreshToken() throws MarketoException {
        AuthenticationResponse response = getAccessToken();
        if (response != null) {
            if (response.getError() == null) {
                this.accessToken = response.getAccess_token();
            } else {
                throw new MarketoException(REST, response.getError(), response.getErrorDescription());
            }
        }
    }

    public boolean isAvailable() {
        return accessToken != null;
    }

    public boolean isAccessTokenExpired(List<MarketoError> errors) {
        if (errors != null) {
            for (MarketoError error : errors) {
                if ("602".equals(error.getCode())) {
                    return true;
                }
            }
        }
        return false;
    }

    public RequestResult executeGetRequest(Class<?> resultClass) throws MarketoException {

        Response response = webClient.get();
        if (response.getStatus() == 200 && response.hasEntity()) {
            InputStream inStream = response.readEntity(InputStream.class);
            Reader reader = new InputStreamReader(inStream);
            Gson gson = new Gson();
            return (RequestResult) gson.fromJson(reader, resultClass);
        } else {
            throw new MarketoException(REST, response.getStatus(), "Request failed! Please check your request setting!");
        }
    }

    public RequestResult executePostRequest(Class<?> resultClass, JsonObject inputJson) throws MarketoException {
        return doPost(resultClass, inputJson.toString());
    }

    public RequestResult executePostRequest(Class<?> resultClass, String postContent) throws MarketoException {
        webClient.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
        return doPost(resultClass, postContent);
    }

    public RequestResult doPost(Class<?> resultClass, String postContent) throws MarketoException {
        Response response = webClient.post(postContent);
        if (response.getStatus() == 200 && response.hasEntity()) {
            InputStream inStream = response.readEntity(InputStream.class);
            Reader reader = new InputStreamReader(inStream);
            Gson gson = new Gson();
            return (RequestResult) gson.fromJson(reader, resultClass);
        }
        throw new MarketoException(REST, response.getStatus(), "Request failed! Please check your request setting!");
    }

    public String fmtParams(String paramName, Object paramValue, boolean first) {
        return String.format(first ? "%s=%s" : "&%s=%s", paramName, paramValue);
    }

    public String csvString(Object[] fields) {
        StringBuilder fieldCsv = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            fieldCsv.append(fields[i]);
            if (i + 1 != fields.length) {
                fieldCsv.append(",");
            }
        }
        return fieldCsv.toString();
    }

    public String getPageToken(String sinceDatetime) throws MarketoException {
        webClient.resetQuery();
        webClient.replacePath(basicPath + API_PATH_PAGINGTOKEN).query(FIELD_ACCESS_TOKEN, accessToken).query(FIELD_SINCE_DATETIME,
                sinceDatetime);
        LeadResult getResponse = (LeadResult) executeGetRequest(LeadResult.class);
        if (getResponse != null) {
            return getResponse.getNextPageToken();
        }
        return null;
    }

    public Integer getListIdByName(String listName) throws MarketoException {

        webClient.resetQuery();
        webClient.replacePath(basicPath + API_PATH_LISTS_JSON).query(FIELD_ACCESS_TOKEN, accessToken).query(FIELD_NAME, listName);

        StaticListResult getResponse = (StaticListResult) executeGetRequest(StaticListResult.class);

        if (getResponse != null && getResponse.isSuccess()) {
            if (getResponse.getResult().size() > 0) {
                for (ListRecord listObject : getResponse.getResult()) {
                    return listObject.getId();
                }
            } else {
                throw new MarketoException(REST, "No list match `" + listName + "`.");
            }
        } else if (!getResponse.isSuccess()) {
            throw new MarketoException(REST, getResponse.getErrors().toString());
        }
        return null;
    }

    // public Map<Integer, String> getServerActivityTypes() throws MarketoException {
    //
    // webClient.resetQuery();
    // webClient.replacePath(basicPath + API_PATH_ACTIVITIES_TYPES).query(FIELD_ACCESS_TOKEN, accessToken);
    // ActivityTypesResult getResponse = (ActivityTypesResult) executeGetRequest(ActivityTypesResult.class);
    // Map<Integer, String> typeNames = new HashMap<Integer, String>();
    // List<ActivityType> typeMaps = getResponse.getResult();
    // for (ActivityType typeMap : typeMaps) {
    // typeNames.put(typeMap.getId(), typeMap.getName());
    // }
    // return typeNames;
    // }

    public String getActivityTypeNameById(int activityId) {
        if (supportedActivities == null) {
            supportedActivities = getLocalActivityTypes();
        }
        return supportedActivities.get(activityId);
    }

    public Map<Integer, String> getLocalActivityTypes() {
        return new HashMap<Integer, String>() {

            {
                put(1, "Visit Webpage");
                put(2, "Fill Out Form");
                put(3, "Click Link");
                put(6, "Send Email");
                put(7, "Email Delivered");
                put(8, "Email Bounced");
                put(9, "Unsubscribe Email");
                put(10, "Open Email");
                put(11, "Click Email");
                put(12, "New Lead");
                put(13, "Change Data Value");
                put(19, "Sync Lead to SFDC");
                put(21, "Convert Lead");
                put(22, "Change Score");
                put(23, "Change Owner");
                put(24, "Add to List");
                put(25, "Remove from List");
                put(26, "SFDC Activity");
                put(27, "Email Bounced Soft");
                put(29, "Delete Lead from SFDC");
                put(30, "SFDC Activity Updated");
                put(32, "Merge Leads");
                put(34, "Add to Opportunity");
                put(35, "Remove from Opportunity");
                put(36, "Update Opportunity");
                put(37, "Delete Lead");
                put(38, "Send Alert");
                put(39, "Send Sales Email");
                put(40, "Open Sales Email");
                put(41, "Click Sales Email");
                put(42, "Add to SFDC Campaign");
                put(43, "Remove from SFDC Campaign");
                put(44, "Change Status in SFDC Campaign");
                put(45, "Receive Sales Email");
                put(46, "Interesting Moment");
                put(47, "Request Campaign");
                put(48, "Sales Email Bounced");
                put(100, "Change Lead Partition");
                put(101, "Change Revenue Stage");
                put(102, "Change Revenue Stage Manually");
                put(104, "Change Status in Progression");
                put(106, "Enrich with Data.com");
                put(108, "Change Segment");
                put(110, "Call Webhook");
                put(111, "Sent Forward to Friend Email");
                put(112, "Received Forward to Friend Email");
                put(113, "Add to Nurture");
                put(114, "Change Nurture Track");
                put(115, "Change Nurture Cadence");
                put(400, "Share Content");
                put(401, "Vote in Poll");
                put(405, "Click Shared Link");
            }
        };
    }
    /*
     *
     *
     * MarketoClient implementation - Frontend to REST API.
     *
     *
     */
    public List<IndexedRecord> convertLeadRecords(List<Map<String, String>> recordList, Schema schema,
            Map<String, String> mappings) {
        List<IndexedRecord> results = new ArrayList<>();

        for (Map<String, String> input : recordList) {
            IndexedRecord record = new Record(schema);
            for (Field f : schema.getFields()) {
                String col = mappings.get(f.name());
                Object tmp = input.get(col);
                if (col != null) {
                    record.put(f.pos(), tmp);
                }
            }
            results.add(record);
        }
        return results;
    }

    public List<IndexedRecord> convertLeadActivityRecords(List<LeadActivityRecord> recordList, Schema schema,
            Map<String, String> mappings) {
        DateFormat df = new SimpleDateFormat(SCHEMA_DATETIME_PATTERN);
        List<IndexedRecord> results = new ArrayList<>();

        for (LeadActivityRecord input : recordList) {
            IndexedRecord record = new Record(schema);
            for (Field f : schema.getFields()) {
                String col = mappings.get(f.name());
                if (col.equals(FIELD_ID)) {
                    record.put(f.pos(), input.getId());
                } else if (col.equals(FIELD_LEAD_ID)) {
                    record.put(f.pos(), input.getLeadId());
                } else if (col.equals(FIELD_ACTIVITY_DATE)) {
                    record.put(f.pos(), df.format(input.getActivityDate()));
                } else if (col.equals(FIELD_ACTIVITY_TYPE_ID)) {
                    record.put(f.pos(), input.getActivityTypeId());
                } else if (col.equals(FIELD_ACTIVITY_TYPE_VALUE)) {
                    record.put(f.pos(), getActivityTypeNameById(input.getActivityTypeId()));
                } else if (col.equals(FIELD_PRIMARY_ATTRIBUTE_VALUE_ID)) {
                    record.put(f.pos(), input.getPrimaryAttributeValueId());
                } else if (col.equals(FIELD_PRIMARY_ATTRIBUTE_VALUE)) {
                    record.put(f.pos(), input.getPrimaryAttributeValue());
                } else {
                    for (Map<String, String> attr : input.getAttributes()) {
                        if (attr.get(col) != null) {
                            record.put(f.pos(), attr.get(col));
                        }
                    }
                }
            }
            results.add(record);
        }
        return results;
    }

    public List<IndexedRecord> convertLeadChangesRecords(List<LeadChangeRecord> recordList, Schema schema,
            Map<String, String> mappings) {
        DateFormat df = new SimpleDateFormat(SCHEMA_DATETIME_PATTERN);
        List<IndexedRecord> results = new ArrayList<>();
        Gson gson = new Gson();

        for (LeadChangeRecord input : recordList) {
            IndexedRecord record = new Record(schema);
            for (Field f : schema.getFields()) {
                String col = mappings.get(f.name());
                if (col.equals(FIELD_ID)) {
                    record.put(f.pos(), input.getId());
                } else if (col.equals(FIELD_LEAD_ID)) {
                    record.put(f.pos(), input.getLeadId());
                } else if (col.equals(FIELD_ACTIVITY_DATE)) {
                    record.put(f.pos(), df.format(input.getActivityDate()));
                } else if (col.equals(FIELD_ACTIVITY_TYPE_ID)) {
                    record.put(f.pos(), input.getActivityTypeId());
                } else if (col.equals(FIELD_ACTIVITY_TYPE_VALUE)) {
                    record.put(f.pos(), getActivityTypeNameById(input.getActivityTypeId()));
                } else if (col.equals(FIELD_FIELDS)) {
                    record.put(f.pos(), gson.toJson(input.getFields()));
                } else {
                    for (Map<String, String> attr : input.getAttributes()) {
                        if (attr.get(col) != null) {
                            record.put(f.pos(), attr.get(col));
                        }
                    }
                }
            }
            results.add(record);
        }
        return results;
    }

    @Override
    public MarketoRecordResult getLead(TMarketoInputProperties parameters, String offset) {
        String filter = parameters.leadKeyTypeREST.getValue().toString();
        String filterValue = parameters.leadKeyValue.getValue();
        String[] fields = parameters.mappingInput.getNameMappingsForMarketo().values().toArray(new String[]{});
        int batchLimit = parameters.batchSize.getValue() > REST_API_BATCH_LIMIT
                ? REST_API_BATCH_LIMIT
                : parameters.batchSize.getValue();
        MarketoRecordResult mkto = new MarketoRecordResult();
        LeadResult result;
        try {

            webClient.replacePath(basicPath + API_PATH_LEADS);
            webClient.query(MarketoRESTClient.QUERY_METHOD, QUERY_METHOD_GET);
            webClient.query(FIELD_ACCESS_TOKEN, accessToken);
            String postContent = fmtParams(FIELD_FILTER_TYPE, filter, true);
            if (fields != null && fields.length > 0) {
                postContent += fmtParams(FIELD_FIELDS, csvString(fields), false);
            }
            if (filterValue != null && !filterValue.isEmpty()) {
                postContent += fmtParams(FIELD_FILTER_VALUES, filterValue, false);
            }
            postContent += fmtParams(FIELD_BATCH_SIZE, batchLimit, false);
            if (offset != null && offset.length() > 0) {
                postContent += fmtParams(FIELD_NEXT_PAGE_TOKEN, offset, false);
            }
            LOG.debug("getLead: {}{}", webClient.getCurrentURI(), postContent);
            result = (LeadResult) executePostRequest(LeadResult.class, postContent);
            mkto.setSuccess(result.isSuccess());
            if (mkto.isSuccess()) {
                mkto.setRecordCount(result.getResult().isEmpty() ? 0 : result.getResult().size());
                mkto.setRemainCount((result.getNextPageToken() != null && result.isMoreResult()) ? batchLimit : 0);
                mkto.setStreamPosition(result.getNextPageToken());
                if (mkto.getRecordCount() > 0)
                    mkto.setRecords(convertLeadRecords(result.getResult(), parameters.schemaInput.schema.getValue(),
                            parameters.mappingInput.getNameMappingsForMarketo()));
            }
        } catch (MarketoException e) {
            LOG.error("Lead error {}.", e.toString());
            mkto.setSuccess(false);
            mkto.setErrors(Arrays.asList(e.toMarketoError()));
        }
        return mkto;
    }

    @Override
    public MarketoRecordResult getMultipleLeads(TMarketoInputProperties parameters, String offset) {
        String filter;
        String[] filterValues;
        String[] fields = parameters.mappingInput.getNameMappingsForMarketo().values().toArray(new String[]{});
        int batchLimit = parameters.batchSize.getValue() > REST_API_BATCH_LIMIT
                ? REST_API_BATCH_LIMIT
                : parameters.batchSize.getValue();
        MarketoRecordResult mkto = new MarketoRecordResult();
        LeadResult result;
        try {
            webClient.resetQuery();

            if (parameters.leadSelectorREST.getValue().equals(LeadKeySelector)) {
                filter = parameters.leadKeyTypeREST.getValue().toString();
                filterValues = parameters.leadKeyValues.getValue().split(",");
                webClient.replacePath(basicPath + MarketoRESTClient.API_PATH_LEADS);
                webClient.query(MarketoRESTClient.QUERY_METHOD, QUERY_METHOD_GET);
                webClient.query(FIELD_ACCESS_TOKEN, accessToken);
                String postContent = fmtParams(FIELD_FILTER_TYPE, filter, true);
                if (fields != null && fields.length > 0) {
                    postContent += fmtParams(FIELD_FIELDS, csvString(fields), false);
                }
                if (filterValues != null && filterValues.length > 0) {
                    postContent += fmtParams(FIELD_FILTER_VALUES, csvString(filterValues), false);
                }
                postContent += fmtParams(FIELD_BATCH_SIZE, batchLimit, false);
                if (offset != null && offset.length() > 0) {
                    postContent += fmtParams(FIELD_NEXT_PAGE_TOKEN, offset, false);
                }
                LOG.debug("MultipleLeads: {}{}", webClient.getCurrentURI(), postContent);
                result = (LeadResult) executePostRequest(LeadResult.class, postContent);

            } else {
                int listId;
                if (parameters.listParam.getValue().equals(ListParam.STATIC_LIST_NAME)) {
                    listId = getListIdByName(parameters.listParamValue.getValue());
                } else {
                    listId = Integer.parseInt(parameters.listParamValue.getValue());
                }
                webClient.replacePath(basicPath + "/v1/list/" + listId + API_PATH_LEADS_JSON).query(FIELD_ACCESS_TOKEN,
                        accessToken);
                if (fields != null && fields.length > 0) {
                    webClient.query(FIELD_FIELDS, csvString(fields));
                }
                webClient.query(FIELD_BATCH_SIZE, batchLimit);
                if (offset != null) {
                    webClient.query(FIELD_NEXT_PAGE_TOKEN, offset);
                }

                LOG.debug("LeadsByList : {}.", webClient.getCurrentURI());
                result = (LeadResult) executeGetRequest(LeadResult.class);
            }
            mkto.setSuccess(result.isSuccess());
            if (mkto.isSuccess()) {
                mkto.setRecordCount(result.getResult().isEmpty() ? 0 : result.getResult().size());
                mkto.setRemainCount((result.getNextPageToken() != null && mkto.getRecordCount() > 0) ? batchLimit : 0);
                mkto.setStreamPosition(result.getNextPageToken());
                if (mkto.getRecordCount() > 0)
                    mkto.setRecords(convertLeadRecords(result.getResult(), parameters.schemaInput.schema.getValue(),
                            parameters.mappingInput.getNameMappingsForMarketo()));
            } else {
                mkto.setErrors(Arrays.asList(new MarketoError(REST, "No leads found.")));
            }
        } catch (MarketoException e) {
            LOG.error("MultipleLeads error {}.", e.toString());
            mkto.setSuccess(false);
            mkto.setErrors(Arrays.asList(e.toMarketoError()));
        }
        return mkto;
    }

    @Override
    public MarketoRecordResult getLeadActivity(TMarketoInputProperties parameters, String offset) {
        String sinceDateTime = parameters.sinceDateTime.getValue();
        List<String> incs = parameters.includeTypes.type.getValue();
        List<String> excs = parameters.excludeTypes.type.getValue();
        int batchLimit = parameters.batchSize.getValue() > REST_API_BATCH_LIMIT
                ? REST_API_BATCH_LIMIT
                : parameters.batchSize.getValue();
        List<Integer> activityTypeIds = new ArrayList<>();
        // no activity provided, we take all
        if (incs.isEmpty()) {
            int limit = 0;
            LOG.warn("No ActivityTypeId provided! Getting 10 first availables (API limit).");
            for (Object s : parameters.includeTypes.type.getPossibleValues()) {
                incs.add(s.toString());
                limit++;
                if (limit == REST_API_ACTIVITY_TYPE_IDS_LIMIT)
                    break;
            }
        }
        // remove unwanted activities
        if (!excs.isEmpty()) {
            for (String s : excs) {
                if (incs.contains(s))
                    incs.remove(s);
            }
        }
        // translate into ids
        for (String i : incs) {
            LOG.debug("activity {}", i);
            activityTypeIds.add(IncludeExcludeFieldsREST.valueOf(i).fieldVal);
        }
        MarketoRecordResult mkto = new MarketoRecordResult();
        LeadActivitiesResult result;
        try {
            if (offset == null) {
                offset = getPageToken(sinceDateTime);
            }
            // Marketo API in SOAP and REST return a false estimation of remainCount. Watch out !!!
            webClient.resetQuery();
            webClient.replacePath(basicPath + API_PATH_ACTIVITIES).query(FIELD_ACCESS_TOKEN, accessToken);
            if (offset != null && offset.length() > 0) {
                webClient.query(FIELD_NEXT_PAGE_TOKEN, offset);
            }
            if (activityTypeIds != null) {
                webClient.query(FIELD_ACTIVITY_TYPE_IDS, csvString(activityTypeIds.toArray()));
            }
            webClient.query(FIELD_BATCH_SIZE, batchLimit);
            // if (listId != null) { webClient.query(FIELD_LISTID, listId);}
            // if (leadIds != null) { webClient.query(FIELD_LEADIDS, csvString(leadIds)); }
            LOG.debug("Activities: {}.", webClient.getCurrentURI());
            result = (LeadActivitiesResult) executeGetRequest(LeadActivitiesResult.class);
            mkto.setSuccess(result.isSuccess());
            if (mkto.isSuccess()) {
                mkto.setRecordCount(result.getResult() == null ? 0 : result.getResult().size());
                mkto.setRemainCount((result.getNextPageToken() != null && result.isMoreResult()) ? batchLimit : 0);
                mkto.setStreamPosition(result.getNextPageToken());
                if (mkto.getRecordCount() > 0)
                    mkto.setRecords(convertLeadActivityRecords(result.getResult(), parameters.schemaInput.schema.getValue(),
                            parameters.mappingInput.getNameMappingsForMarketo()));
            }
        } catch (MarketoException e) {
            LOG.error("LeadActivities error {}.", e.toString());
            mkto.setSuccess(false);
            mkto.setErrors(Arrays.asList(e.toMarketoError()));
        }
        return mkto;
    }

    @Override
    public MarketoRecordResult getLeadChanges(TMarketoInputProperties parameters, String offset) {
        String sinceDateTime = parameters.sinceDateTime.getValue();
        int batchLimit = parameters.batchSize.getValue() > REST_API_BATCH_LIMIT
                ? REST_API_BATCH_LIMIT
                : parameters.batchSize.getValue();
        String[] fields = parameters.fieldList.getValue().split(",");
        MarketoRecordResult mkto = new MarketoRecordResult();
        LeadChangesResult result;
        try {
            if (offset == null) {
                offset = getPageToken(sinceDateTime);
            }
            webClient.resetQuery();
            webClient.replacePath(basicPath + API_PATH_ACTIVITIES_LEADCHANGES).query(FIELD_ACCESS_TOKEN, accessToken);
            if (offset != null && offset.length() > 0) {
                webClient.query(FIELD_NEXT_PAGE_TOKEN, offset);
            }
            webClient.query(FIELD_BATCH_SIZE, batchLimit);
            // if (listId != null) { webClient.query(FIELD_LISTID, listId); }
            // if (leadIds != null) { webClient.query(FIELD_LEADIDS, csvString(leadIds)); }
            if (fields != null && fields.length > 0) {
                webClient.query(FIELD_FIELDS, csvString(fields));
            }
            LOG.debug("Changes: {}.", webClient.getCurrentURI());
            result = (LeadChangesResult) executeGetRequest(LeadChangesResult.class);
            mkto.setSuccess(result.isSuccess());
            if (mkto.isSuccess()) {
                mkto.setRecordCount(result.getResult().isEmpty() ? 0 : result.getResult().size());
                mkto.setRecords(convertLeadChangesRecords(result.getResult(), parameters.schemaInput.schema.getValue(),
                        parameters.mappingInput.getNameMappingsForMarketo()));
                if (result.isMoreResult()) {
                    mkto.setRemainCount(mkto.getRecordCount());// cannot know how many remain...
                    mkto.setStreamPosition(result.getNextPageToken());
                }
            }
        } catch (MarketoException e) {
            LOG.error("LeadChanges error {}.", e.toString());
            mkto.setSuccess(false);
            mkto.setErrors(Arrays.asList(e.toMarketoError()));
        }
        return mkto;
    }
    /*
     *
     * List Operations
     *
     */

    @Override
    public MarketoSyncResult addToList(ListOperationParameters parameters) {
        webClient.resetQuery();
        webClient.replacePath(basicPath + API_PATH_LISTS + parameters.getListId() + API_PATH_LEADS_JSON);
        webClient.query(FIELD_ACCESS_TOKEN, accessToken).query(FIELD_ID, csvString(parameters.getLeadIdsValues()));
        webClient.query(QUERY_METHOD, QUERY_METHOD_POST);
        JsonArray json = new JsonArray();
        for (Integer leadId : parameters.getLeadIdsValues()) {
            JsonObject leadKey = new JsonObject();
            leadKey.addProperty(FIELD_ID, leadId);
            json.add(leadKey);
        }
        JsonObject jsonObj = new JsonObject();
        jsonObj.add(FIELD_INPUT, json);

        LOG.debug("addTo: {}.", webClient.getCurrentURI());
        SyncResult result = null;
        MarketoSyncResult mkto = new MarketoSyncResult();
        try {
            result = (SyncResult) executePostRequest(SyncResult.class, jsonObj);
            mkto.setSuccess(result.isSuccess());
            if (mkto.isSuccess()) {
                mkto.setRecordCount(result.getResult().isEmpty() ? 0 : result.getResult().size());
                mkto.setRecords(result.getResult());
                if (result.isMoreResult()) {
                    mkto.setRemainCount(mkto.getRecordCount());// cannot know how many remain...
                    mkto.setStreamPosition(result.getNextPageToken());
                }
            } else {
                mkto.setErrors(result.getErrors());
            }
        } catch (MarketoException e) {
            LOG.error("addToList error: {}.", e.toString());
            mkto.setSuccess(false);
            mkto.setErrors(Arrays.asList(e.toMarketoError()));
        }
        return mkto;
    }

    @Override
    public MarketoSyncResult removeFromList(ListOperationParameters parameters) {
        webClient.resetQuery();
        webClient.replacePath(basicPath + API_PATH_LISTS + parameters.getListId() + API_PATH_LEADS_JSON);
        webClient.query(FIELD_ACCESS_TOKEN, accessToken);
        webClient.query(QUERY_METHOD, QUERY_METHOD_DELETE);
        JsonArray json = new JsonArray();
        for (Integer leadId : parameters.getLeadIdsValues()) {
            JsonObject leadKey = new JsonObject();
            leadKey.addProperty(FIELD_ID, leadId);
            json.add(leadKey);
        }
        JsonObject jsonObj = new JsonObject();
        jsonObj.add(FIELD_INPUT, json);
        LOG.debug("removeFrom: {}{}", webClient.getCurrentURI(), jsonObj);
        SyncResult result = null;
        MarketoSyncResult mkto = new MarketoSyncResult();
        try {
            result = (SyncResult) executePostRequest(SyncResult.class, jsonObj);
            mkto.setSuccess(result.isSuccess());
            if (mkto.isSuccess()) {
                mkto.setRecordCount(result.getResult().isEmpty() ? 0 : result.getResult().size());
                mkto.setRecords(result.getResult());
                if (result.isMoreResult()) {
                    mkto.setRemainCount(mkto.getRecordCount());// cannot know how many remain...
                    mkto.setStreamPosition(result.getNextPageToken());
                }
            } else {
                mkto.setErrors(result.getErrors());
            }
        } catch (MarketoException e) {
            LOG.error("removeFromList error: {}.", e.toString());
            mkto.setSuccess(false);
            mkto.setErrors(Arrays.asList(e.toMarketoError()));
        }
        return mkto;
    }

    @Override
    public MarketoSyncResult isMemberOfList(ListOperationParameters parameters) {
        webClient.resetQuery();
        webClient.replacePath(basicPath + API_PATH_LISTS + parameters.getListId() + API_PATH_LEADS_ISMEMBER);
        webClient.query(FIELD_ACCESS_TOKEN, accessToken).query(FIELD_ID, csvString(parameters.getLeadIdsValues()));
        LOG.debug("isMemberOf: {}.", webClient.getCurrentURI());
        SyncResult result = null;
        MarketoSyncResult mkto = new MarketoSyncResult();
        try {
            result = (SyncResult) executeGetRequest(SyncResult.class);
            mkto.setSuccess(result.isSuccess());
            if (mkto.isSuccess()) {
                mkto.setRecordCount(result.getResult().isEmpty() ? 0 : result.getResult().size());
                mkto.setRecords(result.getResult());
                if (result.isMoreResult()) {
                    mkto.setRemainCount(mkto.getRecordCount());// cannot know how many remain...
                    mkto.setStreamPosition(result.getNextPageToken());
                }
            } else {
                mkto.setErrors(result.getErrors());
            }
        } catch (MarketoException e) {
            LOG.error("isMemberOfList error: {}.", e.toString());
            mkto.setSuccess(false);
            mkto.setErrors(Arrays.asList(e.toMarketoError()));
        }
        return mkto;
    }
    /*
     *
     * SyncLead Operations
     *
     */

    @Override
    public MarketoSyncResult syncLead(TMarketoOutputProperties parameters, IndexedRecord lead) {
        return syncMultipleLeads(parameters, Arrays.asList(lead));
    }

    @Override
    public MarketoSyncResult syncMultipleLeads(TMarketoOutputProperties parameters, List<IndexedRecord> leads) {
        String action = parameters.operationType.getValue().name();
        String lookupField = parameters.lookupField.getValue().name();
        int batchSize = parameters.batchSize.getValue();
        webClient.resetQuery();
        webClient.replacePath(basicPath + MarketoRESTClient.API_PATH_LEADS).query(FIELD_ACCESS_TOKEN, accessToken);
        JsonObject inputJson = new JsonObject();
        Gson gson = new Gson();
        // FIXME no partition or asyncProcessing parameters provided by Studio...
        // inputJson.addProperty("asyncProcessing", asyncProcessing);
        // if (partitionName != null) { inputJson.addProperty("partitionName", partitionName); }
        inputJson.addProperty(FIELD_BATCH_SIZE, batchSize);
        if (action != null) {
            inputJson.addProperty(FIELD_ACTION, action);
        }
        if (lookupField != null) {
            inputJson.addProperty(FIELD_LOOKUP_FIELD, lookupField);
        }
        List<Map<String, Object>> leadsObjects = new ArrayList<>();
        for (IndexedRecord r : leads) {
            Map<String, Object> lead = new HashMap<String, Object>();
            for (Field f : r.getSchema().getFields()) {
                lead.put(f.name(), r.get(f.pos()));
            }
            leadsObjects.add(lead);
        }
        inputJson.add(FIELD_INPUT, gson.toJsonTree(leadsObjects));
        MarketoSyncResult mkto = new MarketoSyncResult();
        try {
            LOG.debug("syncMultipleLeads {}{}.", webClient.getCurrentURI(), inputJson);
            SyncResult rs = (SyncResult) executePostRequest(SyncResult.class, inputJson);
            mkto.setSuccess(rs.isSuccess());
            if (mkto.isSuccess()) {
                mkto.setRecordCount(rs.getResult().isEmpty() ? 0 : rs.getResult().size());
                mkto.setRecords(rs.getResult());
                if (rs.isMoreResult()) {
                    mkto.setRemainCount(mkto.getRecordCount());// cannot know how many remain...
                    mkto.setStreamPosition(rs.getNextPageToken());
                }
            }
        } catch (MarketoException e) {
            LOG.error("syncMultipleLeads error: {}.", e.toString());
            mkto.setSuccess(false);
            mkto.setErrors(Arrays.asList(e.toMarketoError()));
        }
        return mkto;
    }
    /*
     * 
     * management func
     * 
     */
    public MarketoSyncResult deleteLeads(Integer[] leadIds) {
        webClient.resetQuery();
        webClient.replacePath(basicPath + API_PATH_LEADS_DELETE).query(FIELD_ACCESS_TOKEN, accessToken);
        webClient.query(FIELD_ID, csvString(leadIds));
        webClient.query(MarketoRESTClient.QUERY_METHOD, QUERY_METHOD_POST);

        JsonArray json = new JsonArray();
        for (Integer leadId : leadIds) {
            JsonObject leadKey = new JsonObject();
            leadKey.addProperty(FIELD_ID, leadId);
            json.add(leadKey);
        }
        JsonObject jsonObj = new JsonObject();
        jsonObj.add(FIELD_INPUT, json);

        MarketoSyncResult mkto = new MarketoSyncResult();
        try {
            LOG.debug("deleteLeads {}{}.", webClient.getCurrentURI(), jsonObj);
            SyncResult rs = (SyncResult) executePostRequest(SyncResult.class, jsonObj);
            mkto.setSuccess(rs.isSuccess());
            if (mkto.isSuccess()) {
                mkto.setRecordCount(rs.getResult().isEmpty() ? 0 : rs.getResult().size());
                mkto.setRecords(rs.getResult());
                if (rs.isMoreResult()) {
                    mkto.setRemainCount(mkto.getRecordCount());// cannot know how many remain...
                    mkto.setStreamPosition(rs.getNextPageToken());
                }
            }
        } catch (MarketoException e) {
            LOG.error("syncMultipleLeads error: {}.", e.toString());
            mkto.setSuccess(false);
            mkto.setErrors(Arrays.asList(e.toMarketoError()));
        }
        return mkto;
    }

}
