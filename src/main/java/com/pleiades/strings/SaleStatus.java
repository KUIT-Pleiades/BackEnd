package com.pleiades.strings;

public enum SaleStatus {
    ONSALE("onsale"), WAIT("wait"), SOLDOUT("soldout");

    private String status;

    private SaleStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
