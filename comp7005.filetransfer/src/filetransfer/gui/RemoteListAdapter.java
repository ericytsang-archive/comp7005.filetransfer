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
    protected void onSetLabeledScrollPane()
    {
        setTitle("Remote Files:");
    }

    // public interface: server methods

    public void present(Collection<JsonableFile> files)
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
                item.addActionListener(e ->
                    new Thread()
                    {
                        @Override
                        public void run()
                        {
                            present(clientApp.pullDirectoryFiles(getParentComponent(),file.getAbsolutePath()));
                        }
                    }.start());
            }
            else
            {
                ListItem<?> item = new FileListItem(file);
                listItems.add(item);
                item.addActionListener(e ->
                    new Thread()
                    {
                        @Override
                        public void run()
                        {
                            clientApp.pullFile(getParentComponent(),file.getAbsolutePath());
                        }
                    }.start());
            }
        }
        setListItems(listItems);
    }
}
