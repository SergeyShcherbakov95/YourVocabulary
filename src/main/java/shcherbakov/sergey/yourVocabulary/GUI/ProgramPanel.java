package shcherbakov.sergey.yourVocabulary.GUI;

import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import shcherbakov.sergey.yourVocabulary.JDBC.CreatorJDBC;

/**
 * Суперкласс для всех панелей которые добавляются на MainFrame.mainFrame через метод MainFrame.changePanel()
 * @author SergeyShcherbakov95
 */
public abstract class ProgramPanel extends JPanel {
    /**
     * Первый и второй языки, выбраные пользователем, уже в отсортированом порядке методом compareTo
     */
    private static String firstLanguageString;
    private static String secondLanguageString;
    /**
     * Первый и второй языки, выбраные пользователем, для которых не вызывался метод compareTo
     */
    private static String userFirstLanguageString;
    private static String userSecondLanguageString;
    /**
     * Имя таблици в базе данных для выбраных пользователем языков
     */
    private static String tableNameString;
    
    /**
     * Метод который должны реализововать все потомки.
     * Добавляет компоненты на панель
     */
    public abstract void doGui();

    
    public String getUserFirstLanguageString(){
        return userFirstLanguageString;
    }
    
    public String getUserSecondLanguageString(){
        return userSecondLanguageString;
    }
    
    public String getFirstLanguageString(){
        return firstLanguageString;
    }
    
    public String getSecondLanguageString(){
        return secondLanguageString;
    }
    
    public String getTableNameString(){
        return tableNameString;
    }
    
    /**
     * Обновляет значения всех переменных этого класса. 
     * Значение берет из методов getFirstLanguageValue() и getSecondLanguageValue() с класса MainPanel.
     */
    public void updateFirstAndSecondLanguageValues(){
        firstLanguageString = ((MainPanel)this).getFirstLanguageValue();
        secondLanguageString = ((MainPanel)this).getSecondLanguageValue();
        userFirstLanguageString  = firstLanguageString;
        userSecondLanguageString  = secondLanguageString; 
        if(firstLanguageString.compareTo(secondLanguageString) < 0){
                String t = firstLanguageString;
                firstLanguageString = secondLanguageString;
                secondLanguageString = t;
            }
        tableNameString = firstLanguageString + "Table" + secondLanguageString;
    }
    
    /**
     * Посылает sql команду на выполнение в базу данных 
     * @param sql команда
     * @throws SQLException
     * @throws ClassNotFoundException 
     */
    public void sendExecuteStatement(String sql) throws SQLException, ClassNotFoundException{
        try (Statement st = CreatorJDBC.getConnection().createStatement()) {
            st.execute(sql);
        }
    }
    
    /**
     * Диалоговое окно, сообщающее о проблемах в подключении к базе данных. 
     * Спрашивает или пользователь хочет повторить действие.
     * @return 0 если ответ Yes, 1 - если ответ No
     */
    public int showDialog(){
            int answer = JOptionPane.showConfirmDialog(
                MainFrame.mainFrame, "There are some problems with database or connection to database.\n Do you want try again?"
                        ,"Error", JOptionPane.ERROR_MESSAGE, JOptionPane.YES_NO_OPTION);
            return answer;
    }
    
    /**
     * В зависимости от значения buttonText выполняет какое-либо действие
     * @param buttonText текст кнопки из-за которй произошло событие
     */
    public void actionAfterEvent(String buttonText){
        switch (buttonText) {
            case "Start":
                updateFirstAndSecondLanguageValues();
                MainFrame.mainFrame.changePanel(new StartPanel());
                break;
            case "Show my vocabulary":
                updateFirstAndSecondLanguageValues();
                MainFrame.mainFrame.changePanel(new ShowVocabularyPanel());
                break;
            case "Add vocabulary":
                MainFrame.mainFrame.changePanel(new AddVocabularyPanel());
                break;
            case "Add word":
                updateFirstAndSecondLanguageValues();
                MainFrame.mainFrame.changePanel(new AddWordPanel());
                break;
            case "Back":
                MainFrame.mainFrame.changePanel(new MainPanel());
                break;
            case "Add new vocabulary":
                ((AddVocabularyPanel)ProgramPanel.this).addNewVocabulary();
                break;
            case "Add new word":
                ((AddWordPanel)ProgramPanel.this).addNewWord();
                break;
            case "Answer":
                ((StartPanel)ProgramPanel.this).addAnswer();
                break;
            case "Next":
                ((StartPanel)ProgramPanel.this).getQuestion();
                break;
            default:
                break;
                }
    }
    
    /**
     * Класс который является слушателем для всех кнопок
     */
    public class MouseButtonsListener implements MouseListener {
        /**
         * Получает текст кнопки по которой нажали и передает методу actionAfterEvent()
         * @param e 
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getButton() == MouseEvent.BUTTON1){
                JButton clickedButton = (JButton) e.getSource();
                String buttonText = clickedButton.getText();
                actionAfterEvent(buttonText);
            }
        }
        
        @Override
        public void mousePressed(MouseEvent e) { /*NOP*/ }
        @Override
        public void mouseReleased(MouseEvent e) { /*NOP*/ }
        @Override
        public void mouseEntered(MouseEvent e) { /*NOP*/ }
        @Override
        public void mouseExited(MouseEvent e) { /*NOP*/ }  
    }
}
