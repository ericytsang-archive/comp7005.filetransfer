package filetransfer.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

/**
 * a scroll pane that is accompanied with a label above it giving some context
 *   to the user as to what is being displayed by the scroll pane.
 *
 * @file    LabeledScrollPane.java
 *
 * @program comp7005.filetransfer.jar
 *
 * @class   LabeledScrollPane
 *
 * @date    2015-10-02T09:56:03-0800
 *
 * @author  Eric Tsang
 */
public class LabeledScrollPane extends JPanel
{
    // constants: settings

    /**
     * the vertical scroll unit increment. larger number means the scroll pane
     *   scrolls faster; smaller number means it will scroll slower.
     */
    private static final int VERTICAL_SCROLL_UNIT_INCREMENT = 16;

    // instance data: properties

    /**
     * the label that is displayed above the scroll pane.
     */
    private final JLabel label;

    /**
     * the singleton child of the scroll pane.
     */
    private final JPanel scrolledPanel;

    /**
     * instantiates a labeled scroll pane.
     *
     * @method  LabeledScrollPane
     *
     * @date    2015-10-01T10:34:15-0800
     *
     * @author  Eric Tsang
     *
     * @param   adapter the adapter that manages the list items shown in the
     *   scrolled panel.
     *
     * @return  a new instance of the class LabeledScrollPane.
     */
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
        scrollPane.getVerticalScrollBar().setUnitIncrement(VERTICAL_SCROLL_UNIT_INCREMENT);
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

    /**
     * returns the label that is displayed above the scroll pane.
     *
     * @method  getLabel
     *
     * @date    2015-10-01T10:33:25-0800
     *
     * @author  Eric Tsang
     *
     * @return  the label that is displayed above the scroll pane.
     */
    public JLabel getLabel()
    {
        return label;
    }

    /**
     * returns the component that is scrolled inside the scroll pane.
     *
     * @method  getScrolledPanel
     *
     * @date    2015-10-01T10:32:14-0800
     *
     * @author  Eric Tsang
     *
     * @return  the component that is scrolled inside the scroll pane.
     */
    public JPanel getScrolledPanel()
    {
        return scrolledPanel;
    }

    /**
     * manages the components displayed within a labeled scroll pane.
     *
     * @class   Adapter
     *
     * @date    2015-10-01T10:15:40-0800
     *
     * @author  Eric Tsang
     */
    public static abstract class Adapter
    {
        /**
         * the labeled scroll pane that this adapter is bound to.
         */
        private LabeledScrollPane labeledScrollPane;

        /**
         * binds this adapter to the passed labeled scroll pane.
         *
         * @method  setLabeledScrollPane
         *
         * @date    2015-10-01T09:48:52-0800
         *
         * @author  Eric Tsang
         *
         * @param   labeledScrollPane the scroll pane to bind the adapter to.
         */
        private void setLabeledScrollPane(LabeledScrollPane labeledScrollPane)
        {
            this.labeledScrollPane = labeledScrollPane;
            onSetLabeledScrollPane();
        }

        /**
         * template method that is invoked when the adapter is bound to a
         *   labeled scroll pane.
         *
         * @method  onSetLabeledScrollPane
         *
         * @date    2015-10-01T09:47:46-0800
         *
         * @author  Eric Tsang
         */
        protected abstract void onSetLabeledScrollPane();

        protected final void setTitle(String newTitle)
        {
            labeledScrollPane.getLabel().setText(newTitle);
        }

        /**
         * sets the list items displayed in the scroll pane. all previous list
         *   items will be removed.
         *
         * @method  setListItems
         *
         * @date    2015-10-01T09:46:49-0800
         *
         * @author  Eric Tsang
         *
         * @param   newListItems the list items to display in the scroll pane.
         */
        protected final void setListItems(List<ListItem> newListItems)
        {
            labeledScrollPane.getScrolledPanel().removeAll();
            newListItems.forEach(labeledScrollPane.getScrolledPanel()::add);
            labeledScrollPane.setPreferredSize(new Dimension(0,0));
            getParentComponent().revalidate();
            getParentComponent().repaint();
        }

        /**
         * returns this component's parent component if it exists. if it doesn't
         *   exist, then the JPanel itself is returned.
         *
         * @method  getParentComponent
         *
         * @date    2015-10-01T09:53:01-0800
         *
         * @author  Eric Tsang
         *
         * @return  this component's parent component; null if non-existent.
         */
        protected final Component getParentComponent()
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
