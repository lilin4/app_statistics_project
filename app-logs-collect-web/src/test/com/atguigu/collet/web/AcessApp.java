package com.atguigu.collet.web;

import org.junit.Test;

import java.sql.*;

/**
 * @Classname AcessApp
 * @Description TODO
 * @Date 2020/11/12 9:23
 * @Created by 林立
 */
public class AcessApp {
    @Test
    public void test1() throws Exception {
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        Connection con = DriverManager.getConnection("jdbc:hive2://hadoop-tools-app1:10000/default","root","123456");
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * from test");
        while (rs.next()){
            System.out.println(rs.getInt(1) + "," + rs.getString(2));
        }
        rs.close();
        st.close();
        con.close();

    }
}
