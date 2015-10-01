package filetransfer.gui;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

import filetransfer.logic.JsonableFile;

/**
 * Created by Eric on 9/25/2015.
 */
public class FileListItem extends ListItem<JsonableFile>
{
    private static final Icon DEFAULT_ICON = UIManager.getIcon("FileView.fileIcon");

    public FileListItem(JsonableFile file)
    {
        super(file);
    }

    public FileListItem(File file)
    {
        super(new JsonableFile(file));
    }

    @Override
    public Icon getListItemIcon()
    {
        return DEFAULT_ICON;
    }

    @Override
    public String getListItemLabel()
    {
        return getListItemModel().getName();
    }
}
