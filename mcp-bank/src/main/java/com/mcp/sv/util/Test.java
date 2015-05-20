package com.mcp.sv.util;

import com.mcp.sv.cmbc.HttpClientWrapper;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Created by Administrator on 2015/5/20.
 */
public class Test {

    public static void main(String[] args) throws JSONException {

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("gameCode","T51");
        jsonObject.put("pType","05");
        jsonObject.put("passType","1");
        //jsonObject.put("matchCode","201505203009");
        HttpClientWrapper httpClientWrapper=new HttpClientWrapper();
        String str=httpClientWrapper.mcpPost("CQ22", jsonObject.toString());

        //201505203009
        System.out.println(str);


    }

}
