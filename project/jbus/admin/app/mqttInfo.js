

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

  var param = {"method":"getMqttInfo", "data":{},
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
  template +=  '<td>#mqttId# </td>';
  template +=  '<td>#server#</td>';
  template +=  '<td>#status#</td>';
  template +=  '<td align="left">#deivceIds#</td>';
  template +=  '</tr>';

  var rowStr = template;
  rowStr = rowStr.replace("#seq#", seq);
  rowStr = rowStr.replace("#mqttId#", record['mqttId']);
  rowStr = rowStr.replace("#server#", record['server']);
  rowStr = rowStr.replace("#status#", record['isConnected']?'connected':'disconnected');

  var deivceIdList = record['deviceIds'];
  var deivceIds = '';
  for(item in deivceIdList) {
    if (deivceIds != '') {
      deivceIds += '<br/>';
    }
    deivceIds += deivceIdList[item];
  }

  rowStr = rowStr.replace("#deivceIds#", deivceIds);


  return rowStr;
}
