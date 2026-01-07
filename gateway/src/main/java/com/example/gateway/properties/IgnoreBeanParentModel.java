package com.example.gateway.properties;

import lombok.Data;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author : wangbin
 * @date: 2026/1/5 - 01 - 05 - 17:09
 * @Description: com.example.gateway.properties
 */
@Data
public class IgnoreBeanParentModel {
    private Map<String, Pattern> condition;

    private String name;

    private String ignoremodel;

}
