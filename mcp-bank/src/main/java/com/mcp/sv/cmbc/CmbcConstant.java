package com.mcp.sv.cmbc;

/**
 * Created by yeeson on 14-3-16.
 */
public class CmbcConstant {
    public static final String CMBC_ORDER_DESC_CZ = "CZ"; //充值
    public static final String CMBC_ORDER_DESC_TZ = "TZ"; //投注
    public static final String CMBC_CODE = "Q0001"; //民生银行的接入渠道号
    public static final String CMBC_KEY = "135790"; //民生银行的接入渠道号
    public static final String CMBC_PAY_TYPE_BALANCE = "1"; //使用现金帐户投注
    public static final String CMBC_PAY_TYPE_BANK = "0"; //使用银行支付直接投注
    public static final String CMBC_CORP_ID = "01298";//平台在民生银行的帐户
    public static final String CMBC_CER_URL = "/cmbc1024-BASE64.cer";
    public static final String CMBC_PFX_URL = "/minsheng.pfx";
    public static final String CMBC_SIGN_KEY = "!@#$%^&*()";
    public static final String CMBC_PAY_KEY = "0okmnhy6";
    public static final String CMBC_NOTIFY_URL = "http://bank.mobilelottery.cn/bankServices/LotteryService/cmbc/notify"; //后台通知地址.

    public static final String MCP_VERSION="1.0.00";
    public static final String MCP_CODE = "Q0001";
    public static final String MCP_KEY = "135790";
    public static final String MCP_INTERFACE_URL = "http://218.30.107.19:9088/mcp-filter/main/interface.htm";

    //命令
    public static final String MCP_CQ01="CQ01";  //查询



}
