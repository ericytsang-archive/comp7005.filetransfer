package filetransfer.gui;

import java.awt.Component;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import filetransfer.gui.listitem.FileListItem;
import filetransfer.gui.listitem.FolderListItem;
import filetransfer.gui.listitem.ListItem;
import filetransfer.logic.ClientApp;
import filetransfer.logic.JsonableFile;

public class RemoteListAdapter extends LabeledScrollPane.Adapter
{
    private final ClientApp clientApp;

    public RemoteListAdapter(ClientApp clientApp)
    {
        this.clientApp = clientApp;
    }

    @Override
    public List<ListItem> getInitialListItems()
    {
        return Collections.emptyList();
    }

    @Override
    public String getInitialTitle()
    {
        return "Remote Files:";
    }

    public void present(Component parentComponent,Collection<JsonableFile> files)
    {
        // add all the files in the current directory to a list to be returned
        LinkedList<ListItem> listItems = new LinkedList<>();
        //noinspection ConstantConditions
        for(JsonableFile file : files)
        {
            if(file.isDirectory())
            {
                ListItem<?> item = new FolderListItem(file);
                listItems.add(item);
                item.addActionListener(e -> {
                    present(parentComponent,clientApp.pullDirectoryFiles(parentComponent,file.getAbsolutePath()));
                });
            }
            else
            {
                listItems.add(new FileListItem(file));
            }
        }

        setListItems(listItems);
    }
}
