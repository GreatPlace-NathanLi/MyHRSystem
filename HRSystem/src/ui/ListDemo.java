package ui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class ListDemo extends JFrame 
                      implements ListSelectionListener {
    private JList list;
    private DefaultListModel listModel;

    private static final String hireString = "Hire";
    private static final String fireString = "Fire";
    private JButton fireButton;
    private JTextField employeeName;

    public ListDemo() {
        super("ListDemo");

        listModel = new DefaultListModel();
        listModel.addElement("Alison Huml");
        listModel.addElement("Kathy Walrath");
        listModel.addElement("Lisa Friendly");
        listModel.addElement("Mary Campione");

        //Create the list and put it in a scroll pane
        list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        JScrollPane listScrollPane = new JScrollPane(list);

        JButton hireButton = new JButton(hireString);
        hireButton.setActionCommand(hireString);
        hireButton.addActionListener(new HireListener());

        fireButton = new JButton(fireString);
        fireButton.setActionCommand(fireString);
        fireButton.addActionListener(new FireListener());

        employeeName = new JTextField(10);
        employeeName.addActionListener(new HireListener());
        String name = listModel.getElementAt(
                              list.getSelectedIndex()).toString();
        employeeName.setText(name);

        //Create a panel that uses FlowLayout (the default).
        JPanel buttonPane = new JPanel();
        buttonPane.add(employeeName);
        buttonPane.add(hireButton);
        buttonPane.add(fireButton);

        Container contentPane = getContentPane();
        contentPane.add(listScrollPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.SOUTH);
    }

    class FireListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //This method can be called only if
            //there's a valid selection
            //so go ahead and remove whatever's selected.
            int index = list.getSelectedIndex();
            listModel.remove(index);

            int size = listModel.getSize();

            if (size == 0) {
            //Nobody's left, disable firing.
                fireButton.setEnabled(false);

            } else {
            //Adjust the selection.
                if (index == listModel.getSize())//removed item in last position
                    index--;
                list.setSelectedIndex(index);   //otherwise select same index
            }
        }
    }

    //This listener is shared by the text field and the hire button
    class HireListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            //User didn't type in a name...
            if (employeeName.getText().equals("")) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }

            int index = list.getSelectedIndex();
            int size = listModel.getSize();

            //If no selection or if item in last position is selected,
            //add the new hire to end of list, and select new hire.
            if (index == -1 || (index+1 == size)) {
                listModel.addElement(employeeName.getText());
                list.setSelectedIndex(size);

            //Otherwise insert the new hire after the current selection,
            //and select new hire.
            } else {
                listModel.insertElementAt(employeeName.getText(), index+1);
                list.setSelectedIndex(index+1);
            }
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {

            if (list.getSelectedIndex() == -1) {
            //No selection, disable fire button.
                fireButton.setEnabled(false);
                employeeName.setText("");

            } else {
            //Selection, update text field.
                fireButton.setEnabled(true);
                String name = list.getSelectedValue().toString();
                employeeName.setText(name);
            }
        }
    }

    public static void main(String s[]) {
        JFrame frame = new ListDemo();

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        frame.pack();
        frame.setVisible(true);
    }
}
