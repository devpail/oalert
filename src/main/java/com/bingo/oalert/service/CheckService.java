package com.bingo.oalert.service;

public class CheckService {


    /**检查OA打卡记录*/
    public void checkOA(){
        /**
         * 1.创建定时任务在三个时间点检测打卡信息
         * 2.登录oa系统（判断是不是工作日）
         * 2.1识别验证码登陆成功（难）
         * 2.2保持session不变
         * 2.3访问打卡信息页面获取打卡信息
         * 2.4解析打卡数据
         * 2.5如果没有及时打卡，发送未打卡提醒
         * */







    }
}
