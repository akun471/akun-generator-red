var IBCP_tableList = function (options) {
    this.init(options);
};
var sort = "";
var order = "";
IBCP_tableList.prototype = {
    init: function (options) {
        this.options = options;
        var that = this;
        getDataSource();

        //1.初始化Table
        var tableListTable = that.initTableListTable();
        //加载表格
        tableListTable.init();
        // this.dataSourceEntityDetail = new IBCP_dataSourceEntityDetail()
        this.dataSourceEntityList = new IBCP_dataSourceEntityList({})


        //搜索
        $("#btn_search_tablelist").off("click.ibcp").on("click.ibcp", function () {
            $("#btn_search_tablelist").addClass("disabled");
            that.tableSearch();
        });
        //生成代码
        $("#btn_generator_tablelist").off("click.ibcp").on("click.ibcp", function () {
            that.generator();
        });
        //动态数据源
        $("#dataSourceEntity_tab").off("click.ibcp").on("click.ibcp", function () {
            that.dataSourceEntityList.dataSourceEntityDetail.showPanel("div_dataSourceEntity_list");
        });

        //动态数据源
        $("#table_tab").off("click.ibcp").on("click.ibcp", function () {
            that.dataSourceEntityList.dataSourceEntityDetail.showPanel("div_table_list");
        });


        //生成代码
        $("#s_ds_tablelist").on("change", function () {
            $("#btn_search_tablelist").addClass("disabled");
            $("#s_ds_tablelist").addClass("disabled");
            that.tableRefresh();
            $("#btn_search_tablelist").removeClass("disabled");
            $("#s_ds_tablelist").removeClass("disabled");
        });
    },
    initTableListTable: function () {
        var that = this;
        var oTableInit = new Object();
        //初始化Table
        oTableInit.init = function () {
            $('#table_tablelist').bootstrapTable({
                url: 'table/getpage', //请求后台的URL（*）
                method: 'post', //请求方式（*）
                toolbar: '#toolbar_tablelist', //工具按钮用哪个容器
                striped: true, //是否显示行间隔色
                cache: false, //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
                pagination: true, //是否显示分页（*）
                ajaxOptions: that.getOptions(),//自定义请求头
                queryParams: function (params) {
                    // return that.getQueryParams(params);
                    var orderBy = {};
                    if (order == "asc") {
                        orderBy.asc = sort;
                    } else if (order == "desc") {
                        orderBy.desc = sort;
                    } else {
                        orderBy = {desc: "createTime"};
                    }
                    return {
                        pageSize: params.limit,                         //页面大小
                        pageNo: (params.offset / params.limit) + 1,   //页码
                        orderBy: orderBy,
                        tableName: $("#s_name_tablelist").val().trim(),
                    }
                        ;
                }, //传递参数（*）
                responseHandler: function (res) {
                    if (res.status === 200) {
                        return {
                            "total": res.count,//总页数
                            "rows": res.result   //数据
                        };
                    } else {
                        $.gritter.add({
                            title: "提示信息：",
                            text: "查询数据失败：" + res.message,
                            time: 10000
                        });
                    }
                },
                sidePagination: "server", //分页方式：client客户端分页，server服务端分页（*）
                pageNumber: 1, //初始化加载第一页，默认第一页
                pageSize: 10, //每页的记录行数（*）
                pageList: [10, 50, 100, 150], //可供选择的每页的行数（*）
                search: false, //是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
                strictSearch: true,
                showColumns: true, //是否显示所有的列
                showRefresh: true, //是否显示刷新按钮
                minimumCountColumns: 2, //最少允许的列数
                clickToSelect: true, //是否启用点击选中行
                // height: 500, //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
                showToggle: false, //是否显示详细视图和列表视图的切换按钮
                cardView: false, //是否显示详细视图
                detailView: false, //是否显示父子表
                order: "desc",
                sort: "createTime",
                uniqueId: "tableName", //每一行的唯一标识，一般为主键列
                buttonsAlign: "right", //按钮位置
                Icons: 'glyphicon-export',
                columns: [{
                    checkbox: true
                },
                    {
                        field: 'tableName',
                        title: '表名',
                        align: 'center',
                        sortable: true,
                    }, {
                        field: 'tableComment',
                        title: '表注释',
                        align: 'center',
                        sortable: true,
                    }, {
                        field: 'engine',
                        title: '引擎',
                        align: 'center',
                        sortable: true,
                    },
                    {
                        field: 'createTime',
                        title: '创建时间',
                        sortable: true,
                        formatter: function (value, row, index) {
                            if (value != null) {
                                return new Date(value).format("yyyy-MM-dd hh:mm:ss");
                            } else {
                                return "";
                            }
                        }
                    }, {
                        field: 'updateTime',
                        title: '更新时间',
                        sortable: true,
                        formatter: function (value, row, index) {
                            if (value != null) {
                                return new Date(value).format("yyyy-MM-dd hh:mm:ss");
                            } else {
                                return "";
                            }
                        }
                    }],
                onLoadSuccess: function () {
                    $("#btn_search_tablelist").removeClass("disabled");
                    that.initOrderBy();
                },
                onColumnSwitch: function () {
                    that.tableRefresh()
                },
                onToggle: function () {
                    that.tableRefresh()
                },
                onRefresh: function () {
                    that.tableRefresh()
                }
            })
            ;
        };

        return oTableInit;
    },
    getOptions: function () {
        return {headers: {"dataSource": $("#s_ds_tablelist").find("option:selected").val()}};
    },
    tableSearch: function () {
        var that = this;
        $("#table_tablelist").bootstrapTable('refreshOptions', {
            ajaxOptions: that.getOptions(),
            pageNumber: 1
        });
    },
    tableRefresh: function () {
        var that = this;
        $("#table_tablelist").bootstrapTable('refreshOptions', {
            ajaxOptions: that.getOptions(),
            pageNumber: 1
        });
    },
    generator: function () {
        var selItems = $("#table_tablelist").bootstrapTable('getSelections');
        if (selItems.length <= 0) {
            $.gritter.add({
                title: "提示信息：",
                text: "请选择要生成代码的表!!!",
                time: 3000
            });
            return;
        }
        var tableNames = [];
        for (var i = 0; i < selItems.length; i++) {
            tableNames.push(selItems[i].tableName);
        }
        var modelName = $("#g_modelName_tablelist").val().trim();
        var packageName = $("#g_package_tablelist").val().trim();
        var port = $("#g_port_tablelist").val().trim();
        var isLogic = $("#g_isLogic_tablelist").val().trim();
        var tablePrefix = $("#g_tablePrefix_tablelist").val().trim();
        var tableFieldPrefix = $("#g_tableFieldPrefix_tablelist").val().trim();
        var author = $("#g_author_tablelist").val().trim();
        var email = $("#g_email_tablelist").val().trim();
        var href = "/table/code?tables=" + JSON.stringify(tableNames);
        if (modelName) {
            href += "&modelName=" + modelName;
        }
        if (packageName) {
            href += "&packageName=" + packageName;
        }
        if (port) {
            href += "&port=" + port;
        }
        if (isLogic) {
            href += "&isLogic=" + isLogic;
        }
        if (tablePrefix) {
            href += "&tablePrefix=" + tablePrefix;
        }
        if (tableFieldPrefix) {
            href += "&tableFieldPrefix=" + tableFieldPrefix;
        }
        if (author) {
            href += "&author=" + author;
        }
        if (email) {
            href += "&email=" + email;
        }
        href += "&dataSource=" + $("#s_ds_tablelist").find("option:selected").val();
        location.href = href;

    },
    initOrderBy: function () {
        //点击进行排序
        $("#table_tablelist .th-inner").parent().on("click", function () {
            // //($(this).attr("data-field"));
            sort = $(this).attr("data-field")
            if ($(this).children(".th-inner").hasClass("asc")) {
                order = "asc";
            } else if ($(this).children(".th-inner").hasClass("desc")) {
                order = "desc";
            }
        });
    },

};

