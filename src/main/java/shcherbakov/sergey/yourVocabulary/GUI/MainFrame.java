package shcherbakov.sergey.yourVocabulary.GUI;

import java.awt.*;
import javax.swing.*;

/**
 * Содержит статическую переменную mainFrame. Вызовом метода changePanel(ProgramPanel programPanel) меняется содержимое mainFrame.
 * @author SergeyShcherbakov95
 */
public class MainFrame extends JFrame{
    /**
     * Главное окно в программе на которое добавляются элементы JPanel
     */
    public static MainFrame mainFrame = new MainFrame(); 
    
    private MainFrame(){
        this.setTitle("Your Vocabulary");
        this.setSize(500, 200);
        this.setLocationByPlatform(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
    
    /**
     * Удаляет все содерживое с mainFrame и добавляет новый элемент. Вызывает у нового элемента метод doGui()
     * @param programPanel элемент, который будет добавлен на mainFrame
     */
    public void changePanel(ProgramPanel programPanel){
        this.getContentPane().removeAll();
        this.add(programPanel, BorderLayout.CENTER);
        programPanel.doGui();
        this.invalidate();
        this.validate();
    }
}
