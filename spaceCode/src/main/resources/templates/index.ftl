<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>hello</title>

    <link href="/jquery-easyui-1.7.0/themes/default/easyui.css"/>
    <script src="/jquery-easyui-1.7.0/jquery.min.js"></script>
    <script src="/jquery-easyui-1.7.0/jquery.min.js"></script>
    <script src="/jquery-easyui-1.7.0/jquery.easyui.min.js"></script>
    <script src="/jquery-easyui-1.7.0/locale/easyui-lang-zh_CN.js"></script>
</head>
<body>
<h2>Hello 读取MDB文件.</h2>

<form method="POST" enctype="multipart/form-data" action="/space/upload">
    文件:<input type="file" name="spaceFile"/> <input type="submit" value="上传"/>
</form>

<div id="container">

    <div id="box" style="width :600px; background:orange;">
        <!-- 内容部分 -->
    </div>

</div>

<script>
    $('#box').datagrid({
        title: '数据列表',
        url: '/space/getPage',
        contentType: 'application/json;charset=UTF-8',
        columns: [[
            {
                field: 'index', title: 'Index', width: 50, formatter: function (value, row, index) {
                    return index + 1;
                }
            },
            //{ field: 'Id', title: 'Id', width: 100 },
            {field: 'Data', title: 'Data', width: 100}
        ]],
        pagination: true
    });
</script>

</body>
</html>