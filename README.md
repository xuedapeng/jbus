# jbus
基于java netty的TCP透传服务器

## 功能
* 接收透传网关的TCP连接
* 将网关作为一个设备，向mqtt服务器发布来自设备的数据消息
* 通过向mqtt服务器订阅命令消息，将来自mqtt服务器的命令消息，转发给网关

## 工具
* 服务器状态监视接口：json-rpc
* 网关模拟器：jbus-sim device
* 控制模拟器：jbus-sim ctrl

## 参考文档
    
   * [一种同时支持云端和现地两种运维模式的物联网通讯架构](https://github.com/xuedapeng/jbus/wiki/一种同时支持云端和现地两种运维模式的物联网通讯架构)

