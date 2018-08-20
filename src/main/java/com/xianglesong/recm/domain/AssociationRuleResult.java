/**
 * @CopyRight all rights reserved
 */

package com.xianglesong.recm.domain;

import org.springframework.data.annotation.Id;

public class AssociationRuleResult {

    @Id
    private String id;

    private String pre;

    private String post;

    private Double score;


    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getPre() {
        return pre;
    }

    public void setPre(String pre) {
        this.pre = pre;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "AssociationRuleResult{" +
                "id='" + id + '\'' +
                ", pre='" + pre + '\'' +
                ", post='" + post + '\'' +
                ", score=" + score +
                '}';
    }
}
