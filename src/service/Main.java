package service;

import adt.CircularLinkedList;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import model.Node;
import model.Student;

/**
 * Student Record Management System (Academic Premium Edition)
 * Powered by Circular Linked List ADT.
 */
public class Main extends JFrame {

    private static final String FILE_NAME = "students.csv";
    private static final CircularLinkedList list = new CircularLinkedList();
    private static boolean dataModified = false;

    // --- Design Tokens: Warm Editorial Academic Palette ---
    private static final Color COLOR_BG = new Color(0xF7, 0xF5, 0xF0);           // Warm Ivory Canvas
    private static final Color COLOR_CARD_BG = new Color(0xFF, 0xFF, 0xFF);      // Pure White Card
    private static final Color COLOR_OXFORD_BLUE = new Color(0x0D, 0x1E, 0x36);  // Oxford Navy Header
    private static final Color COLOR_OXFORD_HOVER = new Color(0x1B, 0x36, 0x5D); // Oxford Blue Hover
    private static final Color COLOR_OXFORD_LIGHT = new Color(0x22, 0x3A, 0x5E); // Slate Navy
    private static final Color COLOR_GOLD = new Color(0xB4, 0x82, 0x22);         // Academic Warm Gold
    private static final Color COLOR_GOLD_HOVER = new Color(0xC9, 0x94, 0x2D);   // Gold Hover
    private static final Color COLOR_DANGER = new Color(0xBE, 0x12, 0x3C);       // Rose-Crimson Danger
    private static final Color COLOR_DANGER_HOVER = new Color(0x9F, 0x12, 0x39); // Danger Hover
    private static final Color COLOR_BORDER = new Color(0xE2, 0xDD, 0xD2);       // Subtle Warm Border
    private static final Color COLOR_TEXT_DARK = new Color(0x1F, 0x29, 0x37);     // Primary Text
    private static final Color COLOR_TEXT_MUTED = new Color(0x6B, 0x72, 0x80);    // Secondary Text
    private static final Color COLOR_ROW_ALT = new Color(0xFC, 0xFB, 0xF9);      // Gentle Zebra Row
    private static final Color COLOR_SELECTED_BG = new Color(0xD8, 0xE8, 0xF8);  // Noticeable Rich Blue Selection
    private static final Color COLOR_SELECTED_FG = new Color(0x0B, 0x25, 0x45);  // Selected Text Color

    // Badge Colors
    private static final Color COLOR_DEANS_BG = new Color(0xE6, 0xF4, 0xEA);
    private static final Color COLOR_DEANS_TXT = new Color(0x13, 0x73, 0x33);
    private static final Color COLOR_FIRST_BG = new Color(0xEE, 0xF2, 0xFF);
    private static final Color COLOR_FIRST_TXT = new Color(0x1E, 0x40, 0xAF);
    private static final Color COLOR_SECOND_BG = new Color(0xFE, 0xF3, 0xC7);
    private static final Color COLOR_SECOND_TXT = new Color(0x92, 0x40, 0x0E);
    private static final Color COLOR_PASS_BG = new Color(0xF3, 0xF4, 0xF6);
    private static final Color COLOR_PASS_TXT = new Color(0x4B, 0x55, 0x63);

