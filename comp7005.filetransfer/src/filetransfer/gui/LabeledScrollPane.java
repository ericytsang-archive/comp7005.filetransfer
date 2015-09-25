package filetransfer.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import filetransfer.gui.listitem.ListItem;

public class LabeledScrollPane extends JPanel
{
    private final JLabel label;
    private final JPanel scrolledPanel;
    private final JScrollPane scrollPane;

    public LabeledScrollPane(Adapter adapter)
    {
        super();

        // initialize instance data
        label = new JLabel();
        scrolledPanel = new JPanel();
        scrollPane = new JScrollPane(scrolledPanel);

        // configure swing components
        label.setHorizontalAlignment(SwingConstants.LEFT);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setMinimumSize(new Dimension(0,0));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
        scrolledPanel.setLayout(new BoxLayout(scrolledPanel,BoxLayout.Y_AXIS));
        scrolledPanel.setMinimumSize(new Dimension(0,0));
        scrolledPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));

        // set the panel's layout manager & add components to the panel
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        add(label);
        add(scrollPane);

        // configure adapter, and populate list with default items from adapter
        adapter.setLabeledScrollPane(this);
        adapter.getInitialListItems().forEach(scrollPane::add);
    }

    public static abstract class Adapter
    {
        private LabeledScrollPane labeledScrollPane;

        public abstract List<ListItem> getInitialListItems();
        public abstract String getInitialTitle();

        public final void setLabeledScrollPane(LabeledScrollPane labeledScrollPane)
        {
            this.labeledScrollPane = labeledScrollPane;
            labeledScrollPane.getLabel().setText(getInitialTitle());
            getInitialListItems().forEach(labeledScrollPane.getScrolledPanel()::add);
        }

        public final void setTitle(String newTitle)
        {
            labeledScrollPane.getLabel().setText(newTitle);
        }

        public final void setListItems(List<ListItem> newListItems)
        {
            labeledScrollPane.getScrolledPanel().removeAll();
            newListItems.forEach(labeledScrollPane.getScrolledPanel()::add);
        }
    }

    public JLabel getLabel()
    {
        return label;
    }

    public JPanel getScrolledPanel()
    {
        return scrolledPanel;
    }
}
