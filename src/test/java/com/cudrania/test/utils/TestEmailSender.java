package com.cudrania.test.utils;

import com.cudrania.core.text.Encryptor;
import com.cudrania.core.utils.EmailSender;
import org.junit.jupiter.api.Test;

/**
 * @author skyfalling
 */
public class TestEmailSender {

    @Test
    public void test() throws Exception {
        new EmailSender(Encryptor.decrypt("01m01h01Z01m01h01d01m01200n00o00s00k01b01n01l"),Encryptor.decrypt("00o01y01y01801h01f01g01s01h01m01f00X")).subject("邮件发送升级测试2").content("<table border='0' cellspacing='0' cellpadding='8' bgcolor='#FFFFFF' style='text-align:center;width:98%;height:30px'>\n" +
                "\t<tr>\n" +
                "\t\t<td bgcolor='#FFFFFF' colspan='2' style='border-width:0;'>文件下载失败列表</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td bgcolor='#F6FAFD' width='75%'; style='border-style:solid;border-width:1;border-color:#CCCCCC;'>URL地址</td>\n" +
                "\t\t<td bgcolor='#F6FAFD' style='border-style:solid;border-width:1 1 1 0;border-color:#CCCCCC;'>原因</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td bgcolor='#FFFFFF' style='border-style:solid;border-width:0 1 1 1;border-color:#CCCCCC;'>ftp://127.0.0.1/xiami/20140605/mysql-proxy.cnf</td>\n" +
                "\t\t<td bgcolor='#FFFFFF' style='border-style:solid;border-width:0 1 1 0;border-color:#CCCCCC;'>文件不存在</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td bgcolor='#F5FAFE' style='border-style:solid;border-width:0 1 1 1;border-color:#CCCCCC;'>ftp://127.0.0.1/xiami/20140530/mysql-proxy.cnf</td>\n" +
                "\t\t<td bgcolor='#F5FAFE' style='border-style:solid;border-width:0 1 1 0;border-color:#CCCCCC;'>文件不存在</td>\n" +
                "\t</tr>\n" +
                "</table>", "text/html;charset=utf-8;").to("abc<lining05@baidu.com>").cc("lining05@baidu.com").send();
    }

}
