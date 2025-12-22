package com.example.gateway.config;

import com.alibaba.fastjson2.JSONObject;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.LoaderOptions;

import java.util.Map;

public class YAMLParser {
    
    /**
     * 将YAML字符串转换为JSONObject
     * @param yamlContent YAML内容
     * @return JSONObject对象
     */
    @SuppressWarnings("unchecked")
    public static JSONObject yamlToJsonObject(String yamlContent) {
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setMaxAliasesForCollections(Integer.MAX_VALUE);
        Yaml yaml = new Yaml(new SafeConstructor(loaderOptions));
        Map<String, Object> map = yaml.load(yamlContent);
        return new JSONObject(map);
    }
}