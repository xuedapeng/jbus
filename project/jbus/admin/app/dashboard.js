

var app = new Vue({
  el: '#app',
  data: {

  },
  methods:{

  }
});


loadData();
function loadData() {

  if (!checkAuth()) {
    return;
  }

  var param = {"method":"getDashboard", "data":{},
              "auth":{"appId":localStorage.appId, "appToken":localStorage.appToken}};

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:2000});
        return;
      }

      var rowsStr = "";
      var result = response.result;
      var seq = 0;
      var keys = ['tcp_port','rpc_port','mqtt_broker', 'sessionCount', 'deviceCount', 'mqttClientCount', 'mqttClientCountLocal'];
      for(key in keys) {
        seq++;
        if (typeof(result[keys[key]])=='undefined') {
          continue;
        }
        rowsStr += createRow(keys[key], result[keys[key]]);
      }

      $('#table_rows').append(rowsStr);

      layer.msg("查询成功！", {icon:1,time:1000});

    });
}


function createRow(key, value) {

  var template = '';
  template +=  '<tr class="text-c">';
  template +=  '<td>#prop# </td>';
  template +=  '<td>#value#</td>';
  template +=  '</tr>';

  var rowStr = template;
  rowStr = rowStr.replace("#prop#", key);
  rowStr = rowStr.replace("#value#", value);


  return rowStr;
}
