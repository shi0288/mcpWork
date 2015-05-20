package com.mcp.sv.cmbc;


import com.mcp.sv.util.*;
import com.mongodb.*;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * REST Web Service
 *
 * @author Administrator
 */

@Path("LotteryService")
public class LotteryService {

    private static final Logger logger = LoggerFactory.getLogger(LotteryService.class);

    private static RedisCilent redisCilent = new RedisCilent();

    public LotteryService() {
    }

    @POST
    @Produces({"application/json"})
    @Path("commonTrans")
    public String commonTrans(@Context HttpServletRequest userRequest, @Context HttpServletResponse httpResponse, @FormParam("cmd") String cmd, @FormParam("body") String body, @FormParam("payType") String payType, @FormParam("Name") String name, @FormParam("Id") String id, @FormParam("St") String st) {
        String resMessage = "";
        //取期次
        if(cmd != null && body != null){
            try {
                resMessage = HttpClientWrapper.post(userRequest, httpResponse, cmd, body, st, id);
                logger.info("收到的：  "+resMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resMessage;//不是投注的请求。其它的请求。
    }


    @POST
    @Produces({"application/json"})
    @Path("getTerms")
    public String getTerms(@FormParam("body") String body) {
        String resMessage = "";
        //取期次
        System.out.println(body);
        if(body != null){
            try {
                resMessage = HttpClientWrapper.mcpPost(CmbcConstant.MCP_CQ01,body);
                System.out.println("收到的：  " + resMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resMessage;
    }


//    //新用户赠彩币活动
//    @POST
//    @Produces({"application/json"})
//    @Path("userTrans")
//    public String userTrans(@Context HttpServletRequest userRequest, @Context HttpServletResponse httpResponse,@FormParam("name") String name,@FormParam("st") String st, @FormParam("userId") String userId) {
//        //todo  先查缓存  如没有则放入缓存并继续运行
//        DB db= MongoManager.getDB(MongoConst.MONGO_NAME);
//        DBCollection users = db.getCollection("newUsers");
//        DBCursor cur = users.find(new BasicDBObject("name", name));
//        logger.info("用户："+name+"   "+st+"    "+userId);
//        if(cur.length()==0){
//            DBObject user = new BasicDBObject();
//            user.put("name", name);
//            user.put("present", false);
//            users.save(user);
//        }else{
//            return "false";
//        }
//        try {
//            JSONObject body = new JSONObject();
//            String resMessage = HttpClientWrapper.post(userRequest, httpResponse, "A02", body.toString(), st, userId);
//            JSONObject res = new JSONObject(resMessage);
//            String resCode = (String) res.get("repCode");
//            if (!resCode.equals("0000")) {
//                return "false";
//            } else {
//                JSONArray accounts = res.getJSONArray("accounts");
//                JSONObject account = (JSONObject) accounts.get(0);
//                String recharge = account.get("recharge").toString();
//                int tempRecharge=Integer.parseInt(recharge);
//                if(tempRecharge==0){
//                    boolean finalRes=getTZJL(userRequest, httpResponse, st, userId);
//                    if(finalRes){
//                        String cmd = "C02";
//                        String addBody = "{" +
//                                "\"amount\":" + (int) Math.round(Double.parseDouble("3.00")) * 100 + "," +
//                                "\"name\":\"" + name + "\"," +
//                                "\"fromType\":7" + "," +
//                                "\"orderId\":\"" + UUID.randomUUID().toString().replace("-", "") + "\"" +
//                                "}";
//                        resMessage = HttpClientWrapper.post(userRequest, httpResponse, cmd, addBody, CmbcConstant.CMBC_KEY, "");
//                        JSONObject obj = new JSONObject(resMessage);
//                        if ("0000".equals(obj.getString("repCode"))) { //TODO 判断充值成功还是失败。
//                            DBObject strUser = new BasicDBObject();
//                            strUser.put("name", name);
//                            strUser.put("present", true);
//                            users.update(new BasicDBObject("name", name), strUser);
//                            return "true";
//                        }else {
//                            return "false";
//                        }
//                    }else{
//                        return "false";
//                    }
//                }else {
//                    return "false";
//                }
//            }
//        }catch (Exception e){
//            logger.info(name+"赠送彩币失败");
//        }
//        return "false";
//        //return resMessage;//不是投注的请求。其它的请求。
//    }

    public static boolean getTZJL(HttpServletRequest userRequest, HttpServletResponse httpResponse,String st, String userId)
            throws Exception {
        JSONObject body = new JSONObject();
        body.put("exOrderStatus", "1001,1000,1600"); // 要排除在外的状态代码列表 N
        body.put("startIndex", "0"); // 开始页码，默认为0 N
        body.put("size", "1"); // 每页记录条数，默认为10 N
        body.put("schemeType", "1"); // 方案类型 N
        String resMessage = HttpClientWrapper.post(userRequest, httpResponse, "Q03", body.toString(), st, userId);
        JSONObject res = new JSONObject(resMessage);
        JSONObject pageInfo = null;
        try {
            pageInfo = res.getJSONObject("pageInfo");
        } catch (Exception e) {
            return false;
        }
        int i = pageInfo.getInt("totalPages");
        if (i == 0) {
            return true;
        }else{
            return false;
        }
    }


    @POST
    @Produces({"application/json"})
    @Path("commonTransA04")
    public String commonTransA04(@Context HttpServletRequest userRequest, @Context HttpServletResponse httpResponse, @FormParam("cmd") String cmd, @FormParam("password") String password, @FormParam("userId") String userId) {
        String resMessage = "", reqBody = "";
        //先调用投注接口。
        try {
            reqBody = "{" +
                    "    \"name\":\"" + userId + "\"," +
                    "    \"password\":\"+" + password + "\"," +
                    "    \"phone\":\"" + userId + "\"" +
                    "}";
            resMessage = HttpClientWrapper.post(userRequest, httpResponse, cmd, reqBody, "", "");
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return resMessage;//不是投注的请求。其它的请求。
    }

    //获取充值的验证码
//    民生银行直接支付。用于彩币充值。

    @POST
    @Produces({"application/xml", "application/json"})
    @Path("cmbc/pay")
    public String cmbcPay(@FormParam("userId") String userId, @FormParam("money") String money, @FormParam("orderId") String orderId) {
        //民生银行的产生支付请求字符串的方法。最终产生一个字符串，用于web页面发起充值请求。
        if (orderId == null) {
            orderId = System.currentTimeMillis() + userId;
        }
        String payDesc = CmbcConstant.CMBC_ORDER_DESC_CZ;//充值。
        Dom4jXMLUtil xmlUtil = new Dom4jXMLUtil();
        Document doc = xmlUtil.getXMLDoc();
        Element rootElement = doc.addElement("CreateOrder");
        Element resultCode = rootElement.addElement("ResultCode");
        try {
            String envelopData = getCmbcPayData(userId, money, orderId, payDesc);
            Element strElement = rootElement.addElement("EnvolopData");
            strElement.addText(envelopData);
            resultCode.addText("0");
        } catch (Exception e) {
            e.printStackTrace();
            resultCode.addText("1");
            Element errorMsg = rootElement.addElement("ErrorMsg");
            errorMsg.addText("处理订单异常");
        }
        String retStr = xmlUtil.getXMLString(doc);
        return retStr;

    }

    // 民生银行的产生充值方法。
    public String getCmbcPayData(String userId, String money, String orderId, String payDesc) {
        //民生银行的产生支付请求字符串的方法。最终产生一个字符串，用于web页面发起充值请求。
        String envolopData = "";
        try {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd", Locale.SIMPLIFIED_CHINESE);
            SimpleDateFormat timesdf = new SimpleDateFormat("HHmmss", Locale.SIMPLIFIED_CHINESE);
//            String serialNo = System.currentTimeMillis() + userId.substring(9);
            String serialNo = System.currentTimeMillis() + "99";
            Date currTime = new Date();
            String txAmt = money;
            String PayerCurr = "01";
            String txDate = sdf1.format(currTime);
            String txTime = timesdf.format(currTime);
            String corpID = CmbcConstant.CMBC_CORP_ID;
            String billNo = corpID + serialNo;
//            String corpName = "北京九歌在线科技有限责任公司";
            String corpName = "bjjg";
            String Billremark1 = payDesc + "CMBC" + orderId; //payDesc为CZ代表充值。TZ代表投注。 九歌的订单ID
            String Billremark2 = userId; //
            String CorpRetType = "0";
            String retUrl = CmbcConstant.CMBC_NOTIFY_URL;
            String langMAC = "lottery";
            String preSignMsg = billNo + "|" + txAmt + ".00|" + PayerCurr + "|" + txDate + "|" + txTime + "|" + corpID + "|" + corpName + "|" + Billremark1 + "|" + Billremark2 + "|" + CorpRetType + "|" + retUrl + "|" + langMAC;
            System.out.println(preSignMsg);
			//处理未收到民生回执的情况
            String data = userId+","+orderId+","+CmbcConstant.CMBC_CORP_ID+"|"+billNo+","+payDesc;
            //放入缓存
            String key = "payNo" + UUID.randomUUID().toString().replace("-", "");
            redisCilent.put(key, data, JedisConst.ORDER_SECOND);
            System.out.println("放入缓存成功key："+key +"data:"+redisCilent.get(key));
           // HttpClientWrapper.cmbcPost(CmbcConstant.CMBC_ORDER_DEAL,"data",data);
//            com.yitong.commons.ecshop.security.JnkyServer my = new com.yitong.commons.ecshop.security.JnkyServer(LotteryService.class.getResource(CmbcConstant.CMBC_CER_URL).getPath(), LotteryService.class.getResource(CmbcConstant.CMBC_PFX_URL).getPath(), CmbcConstant.CMBC_PAY_KEY);
//            envolopData = my.EnvelopData(preSignMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(envolopData);
        return envolopData;
    }

    @GET
    @Path("cmbc/notify")
    public void cmbcNotify(@Context HttpServletRequest request, @Context HttpServletResponse response) throws ServletException, IOException { //
        int resultCode = 0;
        //返回数据的格式：0订单号｜1商户代码｜2 交易金额｜3交易日期｜4交易时间｜5订单状态｜6备注1｜7备注2
        //订单状态:0 成功  1 不确定  2 失败
        String payresultStrStr = request.getParameter("payresult");
        logger.info("payresultStrStr2:" + payresultStrStr);
        logger.info("不处理，主动查询处理！");
//        String jgOrderId = "";
//        String jgPayDesc = "";
//        try {
//            JnkyServer my = new JnkyServer(LotteryService.class.getResource(CmbcConstant.CMBC_CER_URL).getPath(), LotteryService.class.getResource(CmbcConstant.CMBC_PFX_URL).getPath(), CmbcConstant.CMBC_PAY_KEY, "sysDefault");
//            String result = my.DecryptData(payresultStrStr);
//            logger.info("result:" + result);
////            TODO  测试使用
////            String result=payresult;
////http://localhost:8090/mcp8sv/Mcp8Services/LotteryService/cmbc/notifyGet?payresult=8356|06005|8.00|20141214|20141214|0|TZCMBCc87f53bd81d1456b99fedb25ef671296|13810989667
//            String rst[] = result.split("\\|");
//            String amount = rst[2];
//            String status = rst[5];
//            String orderIdAndDesc = rst[6];
//            String userId = rst[7];
//            if ("0".equals(status)) {
//                String body = "";
//                String cmd = "";
//                String payDesc = orderIdAndDesc.split("CMBC")[0];
//                String orderId = orderIdAndDesc.split("CMBC")[1];
//
//                HttpClientWrapper.cmbcPost(CmbcConstant.CMBC_ORDER_INFO,"data",orderId);
//
//                //无论是充值还是投注，都必须先充值。
////                if (CmbcConstant.CMBC_ORDER_DESC_CZ.equals(payDesc)) {
//                //给用户充值。
//                //TODO 充值接口  。如果充值失败，需要退款。写入掉单文件。并且页面通知彩民。
//                cmd = "C02";
//                String addBody = "{" +
//                        "\"amount\":" + (int) Math.round(Double.parseDouble(amount)) * 100 + "," +
//                        "\"name\":\"" + userId + "\"," +
//                        "\"fromType\":7" + "," +
//                        "\"orderId\":\"" + orderId + "\"" +
//                        "}";
//
//                String resMessage = HttpClientWrapper.post(request, response, cmd, addBody, CmbcConstant.CMBC_KEY, "");
//                JSONObject obj = new JSONObject(resMessage);
//                if ("0000".equals(obj.getString("repCode"))) { //TODO 判断充值成功还是失败。
//                    //充值成功
//                    logger.info("充值成功");
//                    if (CmbcConstant.CMBC_ORDER_DESC_TZ.equals(payDesc)) {
//                        //订单号投注。
//                        cmd = "T02";
//                        body = "{\"orderId\":\"" + orderId + "\"}";
//                        resMessage = HttpClientWrapper.post(request, response, cmd, body, CmbcConstant.CMBC_KEY, "");
//                        obj = new JSONObject(resMessage);
//                        if ("0000".equals(obj.getString("repCode"))) {
//                            //TODO 订单号投注成功。页面通知彩民。
//                            logger.info("订单号投注成功");
//                            //结束挖宝活动
//                         //   String user_st =MD5.MD5Encode(userId + CmbcConstant.CMBC_SIGN_KEY);
//                         //   logger.info("增加挖宝地址："+CmbcConstant.HD_WB+"?userId="+userId+"&userSt="+user_st+"&orderId="+orderId);
//                        //    response.sendRedirect(CmbcConstant.HD_WB+"?userId="+userId+"&userSt="+user_st+"&orderId="+orderId);
//                            //response.sendRedirect(CmbcConstant.TZ_SUCC);
//                        } else {
//                            //TODO 订单号投注失败。调用充值接口。或者调用退款接口。并且通知彩民。
//                            logger.info("订单号投注失败");
//                            response.sendRedirect(CmbcConstant.TZ_FAIL);
//                        }
//                    } else {
//                        response.sendRedirect(CmbcConstant.CZ_SUCC);
//                    }
//                } else {
//                    //充值失败
//                    logger.info("支付失败");
//                    response.sendRedirect(CmbcConstant.CZ_FAIL);
//                }
//            } else {
//                logger.error("CMBC支付验证失败:" + result);
//                //支付验证失败。需要提示验证失败。另外把失败信息写入日志表。
//                response.sendRedirect(CmbcConstant.YZ_FAIL);
//            }
//        } catch (Exception e) {
//            logger.error("CMBC支付验签异常", e);
//        }
    }

    @GET
    @Path("cmbc/notifyGet")
    public void cmbcNotifyGet(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        try {
            cmbcNotify(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GET
    @Produces({"application/json"})
    @Path("cmbcNotifytest")
    public String cmbcNotifytest(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        String cmd = "C02";
        String userId = "13810989667";
        String orderId = "111111111";
        String addBody = "{" +
                "\"amount\":" + 301000 + "," +
                "\"name\":\"" + userId + "\"," +
                "\"fromType\":7" + "," +
                "\"orderId\":\"" + orderId + "\"" +
                "}";

        String resMessage = HttpClientWrapper.post(request, response, cmd, addBody, CmbcConstant.CMBC_KEY, "");
        JSONObject obj = null;
        try {
            obj = new JSONObject(resMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "cmbcNotifytest";
    }

    public static void main(String[] args) throws JSONException {

//        String body="{\"order\":{\"amount\":1000,\"platform\":\"HTML5\",\"gameCode\":\"T01\",\"termCode\":\"14113\",\"type\":0,\"multiple\":\"1\",\"tickets\":[{\"amount\":200,\"betTypeCode\":\"00\",\"playTypeCode\":\"00\",\"numbers\":\"02,06,07,26,28|03,04\",\"multiple\":\"1\"},{\"amount\":200,\"betTypeCode\":\"00\",\"playTypeCode\":\"00\",\"numbers\":\"03,07,22,30,32|06,09\",\"multiple\":\"1\"},{\"amount\":200,\"betTypeCode\":\"00\",\"playTypeCode\":\"00\",\"numbers\":\"03,04,22,27,28|05,10\",\"multiple\":\"1\"},{\"amount\":200,\"betTypeCode\":\"00\",\"playTypeCode\":\"00\",\"numbers\":\"06,09,24,27,34|02,12\",\"multiple\":\"1\"},{\"amount\":200,\"betTypeCode\":\"00\",\"playTypeCode\":\"00\",\"numbers\":\"03,05,10,20,31|07,08\",\"multiple\":\"1\"}]},\"payType\":0}";
//        JSONObject bodyObj = new JSONObject(body);
//        String amount = bodyObj.getJSONObject("order").getString("amount");
//        System.out.println( bodyObj.getJSONObject("order").get("platform"));


      //
//        String key = "payNo" + UUID.randomUUID().toString().replace("-", "");
//        redisCilent.put(key, "123", 900);

        System.out.println(MD5.MD5Encode("13834656673" + CmbcConstant.CMBC_SIGN_KEY));


    }
}
