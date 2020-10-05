package com.neo.nbdapi.rest.vm;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class DefaultRequestPagingVM implements Serializable {

    @NotEmpty(message = "draw không được trống")
    private String draw;

    @NotEmpty(message = "start không được trống")
    @Pattern(regexp = "\\d", message = "start phải là số")
    @Size(min = 1, max = 9999999, message = "start trong khoảng 1 đến 9999999")
    private String start;

    @NotEmpty(message = "length không được trống")
    private String length;

    private String search;

    public DefaultRequestPagingVM() {
    }

    public String getDraw() {
        return draw;
    }

    public void setDraw(String draw) {
        this.draw = draw;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    @Override
    public String toString() {
        return "DefaultRequestPagingVM{" +
                "draw='" + draw + '\'' +
                ", start='" + start + '\'' +
                ", length='" + length + '\'' +
                ", search='" + search + '\'' +
                '}';
    }
}
