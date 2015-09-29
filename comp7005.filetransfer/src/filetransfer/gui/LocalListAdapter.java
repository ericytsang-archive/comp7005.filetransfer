package filetransfer.gui;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import filetransfer.gui.listitem.FileListItem;
import filetransfer.gui.listitem.FolderListItem;
import filetransfer.gui.listitem.ListItem;
import filetransfer.gui.listitem.ParentFolderListItem;
import filetransfer.logic.ClientApp;

public class LocalListAdapter extends LabeledScrollPane.Adapter
{
    private final ClientApp clientApp;

    public LocalListAdapter(ClientApp clientApp)
    {
        this.clientApp = clientApp;
    }

    @Override
    public List<ListItem> getInitialListItems()
    {
        return getDirectoryItems();
    }

    @Override
    public String getInitialTitle()
    {
        return "Local Files:";
    }

    // private interface: support methods

    private List<ListItem> getDirectoryItems()
    {
        // add all the files in the current directory to a list to be returned
        LinkedList<ListItem> listItems = new LinkedList<>();
        //noinspection ConstantConditions
        for(File file : clientApp.getCurrentDirectory().listFiles())
        {
            if(file.isDirectory())
            {
                ListItem<?> item = new FolderListItem(file);
                listItems.add(item);
                item.addActionListener(e -> {
                    clientApp.setCurrentDirectory(file);
                    setListItems(getDirectoryItems());
                });
            }
            else
            {
                listItems.add(new FileListItem(file));
            }
        }

        // put parent directory as element in list to be returned if it exists
        File parentFile = clientApp.getCurrentDirectory().getAbsoluteFile().getParentFile();
        if(parentFile != null)
        {
            ListItem<?> item = new ParentFolderListItem(parentFile);
            listItems.addFirst(item);
            item.addActionListener(e -> {
                clientApp.setCurrentDirectory(parentFile);
                setListItems(getDirectoryItems());
            });
        }

        return listItems;
    }
}
