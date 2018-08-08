package com.ximuyi.demo.freemarker;

import java.lang.Integer;
import java.lang.String;
import java.util.Date;

public class FreeMarkerOut {
    private Integer value;
    private String string;
    private Date date;

    public FreeMarkerOut() {
    }

    public FreeMarkerOut(Integer value, String string, Date date) {
    }

    public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

    public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

    public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
