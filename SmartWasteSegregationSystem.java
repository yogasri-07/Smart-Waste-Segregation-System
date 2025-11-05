import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class SmartWasteSegregationSystem extends JFrame implements ActionListener, KeyListener {

    private JTextField txtWasteName, txtWeight;
    private JLabel lblDetectedType;
    private JComboBox<String> cmbType;
    private DefaultTableModel tableModel;

    public SmartWasteSegregationSystem() {
        setTitle("Smart Waste Segregation System");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Title
        JLabel lblTitle = new JLabel("Smart Waste Segregation System", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0, 128, 0));
        add(lblTitle, BorderLayout.NORTH);

        // Left panel for inputs
        JPanel inputPanel = new JPanel(new GridLayout(10, 1, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add Waste Details"));
        inputPanel.setPreferredSize(new Dimension(350, 0));

        inputPanel.add(new JLabel("Waste Name:"));
        txtWasteName = new JTextField();
        txtWasteName.addKeyListener(this);
        inputPanel.add(txtWasteName);

        inputPanel.add(new JLabel("Detected Type:"));
        lblDetectedType = new JLabel("Unknown");
        lblDetectedType.setForeground(Color.BLUE);
        inputPanel.add(lblDetectedType);

        inputPanel.add(new JLabel("Weight (kg):"));
        txtWeight = new JTextField();
        inputPanel.add(txtWeight);

        inputPanel.add(new JLabel("Select Type (if Unknown):"));
        cmbType = new JComboBox<>(new String[]{"Biodegradable", "Non-biodegradable"});
        inputPanel.add(cmbType);

        add(inputPanel, BorderLayout.WEST);

        // Table for output
        String[] columns = {"Name", "Type", "Weight (kg)"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for buttons
        JPanel bottomPanel = new JPanel();
        JButton btnAdd = new JButton("Add Waste");
        JButton btnShowStats = new JButton("Show Statistics");
        btnAdd.addActionListener(this);
        btnShowStats.addActionListener(this);
        bottomPanel.add(btnAdd);
        bottomPanel.add(btnShowStats);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // 🔍 Detect waste type using dataset
    public String detectWasteType(String wasteName) {
        wasteName = wasteName.toLowerCase();

        try (BufferedReader br = new BufferedReader(new FileReader("waste_data.csv"))) {
            String line;
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 3) {
                    String item = values[0].toLowerCase().trim();
                    String classification = values[2].trim();

                    // Partial match
                    if (wasteName.contains(item) || item.contains(wasteName)) {
                        return classification;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading dataset: " + e.getMessage());
        }
        return "Unknown";
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("Add Waste")) {
            String name = txtWasteName.getText().trim();
            String type = lblDetectedType.getText();
            if (type.equals("Unknown")) type = cmbType.getSelectedItem().toString();
            String weight = txtWeight.getText().trim();

            if (name.isEmpty() || weight.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            tableModel.addRow(new Object[]{name, type, weight});
            txtWasteName.setText("");
            txtWeight.setText("");
            lblDetectedType.setText("Unknown");
        } 
        else if (command.equals("Show Statistics")) {
            int biodegradable = 0, nonBiodegradable = 0;

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String type = tableModel.getValueAt(i, 1).toString();
                if (type.equalsIgnoreCase("Biodegradable")) biodegradable++;
                else nonBiodegradable++;
            }

            JOptionPane.showMessageDialog(this,
                    "Biodegradable: " + biodegradable + "\nNon-Biodegradable: " + nonBiodegradable,
                    "Waste Statistics", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == txtWasteName) {
            String detected = detectWasteType(txtWasteName.getText().trim());
            lblDetectedType.setText(detected);
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SmartWasteSegregationSystem::new);
    }
}
