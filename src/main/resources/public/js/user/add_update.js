layui.use(['form', 'layer','formSelects'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;
    formSelects=layui.formSelects;

    /**
     * 添加或更新用户
     */
    form.on("submit(addOrUpdateUser)", function (data) {
        // 弹出loading层
        alert("数据提交中");
        var index = layer.msg('数据提交中，请稍候', {icon: 16, time: false, shade: 0.8});
        //添加数据（添加用户）
        var url = ctx + "/user/save";
        //修改数据（编辑）
        if($("input[name='id']").val()){
            url = ctx + "/user/update";
        }
        $.post(url, data.field, function (res) {
            if (res.code == 200) {
                setTimeout(function () {
                    // 关闭弹出层（返回值为index的弹出层）
                    top.layer.close(index);
                    top.layer.msg("操作成功！");
                    // 关闭所有ifream层
                    layer.closeAll("iframe");
                    // 刷新父页面
                    parent.location.reload();
                }, 500);
            } else {
                layer.msg(res.msg, {icon: 5});
            }
        });
        return false;
    });

    /**
     * 关闭弹出层
     */
    $("#closeBtn").click(function () {
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执行关闭
    });

    /**
     * 添加用户弹出框的取消功能
     */
    $("#closeBtn").click(function(){
        //假设这是iframe页
        // var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        // parent.layer.close(index); //再执行关闭
        //获取当前弹出层的索引
        var idx= parent.layer.getFrameIndex(window.name);
        //根据索引关闭
        parent.layer.close(idx);
    });

    /**
     * 加载角色下拉框
     */
    formSelects.config('selectId',{
        type:"post",
        searchUrl:ctx + '/role/queryAllRoles?userId='+$("input[name=id]").val(),
        //自定义返回数据中name的key, 默认 name
        keyName: 'roleName',
        //自定义返回数据中value的key, 默认 value
        keyVal: 'id'
    },true);
});