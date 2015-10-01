package filetransfer.gui;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 * a list item used to represent an object and can be displayed in a component.
 *
 * @class   ListItem
 *
 * @date    2015-10-01T08:54:10-0800
 *
 * @author  Eric Tsang
 */
public abstract class ListItem<Model> extends JButton
{
    /**
     * the preferred height of one of these items.
     */
    private static final int ITEM_HEIGHT = 30;

    /**
     * the model that this view is presenting.
     */
    private final Model model;

    /**
     * instantiates a ListItem instance used to display the passed model.
     *
     * @method  ListItem
     *
     * @date    2015-10-01T08:27:33-0800
     *
     * @author  Eric Tsang
     *
     * @param   model the model to present in this component
     *
     * @return  a new instance of a ListItem.
     */
    public ListItem(Model model)
    {
        this.model = model;
        setText(getListItemLabel());
        setIcon(getListItemIcon());
        setMaximumSize(new Dimension(Integer.MAX_VALUE,ITEM_HEIGHT));
        setMinimumSize(new Dimension(0,ITEM_HEIGHT));
        setHorizontalAlignment(JButton.LEFT);
    }

    /**
     * returns the icon to display next to the list item.
     *
     * @method  getListItemIcon
     *
     * @date    2015-10-01T08:49:49-0800
     *
     * @author  Eric Tsang
     *
     * @return  the icon associated with the list item.
     */
    protected abstract Icon getListItemIcon();

    /**
     * returns the string to display with the list item.
     *
     * @method  getListItemLabel
     *
     * @date    2015-10-01T08:50:50-0800
     *
     * @author  Eric Tsang
     *
     * @return  the string to display with the list item.
     */
    protected abstract String getListItemLabel();

    /**
     * returns the model associated with the list item.
     *
     * @method  getListItemModel
     *
     * @date    2015-10-01T08:53:13-0800
     *
     * @author  Eric Tsang
     *
     * @return  the model associated with the list item.
     */
    public final Model getListItemModel()
    {
        return model;
    }
}