var IBCP_dataSourceEntityList = function (options) {
    this.init(options);
};
var sort = "";
var order = "";
IBCP_dataSourceEntityList.prototype = {
    init: function (options) {
        this.options = options;
        var that = this;

        //1.初始化Table
        var dataSourceEntityListTable = that.initDataSourceEntityListTable();
        //加载表格
        dataSourceEntityListTable.init();

        this.dataSourceEntityDetail = new IBCP_dataSourceEntityDetail({});
        //搜索
        $("#btn_search_dataSourceEntitylist").off("click.ibcp").on("click.ibcp", function () {
            $("#btn_search_dataSourceEntitylist").addClass("disabled");
            that.tableSearch();
        });
        //添加
        $("#btn_add_dataSourceEntitylist").off("click.ibcp").on("click.ibcp", function () {
            that.dataSourceEntityNew();
        });      //添加
    },
    initDataSourceEntityListTable: function () {
        var that = this;
        var oTableInit = new Object();
        //初始化Table
        oTableInit.init = function () {
            $('#table_dataSourceEntitylist').bootstrapTable({
                url: 'dataSourceEntity/getpage', //请求后台的URL（*）
                method: 'post', //请求方式（*）
                toolbar: '#toolbar_dataSourceEntitylist', //工具按钮用哪个容器
                striped: true, //是否显示行间隔色
                cache: false, //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
                pagination: true, //是否显示分页（*）
                queryParams: function (params) {
                    // return that.getQueryParams(params);
                    var orderBy = {};
                    if (order == "asc") {
                        orderBy.asc = sort;
                    } else if (order == "desc") {
                        orderBy.desc = sort;
                    } else {
                        orderBy = {desc: "createTime"};
                    }
                    return {
                        pageSize: params.limit,                         //页面大小
                        pageNo: (params.offset / params.limit) + 1,   //页码
                        orderBy: orderBy,
                        pollName: $("#s_poll_name_dataSourceEntitylist").val().trim(),
                        url: $("#s_url_dataSourceEntitylist").val().trim(),
                        state: $("#s_state_dataSourceEntitylist").val().trim(),
                    }
                        ;
                }, //传递参数（*）
                responseHandler: function (res) {
                    if (res.status === 200) {
                        return {
                            "total": res.count,//总页数
                            "rows": res.result   //数据
                        };
                    } else {
                        $.gritter.add({
                            title: "提示信息：",
                            text: "查询数据失败：" + res.message,
                            time: 10000
                        });
                    }
                },
                sidePagination: "server", //分页方式：client客户端分页，server服务端分页（*）
                pageNumber: 1, //初始化加载第一页，默认第一页
                pageSize: 10, //每页的记录行数（*）
                pageList: [10, 50, 100, 150], //可供选择的每页的行数（*）
                search: false, //是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
                strictSearch: true,
                showColumns: true, //是否显示所有的列
                showRefresh: true, //是否显示刷新按钮
                minimumCountColumns: 2, //最少允许的列数
                clickToSelect: true, //是否启用点击选中行
                // height: 500, //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
                showToggle: false, //是否显示详细视图和列表视图的切换按钮
                cardView: false, //是否显示详细视图
                detailView: false, //是否显示父子表
                order: "desc",
                sort: "createTime",
                uniqueId: "id", //每一行的唯一标识，一般为主键列
                buttonsAlign: "right", //按钮位置
                Icons: 'glyphicon-export',
                columns: [{
                    checkbox: true
                },

                    {
                        field: 'id',
                        title: 'id',
                        align: 'center',
                        sortable: true,
                    },

                    {
                        field: 'pollName',
                        title: 'pollName',
                        align: 'center',
                        sortable: true,
                    },
                    {
                        field: 'driverClassName',
                        title: 'driverClassName',
                        align: 'center',
                        visible: false,
                        sortable: true,
                    },

                    {
                        field: 'url',
                        title: 'url',
                        align: 'center',
                        width: '7%',
                        sortable: true,
                    },

                    {
                        field: 'username',
                        title: 'username',
                        align: 'center',
                        sortable: true,
                    },

                    {
                        field: 'password',
                        title: 'password',
                        align: 'center',
                        sortable: true,
                    },
                    {
                        field: 'desc',
                        title: 'desc',
                        align: 'center',
                        sortable: true,
                    },
                    {
                        field: 'state',
                        title: '状态',
                        sortable: true,
                        formatter: function (value, row, index) {
                            if (value == 1) {
                                return "<span class='label label-success'>有效</span>";
                            } else {
                                return "<span class='label label-danger'>无效</span>";
                            }
                        }
                    },
                    {
                        field: 'createTime',
                        title: '创建时间',
                        sortable: true,
                        formatter: function (value, row, index) {
                            if (value != null) {
                                return new Date(value).format("yyyy-MM-dd hh:mm:ss");
                            } else {
                                return "";
                            }
                        }
                    }, {
                        field: 'updateTime',
                        title: '更新时间',
                        sortable: true,
                        formatter: function (value, row, index) {
                            if (value != null) {
                                return new Date(value).format("yyyy-MM-dd hh:mm:ss");
                            } else {
                                return "";
                            }
                        }
                    }, {
                        title: '操作',
                        formatter: function (value, row, index) {
                            var dis_online = "";
                            var dis_offline = "";
                            if (row.state == 1) {
                                dis_online = "hide";
                            } else {
                                dis_offline = "hide";
                            }

                            return _.template($("#temp_table_action").html())({
                                id: row.id,
                                state: row.state,
                                actions: [
                                    {
                                        color: "btn-danger",
                                        name: "删除",
                                        value: "btn_del_dataSourceEntityList"
                                    }, {
                                        color: "btn-success",
                                        name: "上线",
                                        value: "btn_state_dataSourceEntityList",
                                        dis: dis_online,
                                    }, {
                                        color: "btn-warning",
                                        name: "下线",
                                        value: "btn_state_dataSourceEntityList",
                                        dis: dis_offline,
                                    }, {
                                        color: "btn-primary",
                                        name: "编辑",
                                        value: "btn_edit_dataSourceEntityList",
                                    }
                                ]
                            });
                        }
                    }],
                onLoadSuccess: function () {

                    $("#btn_search_dataSourceEntitylist").removeClass("disabled");
                    that.tableDataSourceEntityFinish();
                    that.initSwitch();
                    that.initOrderBy();
                }
                ,
                onColumnSwitch: function () {
                    that.tableDataSourceEntityFinish();
                }
                ,
                onToggle: function () {
                    that.tableDataSourceEntityFinish();
                }
            })
            ;
        };

        return oTableInit;
    },
    tableSearch: function () {
        $("#table_dataSourceEntitylist").bootstrapTable('refreshOptions', {
            pageNumber: 1
        });
    },
    tableDataSourceEntityFinish: function () {
        var that = this;
        //编辑数据源
        $("a[data-action=btn_edit_dataSourceEntityList]").on("click.ibcp", function () {
            var $dataSourceEntity = $(this).closest("span[data-sign=action_table]");
            var id = $dataSourceEntity.data("id");
            that.dataSourceEntityEdit(id);

        });
        //删除数据源
        $("a[data-action=btn_del_dataSourceEntityList]").on("click.ibcp", function () {
            var $dataSourceEntity = $(this).closest("span[data-sign=action_table]");
            that.dataSourceEntityDel($dataSourceEntity.data("id"));
        });

        //数据源状态修改
        $("a[data-action=btn_state_dataSourceEntityList]").on("click.ibcp", function () {
            var $dataSourceEntity = $(this).closest("span[data-sign=action_table]");
            that.dataSourceEntityState($dataSourceEntity.data("id"), $dataSourceEntity.data("state"));
        });
    },
    tableRefresh: function () {
        $("#table_dataSourceEntitylist").bootstrapTable('refreshOptions', {
            pageNumber: 1
        });
    },
    dataSourceEntityNew: function () {
        var that = this;
        this.dataSourceEntityDetail.resetForm();
        this.dataSourceEntityDetail.initSelOptions(function () {
            that.dataSourceEntityDetail.setDataSourceEntityFormData({}, "#form_dataSourceEntity");
            that.dataSourceEntityDetail.showPanel("div_edit_dataSourceEntity");
        });
    },
    dataSourceEntityEdit: function (id) {
        this.dataSourceEntityDetail.resetForm();
        this.dataSourceEntityDetail.loadForm(id);
    },
    dataSourceEntityDel: function (id) {
        var that = this;
        $.ajax({
            url: "/dataSourceEntity/delete/" + id,
            type: "get",
            data: {},
            success: function (json) {
                if (json.status == 200) {
                    that.tableRefresh();
                    getDataSource();
                    IBCP_tableList.prototype.tableRefresh();
                    $.gritter.add({
                        title: "提示信息：",
                        text: "删除成功!!!",
                        time: 5000
                    });
                } else {
                    $.gritter.add({
                        title: "提示信息：",
                        text: "删除失败：" + json.message,
                        time: 10000
                    });
                }
            }
        });
    },
    dataSourceEntityState: function (id, state) {
        var text = "下线";
        var that = this;
        if (state != 1) {
            state = 1
            text = "上线";
        } else {
            state = 0
            text = "下线";
        }
        $.ajax({
            url: "/dataSourceEntity/modifyState",
            type: "post",
            data: JSON.stringify({idAry: id, state: state}),
            contentType: 'application/json',
            success: function (json) {
                if (json.status == 200) {
                    that.tableRefresh();
                    getDataSource();
                    IBCP_tableList.prototype.tableRefresh();
                    $.gritter.add({
                        title: "提示信息：",
                        text: text + "成功!!!",
                        time: 5000
                    });
                } else {
                    $.gritter.add({
                        title: "提示信息：",
                        text: text + "失败：" + json.message,
                        time: 10000
                    });
                }
            }
        });
    },
    initSwitch: function () {
        $("#form_dataSourceEntity input[name='state']").bootstrapSwitch({
            onText: '有效',
            offText: '无效',
            state: true,
            offColor: "danger",
            onInit: function (event, state) {
                state = true;
            },
            onSwitchChange: function (event, state) {
                if (state == true) {
                    $(this).val("1");
                } else {
                    $(this).val("0");
                }
            }
        });
    },
    initOrderBy: function () {
        //点击进行排序
        $("#table_dataSourceEntitylist .th-inner").parent().on("click", function () {
            // //($(this).attr("data-field"));
            sort = $(this).attr("data-field")
            if ($(this).children(".th-inner").hasClass("asc")) {
                order = "asc";
            } else if ($(this).children(".th-inner").hasClass("desc")) {
                order = "desc";
            }
        });
    },

};

