<%@ page language="java" pageEncoding="UTF-8" %>
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>账户信息</title>
<meta name="viewport"
      content="initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no, width=device-width">
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<META HTTP-EQUIV="Expires" CONTENT="0">
<meta http-equiv="cache-control" content="no-cache">
<link type="text/css" rel="stylesheet" href="css/reset.css"/>
<link type="text/css" rel="stylesheet" href="css/common.css"/>
<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/common.js"></script>
<script type="text/javascript">
$(document).ready(function () {
    /*判断登陆  */
    var login = sessionStorage.getItem("login");
    if(!login){
        window.location.href="login.html";
        return;
    }
    /*判断登陆  end */
    getUseData();
    var body2 = {'startIndex': 0,
        'size': 10}
    $("#my-zhuhao").click(function () {
        $("#typetogo").val("2");
        $('.tab-content').eq(1).find(".zhanghu-nodata").remove();
        $('.tab-content').eq(1).find(".page").eq(0).attr("data-page", 0);
        getCaipiao('S02', $('.tab-content').eq(1), body2, 10, 2);
    });
});
function getUseData() {
    var pagesize = 10;	//每页显示条数
    //获取账户信息
    $.ajax({
        type: "POST",
        url: "/bankServices/LotteryService/commonTrans?timestamp=?timestamp=" + new Date().getTime(),
        dataType: "json",
        cache: false,
        data: {
            cmd: 'A02',
            Id: sessionStorage.getItem("Id"),
            St: sessionStorage.getItem("St"),
            body: '{}'
        },
        success: function (result) {
            var repCode = result.repCode;
            if (repCode == '0000') {
                sessionStorage.setItem("identityId", result['identityId']);
                sessionStorage.setItem("realName", result['realName']);
                sessionStorage.setItem("phone", result['phone']);
                sessionStorage.setItem("recharge", result['accounts'][0]['recharge']);
                sessionStorage.setItem("prize", result['accounts'][0]['prize']);
                sessionStorage.setItem("accountId", result['accounts'][0]['id']);
                var name = sessionStorage.getItem("Name");
                var recharge = sessionStorage.getItem("recharge");
                var prize = sessionStorage.getItem("prize");
                if (result['payments'].length > 0) {
                    var cardNumber = result['payments'][0]['cardNumber'];
                    sessionStorage.setItem("cardNumber", cardNumber);
                } else {
                    sessionStorage.setItem("cardNumber", "null");
                }
                $('#user_name').html(name);
                $('#recharge').html(toDecimalMoney(recharge / 100));
                $('#bonus').html(toDecimalMoney(prize / 100));
                after();//删除加载动画
            }
        }
    });
    //获取彩票和追号信息
    var body1 = {
        'exOrderStatus': '1001,1000,1600',
        'schemeType': 1,
        'startIndex': 0,
        'size': pagesize
    };
    getCaipiao('Q03', $(".tab-content").eq(0), body1, pagesize, 1);//我的彩票
    //getCaipiao('S02',$(".tab-content").eq(1),body2,pagesize,2);//我的追号
    //点击加载
    $("#load0").die().live("click", function () {
        var aPage = $(".tab-content").eq(0).find(".page");
        if (aPage.attr("data-page") != false) {
            var body11 = {
                'exOrderStatus': '1001,1000,1600',
                'schemeType': 1,
                'startIndex': aPage.attr("data-page"),
                'size': pagesize
            };
            getCaipiao('Q03', $(".tab-content").eq(0), body11, pagesize, 1);//我的彩票
        }
    });
    $("#load1").die().live("click", function () {
        var aPage = $(".tab-content").eq(1).find(".page");
        if (aPage.attr("data-page") != false) {
            var body22 = {
                'startIndex': aPage.attr("data-page"),
                'size': pagesize
            };
            getCaipiao('S02', $(".tab-content").eq(1), body22, pagesize, 2);//我的追号
        }
    });
    /*滚动加载*/
    /*	$(window).scroll(function(){
     $(".top").css("position","fixed");
     var dheight=$(document).height();
     var wheight=$(window).height();
     var sctop=$(window).scrollTop();
     if($(".tab-nav").eq(0).hasClass("now")){
     var aPage=$(".tab-content").eq(0).find(".page");
     if(aPage.attr("data-page")!=false){
     if(sctop>=dheight-wheight){
     var body11= {
     'exOrderStatus' : '1001,1000,1600',
     'schemeType' : 1,
     'startIndex':aPage.attr("data-page"),
     'size':pagesize
     };
     getCaipiao('Q03',$(".tab-content").eq(0),body11,pagesize,1);//我的彩票
     }
     }
     }
     if($(".tab-nav").eq(1).hasClass("now")){
     var aPage=$(".tab-content").eq(1).find(".page");
     if(aPage.attr("data-page")!=false){
     if(sctop>=dheight-wheight){
     var body22= {
     'startIndex':aPage.attr("data-page"),
     'size':pagesize
     };
     getCaipiao('S02',$(".tab-content").eq(1),body22,pagesize,2);//我的追号
     }
     }
     }
     });*/
    /*end 滚动加载*/
}
function getCaipiao(cmd, obj, body, pagesize, type) {
    $.ajax({
        type: "POST",
        url: "/bankServices/LotteryService/commonTrans?timestamp=?timestamp=" + new Date().getTime(),
        dataType: "json",
        cache: false,
        data: {
            cmd: cmd,
            Id: sessionStorage.getItem("Id"),
            St: sessionStorage.getItem("St"),
            body: JSON.stringify(body)
        },
        success: function (result) {
            var repCode = result.repCode;
            if (repCode == '0000') {
                $(".index").show();
                if (result['rst'].length <= 0) {
                    obj.find(".zhanghu-list").remove();
                    obj.find(".zhanghu-nodata").remove();
                    var nohtml = '<div class="zhanghu-nodata">您暂时没有投注记录，快去购彩大厅试试手气吧！</div>';
                    obj.append(nohtml);
                    return false;
                }
                var ind = obj.index(".tab-content");
                obj.find("#load" + ind).remove();
                obj.find(".zhanghu-list").remove();
                if (result['rst'].length <= 0) {
                    return false;
                }
                $.each(result['rst'], function (key, val) {
                    var caizhong = getGame(val);
                    var price = toDecimalMoney(val['amount'] / 100);
                    var tip = "";
                    var endTime = val['createTime'];
                    var tztime = endTime.substring(0, 10);
                    endTime = Date.parse(endTime);
                    var myDate = new Date();
                    var now = myDate.getTime();
                    var date3 = now - endTime;
                    var days = Math.floor(date3 / (24 * 3600 * 1000));
                    var atime = tztime.split("-");
                    tztime = '<br><strong>' + atime[2] + '</strong><br>' + atime[1] + '月';
                    if (days < 1) {
                        tip = '<span class="zhanghu-list-tit linhei80">今</span>';
                    } else {
                        tip = '<span class="zhanghu-list-tit">' + tztime + '</span>';
                    }
                    var state = getOrderStatus(val['status']);

                    var game = getGame(val);
                    var funstr = "";
                    var termHtml = "";
                    if (game == "竞彩足球") {
                        funstr = "window.location.href='fanganjc.html#" + val['id'] + "'";
                        termHtml = val.termCode;
                        termHtml = termHtml.substring(0, 8);
                    } else {
                        funstr = "window.location.href='fangan.html#" + val['id'] + "'";
                        termHtml = val.termCode + '期';
                    }
                    if (type == 2) {
                        funstr = "window.location.href='fanganzh.html#" + val['id'] + "'";
                        state = getZhuhaoStatus(val['status']);
                    }
                    if (val['bonus'] != 0) {
                        var bonus = toDecimalMoney(val['bonus'] / 100);
                        var stateHtml = '<span class="zhongjiang">' + bonus + '元</span>'
                    } else {
                        var stateHtml = '<span class="meizj">' + state + '</span>'
                    }
                    var html = '<div class="zhanghu-list  clearfix">' +
                            tip +
                            '<div class="zhanghu-list-cot clearfix" onclick="' + funstr + '">' +
                            '<span class="fl">' +
                            '<h1>' + game + '<span class="acount-qi">' + termHtml + '</span>' + '<span class="acount-zhui">追' + val.orderCount + '期</span>' + '</h1>' +
                            '<p>' + price + '元</p>' +
                            '</span>' +
                            ' <a class="litt-go" href="#"></a>' + stateHtml +
                            '</div>' +
                            '</div>';
                    obj.append(html);
                    obj.find(".zhanghu-list").eq(0).addClass("fist");
                });
                var page = obj.find(".page").eq(0).attr("data-page");
                page = parseInt(page) + 1;
                obj.find(".page").eq(0).attr("data-page", page);


                //var ssshtml = '<div class="text-center greentext mt30"  id="newload" >点击加载</div>'
                //obj.append(ssshtml);
            } else {
                //alert(result.description);
            }
        },
        error: onError
    });
}
function getLocalTime(nS) {
    return new Date(parseInt(nS) * 1000).toLocaleDateString().replace(/:\d{1,2}$/, ' ');
}
//登录
function startLogin(name, password) {
    before();//显示加载动画
    var body = {
        'name': name,
        'password': password
    };
    $.ajax({
        type: "POST",
        url: "/bankServices/LotteryService/commonTrans?timestamp=?timestamp=" + new Date().getTime(),
        dataType: "json",
        cache: false,
        data: {
            cmd: 'A04',
            body: JSON.stringify(body)
        },
        success: function (result) {
            var repCode = result.repCode;
            if (repCode == '0000') {
                sessionStorage.setItem("name", name);
                sessionStorage.setItem("UserId", name);
                getUseData();
                //登录成功
            } else if (repCode == "1003") {
                //如果没注册，则自动注册
                startSing(name, password);
            } else {
                //登录不成功
                sessionStorage.removeItem("name");
                sessionStorage.removeItem("UserId");

            }
        }
    });
}
function startSing(name, password) {
    var body = {
        'stationId': "24fb88b47c694ec4880ce36d3293e647",
        'customer': {'name': name,
            'password': password
        }
    };
    $.ajax({
        type: "POST",
        url: "/bankServices/LotteryService/commonTrans?timestamp=?timestamp=" + new Date().getTime(),
        dataType: "json",
        cache: false,
        data: {
            cmd: 'A01',
            body: JSON.stringify(body)
        },
        success: function (result) {
            var repCode = result.repCode;
            if (repCode == '0000') {
                //发送登录请求
                var body = {
                    'name': name,
                    'password': password
                };
                $.ajax({
                    type: "POST",
                    url: "/bankServices/LotteryService/commonTrans?timestamp=?timestamp=" + new Date().getTime(),
                    dataType: "json",
                    cache: false,
                    data: {
                        cmd: 'A04',
                        body: JSON.stringify(body)
                    },
                    success: function (result) {
                        var repCode = result.repCode;
                        if (repCode == '0000') {
                            //登录成功
                            sessionStorage.setItem("name", name);
                            sessionStorage.setItem("UserId", name);
                            getUseData();
                        } else {
                            //登录不成功
                            sessionStorage.removeItem("name");
                            sessionStorage.removeItem("UserId");
                            alert(result.description);
                        }

                    }
                });
            }
        }
    });

}
</script>
</head>
<body>
<div class="page-from-left index">
    <div class="top fix" style="position:absolute;">
        <div class="top-relative clearfix">
            <a href="index.jsp" class="go-pre"></a>
            <img style="float:right;" id="refresh" src="img/reflash.png" onClick="window.location.reload();"/>
            <span class="title"
                  style="float:none; display:block; width:220px; text-align:center; margin:0px auto;">我的彩票</span>

        </div>
    </div>
    <div class="cb"></div>
    <!--顶部结束-->
    <!--正文内容开始-->
    <div class="content pt10">
        <div class="zhanghu-litt"></div>
        <div class="yingying mb10">
            <div class="zhanghu-box1 clearfix">
                <div class="touxiang-rightbox" style="padding-left:0px;">
                    <a href="usedata.html" class="username clearfix"
                       style=" display:block;position:relative; padding-left:15px;">
                        <span id="user_name" class="cott-touxiang" onClick="return false;"></span>
                        <span class="yellow-text">实名认证更安全</span><span class="list-item-go"></span>
                    </a>

                    <div class="sline-dark mb10"></div>
                    <a href="acdetail.html" style=" display:block;position:relative; padding-left:15px;">
                        <P class="clearfix"><span class="cott-touxiang">
                        	 	彩金：<font class="orgtext" id="recharge"></font> 元
                            </span>
                            <span class="yellow-text"> 彩金购彩更方便</span>
                        </P>

                        <p class="clearfix"><span class="cott-touxiang">
                        	奖金：<font class="orgtext" id="bonus"></font> 元
                            </span>
                            <span class="yellow-text"> 大奖小奖及时兑</span>
                        </p>
                        <span class="list-item-go"></span>
                    </a>
                </div>
                <div class="cb"></div>

            </div>
        </div>
        <center>
            <div class=" pb10 pl5 pr5 mt5 clearfix">
                <a href="getmoney.html" class="center-org ">提款</a><a href="ylzhifu.html" class="center-org ">充值</a>
                <a href="xb_search.jsp" class="center-org ">去挖宝</a>
            </div>
        </center>
        <div class="zhanghu-tab">
            <div class="nav-box clearfix">
                <a class="tab-nav now" href="acount.jsp" style="width:50%">我的彩票</a><a class="tab-nav"
                                                                                      href="javascript:void(-1)"
                                                                                      style="width:50%"
                                                                                      id="my-zhuhao">我的追号</a>
                <!-- <a class="tab-nav" href="javascript:void(-1)">我的合买</a><a class="tab-nav" href="javascript:void(-1)">我的跟单</a>-->
            </div>
            <input type="hidden"  id="typetogo"  value="1"  />

            <div class="tab-content" style="display:block;"><span style="width:100%;height:0px;" class="page"
                                                                  data-page="0"></span></div>

            <div class="tab-content" id="zhuihao"><span style="width:100%;height:0px;" class="page"
                                                        data-page="0"></span></div>
            <div class="text-center greentext mt30"  id="newload" >点击加载</div>
            <div class="clearfix zhanghu-dian"><span class="tab-dot now"></span><span class="tab-dot"></span></div>
        </div>

    </div>
    <!--正文内容结束-->
    <!--底部开始-->
    <div class="footer">
        <a class="footer-nav nav-gc" href="main.html">购彩</a> <a class="footer-nav nav-zh now" href="acount.jsp">账户</a><a
            class="footer-nav nav-kj" href="result.html">开奖</a>
        <a class="footer-nav nav-xx" href="caiyuan.html">财园</a><a class="footer-nav nav-sz" href="more.html">更多</a>

        <div class="cb"></div>
    </div>
</div>
</body>

<script>

    $(function(){


        $("#newload").click(function(){

            var pagesize=10;
            var type=$("#typetogo").val();
            if(type==1){
                var aPage = $(".tab-content").eq(0).find(".page");
                if (aPage.attr("data-page") != false) {
                    var body11 = {
                        'exOrderStatus': '1001,1000,1600',
                        'schemeType': 1,
                        'startIndex': aPage.attr("data-page"),
                        'size': pagesize
                    };
                    getCaipiao('Q03', $(".tab-content").eq(0), body11, pagesize, 1);//我的彩票
                }


            }else{

                var aPage = $(".tab-content").eq(1).find(".page");
                if (aPage.attr("data-page") != false) {
                    var body22 = {
                        'startIndex': aPage.attr("data-page"),
                        'size': pagesize
                    };
                    getCaipiao('S02', $(".tab-content").eq(1), body22, pagesize, 2);//我的追号
                }

            }

        });

    })

</script>
</html>
