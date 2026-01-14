package gui;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;

import run.Main;

public class MainWindow extends JFrame {
    JLabel inputLabel = new JLabel();
    JLabel outputLabel = new JLabel();
    Path inputPath;
    Path outputPath;
    final String DESCRIPTION = """
            ターム分けシステム
            詳細 : https://github.com/KNTH-ENGINEER-2025-ALGORITHM/
            zenjyocon-termDividingWithMaxflow/blob/main/description.md
            """;
    JButton goButton = new JButton("実行");

    public static void main(String[] args) {
        MainWindow frame = new MainWindow();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(10, 10, 600, 400);
        frame.setTitle("ターム分け");
        frame.setVisible(true);
    }

    public MainWindow() {
        JPanel description = new JPanel();
        description.add(new JTextArea(DESCRIPTION));

        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.PAGE_AXIS));

        var inputDsc = new JLabel("入力ファイル: ");
        panel1.add(inputDsc);
        JButton inputButton = new JButton("参照");
        inputButton.addActionListener(new Input(this));
        panel1.add(inputButton);
        panel1.add(inputLabel);

        inputDsc.setAlignmentX(0.5f);
        inputButton.setAlignmentX(0.5f);
        inputLabel.setAlignmentX(0.5f);

        var outputDsc = new JLabel("出力ディレクトリ: ");
        panel1.add(outputDsc);
        JButton outputButton = new JButton("参照");
        outputButton.addActionListener(new Output(this));
        panel1.add(outputButton);
        panel1.add(outputLabel);

        outputDsc.setAlignmentX(0.5f);
        outputButton.setAlignmentX(0.5f);
        outputLabel.setAlignmentX(0.5f);

        JPanel goPanel = new JPanel();
        goButton.addActionListener(new RunButton(this));
        goPanel.add(goButton);

        getContentPane().add(description, BorderLayout.PAGE_START);
        getContentPane().add(panel1);
        getContentPane().add(goPanel, BorderLayout.SOUTH);
    }

    protected class Input implements ActionListener {
        JFrame jFrame;
        protected Input(JFrame jFrame) {
            this.jFrame = jFrame;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV ファイル", "csv"));
            fileChooser.setAcceptAllFileFilterUsed(false);

            int selected = fileChooser.showOpenDialog(jFrame);
            if (selected == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                if (file.exists() && file.isFile() && file.canRead()) {
                    inputPath = file.toPath();
                    inputLabel.setText("選択されたファイル: "+file.getName());
                } else {
                    inputLabel.setText("ファイルが見つからないか開けません");
                }
                
            } else {
                inputLabel.setText("");
            }
        }
    }

    protected class Output implements ActionListener {
        JFrame jFrame;
        protected Output(JFrame jFrame) {
            this.jFrame = jFrame;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int selected = fileChooser.showOpenDialog(jFrame);
            if (selected == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                if (file.exists() && file.isDirectory()) {
                    outputPath = file.toPath();
                    outputLabel.setText("選択されたディレクトリ: "+file.getName());
                } else {
                    outputLabel.setText("ディレクトリが見つかりません");
                }
                
            } else {
                outputLabel.setText("");
            }
        }
    }

    protected class RunButton implements ActionListener {
        JFrame jFrame;
        protected RunButton(JFrame jFrame) {
            this.jFrame = jFrame;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            Main.fromGui(jFrame, inputPath, outputPath, null);
            goButton.setVisible(false);
        }
        
    }
}
/*
参考:
https://www.javadrive.jp/tutorial/jfilechooser/
https://www.javadrive.jp/tutorial/boxlayout/

*/
