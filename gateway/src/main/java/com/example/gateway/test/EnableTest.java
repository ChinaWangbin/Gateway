package com.example.gateway.test;

import com.example.gateway.properties.IgnoreBeanParentModel;
import com.example.gateway.properties.LlmProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author : wangbin
 * @date: 2026/1/7 - 01 - 07 - 10:25
 * @Description: com.example.gateway.test
 */
@Component
public class EnableTest {

    @Autowired
    private LlmProperties llmProperties;

   public void checkContent(ServerWebExchange exchange){
       ConcurrentHashMap<String, IgnoreBeanParentModel> ignores = llmProperties.getIgnores();
       String modelId= (String) exchange.getAttributes().get("modelId");
       Map<String, String> requestbody = exchange.getRequest().getHeaders().toSingleValueMap();
       for (IgnoreBeanParentModel model : ignores.values()) {
           String ignoremodelsStr = model.getIgnoremodel();
           if(check(requestbody, model.getCondition())){
               if (StringUtils.hasLength(ignoremodelsStr)){
                   String[] split = ignoremodelsStr.split(";");
                   boolean contains = Arrays.asList(split).contains(modelId);
                   if(contains){
                       return;
                   }
               }else{
                   return;
               }
           }
       }


       //后续逻辑
   }

    private boolean check(Map<String, String> requestbody, Map<String, Pattern> condition) {
       return true;
    }

}
