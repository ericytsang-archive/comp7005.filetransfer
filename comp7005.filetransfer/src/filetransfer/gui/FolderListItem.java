package filetransfer.gui;

import java.io.File;

import javax.swing.Icon;
import javax.swing.UIManager;

import filetransfer.logic.JsonableFile;

/**
 * a list item used to represent folder and can be displayed in a component.
 *
 * @file    FolderListItem.java
 *
 * @program comp7005.filetransfer.jar
 *
 * @class   FolderListItem
 *
 * @date    2015-10-01T08:54:10-0800
 *
 * @author  Eric Tsang
 */
public class FolderListItem extends ListItem<JsonableFile>
{
    private static final Icon DEFAULT_ICON = UIManager.getIcon("FileView.directoryIcon");

    /**
     * instantiates a FolderListItem instance used to display the passed model.
     *
     * @method  FolderListItem
     *
     * @date    2015-10-01T08:27:33-0800
     *
     * @author  Eric Tsang
     *
     * @param   model the model to present in this component
     *
     * @return  a new instance of a FolderListItem.
     */
    public FolderListItem(JsonableFile file)
    {
        super(file);
    }

    /**
     * instantiates a FolderListItem instance used to display the passed model.
     *
     * @method  FolderListItem
     *
     * @date    2015-10-01T08:27:33-0800
     *
     * @author  Eric Tsang
     *
     * @param   model the model to present in this component
     *
     * @return  a new instance of a FolderListItem.
     */
    public FolderListItem(File file)
    {
        super(new JsonableFile(file));
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
    @Override
    public Icon getListItemIcon()
    {
        return DEFAULT_ICON;
    }

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
    @Override
    public String getListItemLabel()
    {
        return getListItemModel().getName();
    }
}