var IBCP_dataSourceEntityDetail = function (options) {
    this.init(options);
};
IBCP_dataSourceEntityDetail.prototype = {
    init: function (options) {
        this.options = options;
        var that = this;

        //取消保存
        $("#dataSourceEntitySaveBack").click(function () {
            that.showPanel("div_dataSourceEntity_list");
        });

        //保存
        $("#btn_dataSourceEntity_save").click(function () {
            that.saveDataSourceEntity();
        });
        //返回列表
        $("#btn_back_dataSourceEntitylist").click(function () {
            that.showPanel("div_dataSourceEntity_list");
        });

        that.formdataSourceEntityValidation();

    },
    showPanel: function (id) {
        switch (id) {
            case 'div_edit_dataSourceEntity' :
                $(".nav-tabs li").eq(0).removeClass("active");
                $(".nav-tabs li").eq(1).addClass("active");
                $("#div_dataSourceEntity_list").addClass("hide");
                $("#div_table_list").addClass("hide");
                $("#div_edit_dataSourceEntity").removeClass("hide");
                break;
            case 'div_dataSourceEntity_list' :
                $(".nav-tabs li").eq(0).removeClass("active");
                $(".nav-tabs li").eq(1).addClass("active");
                $("#div_edit_dataSourceEntity").addClass("hide");
                $("#div_table_list").addClass("hide");
                $("#div_dataSourceEntity_list").removeClass("hide");
                break;
            case 'div_table_list' :
                $(".nav-tabs li").eq(0).addClass("active");
                $(".nav-tabs li").eq(1).removeClass("active");
                $("#div_edit_dataSourceEntity").addClass("hide");
                $("#div_dataSourceEntity_list").addClass("hide");
                $("#div_table_list").removeClass("hide");
                break;
            default :
                $(".nav-tabs li").eq(0).addClass("active");
                $(".nav-tabs li").eq(1).removeClass("active");
                $("#div_edit_dataSourceEntity").addClass("hide");
                $("#div_dataSourceEntity_list").addClass("hide");
                $("#div_table_list").removeClass("hide");
        }
    },
    initSelOptions: function (callBack) {
        if ($.isFunction(callBack)) {
            callBack();
        }
    },
    formdataSourceEntityValidation: function () {
        var that = this;
        return $("#form_dataSourceEntity").validate({
            errorElement: 'label', //default input error message container
            errorClass: 'error', // default input error message class
            ignore: "",
            onkeyup: false,
            onclick: false,
            onfocusout: false,
            rules: {
                pollName: {required: true},
                driverClassName: {required: true},
                url: {required: true},
                username: {required: true},
                password: {required: true},
            },
            messages: { // custom messages for radio buttons and checkboxes
                pollName: {required: "请输入 数据源名称！"},
                driverClassName: {required: "请输入 driverClassName！"},
                url: {required: "请输入 url！"},
                username: {required: "请输入 username！"},
                password: {required: "请输入 password！"},
            },
            errorPlacement: function (error, element) {
                error.insertAfter(element);
            },
            highlight: function (element) { // hightlight error inputs
                // set error class to the control group
                $(element).closest('.control-group').removeClass('success').addClass('error');
            },
            unhighlight: function (element) { // revert the change dony by hightlight
                // set error class to the control group
                $(element).closest('.control-group').removeClass('error');
            },
            success: function (label) {
                // set success class to the control group
                label.addClass('valid').addClass('help-inline ok').closest('.control-group').removeClass('error').addClass('success');
            },
            invalidHandler: function (event, validator) { //display error alert on form submit
            },
            submitHandler: function (form) {
                that.saveDataSourceEntity();
            }
        })
            ;
    },
    resetForm: function () {
        $("#form_dataSourceEntity input[type='text']").val("");
        $("#form_dataSourceEntity input[type='hidden']").val("");
        $("#form_dataSourceEntity select").val("");
        $("#form_dataSourceEntity textarea").val("");
        $("#form_dataSourceEntity input[type='checkbox']").removeAttr("checked");
        $("#form_dataSourceEntity input[type='radio']:eq(1)").attr("checked", 'checked');
        $("#form_dataSourceEntity_driverClassName").val("com.mysql.jdbc.Driver")
    },
    loadForm: function (id) {
        var that = this;
        $.ajax({
            url: "/dataSourceEntity/getModelById/" + id,
            type: "get",
            data: {},
            success: function (json) {
                if (json.status == 200) {
                    that.resetForm();
                    that.initSelOptions(function () {
                        that.showPanel("div_edit_dataSourceEntity");
                        that.setDataSourceEntityFormData(json.data, "#form_dataSourceEntity");
                    });
                } else {
                    $.gritter.add({
                        title: "提示信息：",
                        text: "获取详情失败：" + json.message,
                        time: 10000
                    });
                }
            }
        });
    },
    getDataSourceEntityFormData: function (form) {
        var data = {};
        $(form + " input[type='text']").each(function (index) {
            data[this.name] = $(this).val();
        });
        $(form + " input[type='checkbox']").each(function (index) {
            data[this.name] = $(this).val();
        });
        $(form + " input[type='radio']:checked").each(function (index) {
            data[this.name] = $(this).val();
        });
        $(form + " input[type='hidden']").each(function (index) {
            data[this.name] = $(this).val();
        });
        $(form + " input[type='file']").each(function (index) {
            data[this.name] = $(this).val();
        });
        $(form + " select").each(function (index) {
            data[this.name] = $(this).val();
        });
        $(form + " textarea").each(function (index) {
            data[this.name] = $(this).val();
        });
        data["partnerCode"] = $("#partner").val();
        return data;
    },
    setDataSourceEntityFormData: function (data, form) {
        // 设置表单信息
        $.each(data, function (key, value) {
            if (key == "state" && value == 1) {
                $(form + " input[name='state']").bootstrapSwitch('state', true)
            } else if (key == "state" && value != 1) {
                $(form + "  input[name='state']").bootstrapSwitch('state', false)
            }
            $(form + " input[name=" + key + "]").val(value);
            $(form + " select[name=" + key + "]").val(value);
            $(form + " textarea[name=" + key + "]").val(value);

        });
    },
    saveDataSourceEntity: function () {
        var that = this;
        if ($("#form_dataSourceEntity").valid()) {
            var data = that.getDataSourceEntityFormData("#form_dataSourceEntity");
            console.log("save, data=" + JSON.stringify(data));

            $("#btn_dataSourceEntity_save").attr("disabled", true);
            $.ajax({
                url: "/dataSourceEntity/addOrUpdate",
                type: "post",
                data: JSON.stringify(data),
                contentType: 'application/json',
                success: function (json) {
                    if (json.status === 200) {
                        $.gritter.add({
                            title: "提示信息：",
                            text: "保存成功！",
                            time: 2000
                        });
                        IBCP_dataSourceEntityList.prototype.tableRefresh();
                        getDataSource();
                        IBCP_tableList.prototype.tableRefresh();

                        that.showPanel("div_dataSourceEntity_list");
                    } else {
                        $.gritter.add({
                            title: "提示信息：",
                            text: "保存失败：" + json.message,
                            time: 10000
                        });
                    }
                }
            });
            $("#btn_dataSourceEntity_save").attr("disabled", false);
        }
    },
};


function getDataSource() {
    $.ajax({
        url: "/dataSourceEntity/getlist",
        type: "get",
        data: {},
        async: false,
        success: function (json) {
            if (json.status == 200) {
                $("#s_ds_tablelist").html("");
                // $("#s_ds_tablelist").append("<option value='' check='true' selected='selected'>请选择</option>");
                $.each(json.result, function (index, ds) {
                    $("#s_ds_tablelist").append("<option  value=\"" + ds.pollName + "\">" + ds.pollName + "</option>");
                });
            } else {
                $.gritter.add({
                    title: "提示信息：",
                    text: "获取活动失败：" + json.message,
                    time: 10000
                });
            }
        }
    });
}