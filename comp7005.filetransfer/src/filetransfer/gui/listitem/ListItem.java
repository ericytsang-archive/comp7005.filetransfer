package filetransfer.gui.listitem;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public abstract class ListItem<Model> extends JButton
{
    private static final int ITEM_HEIGHT = 30;
    private final Model model;

    public ListItem(Model model)
    {
        this.model = model;
        setText(getListItemLabel());
        setIcon(getListItemIcon());
        setMaximumSize(new Dimension(Integer.MAX_VALUE,ITEM_HEIGHT));
        setMinimumSize(new Dimension(0,ITEM_HEIGHT));
        setHorizontalAlignment(JButton.LEFT);
    }

    public abstract ImageIcon getListItemIcon();
    public abstract String getListItemLabel();
    public final Model getListItemModel()
    {
        return model;
    }
}
