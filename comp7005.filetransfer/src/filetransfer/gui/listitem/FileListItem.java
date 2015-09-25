package filetransfer.gui.listitem;

import java.io.File;

import javax.swing.ImageIcon;

/**
 * Created by Eric on 9/25/2015.
 */
public class FileListItem extends ListItem<File>
{
    private static final ImageIcon DEFAULT_ICON = new ImageIcon("./res/icons/file.png");

    public FileListItem(File file)
    {
        super(file);
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
