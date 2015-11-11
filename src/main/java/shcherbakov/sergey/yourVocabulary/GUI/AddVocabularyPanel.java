package shcherbakov.sergey.yourVocabulary.GUI;

import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;

/**
 * Класс который добавляет новую таблицу в базу данных. 
 * @author SergeyShcherbakov95
 */
public class AddVocabularyPanel extends ProgramPanel {
    /**
     * Поля для ввода пользователем новых языков
     */
    private final JTextField firstLanguageField = new JTextField();
    private final JTextField secondLanguageField = new JTextField();
    
    /**
     * Добавляет новую таблицу в базу данных. 
     * Значения берутся из полей firstLanguageField и secondLanguageField сортируются методом compareTo.
     * Имя таблицы имеет вид firstLanguage + "Table" + secondLanguage.
     * Проверяется что-бы значения не были пустыми или одинаковыми.
     * При успешном выполнеии выводится дилоговое окно с текстом "The transaction is completed"
     */
    public void addNewVocabulary(){
        String firstLanguageString =  firstLanguageField.getText();
        String secondLanguageString =  secondLanguageField.getText();
        if(firstLanguageString.isEmpty() || secondLanguageString.isEmpty() || firstLanguageString.equals(secondLanguageString)){
            JOptionPane.showMessageDialog(MainFrame.mainFrame, "You entered wrong value!", "Attention", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            if(firstLanguageString.compareTo(secondLanguageString) < 0){
                String t = firstLanguageString;
                firstLanguageString = secondLanguageString;
                secondLanguageString = t;
            }
            String tableNameString = firstLanguageString + "Table" + secondLanguageString;
           
            StringBuilder sb = new StringBuilder(209 + 60 + 5);
            sb.append("CREATE TABLE IF NOT EXISTS ");
            sb.append(tableNameString);
            sb.append("( id int(10) UNSIGNED AUTO_INCREMENT,");
            sb.append(firstLanguageString);
            sb.append(" VARCHAR(50) CHARACTER SET \'utf8\' NOT NULL,");
            sb.append(secondLanguageString);
            sb.append(" VARCHAR(50) CHARACTER SET \'utf8\' NOT NULL,");
            sb.append("numberOfAnswers INT(3) NOT NULL DEFAULT 0,");
            sb.append("PRIMARY KEY (id))");
            
            this.sendExecuteStatement(sb.toString());
            JOptionPane.showMessageDialog(MainFrame.mainFrame, "The transaction is completed", "Information", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException | ClassNotFoundException | HeadlessException e) {
            e.printStackTrace();
            if(this.showDialog() == 0)
                addNewVocabulary();
        }
        
    }
    
    /**
     * Добавляет элементы на панель
     * Добавляет слушателей для некоторых елементов
     */
    @Override
    public void doGui(){
        this.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        
        JLabel firstLanguageLabel = new JLabel("First Language");
        JLabel secondLanguageLabel = new JLabel("Second Language");
        JButton backButton = new JButton("Back");
        JButton addButton = new JButton("Add new vocabulary");
        
        MouseButtonsListener mouseButtonsListener = new MouseButtonsListener();
        backButton.addMouseListener(mouseButtonsListener);
        addButton.addMouseListener(mouseButtonsListener);
        
        firstLanguageField.setPreferredSize(new Dimension(150, 25));
        secondLanguageField.setPreferredSize(new Dimension(150, 25));
        
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
        this.add(firstLanguageField, gridBagConstraints);
        
        gridBagConstraints.gridx = 1;
        this.add(secondLanguageField, gridBagConstraints);
     
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        this.add(backButton, gridBagConstraints);
        
        gridBagConstraints.gridx = 1;
        this.add(addButton, gridBagConstraints);                                        
    }
}
