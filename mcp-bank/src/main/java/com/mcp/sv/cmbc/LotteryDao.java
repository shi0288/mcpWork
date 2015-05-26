package com.mcp.sv.cmbc;

import com.mcp.sv.util.MongoConst;
import com.mcp.sv.util.MongoManager;
import com.mcp.sv.util.MongoUtil;
import com.mongodb.*;
import com.mongodb.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/5/26.
 */
public  class LotteryDao {

    private static final Logger logger = LoggerFactory.getLogger(LotteryDao.class);

    //注册用户
    public static String register(String userName, String passWord, String rePassWord) {
        //查询库中是否有此记录
        Map param =new HashMap();
        param.put("userName", userName);
        List datas= MongoUtil.query(MongoConst.MONGO_USERS,param);
        logger.info("新用户注册：" + userName);
        if (datas.size() > 0) {
            return "用户名已存在";
        }
        if (!passWord.equals(rePassWord)) {
            return "两次密码输入不一致";
        }
        param.put("passWord", MD5.MD5Encode(passWord));
        int res=MongoUtil.save(MongoConst.MONGO_USERS, param);

        if(res!=0){
            return "注册失败";
        }
        return "";
    }

    public static String login(String userName, String passWord) {
        //查询库中是否有此记录
        Map param =new HashMap();
        param.put("userName", userName);
        List datas= MongoUtil.query(MongoConst.MONGO_USERS, param);
        logger.info("用户登陆：" + userName + "  " + datas.size());
        if(datas.size()==1){
                DBObject user = (DBObject) datas.get(0);
                String dbPassWord= (String)user.get("passWord");
                if(MD5.MD5Encode(passWord).equals(dbPassWord)){
                     //登录成功
                    return "";
                }else {
                    return "密码输入错误";
                }
        }
        return "登陆异常";
    }

    public static String recharge(String userName, int money) {
        //查询库中是否有此记录
        Map param =new HashMap();
        param.put("userName", userName);
        List datas= MongoUtil.query(MongoConst.MONGO_RECHARGE, param);
        logger.info("用户充值：" + userName + "  " + datas.size());
        if(datas.size()==1){
            DBObject acount = (DBObject) datas.get(0);
            int recharge=(int) acount.get("recharge");
            money+=recharge;
            String acountStr=JSON.serialize(acount);
            DBObject newAcount= (DBObject) JSON.parse(acountStr);
            newAcount.put("recharge",money);
            int res=MongoUtil.update(MongoConst.MONGO_RECHARGE, acount,newAcount);
            if(res==1){
                return "";
            }else {
                return "充值失败";
            }
        }else if(datas.size()==0){
            param.put("recharge",money);
            int res=MongoUtil.save(MongoConst.MONGO_RECHARGE, param);
            if(res==0){
                return "";
            }else {
                return "充值失败";
            }
        }
        return "充值异常";
    }
}
