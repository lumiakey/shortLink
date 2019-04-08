<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: lumia
  Date: 19-4-5
  Time: 下午9:11
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<c:set var="basePath" value="${pageContext.request.contextPath }"/>
<!DOCTYPE html>
<html>
<head>


  <link rel="stylesheet" href="http://static.suo.im/static/suo.im/css/suoim_mobile.css">
  <script type="text/javascript">
    var CAR_PATH = '${basePath}';
  </script>
</head>
<body>
<div class="section">
  <h1>
    <strong>
      <a href="#">网址缩短</a>
    </strong>
  </h1>
  <div style="position:relative;">
    <p class="p1">输入将要缩短的长网址:</p >
    <div class="menu_box">
      <a href="./visits.jsp" target="_blank" class="dataAnalysis">查看访问次数</a>
  </div>

  <textarea class="text" placeholder="请输入长网址" id="URL" name="multiurl"></textarea>

  <!-- 有效期选项 -->
  <div class="validityTerm">
    <div>
      <span class="tit">短网址有效期:</span>
      <label>
        <input type="radio" name="effective-date" value=0 title="永久有效" checked="checked"> 永远有效
        <input type="radio" name="effective-date" value=1 title="1天有效" > 1天有效
        <input type="radio" name="effective-date" value=7 title="1周有效" > 1周有效
      </label>
    </div>
  </div>

  <div>
    <span class="" >自定义短链接：</span>
    <input size="30" id="custom" value="" type="text">
  </div>

    <div>
      <span class="">自定义长度：</span>
      &nbsp&nbsp&nbsp
      <input size="30" id="length" value="" type="text">
    </div>



</div>

<div class="shorten">
  <button id="create" type="button" onclick="addcheck()">生成</button>
</div>

</div>

<script src="http://static.suo.im/static/home/js/jquery-3.2.1.min.js"></script>
<script src="http://static.suo.im/static/home/js/jquery.qrcode.min.js"></script>
<script src="http://static.suo.im/static/suo.im/js/index.js?v=1.1"></script>
<script src="http://static.suo.im/static/suo.im/js/dialog.js?v=2"></script>

<script type="text/javascript">
  var CUSTOMREG = /[a-zA-Z0-9]{1,6}/;
  function addcheck(){
    var URL = $("#URL").val();
    var validity = $("input[type='radio']:checked").val();;
    var length = $("#length").val();
    var custom = $("#custom").val();

    if(length !="" && length <7){
      alert("自定义长度需要大于等于7");
      return;
    }
    if(custom != "" &&  length != ""){
      alert("自定义短链接和自定义长度不能同时进行！！");
      return;
    }
    if(custom != "" && strlen(custom)>=7){
      alert("短链接的长度必须小于7！！");
      return;
    }
    if(custom != "" && !CUSTOMREG.test(custom)){
      alert("自定义短链接仅支持6位 且 由a-Z 0-9组成！！");
      return;
    }
    if(custom == null || custom == "undefined" || custom == ""){
      custom = 'NULLCUSTOM';
    }
    if(length == null || length == "undefined" || length == ""){
      length = 8;
    }

    $.ajax({
        type: 'post',
        url: CAR_PATH +'/conversion/getShortLink',
        data: {
          "URl": URL,
          "custom":custom,
          "validity":validity,
          "length":length
        },
        cache: false,
        dataType:"text",
        success: function (data) {
          $("#URL").val(data);
        },
        error: function (data) {
          alert("错了吧！！")
        }
    });
  }

  function strlen(str){
    var len = 0;
    for (var i=0; i<str.length; i++) {
      var c = str.charCodeAt(i);
      //单字节加1
      if ((c >= 0x0001 && c <= 0x007e) || (0xff60<=c && c<=0xff9f)) {
        len++;
      }
      else {
        len+=2;
      }
    }
    return len;
  }
  function getValue(value) {
    // method2_1
    alert(value);
  }
</script>
</body>
</html>

