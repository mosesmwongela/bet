package com.sikumojaventures.betmwitu.model;

/**
 * Created by mwongela on 12/20/16.
 */
public class Account {

    String trans_id, trans_type, trans_amount, desc;

    public Account() {

    }

    public Account(String trans_id, String trans_type, String trans_amount, String desc) {
        this.trans_id = trans_id;
        this.trans_type = trans_type;
        this.trans_amount = trans_amount;
        this.desc = desc;
    }

    public String getTrans_id() {
        return trans_id;
    }

    public void setTrans_id(String trans_id) {
        this.trans_id = trans_id;
    }

    public String getTrans_type() {
        return trans_type;
    }

    public void setTrans_type(String trans_type) {
        this.trans_type = trans_type;
    }

    public String getTrans_amount() {
        return trans_amount;
    }

    public void setTrans_amount(String trans_amount) {
        this.trans_amount = trans_amount;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
