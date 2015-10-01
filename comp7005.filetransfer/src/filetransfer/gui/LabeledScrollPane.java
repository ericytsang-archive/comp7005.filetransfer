package filetransfer.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
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

    public LabeledScrollPane(Adapter adapter)
    {
        super();

        // initialize instance data
        label = new JLabel();
        scrolledPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(scrolledPanel);

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
    }

    public JLabel getLabel()
    {
        return label;
    }

    public JPanel getScrolledPanel()
    {
        return scrolledPanel;
    }

    public static abstract class Adapter
    {
        private LabeledScrollPane labeledScrollPane;

        private void setLabeledScrollPane(LabeledScrollPane labeledScrollPane)
        {
            this.labeledScrollPane = labeledScrollPane;
            onSetLabeledScrollPane();
        }

        protected abstract void onSetLabeledScrollPane();

        public final void setTitle(String newTitle)
        {
            labeledScrollPane.getLabel().setText(newTitle);
        }

        public final void setListItems(List<ListItem> newListItems)
        {
            labeledScrollPane.getScrolledPanel().removeAll();
            newListItems.forEach(labeledScrollPane.getScrolledPanel()::add);
            labeledScrollPane.setPreferredSize(new Dimension(0,0));
            getParentComponent().revalidate();
            getParentComponent().repaint();
        }

        public final Component getParentComponent()
        {
            if(labeledScrollPane.getRootPane() != null)
            {
                return labeledScrollPane.getRootPane();
            }
            else
            {
                return labeledScrollPane;
            }
        }
    }
}
