package com.atguigu.app.common;

/**
 *地理信息
 *
 */
public class GeoInfo extends AppBaseLog {
    private String country ;
    private String province ;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
