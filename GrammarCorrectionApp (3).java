package FinalProj;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.prefs.Preferences;

public class GrammarCorrectionApp extends JFrame {

    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JTextField textSizeField;
    private JComboBox<String> fontComboBox;
    private JComboBox<String> colorComboBox;
    private JCheckBox darkModeCheckBox;

    private int savedTextSize = 12;
    private String savedFont = "Arial";
    private Color savedTextColor = Color.BLACK;
    private boolean darkModeEnabled = false;

    private Preferences preferences;

    public GrammarCorrectionApp() {
        preferences = Preferences.userNodeForPackage(this.getClass());
        loadPreferences();  

        initializeUi();
    }

    private void loadPreferences() {
        savedTextSize = preferences.getInt("textSize", 12);
        savedFont = preferences.get("font", "Arial");
        int rgb = preferences.getInt("textColor", Color.BLACK.getRGB());
        savedTextColor = new Color(rgb);
        darkModeEnabled = preferences.getBoolean("darkMode", false);
    }

    private void savePreferences() {
        preferences.putInt("textSize", savedTextSize);
        preferences.put("font", savedFont);
        preferences.putInt("textColor", savedTextColor.getRGB());
        preferences.putBoolean("darkMode", darkModeEnabled);
    }

    private void initializeUi() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Grammar Correction App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(darkModeEnabled ? Color.DARK_GRAY : new Color(240, 240, 240));

