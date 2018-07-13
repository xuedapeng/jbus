

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

  var param = {"method":"getDeviceInfo", "data":{},
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
  template +=  '<td>#deviceId# </td>';
  template +=  '<td>#sessionIds#</td>';
  template +=  '</tr>';

  var rowStr = template;
  rowStr = rowStr.replace("#seq#", seq);
  rowStr = rowStr.replace("#deviceId#", record['deviceId']);

  var sessionIdList = record['sessionIds'];
  var sessionIds = '';
  for(item in sessionIdList) {
    if (sessionIds != '') {
      sessionIds += '<br/>';
    }
    sessionIds += sessionIdList[item];
  }

  rowStr = rowStr.replace("#sessionIds#", sessionIds);


  return rowStr;
}
