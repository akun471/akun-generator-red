(function () {

    var inttPartner = function (callBack) {
        $('#partner').empty();

        $.ajax({
            url: "/partnerInfo/getParnerList",
            type: "post",
            async:false,
            data: {},
            success: function (data) {
                var curPartner = data.curPartner;
                if (data.aaData) {
                    for (var i = 0; i < data.aaData.length; i++) {
                        var partner = data.aaData[i];
                        var partnerName = partner["projectName"];
                        var partnerCode = partner["projectCode"];
                        $('#partner').append('<option value="' + partnerCode + '">' + partnerName + '</option>');
                    }
                }
                $("#partner").val(curPartner);
                if ($.isFunction(callBack)) {
                    callBack();
                }
            }
        });

        $("#partner").change(function () {
            $.ajax({
                url: "/partnerInfo/changeSessionPartcode",
                data: {"curPartner": $('#partner').val()},
                type: "post",
                success: function (data) {
                    window.location.reload();
                }
            });
        });
    }

    window._init_partner_info = function (callBack) {
        inttPartner(callBack);
    };
})();