    // Fonts
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 17);
    private static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_METRIC_VALUE = new Font("Segoe UI", Font.BOLD, 22);

    // GUI Components
    private DefaultTableModel tableModel;
    private JTable studentTable;
    private TableRowSorter<DefaultTableModel> tableSorter;
    private JTextField searchField;

    // Status Bar & Toast Timer
    private JLabel statusBadgeLabel;
    private JLabel statusTextLabel;
    private JLabel statusTechLabel;
    private Timer toastTimer;

    // Metric Labels (Separate Value & Student Names to prevent truncation)
    private JLabel totalCountLabel;
    private JLabel avgCgpaLabel;
    private JLabel highestCgpaValLabel;
    private JLabel highestStudentNameLabel;
    private JLabel lowestCgpaValLabel;
    private JLabel lowestStudentNameLabel;

    // Selected Student Inspection Card
    private JLabel inspectTitleLabel;
    private JLabel inspectDetailLabel;

    // Progress Bars (12px rounded height)
    private RoundedDistributionBar deansBar;
    private RoundedDistributionBar firstClassBar;
    private RoundedDistributionBar secondClassBar;
    private RoundedDistributionBar passBar;

    public Main() {
        setTitle("Student Record Management System (CLL ADT) • Academic Premium Edition");
        setSize(1160, 760);
        setMinimumSize(new Dimension(1020, 660));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // Window closing confirmation
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });

        initUI();
        refreshAllData();
    }

    private void initUI() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(COLOR_BG);

        // 1. Top Header Bar
        rootPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Center Content: Left Table & Right Analytics Dashboard
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(COLOR_BG);
        contentPanel.setBorder(new EmptyBorder(14, 16, 12, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Left Roster Panel (61% width)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.61;
        gbc.insets = new Insets(0, 0, 0, 14);
        contentPanel.add(createRosterPanel(), gbc);

        // Right Analytics Panel (39% width)
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.39;
        gbc.insets = new Insets(0, 0, 0, 0);
        contentPanel.add(createAnalyticsPanel(), gbc);

        rootPanel.add(contentPanel, BorderLayout.CENTER);

        // 3. Bottom Status Bar with Toast support
        rootPanel.add(createStatusBar(), BorderLayout.SOUTH);

        setContentPane(rootPanel);
    }

    // ==========================================
    // 1. Top Header Component (Live Search)
    // ==========================================
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_OXFORD_BLUE);
        header.setBorder(new EmptyBorder(12, 20, 12, 20));

        // Brand / Title Area
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brandPanel.setOpaque(false);

        // Styled Academic Crest Badge (No Emojis to prevent tofu)
        JLabel crestBadge = new JLabel("SRMS", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_GOLD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        crestBadge.setPreferredSize(new Dimension(54, 34));
        crestBadge.setFont(new Font("Segoe UI", Font.BOLD, 14));
        crestBadge.setForeground(Color.WHITE);
        crestBadge.setOpaque(false);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("STUDENT RECORD MANAGEMENT SYSTEM");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("Circular Linked List (CLL) ADT Architecture • Interactive Sorter & Live Filter");
        subLabel.setFont(FONT_SUBTITLE);
        subLabel.setForeground(new Color(0xD1, 0xDA, 0xE6));

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(subLabel);

        brandPanel.add(crestBadge);
        brandPanel.add(textPanel);

        // Search Bar Area (Live Instant Filtering + Action Buttons)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        searchPanel.setOpaque(false);

        searchField = new JTextField(14);
        searchField.setFont(FONT_REGULAR);
        searchField.setPreferredSize(new Dimension(200, 32));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0x56, 0x73, 0x9B), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));
        searchField.setToolTipText("Type to filter records live by Name, ID, or Programme");

        // Live filtering listener (DocumentListener)
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyLiveSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyLiveSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyLiveSearch();
            }
        });

        ModernButton searchBtn = new ModernButton("Search", COLOR_GOLD, COLOR_GOLD_HOVER, Color.WHITE);
        searchBtn.setPreferredSize(new Dimension(80, 32));
        searchBtn.addActionListener(e -> applyLiveSearch());

        ModernButton resetBtn = new ModernButton("Reset", COLOR_OXFORD_LIGHT, new Color(0x35, 0x51, 0x7A), Color.WHITE);
        resetBtn.setPreferredSize(new Dimension(72, 32));
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            if (tableSorter != null) {
                tableSorter.setRowFilter(null);
            }
            studentTable.clearSelection();
            inspectTitleLabel.setText("No student selected");
            inspectDetailLabel.setText("Click on any row to inspect student profile");
            showToast("INFO", "Search filter cleared. Showing all " + list.getSize() + " records.");
        });

        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(resetBtn);

        header.add(brandPanel, BorderLayout.WEST);
        header.add(searchPanel, BorderLayout.EAST);

        return header;
    }

    private void applyLiveSearch() {
        String query = searchField.getText().trim();
        if (tableSorter == null) return;

        if (query.isEmpty()) {
            tableSorter.setRowFilter(null);
            showToast("INFO", "Showing all " + list.getSize() + " records.");
        } else {
            try {
                tableSorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(query)));
                int matchCount = studentTable.getRowCount();
                showToast("INFO", "Live Search: " + matchCount + " record(s) matching '" + query + "'.");
            } catch (Exception ex) {
                tableSorter.setRowFilter(null);
            }
        }
    }

    // ==========================================
    // 2. Left Roster & Table Component
    // ==========================================
    private JPanel createRosterPanel() {
        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout(0, 10));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);

        ModernButton addBtn = new ModernButton("+ Add Student", COLOR_OXFORD_BLUE, COLOR_OXFORD_HOVER, Color.WHITE);
        addBtn.addActionListener(e -> showAddStudentDialog());

        ModernButton deleteBtn = new ModernButton("Delete Selected", COLOR_DANGER, COLOR_DANGER_HOVER, Color.WHITE);
        deleteBtn.addActionListener(e -> executeDelete());

        ModernButton reverseBtn = new ModernButton("Reverse Inspector", new Color(0xF5, 0xF2, 0xEB), new Color(0xEA, 0xE4, 0xD8), COLOR_TEXT_DARK);
        reverseBtn.setBorderColor(new Color(0xD4, 0xCC, 0xBF));
        reverseBtn.addActionListener(e -> showReverseCopyInspector());

        ModernButton saveBtn = new ModernButton("Save to CSV", COLOR_GOLD, COLOR_GOLD_HOVER, Color.WHITE);
        saveBtn.addActionListener(e -> {
            saveToFile();
            dataModified = false;
            showToast("OK", "Successfully saved records to " + FILE_NAME);
            JOptionPane.showMessageDialog(this, "Records successfully saved to " + FILE_NAME,
                    "Save Complete", JOptionPane.INFORMATION_MESSAGE);
        });

        ModernButton resetSortBtn = new ModernButton("CLL Order", new Color(0xF5, 0xF2, 0xEB), new Color(0xEA, 0xE4, 0xD8), COLOR_TEXT_DARK);
        resetSortBtn.setBorderColor(new Color(0xD4, 0xCC, 0xBF));
        resetSortBtn.setToolTipText("Reset table sorting back to physical Circular Linked List order");
        resetSortBtn.addActionListener(e -> {
            if (tableSorter != null) {
                tableSorter.setSortKeys(null);
                showToast("INFO", "Reset to original CLL physical ascending order.");
            }
        });

        toolbar.add(addBtn);
        toolbar.add(deleteBtn);
        toolbar.add(reverseBtn);
        toolbar.add(saveBtn);
        toolbar.add(resetSortBtn);

        // Data Table
        String[] columnNames = {"Student ID", "Full Name", "Programme", "Age", "Academic Standing / CGPA"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Integer.class;
                if (columnIndex == 4) return Double.class;
                return String.class;
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setFont(FONT_REGULAR);
        studentTable.setRowHeight(36);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setGridColor(new Color(0xEE, 0xEA, 0xE2));
        studentTable.setShowVerticalLines(false);
        studentTable.setSelectionBackground(COLOR_SELECTED_BG);
        studentTable.setSelectionForeground(COLOR_SELECTED_FG);

        // Enable Table Header Sorting
        tableSorter = new TableRowSorter<>(tableModel);
        studentTable.setRowSorter(tableSorter);

        // Header Styling
        JTableHeader tableHeader = studentTable.getTableHeader();
        tableHeader.setFont(FONT_BOLD);
        tableHeader.setBackground(new Color(0xEF, 0xEB, 0xE3));
        tableHeader.setForeground(COLOR_TEXT_DARK);
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 38));
        tableHeader.setReorderingAllowed(false);
        tableHeader.setToolTipText("Click column header to sort ascending/descending");

        // Column Renderers & Width Adjustments (Prevents long programme truncation)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer leftPaddingRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 12, 0, 0));
                if (!isSelected) {
                    c.setBackground((row % 2 == 1) ? COLOR_ROW_ALT : COLOR_CARD_BG);
                    c.setForeground(COLOR_TEXT_DARK);
                } else {
                    c.setBackground(COLOR_SELECTED_BG);
                    c.setForeground(COLOR_SELECTED_FG);
                    setFont(FONT_BOLD);
                }
                return c;
            }
        };

        studentTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        studentTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        studentTable.getColumnModel().getColumn(1).setPreferredWidth(140);
        studentTable.getColumnModel().getColumn(1).setCellRenderer(leftPaddingRenderer);

        studentTable.getColumnModel().getColumn(2).setPreferredWidth(115); // Ample width for "Cybersecurity" & "Data Science"
        studentTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        studentTable.getColumnModel().getColumn(3).setPreferredWidth(55);
        studentTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        studentTable.getColumnModel().getColumn(4).setPreferredWidth(185);
        studentTable.getColumnModel().getColumn(4).setCellRenderer(new CgpaBadgeRenderer());

        // Table Selection Listener (Dynamic Feedback onto Right Dashboard)
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectedStudentProfile();
            }
        });

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.getViewport().setBackground(COLOR_CARD_BG);
        scrollPane.setBorder(new LineBorder(COLOR_BORDER, 1));

        card.add(toolbar, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private void updateSelectedStudentProfile() {
        int viewRow = studentTable.getSelectedRow();
        if (viewRow >= 0) {
            int modelRow = studentTable.convertRowIndexToModel(viewRow);
            String id = (String) tableModel.getValueAt(modelRow, 0);
            String name = (String) tableModel.getValueAt(modelRow, 1);
            String prog = (String) tableModel.getValueAt(modelRow, 2);
            int age = (Integer) tableModel.getValueAt(modelRow, 3);
            double cgpa = (Double) tableModel.getValueAt(modelRow, 4);

            String standing = getStandingLabel(cgpa);
            inspectTitleLabel.setText(name + " (" + id + ")");
            inspectDetailLabel.setText(String.format("Programme: %s  |  Age: %d  |  CGPA: %.2f (%s)", prog, age, cgpa, standing));
            showToast("INFO", "Selected student: " + name + " [CGPA: " + String.format("%.2f", cgpa) + "]");
        } else {
            inspectTitleLabel.setText("No student selected");
            inspectDetailLabel.setText("Click on any row in the roster to inspect details");
        }
    }

    private String getStandingLabel(double cgpa) {
        if (cgpa >= 3.75) return "Dean's List";
        if (cgpa >= 3.50) return "First Class";
        if (cgpa >= 3.00) return "Second Upper";
        return "Pass";
    }

    // ==========================================
    // 3. Right Live Analytics Component
    // ==========================================
    private JPanel createAnalyticsPanel() {
        JPanel card = createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Section Title
        JLabel titleLabel = new JLabel("Live Academic Performance Dashboard");
        titleLabel.setFont(FONT_HEADING);
        titleLabel.setForeground(COLOR_TEXT_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Aggregated live cohort analytics from StudentReport");
        subtitleLabel.setFont(FONT_SUBTITLE);
        subtitleLabel.setForeground(COLOR_TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 2x2 Metric Cards Grid (Distinct two-line labels to eliminate truncation)
        JPanel metricsGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        metricsGrid.setOpaque(false);
        metricsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        metricsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 175));

        totalCountLabel = new JLabel("0");
        avgCgpaLabel = new JLabel("0.00");

        highestCgpaValLabel = new JLabel("-");
        highestStudentNameLabel = new JLabel("None");

        lowestCgpaValLabel = new JLabel("-");
        lowestStudentNameLabel = new JLabel("None");

        metricsGrid.add(createSingleValueMetricBox("Total Enrolled", totalCountLabel, "Active Students"));
        metricsGrid.add(createSingleValueMetricBox("Cohort Average", avgCgpaLabel, "Mean CGPA"));
        metricsGrid.add(createDualValueMetricBox("Highest CGPA", highestCgpaValLabel, highestStudentNameLabel));
        metricsGrid.add(createDualValueMetricBox("Lowest CGPA", lowestCgpaValLabel, lowestStudentNameLabel));

        // Selected Student Inspection Card
        JPanel inspectCard = new JPanel();
        inspectCard.setLayout(new BoxLayout(inspectCard, BoxLayout.Y_AXIS));
        inspectCard.setBackground(new Color(0xF4, 0xF7, 0xFA));
        inspectCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0xD0, 0xDD, 0xEB), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        inspectCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        inspectCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel inspectHeader = new JLabel("STUDENT PROFILE INSPECTOR");
        inspectHeader.setFont(new Font("Segoe UI", Font.BOLD, 10));
        inspectHeader.setForeground(new Color(0x3B, 0x59, 0x82));

        inspectTitleLabel = new JLabel("No student selected");
        inspectTitleLabel.setFont(FONT_BOLD);
        inspectTitleLabel.setForeground(COLOR_OXFORD_BLUE);

        inspectDetailLabel = new JLabel("Click on any row in the roster to inspect details");
        inspectDetailLabel.setFont(FONT_SUBTITLE);
        inspectDetailLabel.setForeground(COLOR_TEXT_MUTED);

        inspectCard.add(inspectHeader);
        inspectCard.add(Box.createVerticalStrut(2));
        inspectCard.add(inspectTitleLabel);
        inspectCard.add(Box.createVerticalStrut(2));
        inspectCard.add(inspectDetailLabel);

        // Academic Distribution Section (12px rounded progress bars)
        JPanel distPanel = new JPanel();
        distPanel.setLayout(new BoxLayout(distPanel, BoxLayout.Y_AXIS));
        distPanel.setOpaque(false);
        distPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel distTitle = new JLabel("Academic Standing Distribution");
        distTitle.setFont(FONT_HEADING);
        distTitle.setForeground(COLOR_TEXT_DARK);
        distTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        deansBar = new RoundedDistributionBar("Dean's List (≥ 3.75)", new Color(0x16, 0xA3, 0x4A));
        firstClassBar = new RoundedDistributionBar("First Class (3.50 ~ 3.74)", new Color(0x25, 0x63, 0xEB));
        secondClassBar = new RoundedDistributionBar("Second Upper (3.00 ~ 3.49)", new Color(0xD9, 0x77, 0x06));
        passBar = new RoundedDistributionBar("Pass / Others (< 3.00)", new Color(0x64, 0x74, 0x8B));

        distPanel.add(distTitle);
        distPanel.add(Box.createVerticalStrut(10));
        distPanel.add(deansBar);
        distPanel.add(Box.createVerticalStrut(9));
        distPanel.add(firstClassBar);
        distPanel.add(Box.createVerticalStrut(9));
        distPanel.add(secondClassBar);
        distPanel.add(Box.createVerticalStrut(9));
        distPanel.add(passBar);

        // Console Report Action Button
        ModernButton printConsoleBtn = new ModernButton("Print Full Terminal Report", new Color(0xF5, 0xF2, 0xEB), new Color(0xEA, 0xE4, 0xD8), COLOR_OXFORD_BLUE);
        printConsoleBtn.setBorderColor(new Color(0x0D, 0x1E, 0x36));
        printConsoleBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        printConsoleBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        printConsoleBtn.addActionListener(e -> {
            StudentReport.generateReport(list);
            showToast("OK", "Generated formatted performance report in console stream.");
        });

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(2));
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(metricsGrid);
        card.add(Box.createVerticalStrut(10));
        card.add(inspectCard);
        card.add(Box.createVerticalStrut(12));
        card.add(new JSeparator(SwingConstants.HORIZONTAL));
        card.add(Box.createVerticalStrut(12));
        card.add(distPanel);
        card.add(Box.createVerticalGlue());
        card.add(printConsoleBtn);

        return card;
    }

    private JPanel createSingleValueMetricBox(String title, JLabel valueLabel, String note) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(new Color(0xFA, 0xF9, 0xF6));
        box.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLbl.setForeground(COLOR_TEXT_MUTED);

        valueLabel.setFont(FONT_METRIC_VALUE);
        valueLabel.setForeground(COLOR_OXFORD_BLUE);

        JLabel noteLbl = new JLabel(note);
        noteLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        noteLbl.setForeground(new Color(0x9C, 0xA3, 0xA0));

        box.add(titleLbl);
        box.add(Box.createVerticalStrut(1));
        box.add(valueLabel);
        box.add(Box.createVerticalStrut(1));
        box.add(noteLbl);

        return box;
    }

    private JPanel createDualValueMetricBox(String title, JLabel valueLabel, JLabel subNameLabel) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(new Color(0xFA, 0xF9, 0xF6));
        box.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLbl.setForeground(COLOR_TEXT_MUTED);

        valueLabel.setFont(FONT_METRIC_VALUE);
        valueLabel.setForeground(COLOR_OXFORD_BLUE);

        subNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        subNameLabel.setForeground(new Color(0x37, 0x41, 0x51));

        box.add(titleLbl);
        box.add(Box.createVerticalStrut(1));
        box.add(valueLabel);
        box.add(Box.createVerticalStrut(1));
        box.add(subNameLabel);

        return box;
    }

    // ==========================================
    // 4. Bottom Status Bar with Toast Feedback
    // ==========================================
    private JPanel createStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(0xEE, 0xEB, 0xE2));
        statusPanel.setBorder(new EmptyBorder(6, 16, 6, 16));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftPanel.setOpaque(false);

        statusBadgeLabel = new JLabel("[OK]");
        statusBadgeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusBadgeLabel.setForeground(new Color(0x15, 0x80, 0x3D));

        statusTextLabel = new JLabel("System Ready.");
        statusTextLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusTextLabel.setForeground(COLOR_TEXT_DARK);

        leftPanel.add(statusBadgeLabel);
        leftPanel.add(statusTextLabel);

        statusTechLabel = new JLabel("CLL Traversal Engine • Zero External Dependencies");
        statusTechLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusTechLabel.setForeground(COLOR_TEXT_MUTED);

        statusPanel.add(leftPanel, BorderLayout.WEST);
        statusPanel.add(statusTechLabel, BorderLayout.EAST);

        return statusPanel;
    }

    /**
     * Shows a toast message with automatic reset after 4 seconds
     */
    private void showToast(String type, String message) {
        if ("OK".equalsIgnoreCase(type)) {
            statusBadgeLabel.setText("[OK]");
            statusBadgeLabel.setForeground(new Color(0x15, 0x80, 0x3D));
        } else if ("WARN".equalsIgnoreCase(type)) {
            statusBadgeLabel.setText("[WARN]");
            statusBadgeLabel.setForeground(new Color(0xB4, 0x53, 0x09));
        } else {
            statusBadgeLabel.setText("[INFO]");
            statusBadgeLabel.setForeground(new Color(0x1D, 0x4E, 0xD8));
        }
        statusTextLabel.setText(message);

        if (toastTimer != null && toastTimer.isRunning()) {
            toastTimer.stop();
        }

        toastTimer = new Timer(4500, e -> {
            statusBadgeLabel.setText("[OK]");
            statusBadgeLabel.setForeground(new Color(0x15, 0x80, 0x3D));
            statusTextLabel.setText("System Ready • " + list.getSize() + " records in sorted CLL • Natural Order");
        });
        toastTimer.setRepeats(false);
        toastTimer.start();
    }

    // ==========================================
    // 5. Data Synchronization & Analytics Pipeline
    // ==========================================
    private void refreshAllData() {
        refreshTableAndDashboard();
        showToast("OK", "Loaded " + list.getSize() + " records from " + FILE_NAME + ". Strictly sorted ascending.");
    }

    private void refreshTableAndDashboard() {
        // 1. Populate Table
        tableModel.setRowCount(0);
        if (!list.isEmpty()) {
            Node current = list.getHead();
            do {
                Student s = current.data;
                tableModel.addRow(new Object[]{
                        s.getId(),
                        s.getName(),
                        s.getProgramme(),
                        s.getAge(),
                        s.getCgpa()
                });
                current = current.link;
            } while (current != list.getHead());
        }

        // 2. Compute live analytics (Directly reflects StudentReport calculation logic)
        int count = list.getSize();
        totalCountLabel.setText(String.valueOf(count));

        if (count == 0) {
            avgCgpaLabel.setText("0.00");
            highestCgpaValLabel.setText("-");
            highestStudentNameLabel.setText("None");
            lowestCgpaValLabel.setText("-");
            lowestStudentNameLabel.setText("None");
            deansBar.updateData(0, 0);
            firstClassBar.updateData(0, 0);
            secondClassBar.updateData(0, 0);
            passBar.updateData(0, 0);
            return;
        }

        double sumCgpa = 0.0;
        Node headNode = list.getHead();
        double maxCgpa = headNode.data.getCgpa();
        double minCgpa = headNode.data.getCgpa();
        Student highestStudent = headNode.data;
        Student lowestStudent = headNode.data;

        int deansCount = 0;
        int firstCount = 0;
        int secondCount = 0;
        int passCount = 0;

        Node current = headNode;
        do {
            Student s = current.data;
            double cgpa = s.getCgpa();
            sumCgpa += cgpa;

            if (cgpa > maxCgpa) {
                maxCgpa = cgpa;
                highestStudent = s;
            }
            if (cgpa < minCgpa) {
                minCgpa = cgpa;
                lowestStudent = s;
            }

            if (cgpa >= 3.75) {
                deansCount++;
            } else if (cgpa >= 3.50) {
                firstCount++;
            } else if (cgpa >= 3.00) {
                secondCount++;
            } else {
                passCount++;
            }

            current = current.link;
        } while (current != headNode);

        double avgCgpa = sumCgpa / count;
        avgCgpaLabel.setText(String.format("%.2f", avgCgpa));

        // Two-line layout avoids truncation
        highestCgpaValLabel.setText(String.format("%.2f", maxCgpa));
        highestStudentNameLabel.setText(highestStudent.getName());

        lowestCgpaValLabel.setText(String.format("%.2f", minCgpa));
        lowestStudentNameLabel.setText(lowestStudent.getName());

        deansBar.updateData(deansCount, count);
        firstClassBar.updateData(firstCount, count);
        secondClassBar.updateData(secondCount, count);
        passBar.updateData(passCount, count);
    }

    // ==========================================
    // 6. Interactive Feature Dialogs & Handlers
    // ==========================================
    private void showAddStudentDialog() {
        JDialog dialog = new JDialog(this, "Add New Student Record", true);
        dialog.setSize(440, 360);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(COLOR_BG);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        JTextField idField = new JTextField(15);
        JTextField nameField = new JTextField(15);
        JComboBox<String> progCombo = new JComboBox<>(new String[]{"CS", "SE", "IT", "Data Science", "Cybersecurity"});
        progCombo.setEditable(true);
        JTextField ageField = new JTextField(15);
        JTextField cgpaField = new JTextField(15);

        addFieldRow(panel, gbc, 0, "Student ID:", idField);
        addFieldRow(panel, gbc, 1, "Student Name:", nameField);
        addFieldRow(panel, gbc, 2, "Programme:", progCombo);
        addFieldRow(panel, gbc, 3, "Age (years):", ageField);
        addFieldRow(panel, gbc, 4, "CGPA (0.00 - 4.00):", cgpaField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        ModernButton cancelBtn = new ModernButton("Cancel", new Color(0xF5, 0xF2, 0xEB), new Color(0xEA, 0xE4, 0xD8), COLOR_TEXT_DARK);
        cancelBtn.setBorderColor(new Color(0xD4, 0xCC, 0xBF));
        cancelBtn.addActionListener(e -> dialog.dispose());

        ModernButton submitBtn = new ModernButton("Add Student", COLOR_OXFORD_BLUE, COLOR_OXFORD_HOVER, Color.WHITE);
        submitBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String prog = (String) progCombo.getSelectedItem();
            prog = (prog != null) ? prog.trim() : "";

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Student ID cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                idField.requestFocus();
                return;
            }
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Student Name cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                nameField.requestFocus();
                return;
            }

            int age;
            try {
                age = Integer.parseInt(ageField.getText().trim());
                if (age <= 0) throw new NumberFormatException();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Age must be a valid positive integer.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                ageField.requestFocus();
                return;
            }

            double cgpa;
            try {
                cgpa = Double.parseDouble(cgpaField.getText().trim());
                if (cgpa < 0.00 || cgpa > 4.00) throw new NumberFormatException();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "CGPA must be a valid number between 0.00 and 4.00.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                cgpaField.requestFocus();
                return;
            }

            boolean success = list.insertSorted(new Student(id, name, prog, age, cgpa));
            if (success) {
                dataModified = true;
                refreshTableAndDashboard();
                showToast("OK", "Successfully added student '" + name + "' into sorted list.");
                dialog.dispose();

                // Select newly added row
                for (int i = 0; i < studentTable.getRowCount(); i++) {
                    if (studentTable.getValueAt(i, 1).toString().equalsIgnoreCase(name)) {
                        studentTable.setRowSelectionInterval(i, i);
                        studentTable.scrollRectToVisible(studentTable.getCellRect(i, 0, true));
                        break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "A student named '" + name + "' already exists or is invalid.",
                        "Duplicate Entry", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(submitBtn);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 6, 0, 6);
        panel.add(btnPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void addFieldRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(FONT_REGULAR);
        lbl.setForeground(COLOR_TEXT_DARK);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 30));
        panel.add(field, gbc);
    }

    private void executeDelete() {
        int selectedRow = studentTable.getSelectedRow();
        String targetName;

        if (selectedRow >= 0) {
            targetName = studentTable.getValueAt(selectedRow, 1).toString();
        } else {
            targetName = JOptionPane.showInputDialog(this, "Enter the Student Name to delete:",
                    "Delete Student", JOptionPane.QUESTION_MESSAGE);
            if (targetName == null || targetName.trim().isEmpty()) {
                return;
            }
            targetName = targetName.trim();
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the record for '" + targetName + "'?\nThis action will unlink the node from Circular Linked List.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean deleted = list.delete(targetName);
            if (deleted) {
                dataModified = true;
                refreshTableAndDashboard();
                showToast("OK", "Record for '" + targetName + "' successfully unlinked and deleted.");
            } else {
                showToast("WARN", "Student '" + targetName + "' was not found in Circular Linked List.");
                JOptionPane.showMessageDialog(this, "Student '" + targetName + "' was not found in the list.",
                        "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void showReverseCopyInspector() {
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "The list is currently empty. Cannot generate reverse copy.",
                    "List Empty", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        CircularLinkedList reversed = list.createReverseCopy();

        JDialog dialog = new JDialog(this, "Circular Linked List • Reverse Copy Inspector", true);
        dialog.setSize(680, 440);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(COLOR_BG);

        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(16, 18, 16, 18));

        // Header info
        JPanel notePanel = new JPanel(new BorderLayout());
        notePanel.setBackground(new Color(0xEE, 0xF2, 0xFF));
        notePanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0xC7, 0xD2, 0xFE), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel infoTitle = new JLabel("Reverse Traversal Inspector (Independent Copy)");
        infoTitle.setFont(FONT_BOLD);
        infoTitle.setForeground(new Color(0x1E, 0x40, 0xAF));

        JLabel infoDesc = new JLabel("Generated via createReverseCopy(). The original circular linked list remains strictly sorted.");
        infoDesc.setFont(FONT_SUBTITLE);
        infoDesc.setForeground(new Color(0x3B, 0x82, 0xF6));

        notePanel.add(infoTitle, BorderLayout.NORTH);
        notePanel.add(infoDesc, BorderLayout.SOUTH);

        // Reversed Table
        String[] columns = {"Seq #", "Student ID", "Full Name", "Programme", "Age", "CGPA"};
        DefaultTableModel revModel = new DefaultTableModel(columns, 0);

        Node curr = reversed.getHead();
        int seq = 1;
        do {
            Student s = curr.data;
            revModel.addRow(new Object[]{
                    seq++,
                    s.getId(),
                    s.getName(),
                    s.getProgramme(),
                    s.getAge(),
                    String.format("%.2f", s.getCgpa())
            });
            curr = curr.link;
        } while (curr != reversed.getHead());

        JTable revTable = new JTable(revModel);
        revTable.setFont(FONT_REGULAR);
        revTable.setRowHeight(30);
        revTable.getTableHeader().setFont(FONT_BOLD);
        revTable.getTableHeader().setBackground(new Color(0xEF, 0xEB, 0xE3));

        JScrollPane scrollPane = new JScrollPane(revTable);
        scrollPane.setBorder(new LineBorder(COLOR_BORDER, 1));

        ModernButton closeBtn = new ModernButton("Close Inspector", COLOR_OXFORD_BLUE, COLOR_OXFORD_HOVER, Color.WHITE);
        closeBtn.addActionListener(e -> dialog.dispose());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(closeBtn);

        panel.add(notePanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void handleExit() {
        if (dataModified) {
            int ans = JOptionPane.showConfirmDialog(this,
                    "You have unsaved changes. Would you like to save records to " + FILE_NAME + " before exiting?",
                    "Save on Exit", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (ans == JOptionPane.YES_OPTION) {
                saveToFile();
                System.exit(0);
            } else if (ans == JOptionPane.NO_OPTION) {
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }

    // ==========================================
    // 7. File Persistence (Directly utilizes CircularLinkedList)
    // ==========================================
    private static void saveToFile() {
        if (list.isEmpty()) {
            System.out.println("List is empty. Nothing to save.");
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            Node current = list.getHead();
            do {
                bw.write(current.data.toCsv());
                bw.newLine();
                current = current.link;
            } while (current != list.getHead());

            System.out.println("Successfully saved " + list.getSize() + " records to " + FILE_NAME);
        } catch (IOException e) {
            System.out.println("Failed to write records to file: " + e.getMessage());
        }
    }

    private static void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("[Notice] No data file (" + FILE_NAME + ") found. Starting with empty list.");
            return;
        }

        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String prog = parts[2].trim();
                    int age = Integer.parseInt(parts[3].trim());
                    double cgpa = Double.parseDouble(parts[4].trim());

                    if (list.insertSorted(new Student(id, name, prog, age, cgpa))) {
                        count++;
                    }
                }
            }
            System.out.println("[Notice] Loaded " + count + " records from " + FILE_NAME);
        } catch (Exception e) {
            System.out.println("Error reading " + FILE_NAME + ": " + e.getMessage());
        }
    }

    // ==========================================
    // 8. Custom UI Widgets & Renderers
    // ==========================================
    private static JPanel createCardPanel() {
        JPanel card = new JPanel();
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));
        return card;
    }

    /**
     * Modern Custom-Painted Button
     * Solves Windows Look&Feel background overriding and renders crisp, vibrant styles.
     */
    public static class ModernButton extends JButton {
        private final Color normalBg;
        private final Color hoverBg;
        private Color borderColor;
        private boolean isHovered = false;

        public ModernButton(String text, Color bg, Color hover, Color fg) {
            super(text);
            this.normalBg = bg;
            this.hoverBg = hover;
            this.borderColor = null;

            setForeground(fg);
            setFont(FONT_BOLD);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(6, 14, 6, 14));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        public void setBorderColor(Color color) {
            this.borderColor = color;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Color currentBg = getModel().isPressed() ? normalBg.darker() : (isHovered ? hoverBg : normalBg);
            g2.setColor(currentBg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);

            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
            }

            // Center text rendering
            FontMetrics fm = g2.getFontMetrics(getFont());
            int textX = (getWidth() - fm.stringWidth(getText())) / 2;
            int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

            g2.setColor(getForeground());
            g2.setFont(getFont());
            g2.drawString(getText(), textX, textY);

            g2.dispose();
        }
    }

    /**
     * Custom Pill Badge Table Cell Renderer for CGPA
     */
    private static class CgpaBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            if (!(value instanceof Double)) {
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }

            double cgpa = (Double) value;
            String text;
            Color bgColor;
            Color txtColor;

            if (cgpa >= 3.75) {
                text = String.format("%.2f  Dean's List", cgpa);
                bgColor = COLOR_DEANS_BG;
                txtColor = COLOR_DEANS_TXT;
            } else if (cgpa >= 3.50) {
                text = String.format("%.2f  First Class", cgpa);
                bgColor = COLOR_FIRST_BG;
                txtColor = COLOR_FIRST_TXT;
            } else if (cgpa >= 3.00) {
                text = String.format("%.2f  Second Upper", cgpa);
                bgColor = COLOR_SECOND_BG;
                txtColor = COLOR_SECOND_TXT;
            } else {
                text = String.format("%.2f  Pass", cgpa);
                bgColor = COLOR_PASS_BG;
                txtColor = COLOR_PASS_TXT;
            }

            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(isSelected ? COLOR_SELECTED_BG : (row % 2 == 1 ? COLOR_ROW_ALT : COLOR_CARD_BG));

            JLabel badge = new JLabel(text, SwingConstants.CENTER) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(bgColor);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                    g2.setColor(bgColor.darker());
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
            badge.setForeground(txtColor);
            badge.setOpaque(false);
            badge.setBorder(new EmptyBorder(3, 10, 3, 10));

            panel.add(badge);
            return panel;
        }
    }

    /**
     * Enhanced Rounded Distribution Bar Component (12px height with rounded borders)
     */
    private static class RoundedDistributionBar extends JPanel {
        private final String labelText;
        private final Color barColor;
        private final JLabel textLabel;
        private final JLabel valueLabel;
        private int percent = 0;

        public RoundedDistributionBar(String labelText, Color barColor) {
            this.labelText = labelText;
            this.barColor = barColor;
            setOpaque(false);
            setLayout(new BorderLayout(0, 4));

            JPanel topRow = new JPanel(new BorderLayout());
            topRow.setOpaque(false);

            textLabel = new JLabel(labelText);
            textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            textLabel.setForeground(COLOR_TEXT_DARK);

            valueLabel = new JLabel("0 (0%)");
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            valueLabel.setForeground(barColor);

            topRow.add(textLabel, BorderLayout.WEST);
            topRow.add(valueLabel, BorderLayout.EAST);

            // Custom-painted 12px rounded track progress panel
            JPanel trackPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int w = getWidth();
                    int h = getHeight();

                    // Background track
                    g2.setColor(new Color(0xE7, 0xEA, 0xEE));
                    g2.fillRoundRect(0, 0, w, h, h, h);

                    // Fill progress
                    if (percent > 0) {
                        int fillW = Math.max(h, (int) (w * (percent / 100.0)));
                        g2.setColor(barColor);
                        g2.fillRoundRect(0, 0, fillW, h, h, h);
                    }
                    g2.dispose();
                }
            };
            trackPanel.setPreferredSize(new Dimension(100, 11));
            trackPanel.setOpaque(false);

            add(topRow, BorderLayout.NORTH);
            add(trackPanel, BorderLayout.SOUTH);
        }

        public void updateData(int count, int total) {
            this.percent = (total > 0) ? (int) Math.round(((double) count / total) * 100) : 0;
            valueLabel.setText(String.format("%d (%d%%)", count, percent));
            repaint();
        }
    }

    // ==========================================
    // 9. Main Entry Point
    // ==========================================
    public static void main(String[] args) {
        // Auto load existing records from file into CircularLinkedList
        loadFromFile();

        // Launch Modern GUI on Swing Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            Main frame = new Main();
            frame.setVisible(true);
        });
    }
}
