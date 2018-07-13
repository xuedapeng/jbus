

function moveTo(url) {
  window.location.href = url;
}

function moveToLogin() {
  moveTo("app/login.html");
}

function checkAuth() {

  		if (!localStorage.appId || !localStorage.appToken) {

          layer.msg("登录信息已失效，请重新登录！",{icon:0,time:2000});
          return false;
  		}

      return true;
}

// Post
function ajaxPost(url, param, callback){

    var data = param;

    var dataString = JSON.stringify(data);

    // サーバにsubmit
    $.ajax({

          contentType: "application/json; charset=utf-8",
          type    : "POST",
          url     : url,
          data    : dataString,
          dataType: "json",
          timeout : 12000,
          success : function(response, ex){
            callback(response);
          },

          error   : function(request, status, ex){
            var err = "ajaxPost error!\t(status:"+status+", exception:" + ex + ")";
            console.log(err);
          }
    });

  };
