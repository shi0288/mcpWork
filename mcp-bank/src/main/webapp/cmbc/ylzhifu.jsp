<%@ page language="java" pageEncoding="UTF-8" %>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>银行支付</title>
    <meta http-equiv="cache-control" content="no-cache">
    <meta content="yes" name="apple-mobile-web-app-capable">
    <meta content="black" name="apple-mobile-web-app-status-bar-style">
    <link rel="stylesheet" type="text/css" href="css/jq.ui.css?1290022">
    <script charset="utf-8" src="js/jq.mobi.min.js"></script>
    <script charset="utf-8" src="js/lottery.js"></script>
    <script charset="utf-8" src="js/iphone.js"></script>
    <meta name="viewport"
          content="initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no, width=device-width">
    <link type="text/css" rel="stylesheet" href="css/reset.css"/>
    <link type="text/css" rel="stylesheet" href="css/common.css"/>
    <script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
    <script type="text/javascript" src="js/common.js"></script>
    <jsp:include page="include/login.jsp" flush="true"/>
    <script>
        $(function () {
            $("#ljchongzhi2").bind("click", function (event, ui) {
                var money = $("#money").val();
                if (money == "") {
                    alert("请输入要充值的金额！");
                    return false;
                }
                if (isNaN(money)) {
                    alert("充值金额必须为数字");
                    return false;
                }
                money = parseInt(money);
                if (money < 1) {
                    alert("充值金额必须是大于1的整数。");
                    return false;
                }
                if (money > 10000) {
                    alert("充值金额不能大于10000。");
                    return false;
                }
                before();
                var Id = sessionStorage.getItem("Id");
                if (Id == null || Id == "null"|| Id == "") {//尚未登陆，需要处理登陆.
				    history.back();
                    setWebitEvent(window.location.href, 'LT02');
                }

                $.ajax({
                    type: "POST",
                    url: "/bankServices/LotteryService/cmbc/pay?timestamp=" + new Date().getTime(),
                    cache: false,
                    data: {
                        userId: sessionStorage.getItem("Name"),
                        money: money
                    },
                    success: function (b) {
                        after();
                        var resultCode = $(b).find("ResultCode").text();
                        if (resultCode == 0) {
                            var envolopData = $(b).find("EnvolopData").text();
//                            window.alert(envolopData);
                            setWebitEvent(envolopData, "LT03");
                        }
                        else {
                            var errorMsg = $(b).find("ErrorMsg").text();
                            window.alert(errorMsg);
                        }
                    },
                    error: onError
                });
            });
        });
        $(document).ready(function (e) {
            $("#money").keyup(function (e) {
                if (e.keyCode != 8) {
                    var value = $(this).val();
                    value = value.replace(/\b(0+)/gi, "");
                    value = parseInt(value);
                    if (isNaN(value)) {
                        $(this).val("");
                    } else {
                        if (value > 9999) {
                            value = 9999;
                            alert("最高充值9999元");
                        }
                        //$(this).val("").focus().val(value);
                        $(this).val(value);
                    }

                }
            });
        });
    </script>
</head>
<body>
<div class="page-from-left index">

    <div class="top fix">
        <div class="top-relative clearfix">
            <a href="javascript:history.go(-1)" class="go-pre"></a>
            <span class="title">彩金充值</span>
        </div>
    </div>
    <div class="cb"></div>
    <!--顶部结束-->
    <!--正文内容开始-->
    <div class="content p10">
        <div style="height:1px; margin-top:9px;"></div>
        <div class="box-item-nbg clearfix zhifubox">
            <label>充值金额：</label> <input type="number" id="money" placeholder="请输入充值金额" class="noborder-input">
        </div>
        <div style="height:1px; margin-top:9px;"></div>
        <a class="m-bigbtn-org" id="ljchongzhi2">立即充值</a>

        <p class="greentext mt15" style="font-size:13px;">温馨提示：最少一元钱。</p>
    </div>
    <!--正文内容结束-->
    <!--底部开始-->

</div>
</body>
</html>