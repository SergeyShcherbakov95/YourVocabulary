package shcherbakov.sergey.yourVocabulary.GUI;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.sql.*;
import shcherbakov.sergey.yourVocabulary.JDBC.CreatorJDBC;

/**
 * Главная панель программы. 
 * @author SergeyShcherbakov95
 */
public class MainPanel extends ProgramPanel {
    /**
     * Все возможные комбинации языков из базы данных в формате
     * firstLanguage + " - " + secondLanguage
     * secondLanguage + " - " + firstLanguage
     */
    private Vector<String> languagesVector = new Vector<String>();
    private JComboBox languagesJComboBox = new JComboBox(languagesVector);
    
    /**
     * Конструктор без параметров.
     * Вызывает метод getLanguagesFromDatabase()
     */
    public MainPanel(){
        getLanguagesFromDatabase();
    }
    /**
     * @return первый язык, выбранный пользователем
     */
    public String getFirstLanguageValue(){
        String[] languages = ((String) languagesJComboBox.getSelectedItem()).split(" - ");
        return (String) languages[0];
    }
    
    /**
     * @return второй язык, выбранный пользователем
     */
    public String getSecondLanguageValue(){
        String[] languages = ((String) languagesJComboBox.getSelectedItem()).split(" - ");
        return (String) languages[1];
    }
    
    /**
     * Читает название всех таблиц из баззы данных и добавляет их в languagesVector в формате
     * firstLanguage + " - " + secondLanguage
     * secondLanguage + " - " + firstLanguage
     */
    public void getLanguagesFromDatabase(){
        try(Statement st = CreatorJDBC.getConnection().createStatement();
            ResultSet result = st.executeQuery("SHOW TABLES")){
            while(result.next()){
                String[] values = result.getString(1).split("Table");
                languagesVector.add(values[0] + " - " + values[1]);
                languagesVector.add(values[1] + " - " + values[0]);
            }
            languagesJComboBox.setSelectedIndex(0);
        } catch (Exception e) {
            e.printStackTrace();
            if(this.showDialog() == 0)
                getLanguagesFromDatabase();
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
        
        JButton startButton = new JButton("Start");
        JButton showAllWordsButton = new JButton("Show my vocabulary");
        JButton addWordButton = new JButton("Add word");
        JButton addVocabularyButton = new JButton("Add vocabulary");
        
        MouseButtonsListener mouseButtonsListener = new MouseButtonsListener();
        startButton.addMouseListener(mouseButtonsListener);
        showAllWordsButton.addMouseListener(mouseButtonsListener);
        addWordButton.addMouseListener(mouseButtonsListener);
        addVocabularyButton.addMouseListener(mouseButtonsListener);
        
        languagesJComboBox.setPreferredSize(new Dimension(200, 25));
        startButton.setPreferredSize(new Dimension(200, 25));
        showAllWordsButton.setPreferredSize(new Dimension(200, 25));
        addWordButton.setPreferredSize(new Dimension(200, 25));
        addVocabularyButton.setPreferredSize(new Dimension(200, 25));
       
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        this.add(languagesJComboBox, gridBagConstraints);
        
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.insets = new Insets(5, 0, 5, 0);
        this.add(startButton, gridBagConstraints);
        
        gridBagConstraints.gridy = 2;
        this.add(showAllWordsButton, gridBagConstraints);
        
        gridBagConstraints.gridy = 3;
        this.add(addWordButton, gridBagConstraints);
        
        gridBagConstraints.gridy = 4;
        this.add(addVocabularyButton, gridBagConstraints);
    }
}