        inputTextArea = new JTextArea("Enter your text here...");
        inputTextArea.setFont(new Font(savedFont, Font.PLAIN, savedTextSize));
        inputTextArea.setForeground(savedTextColor);
        inputTextArea.setBackground(darkModeEnabled ? Color.BLACK : Color.WHITE);
        inputTextArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if ("Enter your text here...".equals(inputTextArea.getText())) {
                    inputTextArea.setText("");
                    inputTextArea.setForeground(savedTextColor);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (inputTextArea.getText().trim().isEmpty()) {
                    inputTextArea.setText("Enter your text here...");
                    inputTextArea.setForeground(savedTextColor);
                }
            }
        });

        inputTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applySettings();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applySettings();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        inputTextArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                inputTextArea.setCaretPosition(0);
            }
        });

        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setBackground(darkModeEnabled ? Color.DARK_GRAY : new Color(240, 240, 240));
        outputTextArea.setForeground(savedTextColor);

        JButton correctButton = new JButton("Correct Grammar");
        JButton settingsButton = new JButton("Settings");

        styleButton(correctButton);
        styleButton(settingsButton);

        correctButton.addActionListener(e -> correctGrammar());

        settingsButton.addActionListener(e -> showSettingsDialog());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(settingsButton);
        buttonPanel.add(correctButton); // Add the correctButton to the buttonPanel

        textSizeField = new JTextField(Integer.toString(savedTextSize));
        fontComboBox = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        fontComboBox.setSelectedItem(savedFont);

        String[] colorOptions = {"Black", "Red", "Blue", "Green", "Yellow", "White"};
        colorComboBox = new JComboBox<>(colorOptions);
        colorComboBox.setSelectedItem(getColorName(savedTextColor));

        darkModeCheckBox = new JCheckBox("Dark Mode");
        darkModeCheckBox.setSelected(darkModeEnabled);
        darkModeCheckBox.addItemListener(e -> toggleDarkMode());

        add(new JScrollPane(inputTextArea), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputTextArea), BorderLayout.SOUTH);

        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);

        correctGrammar();
    }

    private void applySettings() {
        String textSizeString = textSizeField.getText();
        int textSize;

        try {
            textSize = Integer.parseInt(textSizeString);
            savedTextSize = textSize;
            savedFont = (String) fontComboBox.getSelectedItem();
            savePreferences();
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            return;
        }

        Font newFont = new Font(savedFont, Font.PLAIN, savedTextSize);
        inputTextArea.setFont(newFont);
        inputTextArea.setForeground(savedTextColor);
        inputTextArea.setBackground(darkModeEnabled ? Color.BLACK : Color.WHITE);

        outputTextArea.setForeground(savedTextColor);
        outputTextArea.setBackground(darkModeEnabled ? Color.DARK_GRAY : new Color(240, 240, 240));

        getContentPane().setBackground(darkModeEnabled ? Color.DARK_GRAY : new Color(240, 240, 240));
    }

    private void correctGrammar() {
        String inputText = inputTextArea.getText();
        String correctedText = fixGrammar(inputText);
        outputTextArea.setText(correctedText);
    }

    private String fixGrammar(String inputText) {
        String lowercaseInput = inputText.toLowerCase();

        // Capitalize the first letter of each sentence
        String[] sentences = lowercaseInput.split("\\. ");
        StringBuilder capitalizedInput = new StringBuilder();
        for (String sentence : sentences) {
            if (!sentence.isEmpty()) {
                String capitalizedSentence = sentence.substring(0, 1).toUpperCase() + sentence.substring(1);
                capitalizedInput.append(capitalizedSentence).append(". ");
            }
        }

        // Remove extra spaces
        String fixedInput = capitalizedInput.toString().trim().replaceAll(" +", " ");

        // Add a period at the end if missing
        if (!fixedInput.endsWith(".")) {
            fixedInput += ".";
        }

        return fixedInput;
    }

    private void showSettingsDialog() {
        JDialog settingsDialog = new JDialog(this, "Settings", true);
        settingsDialog.setLayout(new GridLayout(8, 2));

        textSizeField = new JTextField(Integer.toString(savedTextSize));
        fontComboBox = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        fontComboBox.setSelectedItem(savedFont);

        colorComboBox = new JComboBox<>(new String[]{"Black", "Red", "Blue", "Green", "Yellow", "White"});
        colorComboBox.setSelectedItem(getColorName(savedTextColor));

        darkModeCheckBox = new JCheckBox("Dark Mode");
        darkModeCheckBox.setSelected(darkModeEnabled);

        JLabel textColorLabel = new JLabel("Text Color:");

        JButton textColorButton = new JButton("Choose Color");
        textColorButton.addActionListener(e -> chooseTextColor());

        settingsDialog.add(new JLabel("Text Size:"));
        settingsDialog.add(textSizeField);
        settingsDialog.add(new JLabel("Font:"));
        settingsDialog.add(fontComboBox);
        settingsDialog.add(textColorLabel);
        settingsDialog.add(textColorButton);
        settingsDialog.add(new JLabel("Background:"));
        settingsDialog.add(darkModeCheckBox);

        JButton okButton = new JButton("OK");
        styleButton(okButton);
        okButton.addActionListener(e -> {
            applySettings();
            settingsDialog.dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton);
        cancelButton.addActionListener(e -> settingsDialog.dispose());

        settingsDialog.add(okButton);
        settingsDialog.add(cancelButton);

        settingsDialog.setSize(300, 250);
        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.setVisible(true);
    }

    private void chooseTextColor() {
        Color chosenColor = JColorChooser.showDialog(this, "Choose Text Color", savedTextColor);
        if (chosenColor != null) {
            savedTextColor = chosenColor;
            inputTextArea.setForeground(savedTextColor);
            colorComboBox.setSelectedItem(getColorName(savedTextColor));
            savePreferences();
        }
    }

    private void toggleDarkMode() {
        darkModeEnabled = darkModeCheckBox.isSelected();
        applySettings();
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(63, 81, 181));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 40));
        button.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private String getColorName(Color color) {
        if (color.equals(Color.BLACK)) return "Black";
        else if (color.equals(Color.RED)) return "Red";
        else if (color.equals(Color.BLUE)) return "Blue";
        else if (color.equals(Color.GREEN)) return "Green";
        else if (color.equals(Color.YELLOW)) return "Yellow";
        else if (color.equals(Color.WHITE)) return "White";
        else return "Black";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GrammarCorrectionApp());
    }
}
