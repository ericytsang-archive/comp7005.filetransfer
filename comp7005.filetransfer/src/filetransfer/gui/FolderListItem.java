package filetransfer.gui;

import java.io.File;

import javax.swing.ImageIcon;

import filetransfer.logic.JsonableFile;

/**
 * Created by Eric on 9/25/2015.
 */
public class FolderListItem extends ListItem<JsonableFile>
{
    private static final ImageIcon DEFAULT_ICON = new ImageIcon("./res/icons/folder.png");

    public FolderListItem(JsonableFile file)
    {
        super(file);
    }

    public FolderListItem(File file)
    {
        super(new JsonableFile(file));
    }

    @Override
    public ImageIcon getListItemIcon()
    {
        return DEFAULT_ICON;
    }

    @Override
    public String getListItemLabel()
    {
        return getListItemModel().getName();
    }
}
