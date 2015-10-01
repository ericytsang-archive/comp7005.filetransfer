package filetransfer.logic;

import java.util.Collection;
import java.util.LinkedList;

import filetransfer.gui.LabeledScrollPane;
import filetransfer.gui.FileListItem;
import filetransfer.gui.FolderListItem;
import filetransfer.gui.ListItem;

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
        LinkedList<ListItem> folderLis = new LinkedList<>();
        LinkedList<ListItem> fileLis = new LinkedList<>();
        for(JsonableFile file : files)
        {
            if(file.isDirectory())
            {
                ListItem<?> item = new FolderListItem(file);
                folderLis.add(item);
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
                fileLis.add(item);
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

        listItems.addAll(folderLis);
        listItems.addAll(fileLis);
        setListItems(listItems);
    }
}
