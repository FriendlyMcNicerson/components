package org.talend.components.netsuite.client;

public class NetSuiteCredentials {

    private String email;

    private String password;

    private String account;

    private String roleId;

    private int numberOfSeats = 1;

    private String id;

    private String companyId;

    private String userId;

    private String partnerId;

    private String privateKey; // path to private key in der format

    private boolean useSsoLogin = false;

    public NetSuiteCredentials() {
    }

    public NetSuiteCredentials(String email, String password, String account, String roleId) {
        this(email, password, account, roleId, 1);
    }

    public NetSuiteCredentials(String email, String password, String account, String roleId, int numberOfSeats) {
        this.email = email;
        this.password = password;
        this.account = account;
        this.roleId = roleId;
        this.numberOfSeats = numberOfSeats;
    }

    public String getAccount() {
        return account;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRoleId() {
        return roleId;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public boolean isUseSsoLogin() {
        return useSsoLogin;
    }

    public void setUseSsoLogin(boolean useSsoLogin) {
        this.useSsoLogin = useSsoLogin;
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder("NetSuiteCredentials{");
        sb.append("email='").append(email).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", account='").append(account).append('\'');
        sb.append(", roleId='").append(roleId).append('\'');
        sb.append(", numberOfSeats=").append(numberOfSeats);
        sb.append(", id='").append(id).append('\'');
        sb.append(", companyId='").append(companyId).append('\'');
        sb.append(", userId='").append(userId).append('\'');
        sb.append(", partnerId='").append(partnerId).append('\'');
        sb.append(", privateKey='").append(privateKey).append('\'');
        sb.append(", useSsoLogin=").append(useSsoLogin);
        sb.append('}');
        return sb.toString();
    }
}