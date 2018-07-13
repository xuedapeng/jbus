

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

  var param = {"method":"getSessionInfo", "data":{}, "auth":{"appId":localStorage.appId, "appToken":localStorage.appToken}};

  ajaxPost(G_RPC_URL, param,
    function(response){

      if (response.status < 0) {
        layer.msg(response.msg,{icon:2,time:2000});
        return;
      }

      var rowsStr = "";
      var result = response.result;
      var seq = 0;
      for(i in result) {
        seq++;
        rowsStr += createRow(result[i], seq);
      }

      $('#table_rows').append(rowsStr);

      layer.msg("查询成功！", {icon:1,time:1000});

    });
}


function createRow(record, seq) {

  var template = '';
  template +=  '<tr class="text-c">';
  template +=  '<td>#seq#</td>';
  template +=  '<td>#sessionId#</td>';
  template +=  '<td>#deviceId# </td>';
  template +=  '<td>#host# </td>';
  template +=  '<td>#status# </td>';
  template +=  '<td>#startTime#</td>';
  template +=  '<td>#mqttId#</td>';
  template +=  '</tr>';

  var rowStr = template;
  rowStr = rowStr.replace("#seq#", seq);
  rowStr = rowStr.replace("#sessionId#", record['sessionId']);
  rowStr = rowStr.replace("#deviceId#", record['deviceId']);
  rowStr = rowStr.replace("#host#", record['host']+":"+record['port']);
  rowStr = rowStr.replace("#status#", record['isActive']?"active":"inactive");
  rowStr = rowStr.replace("#startTime#", record['startTime']);

  var mqttId = record['mqttId'];
  if (record['mqttIdLocal'] != '') {
    mqttId += ('<br/>' +  record['mqttIdLocal'] + '(local)');
  }
  rowStr = rowStr.replace("#mqttId#", mqttId);

  return rowStr;
}
