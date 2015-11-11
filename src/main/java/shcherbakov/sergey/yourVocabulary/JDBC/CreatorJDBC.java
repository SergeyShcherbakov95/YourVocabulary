package shcherbakov.sergey.yourVocabulary.JDBC;

import java.io.*;
import java.sql.*;
import java.util.*;
import javax.swing.JOptionPane;
import shcherbakov.sergey.yourVocabulary.GUI.MainFrame;

/**
 * Подключенние к базе данных. Реализует паттерн Singelton. Данные для подключения берет из файла "src/main/resources/config.properties"
 * @author SergeyShcherbakov95
 */
public class CreatorJDBC {
    private static Connection connection = null;
    
    private CreatorJDBC(){
        //NOP
    }
    
    /**
     * Возвращает елемент на который ссылается connection. Если connection == null, то сначала создает элемент типа Connection.
     * Данные берутся из файла "src/main/resources/config.properties".
     * Если создать подключение не удалось, то предлагается изменить значения в вышеуказанном файле.
     * @return connection to database. 
     * @throws SQLException
     * @throws ClassNotFoundException 
     */
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if(connection == null){
            try(InputStream is = CreatorJDBC.class.getClass().getResourceAsStream("/config.properties");) {
                Properties properties = new Properties();
                properties.load(is);
                
                Properties connInfo = new Properties();

                connInfo.put("user",properties.getProperty("db.login"));
                connInfo.put("password", properties.getProperty("db.password"));

                connInfo.put("useUnicode","true");
                connInfo.put("characterEncoding","utf8");
                Class.forName(properties.getProperty("db.driver"));
                connection = DriverManager.getConnection(properties.getProperty("db.host"), connInfo);
            } catch (FileNotFoundException e){
                //NOP
            }
            catch (IOException e) {
                
            } catch (ClassNotFoundException | SQLException e){
                int answer = JOptionPane.showConfirmDialog(
                MainFrame.mainFrame, "There are some problems with database or connection to database.\n Do you want to exchange properties file?"
                        ,"Error", JOptionPane.ERROR_MESSAGE, JOptionPane.YES_NO_OPTION);
                if(answer == 0)
                    exchangeProperties();
            }
        }
        return connection;
    }
    /**
     * Поочередно выводит диалоговые окна для изменеия значений в файле "src/main/resources/config.properties"
     */
    private static void exchangeProperties(){
        File newFile = new File("src/main/resources/configTemp.properties");
        File oldFile = new File("src/main/resources/config.properties");
        try(OutputStream output = new FileOutputStream(newFile);
                InputStream input = new FileInputStream(oldFile);) {
                    String [] nameProperties = {"db.login", "db.password", "db.driver", "db.host"};
                    
                    Properties newProperties = new Properties();
                    Properties oldProperties = new Properties();
                    oldProperties.load(input);
                    
                    for(String nameProperty : nameProperties){
                        String oldProperty = (String)oldProperties.getProperty(nameProperty);
                        String valueProperty = JOptionPane.showInputDialog(MainFrame.mainFrame, nameProperty, oldProperty);
                        newProperties.setProperty(nameProperty, valueProperty);
                    }
                    newProperties.store(output, null);
                    if(oldFile.delete()){
                        newFile.renameTo(oldFile);
                    }
        } catch (Exception e) {
            //NOP
        }
    }
}
