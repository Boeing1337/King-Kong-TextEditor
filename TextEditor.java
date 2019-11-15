import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditor extends JFrame {

    private JTextArea jTextArea;
    private JCheckBox checkBoxRegEx;
    private JTextField fieldSearch;
    private ListIterator<Coincidence> iteratorTextMatch = null;
    private JFileChooser jfc;

    public TextEditor() {
        SwingUtilities.invokeLater(this::createUI); //i don't know what is :: /// new runnable (Dispatch thread)
    }

    private void createUI() {
        ImageIcon windowIcon = new ImageIcon("C:\\icon.png");
        setIconImage(windowIcon.getImage());
        setTitle("First stage");
        setSize(640, 480);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); //to set the window on center a window
        setResizable(false);
        setLayout(null); ///layout(positions) elements in window

        jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setName("FileChooser");
        add(jfc);

////////////////////////////////////TEXT AREA

        jTextArea = new JTextArea();
        jTextArea.setName("TextArea");

        JScrollPane textArea = new JScrollPane(jTextArea);
        textArea.setName("ScrollPane");
        textArea.setBounds(10, 40, super.getWidth() - 25, super.getHeight() / 100 * 90);
        textArea.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        textArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(textArea);

/////////////////////////////////////JPANEL TOP

        checkBoxRegEx = new JCheckBox();
        checkBoxRegEx.setName("UseRegExCheckbox");
        checkBoxRegEx.setBackground(Color.LIGHT_GRAY);
        checkBoxRegEx.setBounds(595, 14, 18, 13);

        JLabel labelRegEx = new JLabel("REG EX");
        labelRegEx.setBounds(545, 8, 50, 25);////twice label for BOLD text
        JLabel labelRegEx2 = new JLabel("REG EX");
        labelRegEx.setBounds(545, 8, 50, 25);

        fieldSearch = new JTextField();
        fieldSearch.setName("SearchField");
        fieldSearch.setBounds(115, 8, super.getWidth() / 100 * 45, 25);

        JButton buttonSave = new JButton();
        ImageIcon iconSave = new ImageIcon("C:\\save.png");
        buttonSave.setIcon(iconSave);
        buttonSave.setName("SaveButton");
        buttonSave.setToolTipText("Quick save");
        buttonSave.setBounds(10, 2, 38, 38);
        buttonSave.addActionListener(e -> {
            if(jfc.getSelectedFile() == null) {
                getPathToSave();
            }
            saveTextToFile();
        });

        JButton buttonOpen = new JButton();
        ImageIcon iconLoad = new ImageIcon("C:\\load.png");
        buttonOpen.setIcon(iconLoad);
        buttonOpen.setName("OpenButton");
        buttonOpen.setToolTipText("Open file");
        buttonOpen.setBounds(50, 2, 38, 38);
        buttonOpen.addActionListener(e -> {
            openTextFromFile();
        });

        JButton buttonSearch = new JButton();
        ImageIcon iconSearch = new ImageIcon("C:\\search.png");
        buttonSearch.setIcon(iconSearch);
        buttonSearch.setName("StartSearchButton");
        buttonSearch.setToolTipText("Start search");
        buttonSearch.setBounds(400, 2, 38, 38);
        buttonSearch.addActionListener(e -> {
            clearMatchesInfo();
            if (!jTextArea.getText().isEmpty()) { ///Method possibleToSelect will not work for this way
                createCoincidences();
            }
        });

        JButton buttonSearchPrevious = new JButton();
        ImageIcon iconSearchPrevious = new ImageIcon("C:\\previous.png");
        buttonSearchPrevious.setIcon(iconSearchPrevious);
        buttonSearchPrevious.setName("PreviousMatchButton");
        buttonSearchPrevious.setToolTipText("Previous");
        buttonSearchPrevious.setBounds(450, 2, 38, 38);
        buttonSearchPrevious.addActionListener(e -> {
            if (isPossibleToSearch()) {
                highlightMatch("previous");
            }
        });

        JButton buttonSearchNext = new JButton();
        ImageIcon iconSearchNext = new ImageIcon("C:\\next.png");
        buttonSearchNext.setIcon(iconSearchNext);
        buttonSearchNext.setName("NextMatchButton");
        buttonSearchNext.setToolTipText("Next");
        buttonSearchNext.setBounds(500, 2, 38, 38);
        buttonSearchNext.addActionListener(e -> {
            if (isPossibleToSearch()) {
                highlightMatch("next");
            }
        });


        JPanel topPanel = new JPanel();
        topPanel.setLayout(null);
        topPanel.setBackground(Color.LIGHT_GRAY);
        topPanel.setBounds(0, 0, super.getWidth() - 15, 40);
        topPanel.add(buttonOpen);
        topPanel.add(buttonSave);
        topPanel.add(fieldSearch);
        topPanel.add(buttonSearch);
        topPanel.add(buttonSearchPrevious);
        topPanel.add(buttonSearchNext);
        topPanel.add(checkBoxRegEx);
        topPanel.add(labelRegEx);
        topPanel.add(labelRegEx2);///for BOLD words
        add(topPanel);

////////////////////////////////////////////MENU BAR

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

//***********OPTION IN MENU::FILE*******************:
        JMenu file = new JMenu("FIle");
        file.setName("MenuFile");
        file.setMnemonic(KeyEvent.VK_F);
        menuBar.add(file);

        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.setName("MenuOpen");
        openMenuItem.addActionListener(e -> {
            openTextFromFile();
        });

        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setName("MenuSave");
        saveMenuItem.addActionListener(e -> {
            getPathToSave();
            saveTextToFile();
        });

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setName("MenuExit");
        exitMenuItem.addActionListener(e -> {
            System.exit(-2);
        });

        file.add(openMenuItem);
        file.add(saveMenuItem);
        file.addSeparator();
        file.add(exitMenuItem);

//***********OPTION IN MENU::SEARCH*******************:
        JMenu search = new JMenu("Search");
        search.setName("MenuSearch");
        search.setMnemonic(KeyEvent.VK_S);
        menuBar.add(search);

        JMenuItem searchStart = new JMenuItem("Start search");
        searchStart.setName("MenuStartSearch");
        search.add(searchStart);
        searchStart.addActionListener(e -> {
            clearMatchesInfo();
            if (!jTextArea.getText().isEmpty()) { ///Method possibleToSelect will not work for this way
                createCoincidences();
            }
        });

        JMenuItem searchPreviousMatch = new JMenuItem("Previous match");
        searchPreviousMatch.setName("MenuPreviousMatch");
        search.add(searchPreviousMatch);
        searchPreviousMatch.addActionListener(e -> {
            if (isPossibleToSearch()) {
                highlightMatch("previous");
            }
        });

        JMenuItem searchNextMatch = new JMenuItem("Next match");
        searchNextMatch.setName("MenuNextMatch");
        search.add(searchNextMatch);
        searchNextMatch.addActionListener(e -> {
            if (isPossibleToSearch()) {
                highlightMatch("next");
            }
        });

        JMenuItem searchRegEx = new JMenuItem("Use regular Exp");
        searchRegEx.setName("MenuUseRegExp");
        search.add(searchRegEx);
        searchRegEx.addActionListener(e -> {
            if (checkBoxRegEx.isSelected()) {
                checkBoxRegEx.setSelected(false);
            } else {
                checkBoxRegEx.setSelected(true);
            }
        });
//***********END ENTITY SEARCH*******************:

        setVisible(true); //set visible
    }

    private void getPathToSave() {
        jfc.setDialogTitle("Choose save path");
        jfc.showSaveDialog(null);
    }

    private void saveTextToFile() {
        try {
            FileWriter save = new FileWriter(jfc.getSelectedFile());
            save.write(jTextArea.getText());
            save.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("uncorrected save path");
        }

    }

    private void openTextFromFile() {
        jTextArea.setText("");
        jfc.setDialogTitle("Choose open file");
        int returnValue = jfc.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            String path = selectedFile.getAbsolutePath();
            try {
                String texttext = Files.readString(Paths.get(path));
                jTextArea.setText(texttext);
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("uncorrected open file");
            }
        }
    }

    private void clearMatchesInfo() {
        next = false;
        previous = false;
        matchObject = null;
        iteratorTextMatch = null;
    }

    private String stringEscaping(String fieldText) {

        StringBuilder string = new StringBuilder();

        for (int i = 0; i < fieldText.length(); i++) {
            String x = String.valueOf(fieldText.charAt(i));
            if (!x.matches("[\\h]") && x.matches("[\\W]")) {
                string.append("\\");
            }
            string.append(x);
        }
        fieldText = string.toString();
        return fieldText;

    }


    private void createCoincidences() {
        String preparedSearchString = fieldSearch.getText();
        if (!checkBoxRegEx.isSelected()) {
            preparedSearchString = stringEscaping(preparedSearchString);
        }

        new BackgroundWork(preparedSearchString).execute();

    }

    private Coincidence matchObject;

    private void highlightMatch(String direction) {
        correctIterator(direction);

        if ("next".equals(direction)) {
            if (!iteratorTextMatch.hasNext()) {
                rollIterator(direction);
            }
            matchObject = iteratorTextMatch.next();
        }
        if ("previous".equals(direction)) {
            if (!iteratorTextMatch.hasPrevious()) {
                rollIterator(direction);
            }
            matchObject = iteratorTextMatch.previous();
        }

        if (isGroupNotChanged()) {
            jTextArea.setCaretPosition(matchObject.getEnd());
            jTextArea.select(matchObject.getFirst(), matchObject.getEnd());
            jTextArea.grabFocus();
            System.out.println(matchObject.getFirst() + " " + " " + matchObject.getEnd());
        }
    }

    private boolean next = false;
    private boolean previous = false;

    private void correctIterator(String direction) {
        if ("next".equals(direction)) {
            if (previous) {
                previous = false;
                iteratorTextMatch.next();
            }
            next = true;
        }
        if ("previous".equals(direction)) {
            if (next) {
                next = false;
                iteratorTextMatch.previous();
            }
            previous = true;
        }
    }

    private void rollIterator(String direction) {
        if ("next".equals(direction)) {
            while (iteratorTextMatch.hasPrevious()) {
                iteratorTextMatch.previous();
            }
        }
        if ("previous".equals(direction)) {
            while (iteratorTextMatch.hasNext()) {
                iteratorTextMatch.next();
            }
        }
    }

    private boolean isGroupNotChanged() {
        String group = jTextArea.getText().substring(matchObject.getFirst(), matchObject.getEnd());
        return matchObject.getCoincidence().equals(group);
    }

    private boolean isPossibleToSearch() {
        return iteratorTextMatch != null && !jTextArea.getText().isEmpty() && !fieldSearch.getText().isEmpty();
    }


    private class BackgroundWork extends SwingWorker<Void, Object> {
        String fieldText;


        BackgroundWork(String fieldText) {
            this.fieldText = fieldText;
        }

        @Override
        public Void doInBackground() {

            Matcher matcher = Pattern.compile(fieldText).matcher(jTextArea.getText());
            ArrayList<Coincidence> listOfCoincidence = new ArrayList<>();

            while (matcher.find()) {
                listOfCoincidence.add(new Coincidence(matcher.start(), matcher.end(), matcher.group()));
            }

            iteratorTextMatch = listOfCoincidence.listIterator();

            highlightMatch("next");///first call always next;
            return null;
        }

    }

    private static class Coincidence {
        private int first;
        private int end;
        private String coincidence;

        Coincidence(int first, int end, String coincidence) {
            this.first = first;
            this.end = end;
            this.coincidence = coincidence;
        }

        int getFirst() {
            return first;
        }

        int getEnd() {
            return end;
        }

        String getCoincidence() {
            return coincidence;
        }
    }

    public static void main(String[] args) {
        new TextEditor();
    }

}





