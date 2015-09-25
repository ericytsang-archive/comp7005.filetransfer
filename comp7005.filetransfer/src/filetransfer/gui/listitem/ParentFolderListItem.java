package filetransfer.gui.listitem;

import java.io.File;

/**
 * Created by Eric on 9/25/2015.
 */
public class ParentFolderListItem extends FolderListItem
{
    public ParentFolderListItem(File file)
    {
        super(file);
    }

    @Override
    public String getListItemLabel()
    {
        return "..";
    }
}
