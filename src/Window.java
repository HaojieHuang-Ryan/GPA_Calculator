import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.math.BigDecimal;

public class Window extends JFrame
{
    public static File selectedFile = null;
    public static int numRow = 40;
    public static int numColumn = 5;
    public static String[] Name = new String[]{"Number", "Name", "Units", "Grade", "GPA"};
    public static String[][] Date = new String[numRow][numColumn];
    public static JTable table;
    public static JLabel stringTotal = new JLabel("Total units = ");
    public static JLabel stringRest = new JLabel("Rest units = ");
    
    public Window()
    {
        initialization();
        setVisible(true);
        setTitle("GPA Calculator");

        // Get user screen's width and height.
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();

        // Set GPA calculator window's width and height
        int winWidth = 640;
        int winHeight = 640;
        setBounds((screenSize.width - winWidth) / 2, (screenSize.height - winHeight) / 2, winWidth, winHeight);

        // Set whether the window size can be changed
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    void initialization()
    {
        // No default layout
        setLayout(null);

        // Divide the window into two parts, display the table and display the conclusion
        JPanel showTable = new JPanel();
        showTable.setLayout(null);
        showTable.setBounds(0,0,640,500);
        add(showTable);
        JPanel showResult = new JPanel();
        showResult.setLayout(null);
        showResult.setBounds(0,500,640,140);
        add(showResult);

        // Table
        for (int i = 0; i < numRow; i++)
        {
            Date[i][0] = String.valueOf(i + 1);
        }
        table = new JTable(Date, Name);
        table.setModel(new DefaultTableModel(Date, Name)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return column != 0 && column != 4;
            }
        });
        table.setAutoCreateRowSorter(true);

        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setSize(640,500);
        showTable.add(scrollPane);

        // Button
        JButton import_Button = new JButton("Import");
        import_Button.setBounds(10,0,150,30);
        showResult.add(import_Button);
        JButton save_Button = new JButton("Save");
        save_Button.setBounds(245,0,150,30);
        showResult.add(save_Button);
        JButton calculate_Button = new JButton("Calculate");
        calculate_Button.setBounds(480,0,150,30);
        showResult.add(calculate_Button);

        // Label
        stringTotal.setBounds(250,35,640,30);
        showResult.add(stringTotal);
        stringRest.setBounds(250,65,640,30);
        showResult.add(stringRest);

        // Button reaction
        // Import
        import_Button.addActionListener(import_Button1 ->
        {
            try
            {
                ImportFunction();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        });

        // Save
        save_Button.addActionListener(save_Button2 ->
        {
            try
            {
                SaveFunction();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        });

        // Calculate
        calculate_Button.addActionListener(calculate_Button3 ->
                CalculateFunction());
    }

    void ImportFunction() throws IOException
    {
        JFileChooser importChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.gpa","gpa");
        importChooser.setFileFilter(filter);
        File current_directory = new File(System.getProperty("user.dir"));//get current directory
        importChooser.setCurrentDirectory(current_directory);//set current directory to file chooser
        importChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (JFileChooser.APPROVE_OPTION == importChooser.showOpenDialog(table))
        {
            for (int i = 0; i < numRow; i++)
            {
                for (int j = 1; j < 5; j++)
                {
                    table.getModel().setValueAt(null, i, j);
                }
            }
            selectedFile = importChooser.getSelectedFile();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(selectedFile));
            for (int i = 0; i < numRow; i++)
            {
                for (int j = 1; j < 4; j++)
                {
                    table.getModel().setValueAt(bufferedReader.readLine(), i, j);
                }
            }
            setTitle("GPA Calculator" + "(" + selectedFile.getName() + ")");
            table.repaint();
        }
    }

