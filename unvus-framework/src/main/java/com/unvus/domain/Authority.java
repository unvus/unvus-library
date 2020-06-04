package com.unvus.domain;

import lombok.Data;
import java.io.Serializable;

/**
 * An authority (a security role) used by Spring Security.
 */
@Data
public class Authority implements Serializable {

    private String name;

}
