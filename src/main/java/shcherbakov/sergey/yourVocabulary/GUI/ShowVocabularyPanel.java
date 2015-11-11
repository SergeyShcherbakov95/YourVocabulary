package shcherbakov.sergey.yourVocabulary.GUI;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import shcherbakov.sergey.yourVocabulary.JDBC.CreatorJDBC;

/**
 * Выводит список слов для указаной таблицы 
 * @author sergey
 */
public class ShowVocabularyPanel extends ProgramPanel{
    
    /**
     * @return выборку которая содержит все слова в таблице
     */
    public ResultSet getWords(){
        ResultSet result = null;
        try {
            result = CreatorJDBC.getConnection().createStatement().executeQuery("SELECT id, " + this.getUserFirstLanguageString() + ", " + this.getUserSecondLanguageString() + 
                    " FROM " + this.getTableNameString());
        } catch (Exception e) {
            e.printStackTrace();
            if(this.showDialog() == 0)
                result = getWords();
        }
        return result;
    }
    
    /**
     * Добавляет элементы на панель.
     * Добавляет слушателей для некоторых елементов
     * Выводит слова в таблице
     */
    @Override
    public void doGui(){
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        
        JButton backButton = new JButton("Back");
        tablePanel.add(backButton, gridBagConstraints);
        backButton.addMouseListener(new MouseButtonsListener());
        
        gridBagConstraints.gridy = 1;
        
        try(ResultSet result = getWords();) {
            if(result == null)
                MainFrame.mainFrame.changePanel(new MainPanel());
            while(result.next()){
                for(int i = 0; i < 3; i++){
                    gridBagConstraints.gridx = i;
                    tablePanel.add(new JLabel(result.getString(i + 1)), gridBagConstraints);
                }
                gridBagConstraints.gridx++;
                gridBagConstraints.gridy++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(this.showDialog() == 0)
                this.doGui();
        }
        
        JScrollPane jScrollPane = new JScrollPane(tablePanel);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane.setPreferredSize(new Dimension(400, 170));
        this.add(jScrollPane);
    }
    
}
