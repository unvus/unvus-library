package com.unvus.query.frag;

import lombok.Data;

@Data
public class Field {
    private String prefix;
    private String property;
    private String column;

    public Field(String property, String column) {
        this("", property, column);
    }

    public Field(String prefix, String property, String column) {
        this.prefix = prefix;
        this.property = property;
        this.column = column;
    }

}
