package com.sqlite;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.*;

import static org.sqlite.core.Codes.SQLITE_ERROR;

public class Driver {

    public static void main(String args[]) throws Exception {
        createTable();
        insert();
        update();
    }

    private static Connection connection(String database)
    {
        Connection connection = null;
        try
        {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:"+database + ".db");

        }catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return connection;
    }

    private static void createTable() throws SQLException {
        Connection connection = connection("test");
        Statement statement = null;

        String sql = "CREATE TABLE image (ID integer primary key autoincrement, photo BLOB)";

        try {
            statement = connection.createStatement();
            statement.executeUpdate(sql);


            statement.close();
            connection.close();
        }catch (Exception e)
        {
            if(SQLITE_ERROR==1)
            System.out.println("This table already exists.");
        }

    }


    private static void insert() throws Exception{
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        FileInputStream fis = null;
        int num_rows = 0;
        byte[] buf = new byte[1024];
        File imagine = new File("test.jpg");

        try
        {
            fis = new FileInputStream(imagine);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            for( int readNum; (readNum = fis.read(buf)) != -1;)
            {
                bos.write(buf, 0, readNum);
            }


            connection = connection("test");
            preparedStatement = connection.prepareStatement("INSERT INTO image (photo) VALUES(?)");
            preparedStatement.setBytes(1, bos.toByteArray());   // "1" say that, this parameter will be pasted to question mark line upper

            num_rows = preparedStatement.executeUpdate();

            if(num_rows>0)
            {
                System.out.println("Data has been inserted");
            }

            preparedStatement.close();
            connection.close();
        }catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }


    private static void update(){
        try{
            File image = new File("updatedtest.jpg");
            int num_rows = 0;
            FileInputStream fis = new FileInputStream(image);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            for(int readNum; (readNum = fis.read(buf)) != -1;){
                bos.write(buf, 0, readNum);
            }
            fis.close();

            Connection conn = connection("test");
            PreparedStatement ps = conn.prepareStatement("UPDATE image SET photo =? WHERE id = 3");
            ps.setBytes(1, bos.toByteArray());
            num_rows = ps.executeUpdate();
            if (num_rows>0){
                System.out.println("Image updated.");

            }
            ps.close();
            conn.close();
        }catch(Exception er){System.out.println(er);}
    }
}
