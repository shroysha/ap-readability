package dev.shroysha.ap.readability.view;

import dev.shroysha.ap.readability.model.ReadabilityDocument;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class ReadabilityFrame extends JFrame {

    JTabbedPane tabbedPane;
    private JTextArea copyArea;
    private File choosenFile;
    private JLabel choosenFileLabel;

    public ReadabilityFrame() {
        super("Readability");
        init();
    }



    private void init() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setBackground(Color.white);

        this.add(createTabbedPane(), BorderLayout.CENTER);

        this.setSize(400, 400);
    }

    private JTabbedPane createTabbedPane() {
        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Copy", createCopyPane());
        tabbedPane.addTab("File", createFilePane());
        tabbedPane.addTab("URL", createURLPane());

        return tabbedPane;
    }

    private JPanel createCopyPane() {
        JPanel copyPanel = new JPanel();
        copyPanel.setLayout(new BoxLayout(copyPanel, BoxLayout.Y_AXIS));
        copyPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        copyPanel.setBackground(Color.white);

        JLabel label = new JLabel("Copy the text into the box");
        label.setAlignmentX(Box.LEFT_ALIGNMENT);
        label.setMaximumSize(label.getPreferredSize());

        copyPanel.add(label);
        copyPanel.add(Box.createVerticalStrut(5));

        copyArea = new JTextArea();
        copyArea.setBorder(new EtchedBorder());
        copyArea.setAlignmentX(Box.LEFT_ALIGNMENT);

        copyPanel.add(copyArea);
        copyPanel.add(Box.createVerticalStrut(10));

        JButton confirmButton = new JButton("Get Readability!");
        confirmButton.setAlignmentX(Box.LEFT_ALIGNMENT);
        confirmButton.addActionListener(e -> {
            ReadabilityDocument document = createDocument(copyArea.getText().trim());
            showReadability(document);
        });

        copyPanel.add(confirmButton);

        return copyPanel;
    }

    private JPanel createFilePane() {
        JPanel filePanel = new JPanel();
        filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.Y_AXIS));
        filePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        filePanel.setBackground(Color.white);

        JLabel label = new JLabel("Find the file you want the readability of");
        label.setAlignmentX(Box.LEFT_ALIGNMENT);

        filePanel.add(label);
        filePanel.add(Box.createVerticalStrut(5));

        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> {
            choosenFile = browseForFile();
            if (choosenFile != null) {
                choosenFileLabel.setText(choosenFile.getAbsolutePath());
            } else {
                choosenFileLabel.setText("");
            }
        });

        filePanel.add(browseButton);
        filePanel.add(Box.createVerticalStrut(10));

        choosenFileLabel = new JLabel("");
        choosenFileLabel.setAlignmentX(Box.LEFT_ALIGNMENT);

        filePanel.add(choosenFileLabel);
        filePanel.add(Box.createVerticalStrut(10));

        JButton confirmButton = new JButton("Get Readability!");
        confirmButton.setAlignmentX(Box.LEFT_ALIGNMENT);
        confirmButton.addActionListener(e -> {
            try {
                if (choosenFile != null) {
                    ReadabilityDocument document = createDocument(choosenFile);
                    showReadability(document);
                } else {
                    throw new Exception("You must choose a file.");
                }
            } catch (Exception ex) {
                createErrorDialog(ex);
            }
        });

        filePanel.add(confirmButton);

        return filePanel;
    }

    private JPanel createURLPane() {
        final JButton confirmButton = new JButton("Get Readability!");

        JPanel urlPanel = new JPanel();
        urlPanel.setLayout(new BoxLayout(urlPanel, BoxLayout.Y_AXIS));
        urlPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        urlPanel.setBackground(Color.white);

        JLabel label = new JLabel("Enter the URL you want the readability of");
        label.setAlignmentX(Box.LEFT_ALIGNMENT);

        urlPanel.add(label);
        urlPanel.add(Box.createVerticalStrut(5));

        final JTextField urlField = new JTextField(5000);
        urlField.setAlignmentX(Box.LEFT_ALIGNMENT);
        urlField.setMaximumSize(urlField.getPreferredSize());
        urlField.addActionListener(ae -> confirmButton.doClick());

        urlPanel.add(urlField);
        urlPanel.add(Box.createVerticalStrut(10));

        confirmButton.setAlignmentX(Box.LEFT_ALIGNMENT);
        confirmButton.addActionListener(e -> {
            URL url;

            try {
                url = new URL(urlField.getText().trim());
                ReadabilityDocument document = createDocument(url);
                showReadability(document);
            } catch (IOException ex) {
                try {
                    url = new URL("http://" + urlField.getText().trim());
                    ReadabilityDocument document = createDocument(url);
                    showReadability(document);
                } catch (IOException ex1) {
                    createErrorDialog(ex1);
                }
            }

            urlField.requestFocus();
        });

        JButton gotoButton = new JButton("Go To");
        gotoButton.addActionListener(ae -> {
            String input = urlField.getText();
            try {
                Desktop.getDesktop().browse(new URI(input));
            } catch (Exception ex) {
                try {
                    input = "http://" + input;
                    Desktop.getDesktop().browse(new URI(input));
                } catch (Exception e) {
                    createErrorDialog(ex);
                }
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(confirmButton, BorderLayout.WEST);
        panel.add(gotoButton, BorderLayout.EAST);
        panel.setMaximumSize(panel.getPreferredSize());
        panel.setAlignmentX(Box.LEFT_ALIGNMENT);
        urlPanel.add(panel);
        urlPanel.add(Box.createVerticalStrut(10));
        urlPanel.add(Box.createVerticalGlue());

        return urlPanel;
    }

    private ReadabilityDocument createDocument(String text) {
        return new ReadabilityDocument(text);
    }

    private ReadabilityDocument createDocument(File file) throws FileNotFoundException {
        return new ReadabilityDocument(file);
    }

    private ReadabilityDocument createDocument(URL url) throws IOException {
        return new ReadabilityDocument(url);
    }

    private void showReadability(ReadabilityDocument document) {
        JOptionPane.showMessageDialog(this,
                "The readability of the " + document.getType() + " is " + document.getReadability(),
                "Readability",
                JOptionPane.PLAIN_MESSAGE);
    }

    private File browseForFile() {
        JFileChooser fc = new JFileChooser();
        int n = fc.showOpenDialog(this);
        if (n == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        } else {
            return null;
        }
    }

    private void createErrorDialog(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.toString(), "Error", JOptionPane.PLAIN_MESSAGE);
    }

}