    void SaveFunction() throws IOException
    {
        JFileChooser saveChooser = new JFileChooser();
        File current_directory = new File(System.getProperty("user.dir"));
        saveChooser.setCurrentDirectory(current_directory);//set current directory to file chooser
        saveChooser.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
        if (selectedFile == null)
        {
            saveChooser.setSelectedFile(new File("temp.gpa"));
        }
        else
        {
            saveChooser.setSelectedFile(selectedFile);
        }
        if (JFileChooser.APPROVE_OPTION == saveChooser.showSaveDialog(table))
        {
            if (saveChooser.getSelectedFile().getName().contains("."))
            {
                if (!saveChooser.getSelectedFile().getName().contains(".gpa"))
                {
                    JOptionPane.showMessageDialog(null, "Error file name");
                    return;
                }
                else
                {
                    selectedFile = saveChooser.getSelectedFile();
                }
            }
            else
            {
                selectedFile = new File(saveChooser.getCurrentDirectory(),saveChooser.getSelectedFile().getName() + ".gpa");
            }
            OutputStream outFile = new FileOutputStream(selectedFile);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outFile));
            for (int i = 0; i < numRow; i++)
            {
                for (int j = 1; j < 4; j++)
                {
                    if (table.getModel().getValueAt(i,j) != null)
                    {
                        bufferedWriter.write(table.getModel().getValueAt(i,j).toString());
                        bufferedWriter.newLine();
                    }
                }
            }
            JOptionPane.showMessageDialog(null, "Saved successfully");
            bufferedWriter.close();
            outFile.close();
        }
    }

    void CalculateFunction()
    {
        for (int i = 0; i < numRow; i++)
        {
            for (int j = 1; j < 4; j++)
            {
                if (table.getModel().getValueAt(i,j) != null)
                {
                    table.getModel().setValueAt(table.getModel().getValueAt(i,j).toString().toUpperCase(), i, j);
                }
            }
        }
        double gradeCredit = 0;
        double totalCredit = 0;
        int credit = 0;
        for (int i = 0; i < numRow; i++)
        {
            if (table.getModel().getValueAt(i,1) != null && table.getModel().getValueAt(i,2) != null && table.getModel().getValueAt(i,3) != null)
            {
                switch (table.getModel().getValueAt(i, 3).toString())
                {
                    case "A+" -> gradeCredit += 4.33 * Integer.parseInt(table.getModel().getValueAt(i, 2).toString());
                    case "A" -> gradeCredit += 4.00 * Integer.parseInt(table.getModel().getValueAt(i, 2).toString());
                    case "A-" -> gradeCredit += 3.67 * Integer.parseInt(table.getModel().getValueAt(i, 2).toString());
                    case "B+" -> gradeCredit += 3.33 * Integer.parseInt(table.getModel().getValueAt(i, 2).toString());
                    case "B" -> gradeCredit += 3.00 * Integer.parseInt(table.getModel().getValueAt(i, 2).toString());
                    case "B-" -> gradeCredit += 2.67 * Integer.parseInt(table.getModel().getValueAt(i, 2).toString());
                    case "C+" -> gradeCredit += 2.33 * Integer.parseInt(table.getModel().getValueAt(i, 2).toString());
                    case "C" -> gradeCredit += 2.00 * Integer.parseInt(table.getModel().getValueAt(i, 2).toString());
                    case "C-" -> gradeCredit += 1.67 * Integer.parseInt(table.getModel().getValueAt(i, 2).toString());
                    case "D" -> gradeCredit += Integer.parseInt(table.getModel().getValueAt(i, 2).toString());
                    case "F", "P" -> gradeCredit += 0;
                }
                //
                if (table.getModel().getValueAt(i, 3).toString().equals("P"))
                {
                    totalCredit += 0;
                }
                else
                {
                    totalCredit += Integer.parseInt(table.getModel().getValueAt(i,2).toString());
                }
                double result = new BigDecimal(gradeCredit / totalCredit).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                table.getModel().setValueAt(result, i, 4);
                credit += Integer.parseInt(table.getModel().getValueAt(i,2).toString());
            }
        }
        table.repaint();
        stringTotal.setText("Total units = "+credit);
        stringRest.setText("Rest units = "+(120-credit));
    }
}


