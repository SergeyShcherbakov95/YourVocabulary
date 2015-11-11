package shcherbakov.sergey.yourVocabulary.GUI;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import shcherbakov.sergey.yourVocabulary.JDBC.CreatorJDBC;

/**
 * Для пользователя выводится слово на языке указаном в userFirstLanguageString,
 * а пользователю нужно ввести перевод этого слова на язык указаный в userSecondLanguageString.
 * @author SergeyShcherbakov95
 */
public class StartPanel extends ProgramPanel{
    /**
     * Слово которое нужно перевести 
     */
    private String questionWordString;
    /**
     * Правильный перевод
     */
    private String correctAnswerWordString;
    /**
     * Количество слов в таблице 
     */
    private int numberOfWords;
    /**
     * Метка содержащая questionWordString или questionWordString + correctAnswerWordString
     */
    private JLabel questionWordLabel;
    /**
     * Поле для ответа
     */
    private JTextField answerField;
    private JButton addAnswerButton;
    
    /**
     * Конструктор по-умолчанию. Вызывает метод, который подсчитывает количество слов в таблице базы данных 
     */
    public StartPanel(){
        getNumberOfWords();
    }
    
    /**
     * Записывает в поле numberOfWords количество слов в таблице.
     * Если в таблице нет слов, выводит сообщение "Database is empty".
     * Количество слов в таблице это максимальное значение id в таблице
     */
    public void getNumberOfWords(){
        StringBuilder sb = new StringBuilder(20 + 30 + 5);
        
        sb.append("SELECT MAX(id) FROM ");
        sb.append(this.getTableNameString());
        try(Statement statement = CreatorJDBC.getConnection().createStatement();
                ResultSet result = statement.executeQuery(sb.toString())) {
            result.next();
            numberOfWords = result.getInt(1);
            if (numberOfWords == 0){
                JOptionPane.showMessageDialog(MainFrame.mainFrame, "Database is empty", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        } catch (SQLException | ClassNotFoundException | HeadlessException e){
            e.printStackTrace();
            if(this.showDialog() == 0)
                this.getNumberOfWords();
        }
    }
    
    /**
     * Получает случайное число от 1 до numberOfWords и делает выборку, где id = этому случайному числу.
     * Полученые значения присваивает соответсвующтм полям обьекта 
     */
    public void getQuestion(){
        addAnswerButton.setText("Answer");
        answerField.setText("");
        questionWordLabel.setForeground(Color.BLACK);
        int randomId = (int)(Math.random() * numberOfWords) + 1;
        if(numberOfWords != 0){
            try (Statement statement = CreatorJDBC.getConnection().createStatement();
                    ResultSet rs = statement.executeQuery("select * from " + this.getTableNameString() + " where id = " + randomId);){
                if(rs.next()){
                    questionWordString = rs.getString(this.getUserFirstLanguageString());
                    questionWordLabel.setText(questionWordString);
                    correctAnswerWordString = rs.getString(this.getUserSecondLanguageString());
                } else{
                    JOptionPane.showMessageDialog(MainFrame.mainFrame, "Database is not correct", "Information", JOptionPane.INFORMATION_MESSAGE);
                    makeDatabaseNormal();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if(this.showDialog() == 0)
                    this.getQuestion();
                }
            }
        else
            MainFrame.mainFrame.changePanel(new MainPanel());
    }
    
    /**
     * Проверяет или база данных в нормальном состоянии. 
     * Нормальное это когда значения первичного ключа идут в возрастающем порядке без пробелов.
     * Например 1, 2, 3, 4, 5 а не 1, 2, 4, 5
     * @return true - если база данных в нормальном состоянии, false - если не в нормальном состоянии
     * @throws SQLException
     * @throws ClassNotFoundException 
     */
    public boolean isDatabaseNormal() throws SQLException, ClassNotFoundException{
        try(ResultSet result = CreatorJDBC.getConnection().createStatement().executeQuery("SELECT * FROM " + this.getTableNameString());
        ResultSet maxId = CreatorJDBC.getConnection().createStatement().executeQuery("SELECT MAX(id) FROM " + this.getTableNameString())) {
            maxId.next();
            for(int i = 1; i < maxId.getInt(1); i++){
                result.next();
                if(result.getInt(1) != i)
                    return false;
            }
        } catch (Exception e) {
        }
        return true;
    }
    
    /**
     * Приводит базу данных в нормальное состояние.
     * Делает две выборки из базы данных. Первая все строки, а вторая максимальный id.
     * В цикле проходит от 1 до максимального id и сравнивает значения в столбце id с i,
     * Если id и i не совпадают то значение всех id, которые больше за i уменьшается на 1
     * Дальше выходит с цикла и опять проверяет или база данных в номальном состоянии, если нет, то повторяет все дуствия.
     */
    public void makeDatabaseNormal(){
        try {
            while(!isDatabaseNormal()){
                try(ResultSet result = CreatorJDBC.getConnection().createStatement().executeQuery("SELECT * FROM " + this.getTableNameString());
                        ResultSet maxId = CreatorJDBC.getConnection().createStatement().executeQuery("SELECT MAX(id) FROM " + this.getTableNameString());) {
                    maxId.next();
                    for(int i = 1; i < maxId.getInt(1); i++){
                        result.next();
                        if(result.getInt(1) != i){
                            this.sendExecuteStatement("UPDATE " + this.getTableNameString() + " set id = id - 1 where id > " + i);
                            break;
                        }
                    }
                } catch (Exception e) {
                    //NOP
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(this.showDialog() == 0)
                    this.makeDatabaseNormal();
                }
        getNumberOfWords();
    }
    
    /**
     * Сверяет ответ пользователя с правильным и добвляет к тексту метки questionWordLabel правильный ответ.
     * Если совпадает то меняет текст в метке на зеленый цвет, если не совпадает, то на красный
     */
    public void addAnswer(){
        String userAnswerString = answerField.getText().toUpperCase();
        if(userAnswerString.equals(correctAnswerWordString.toUpperCase())){
            questionWordLabel.setForeground(Color.green);
            this.incrementNumberOfAnswers(questionWordString, correctAnswerWordString);
        }
        else
            questionWordLabel.setForeground(Color.red);
        questionWordLabel.setText(questionWordString + " - " + correctAnswerWordString);
        addAnswerButton.setText("Next");
    }
    
    /**
     * добавляет 1 к значению в столбце numberOfAnswers этой пары слов
     * @param firstWord первое слово
     * @param secondWord второе слово
     */
    public void incrementNumberOfAnswers(String firstWord, String secondWord){
        StringBuilder sb = new StringBuilder(209 + 60 + 5);
            sb.append("UPDATE ");
            sb.append(this.getTableNameString());
            sb.append(" SET numberOfAnswers = numberOfAnswers + 1 where ");
            sb.append(this.getUserFirstLanguageString());
            sb.append(" = \'");
            sb.append(firstWord);
            sb.append("\' AND ");
            sb.append(this.getUserSecondLanguageString());
            sb.append(" = \'");
            sb.append(secondWord);
            sb.append("\'");
        try {
            this.sendExecuteStatement(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            if(this.showDialog() == 0)
                this.incrementNumberOfAnswers(firstWord, secondWord);
        }
    }
    
    /**
     * Добавляет элементы на панель.
     * Добавляет слушателей для некоторых елементов
     */
    public void doGui(){
        this.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        
        questionWordLabel = new JLabel(questionWordString);
        answerField = new JTextField();
        JLabel correctAnswerLabel = new JLabel("correctAnswer");
        JButton backButton = new JButton("Back");
        addAnswerButton = new JButton("Answer");
        
        MouseButtonsListener mouseButtonsListener = new MouseButtonsListener();
        backButton.addMouseListener(mouseButtonsListener);
        addAnswerButton.addMouseListener(mouseButtonsListener);
        
        answerField.setPreferredSize(new Dimension(150, 25));
        answerField.addActionListener((e) -> {
            actionAfterEvent(addAnswerButton.getText());
        });
        
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        this.add(questionWordLabel, gridBagConstraints);
        
        gridBagConstraints.gridy = 1;
        this.add( answerField, gridBagConstraints);
        
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 1;
        this.add(backButton, gridBagConstraints);
    
        gridBagConstraints.gridx = 1;
        this.add(addAnswerButton, gridBagConstraints);
        
        getQuestion();
    }
}
