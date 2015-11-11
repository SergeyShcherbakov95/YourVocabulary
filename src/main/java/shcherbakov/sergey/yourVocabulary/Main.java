package shcherbakov.sergey.yourVocabulary;

import java.awt.*;
import shcherbakov.sergey.yourVocabulary.GUI.*;

/**
 * В классе содержится только главный метод main, который добавляет MainPanel на mainFrame
 * @author SergeyShcherbakov95
 */
public class Main {
    /**
     * @param args не используется 
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            MainFrame.mainFrame.changePanel(new MainPanel());
        });
    }
}
