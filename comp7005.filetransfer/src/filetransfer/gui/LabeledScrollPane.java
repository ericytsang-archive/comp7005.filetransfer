package filetransfer.gui;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class LabeledScrollPane extends JPanel
{
    private final JLabel label;
    private final JScrollPane scrollPane;

    public LabeledScrollPane(String labelText)
    {
        super();

        // initialize instance data
        label = new JLabel();
        scrollPane = new JScrollPane();

        // configure swing components
        label.setText(labelText);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setMinimumSize(new Dimension(0,0));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
        scrollPane.setLayout(new BoxLayout(scrollPane,BoxLayout.Y_AXIS));

        // set the panel's layout manager & add components to the panel
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        add(label);
        add(scrollPane);
    }

    public JLabel getLabel()
    {
        return label;
    }

    public JScrollPane getScrollPane()
    {
        return scrollPane;
    }
}
