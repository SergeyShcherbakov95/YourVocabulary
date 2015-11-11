package shcherbakov.sergey.yourVocabulary.GUI;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import shcherbakov.sergey.yourVocabulary.JDBC.CreatorJDBC;

/**
 * Добавляет введенную пользователем пару слов в базу данных
 * @author SergeyShcherbakov95
 */
public class AddWordPanel extends ProgramPanel{
    /**
     * Поля для ввода пользователем новых слов
     */
    private JTextField firstWordField = new JTextField();
    private JTextField secondWordField = new JTextField();
    
    /**
     * Добавляет слова в базу данных.
     * Проверяется что-бы такой пары слов не было в базе данных, методом areWordsExist()
     * Проверяется что-бы значения не были пустыми или одинаковыми.
     * Имя таблици в которую будут добавлена пара слов содержится в переменной tableNameString.
     * При успешном выполнеии выводится дилоговое окно с текстом "The transaction is completed"
     */
    public void addNewWord(){
        try {
            String firstWordString = firstWordField.getText();
            String secondWordString = secondWordField.getText();
            if(firstWordString.isEmpty() || secondWordString.isEmpty() || firstWordString.equals(secondWordString)){
                JOptionPane.showMessageDialog(MainFrame.mainFrame, "You entered wrong value!", "Attention", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if(areWordsExist(firstWordString, secondWordString)){
                JOptionPane.showMessageDialog(MainFrame.mainFrame, "These two words exist in database!", "Attention", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            StringBuilder sb = new StringBuilder(38 + 60 + 5 + 40);
            sb.append("INSERT INTO ");
            sb.append(this.getTableNameString());
            sb.append(" ( ");
            sb.append(this.getFirstLanguageString());
            sb.append(" , ");
            sb.append(this.getSecondLanguageString());
            sb.append(" ) VALUES (\'");
            sb.append(firstWordString);
            sb.append("\' , \'");
            sb.append(secondWordString);
            sb.append("\' )");
            
            this.sendExecuteStatement(sb.toString());
            JOptionPane.showMessageDialog(MainFrame.mainFrame, "The transaction is completed", "Information", JOptionPane.INFORMATION_MESSAGE);
        } catch (HeadlessException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            if(this.showDialog() == 0)
                this.addNewWord();
        }        
    }
    
    /**
     * Проверяет или введенная пользователем пара слов уже есть в базе данных
     * @param firstWord первое слово
     * @param secondWord второе слово
     * @return true - пара слов есть в базе данных ,false - такой пары нет
     */
    public boolean areWordsExist(String firstWord, String secondWord){
        StringBuilder sb = new StringBuilder(209 + 60 + 5);
        sb.append("SELECT * FROM ");
        sb.append(this.getTableNameString());
        sb.append(" WHERE ");
        sb.append(this.getFirstLanguageString());
        sb.append(" = \'");
        sb.append(firstWord);
        sb.append("\' AND ");
        sb.append(this.getSecondLanguageString());
        sb.append(" = \'");
        sb.append(secondWord);
        sb.append("\'");
        try(ResultSet result = CreatorJDBC.getConnection().createStatement().executeQuery(sb.toString())) {
            if(result.next())
            return true;
        } catch (Exception e) {
            //NOP
        }
        return false;
    }
    
    /**
     * Добавляет элементы на панель
     * Добавляет слушателей для некоторых елементов
     */
    public void doGui(){
        this.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        
        String firstLanguageString = this.getFirstLanguageString();
        String secondLanguageString = this.getSecondLanguageString();
        JLabel firstLanguageLabel = new JLabel(firstLanguageString);
        JLabel secondLanguageLabel = new JLabel(secondLanguageString);
        JButton backButton = new JButton("Back");
        JButton addButton = new JButton("Add new word");
        
        MouseButtonsListener mouseButtonsListener = new MouseButtonsListener();
        backButton.addMouseListener(mouseButtonsListener);
        addButton.addMouseListener(mouseButtonsListener);
        
        firstWordField.setPreferredSize(new Dimension(150, 25));
        secondWordField.setPreferredSize(new Dimension(150, 25));
        
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        this.add(firstLanguageLabel, gridBagConstraints);
        
        gridBagConstraints.gridx = 1;
        this.add(secondLanguageLabel, gridBagConstraints);
        
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        this.add(firstWordField, gridBagConstraints);
        
        gridBagConstraints.gridx = 1;
        this.add(secondWordField, gridBagConstraints);
     
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        this.add(backButton, gridBagConstraints);
        
        gridBagConstraints.gridx = 1;
        this.add(addButton, gridBagConstraints);
    }
}
