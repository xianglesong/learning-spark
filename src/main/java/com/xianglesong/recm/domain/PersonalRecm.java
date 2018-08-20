/**
 * @CopyRight all rights reserved
 */

package com.xianglesong.recm.domain;

import javax.persistence.Id;


public class PersonalRecm {

    @Id
    private String id;

    private String recm;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecm() {
        return recm;
    }

    public void setRecm(String recm) {
        this.recm = recm;
    }

    @Override
    public String toString() {
        return "PersonalRecm{" +
                "id='" + id + '\'' +
                ", recm='" + recm + '\'' +
                '}';
    }
}
