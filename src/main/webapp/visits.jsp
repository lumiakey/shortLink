<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: lumia
  Date: 19-4-8
  Time: 上午3:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<c:set var="basePath" value="${pageContext.request.contextPath }"/>
<html>
<head>
    <script type="text/javascript">
        var CAR_PATH = '${basePath}';
    </script>
</head>
<body>
    <div>
        <span>请输入短链接：</span>
        <input type="text" id="shortLink" size="60" value="">
    </div>
    <button id="submit" type="button" onclick="getVisitsNum()">
    查看访问次数
    </button>
    <script src="http://static.suo.im/static/home/js/jquery-3.2.1.min.js"></script>
    <script src="http://static.suo.im/static/home/js/jquery.qrcode.min.js"></script>
    <script src="http://static.suo.im/static/suo.im/js/index.js?v=1.1"></script>
    <script src="http://static.suo.im/static/suo.im/js/dialog.js?v=2"></script>
<script type="text/javascript">



    function getVisitsNum() {
        var shortLink = $("#shortLink").val();
        $.ajax({
            type: 'post',
            url: CAR_PATH +'/JumpNum/getNum',
            data: {
                "shortLinkHttp": shortLink,
            },
            cache: false,
            dataType:"text",
            success: function (data) {
                $("#shortLink").val(data+"次");
            },
            error: function (data) {
                alert("错了吧！！")
            }
        });
    }

</script>
</body>
</html>
