import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

class NoteAppFrame extends JFrame {
    private final JTextArea noteArea;
    private final DefaultListModel<String> notesListModel;
    private final JList<String> notesList;
    private final ArrayList<File> notesFiles;
    private final File notesDirectory;

    public NoteAppFrame() {
        // Configure Frame
        setTitle("Note-Taking Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Notes Storage Directory
        notesDirectory = new File("Notes");
        if (!notesDirectory.exists()) {
            notesDirectory.mkdir();
        }

        // Notes List Panel
        notesListModel = new DefaultListModel<>();
        notesList = new JList<>(notesListModel);
        notesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        notesFiles = new ArrayList<>();

        loadNotes();

        notesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedNote();
            }
        });

        JScrollPane notesScrollPane = new JScrollPane(notesList);
        notesScrollPane.setPreferredSize(new Dimension(200, getHeight()));
        add(notesScrollPane, BorderLayout.WEST);

        // Note Area
        noteArea = new JTextArea();
        JScrollPane noteScrollPane = new JScrollPane(noteArea);
        add(noteScrollPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        add(buttonsPanel, BorderLayout.NORTH);

        JButton newButton = new JButton("New Note");
        newButton.addActionListener(e -> createNewNote());
        buttonsPanel.add(newButton);

        JButton saveButton = new JButton("Save Note");
        saveButton.addActionListener(e -> saveCurrentNote());
        buttonsPanel.add(saveButton);

        JButton deleteButton = new JButton("Delete Note");
        deleteButton.addActionListener(e -> deleteSelectedNote());
        buttonsPanel.add(deleteButton);

        setVisible(true);
    }

    private void loadNotes() {
        notesListModel.clear();
        notesFiles.clear();
        for (File file : notesDirectory.listFiles((dir, name) -> name.endsWith(".txt"))) {
            notesFiles.add(file);
            notesListModel.addElement(file.getName().replace(".txt", ""));
        }
    }

    private void loadSelectedNote() {
        int index = notesList.getSelectedIndex();
        if (index != -1) {
            File selectedFile = notesFiles.get(index);
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                noteArea.setText("");
                String line;
                while ((line = reader.readLine()) != null) {
                    noteArea.append(line + "\n");
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading note: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void createNewNote() {
        noteArea.setText("");
        notesList.clearSelection();
    }

    private void saveCurrentNote() {
        String noteTitle = JOptionPane.showInputDialog(this, "Enter Note Title:");
        if (noteTitle == null || noteTitle.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Note title cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File noteFile = new File(notesDirectory, noteTitle + ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(noteFile))) {
            writer.write(noteArea.getText());
            loadNotes();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving note: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedNote() {
        int index = notesList.getSelectedIndex();
        if (index != -1) {
            File selectedFile = notesFiles.get(index);
            if (selectedFile.delete()) {
                loadNotes();
                noteArea.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting note.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No note selected.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
