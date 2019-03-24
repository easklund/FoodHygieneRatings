package com.example.foodhygieneratings;

public class Authority {
    private int LocalAuthorityId;
    private String LocalAuthorityIdCode;
    private String Name;

    public Authority(int localAuthorityId, String localAuthorityIdCode, String name) {
        LocalAuthorityId = localAuthorityId;
        LocalAuthorityIdCode = localAuthorityIdCode;
        Name = name;
    }

    public int getLocalAuthorityId() {
        return LocalAuthorityId;
    }

    public void setLocalAuthorityId(int localAuthorityId) {
        LocalAuthorityId = localAuthorityId;
    }

    public String getLocalAuthorityIdCode() {
        return LocalAuthorityIdCode;
    }

    public void setLocalAuthorityIdCode(String localAuthorityIdCode) {
        LocalAuthorityIdCode = localAuthorityIdCode;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @Override
    public String toString() {
        return Name;
    }
}
