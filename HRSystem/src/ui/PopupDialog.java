// PopupDialog
//Simple popup box to give users messages
package ui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PopupDialog extends JDialog implements ActionListener
{ 
    
    public PopupDialog(String title, String text) {//Constructo

       
        super( new Frame(""), title, true );//Create a framer
        setSize(300,100);
        setResizable( false );
        setLocation (240, 240);

        JLabel message = new JLabel(text, JLabel.CENTER);//Messenger
        getContentPane().add( message, BorderLayout.CENTER );

        JButton close = new JButton( " ¹Ø±Õ " );//close button
        JPanel p = new JPanel();
        close.addActionListener( this );
        p.add(close);
        getContentPane().add( p, BorderLayout.SOUTH );
        setVisible(true);
    }

    public void actionPerformed( ActionEvent e )
    {
        setVisible( false );
    }
}
