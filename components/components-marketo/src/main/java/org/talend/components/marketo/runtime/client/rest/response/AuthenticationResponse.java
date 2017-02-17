package org.talend.components.marketo.runtime.client.rest.response;

public class AuthenticationResponse {

    private String access_token;

    private String token_type;

    private int expires_in;

    private String scope;

    private String error;

    private String error_description;

    public String getAccess_token() {
        return access_token;
    }
    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
    public String getToken_type() {
        return token_type;
    }
    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }
    public int getExpires_in() {
        return expires_in;
    }
    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }
    public String getScope() {
        return scope;
    }
    public void setScope(String scope) {
        this.scope = scope;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
    public String getErrorDescription() {
        return error_description;
    }
    public void setError_description(String error_description) {
        this.error_description = error_description;
    }

}
