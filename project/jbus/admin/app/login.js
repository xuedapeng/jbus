
var app = new Vue({
  el: '#app',
  data: {
    account: '',
    password: '',
    account_hint:'账号',
    password_hint:'密码',
    message:''
  },
  methods:{
    login:function() {
      app.message = '';
      return  _login(app.account, app.password);
    }
  }
});

function _login(account, password) {
  if (account == "") {
    app.account_hint = "请输入账号";
    return;
  }
  if (password == "") {
    app.password_hint = "请输入密码";
    return;
  }

  var param = {"method":"login", "data":{"account":account, "password":password}};

  ajaxPost(G_RPC_URL, param,
    function(response){
      if (response.status<0) {
        app.message = response.msg;
        return;
      }
      localStorage.appId=response.appId;
      localStorage.appToken = response.appToken;
      localStorage.account = app.account;

      window.location.href = "../index.html";
  });

}
