package filetransfer.gui;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

import filetransfer.logic.JsonableFile;

/**
 * a list item used to represent a file and can be displayed in a component.
 *
 * @file    FileListItem.java
 *
 * @program comp7005.filetransfer.jar
 *
 * @class   FileListItem
 *
 * @date    2015-10-01T08:54:10-0800
 *
 * @author  Eric Tsang
 */
public class FileListItem extends ListItem<JsonableFile>
{
    private static final Icon DEFAULT_ICON = UIManager.getIcon("FileView.fileIcon");

    /**
     * instantiates a ListItem instance used to display the passed model.
     *
     * @method  FileListItem
     *
     * @date    2015-10-01T08:27:33-0800
     *
     * @author  Eric Tsang
     *
     * @param   model the model to present in this component
     *
     * @return  a new instance of a FileListItem.
     */
    public FileListItem(JsonableFile file)
    {
        super(file);
    }

    /**
     * instantiates a ListItem instance used to display the passed model.
     *
     * @method  FileListItem
     *
     * @date    2015-10-01T08:27:33-0800
     *
     * @author  Eric Tsang
     *
     * @param   model the model to present in this component
     *
     * @return  a new instance of a FileListItem.
     */
    public FileListItem(File file)
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
    public String getListItemLabel()
    {
        return getListItemModel().getName();
    }
}
