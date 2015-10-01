package filetransfer.gui;

import java.io.File;

import javax.swing.Icon;
import javax.swing.UIManager;

import filetransfer.logic.JsonableFile;

/**
 * Created by Eric on 9/25/2015.
 */
public class FolderListItem extends ListItem<JsonableFile>
{
    private static final Icon DEFAULT_ICON = UIManager.getIcon("FileView.directoryIcon");

    public FolderListItem(JsonableFile file)
    {
        super(file);
    }

    public FolderListItem(File file)
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
