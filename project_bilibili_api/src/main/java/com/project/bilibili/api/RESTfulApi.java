package com.project.bilibili.api;

import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
//@RequestMapping("/objects")
public class RESTfulApi {
    private final Map<Integer,Map<String, Object>> dataMap;
    public RESTfulApi(){
        dataMap = new HashMap<>();
        for (int i = 1; i < 3; i++) {
            Map<String,Object> map = new HashMap<>();
            map.put("id",i);
            map.put("name","name"+i);
            dataMap.put(i,map);
        }
    }

    @GetMapping("/objects/{id}")
    public String getValue(@PathVariable Integer id)
    {
        Map<String,Object> map = dataMap.get(id);
        return "id:"+map.get("id")+",name:"+map.get("name");
    }

    @DeleteMapping("/objects/{id}")
    public String deleteValue(@PathVariable Integer id)
    {
        dataMap.remove(id);
        return "delete success!";
    }

    @PostMapping("/objects")
    public String PostData(@RequestBody Map<String,Object> data){
        Integer[] idArray = dataMap.keySet().toArray(new Integer[0]);
        Arrays.sort(idArray);
        int nextId = idArray[idArray.length-1]+1;
        dataMap.put(nextId,data);
        return "post success!";
    }
    @PutMapping("/objects")
    public String PubData(@RequestBody Map<String,Object> data)
    {
        Integer id = Integer.valueOf(String.valueOf(data.get("id")));
        Map<String, Object> containData = dataMap.get(id);
        if(containData==null)
        {
            Integer[] idArray = dataMap.keySet().toArray(new Integer[0]);
            Arrays.sort(idArray);
            int nextId = idArray[idArray.length-1]+1;
            dataMap.put(nextId,data);
        }else {
            dataMap.put(id,data);
        }
        return "put success!";
    }


}
