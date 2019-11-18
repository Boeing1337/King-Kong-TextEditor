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

final public class TextEditor extends JFrame {

    private enum Direction {
        NEXT,
        PREVIOUS,
    }

    final private JTextArea jTextArea = new JTextArea();
    final private JCheckBox checkBoxRegEx = new JCheckBox();
    final private JTextField fieldSearch = new JTextField();

    private CycleIterator<Coincidence> iteratorTextMatch;
    private Coincidence matchObject;
    private JFileChooser jfc;

    private TextEditor() {
        SwingUtilities.invokeLater(this::createUI); //i don't know what is :: /// new runnable (Dispatch thread)
    }

    private void createUI() {
        final ImageIcon windowIcon = new ImageIcon("C:\\icon.png");
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

        jTextArea.setName("TextArea");

        final JScrollPane textArea = new JScrollPane(jTextArea);
        textArea.setName("ScrollPane");
        textArea.setBounds(10, 40, super.getWidth() - 25, super.getHeight() / 100 * 90);
        textArea.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        textArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(textArea);

/////////////////////////////////////JPANEL TOP

        checkBoxRegEx.setName("UseRegExCheckbox");
        checkBoxRegEx.setBackground(Color.LIGHT_GRAY);
        checkBoxRegEx.setBounds(595, 14, 18, 13);

        final JLabel labelRegEx = new JLabel("REG EX");
        labelRegEx.setBounds(545, 8, 50, 25);////twice label for BOLD text
        JLabel labelRegEx2 = new JLabel("REG EX");
        labelRegEx.setBounds(545, 8, 50, 25);

        fieldSearch.setName("SearchField");
        fieldSearch.setBounds(115, 8, super.getWidth() / 100 * 45, 25);

        final JButton buttonSave = new JButton();
        ImageIcon iconSave = new ImageIcon("C:\\save.png");
        buttonSave.setIcon(iconSave);
        buttonSave.setName("SaveButton");
        buttonSave.setToolTipText("Quick save");
        buttonSave.setBounds(10, 2, 38, 38);
        buttonSave.addActionListener(e -> {
            if (jfc.getSelectedFile() == null) {
                getPathToSave();
            }
            saveTextToFile();
        });

        final JButton buttonOpen = new JButton();
        ImageIcon iconLoad = new ImageIcon("C:\\load.png");
        buttonOpen.setIcon(iconLoad);
        buttonOpen.setName("OpenButton");
        buttonOpen.setToolTipText("Open file");
        buttonOpen.setBounds(50, 2, 38, 38);
        buttonOpen.addActionListener(e -> {
            openTextFromFile();
        });

        final JButton buttonSearch = new JButton();
        ImageIcon iconSearch = new ImageIcon("C:\\search.png");
        buttonSearch.setIcon(iconSearch);
        buttonSearch.setName("StartSearchButton");
        buttonSearch.setToolTipText("Start search");
        buttonSearch.setBounds(400, 2, 38, 38);
        buttonSearch.addActionListener(e -> {
            clearMatchesInfo();
            if (!jTextArea.getText().isEmpty()) {
                ///Method possibleToSelect will not work for this way
                createCoincidences();
            }
        });

        final JButton buttonSearchPrevious = new JButton();
        ImageIcon iconSearchPrevious = new ImageIcon("C:\\previous.png");
        buttonSearchPrevious.setIcon(iconSearchPrevious);
        buttonSearchPrevious.setName("PreviousMatchButton");
        buttonSearchPrevious.setToolTipText("Previous");
        buttonSearchPrevious.setBounds(450, 2, 38, 38);
        buttonSearchPrevious.addActionListener(e -> {
            if (isPossibleToSearch()) {
                highlightMatch(Direction.PREVIOUS);
            }
        });

        final JButton buttonSearchNext = new JButton();
        ImageIcon iconSearchNext = new ImageIcon("C:\\next.png");
        buttonSearchNext.setIcon(iconSearchNext);
        buttonSearchNext.setName("NextMatchButton");
        buttonSearchNext.setToolTipText("Next");
        buttonSearchNext.setBounds(500, 2, 38, 38);
        buttonSearchNext.addActionListener(e -> {
            if (isPossibleToSearch()) {
                highlightMatch(Direction.NEXT);
            }
        });


        final JPanel topPanel = new JPanel();
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

        final JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

//***********OPTION IN MENU::FILE*******************:
        final JMenu file = new JMenu("FIle");
        file.setName("MenuFile");
        file.setMnemonic(KeyEvent.VK_F);
        menuBar.add(file);

        final JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.setName("MenuOpen");
        openMenuItem.addActionListener(e -> {
            openTextFromFile();
        });

        final JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setName("MenuSave");
        saveMenuItem.addActionListener(e -> {
            getPathToSave();
            saveTextToFile();
        });

        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setName("MenuExit");
        exitMenuItem.addActionListener(e -> {
            System.exit(-2);
        });

        file.add(openMenuItem);
        file.add(saveMenuItem);
        file.addSeparator();
        file.add(exitMenuItem);

//***********OPTION IN MENU::SEARCH*******************:
        final JMenu search = new JMenu("Search");
        search.setName("MenuSearch");
        search.setMnemonic(KeyEvent.VK_S);
        menuBar.add(search);

        final JMenuItem searchStart = new JMenuItem("Start search");
        searchStart.setName("MenuStartSearch");
        search.add(searchStart);
        searchStart.addActionListener(e -> {
            clearMatchesInfo();
            if (!jTextArea.getText().isEmpty()) {
                ///Method possibleToSelect will not work for this way
                createCoincidences();
            }
        });

        final JMenuItem searchPreviousMatch = new JMenuItem("Previous match");
        searchPreviousMatch.setName("MenuPreviousMatch");
        search.add(searchPreviousMatch);
        searchPreviousMatch.addActionListener(e -> {
            if (isPossibleToSearch()) {
                highlightMatch(Direction.NEXT);
            }
        });

        final JMenuItem searchNextMatch = new JMenuItem("Next match");
        searchNextMatch.setName("MenuNextMatch");
        search.add(searchNextMatch);
        searchNextMatch.addActionListener(e -> {
            if (isPossibleToSearch()) {
                highlightMatch(Direction.NEXT);
            }
        });

        final JMenuItem searchRegEx = new JMenuItem("Use regular Exp");
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
            final FileWriter save = new FileWriter(jfc.getSelectedFile());
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
            final File selectedFile = jfc.getSelectedFile();
            final String path = selectedFile.getAbsolutePath();
            try {
                jTextArea.setText(Files.readString(Paths.get(path)));
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("uncorrected open file");
            }
        }
    }

    private void clearMatchesInfo() {
        matchObject = null;
        iteratorTextMatch = null;
    }

    private String stringEscaping() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < fieldSearch.getText().length(); i++) {
            String x = String.valueOf(fieldSearch.getText().charAt(i));
            if (!x.matches("[\\h]") && x.matches("[\\W]")) {
                buffer.append('\\');
            }
            buffer.append(x);
        }
        return buffer.toString();
    }

    private void createCoincidences() {
        if (!checkBoxRegEx.isSelected()) {
            new BackgroundWork(stringEscaping()).execute();
        } else {
            new BackgroundWork(fieldSearch.getText()).execute();
        }
    }

    private void highlightMatch(Direction dir) {
        if ("NEXT".equals(dir.name())) {
            matchObject = iteratorTextMatch.next();
        }
        if ("PREVIOUS".equals(dir.name())) {

            matchObject = iteratorTextMatch.previous();
        }

        if (isGroupNotChanged()) {
            jTextArea.setCaretPosition(matchObject.getEnd());
            jTextArea.select(matchObject.getFirst(), matchObject.getEnd());
            jTextArea.grabFocus();
        }
    }


    private boolean isGroupNotChanged() {
        String str = jTextArea.getText().substring(
                matchObject.getFirst(), matchObject.getEnd());
        return matchObject.getGroup().equals(str);
    }

    private boolean isPossibleToSearch() {
        return iteratorTextMatch != null && !jTextArea.getText().isEmpty() && !fieldSearch.getText().isEmpty();
    }


    private class BackgroundWork extends SwingWorker<Void, Object> {
        private String fieldText;

        BackgroundWork(String fieldText) {
            this.fieldText = fieldText;
        }

        @Override
        public Void doInBackground() {

            final Matcher matcher = Pattern.compile(fieldText).matcher(jTextArea.getText());
            final ArrayList<Coincidence> listOfCoincidence = new ArrayList<>();

            while (matcher.find()) {
                listOfCoincidence.add(new Coincidence(matcher.start(), matcher.end(), matcher.group()));
            }

            iteratorTextMatch = new CycleIterator<>(listOfCoincidence);

            highlightMatch(Direction.NEXT);///first call always 0;
            return null;
        }
    }

    private static class Coincidence {
        final private int first;
        final private int end;
        final private String group;

        Coincidence(final int first, final int end, final String group) {
            this.first = first;
            this.end = end;
            this.group = group;
        }

        int getFirst() {
            return first;
        }

        int getEnd() {
            return end;
        }

        String getGroup() {
            return group;
        }
    }

    private static class CycleIterator<T> implements ListIterator<T> {
        private final ArrayList<T> list;
        private int pos = -1;

        CycleIterator(ArrayList<T> list) {
            this.list = list;
        }

        @Override
        public boolean hasNext() {
            return pos != list.size() - 1;
        }

        @Override
        public T next() {
            if (hasNext()) {
                pos++;
                return list.get(pos);
            } else {
                pos = 0;
                return list.get(pos);
            }

        }

        @Override
        public boolean hasPrevious() {
            return pos != 0;
        }

        @Override
        public T previous() {
            if (hasPrevious()) {
                pos--;
                return list.get(pos);
            } else {
                pos = list.size() - 1;
                return list.get(pos);
            }
        }

        @Override
        public int nextIndex() {
            return 0;
        }

        @Override
        public int previousIndex() {
            return 0;
        }

        @Override
        public void remove() {
        }

        @Override
        public void set(Object o) {
        }

        @Override
        public void add(T o) {
            list.add(o);
        }
    }

    public static void main(final String[] args) {
        new TextEditor();
    }
}


