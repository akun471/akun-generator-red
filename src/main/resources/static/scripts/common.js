/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-11-6
 * Time: 上午9:43
 * To change this template use File | Settings | File Templates.
 */

/**
 * 时间对象的格式化;
 */
Date.prototype.format = function(format) {

    /*
     * eg:format="YYYY-MM-dd hh:mm:ss";
     */
    var o = {
        "M+" :this.getMonth() + 1, // month
        "d+" :this.getDate(), // day
        "h+" :this.getHours(), // hour
        "m+" :this.getMinutes(), // minute
        "s+" :this.getSeconds(), // second
        "q+" :Math.floor((this.getMonth() + 3) / 3), // quarter
        "S" :this.getMilliseconds()
        // millisecond
    }

    if (/(y+)/.test(format)) {
        format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }

    for ( var k in o) {
        if (new RegExp("(" + k + ")").test(format)) {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
        }
    }

    return format;
}

var Tools = {
    clone : function (myObj){
        if(typeof(myObj) != 'object') return myObj;
        if(myObj == null) return myObj;
        var myNewObj = new Object();
        for(var i in myObj){
            myNewObj[i] = this.clone(myObj[i]);
        }
        return myNewObj;
    },
    formatNumber:function(s){//111,222
        s = s+"";
        if(/[^0-9\.]/.test(s)) return s;
        s=s.replace(/^(\d*)$/,"$1.");
        s=s.replace(/(\d*\.\d\d)\d*/,"$1");
        s=s.replace(".",",");
        var re=/(\d)(\d{3},)/;
        while(re.test(s))
            s=s.replace(re,"$1,$2");
        s=s.replace(/,(\d\d)$/,".$1");
        return s.replace(/^\./,"0.").substring(0, s.length-1);
   },
   checkNumber:function(input){
   		var re = /^[1-9]+[0-9]*]*$/;
   		if(!re.test(input)) {
			return false;
		}
   		return true;
	},
	bytesToSize: function(bytes,digit){
		if (bytes === 0) {
			return '0 B';
		}
		var k = 1024;
        var sizes = ['B','KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];  
        var i = Math.floor(Math.log(bytes) / Math.log(k));  
        var num = bytes / Math.pow(k, i);
		return num.toFixed(digit) + ' ' + sizes[i];
	}
}

var Map = function (){

    var struct = function(key, value) {
        this.key = key;
        this.value = value;
    }

    var put = function(key, value){
        for (var i = 0; i < this.arr.length; i++) {
            if ( this.arr[i].key === key ) {
                this.arr[i].value = value;
                return;
            }
        }
        this.arr[this.arr.length] = new struct(key, value);
    }

    var get = function(key) {
        for (var i = 0; i < this.arr.length; i++) {
            if ( this.arr[i].key === key ) {
                return this.arr[i].value;
            }
        }
        return null;
    }

    var remove = function(key) {
        var v;
        for (var i = 0; i < this.arr.length; i++) {
            v = this.arr.pop();
            if ( v.key === key ) {
                continue;
            }
            this.arr.unshift(v);
        }
    }

    var removeAll = function() {
        var v;
        for (var i = 0; i < this.arr.length; i++) {
            v = this.arr.pop();
            this.arr.unshift(v);
        }
    }

    var size = function() {
        return this.arr.length;
    }

    var isEmpty = function() {
        return this.arr.length <= 0;
    }
    this.arr = new Array();
    this.get = get;
    this.put = put;
    this.remove = remove;
    this.removeAll = removeAll;
    this.size = size;
    this.isEmpty = isEmpty;
}

// 计算当前日期在本年度的周数
Date.prototype.getWeekOfYear = function(weekStart) { // weekStart：每周开始于周几：周日：0，周一：1，周二：2 ...，默认为周日
    weekStart = (weekStart || 0) - 0;
    if(isNaN(weekStart) || weekStart > 6)
        weekStart = 0;

    var year = this.getFullYear();
    var firstDay = new Date(year, 0, 1);
    var firstWeekDays = 7 - firstDay.getDay() + weekStart;
    var dayOfYear = (((new Date(year, this.getMonth(), this.getDate())) - firstDay) / (24 * 3600 * 1000)) + 1;
    return Math.ceil((dayOfYear - firstWeekDays) / 7) + 1;
}

// 计算当前日期在本月份的周数
Date.prototype.getWeekOfMonth = function(weekStart) {
    weekStart = (weekStart || 0) - 0;
    if(isNaN(weekStart) || weekStart > 6)
        weekStart = 0;

    var dayOfWeek = this.getDay();
    var day = this.getDate();
    return Math.ceil((day - dayOfWeek - 1) / 7) + ((dayOfWeek >= weekStart) ? 1 : 0);
}

String.prototype.startWith=function(s){
    if(s==null||s==""||this.length==0||s.length>this.length)
        return false;
    if(this.substring(0, s.length)==s)
        return true;
    else
        return false;
    return true;
}

function HTMLEncode(text){
	text = text.replace(/&/g, "&amp;") ;
    text = text.replace(/"/g, "&quot;") ;
    text = text.replace(/</g, "&lt;") ;
    text = text.replace(/>/g, "&gt;") ;
    text = text.replace(/'/g, "&#146;") ;
    text = text.replace(/\ /g,"&nbsp;");
    text = text.replace(/\n/g,"<br>");
    text = text.replace(/\t/g,"&nbsp;&nbsp;&nbsp;&nbsp;");
    return text;
}
