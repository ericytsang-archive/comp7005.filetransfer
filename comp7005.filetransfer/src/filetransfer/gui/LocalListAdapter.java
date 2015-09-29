package filetransfer.gui;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import filetransfer.gui.listitem.FileListItem;
import filetransfer.gui.listitem.FolderListItem;
import filetransfer.gui.listitem.ListItem;
import filetransfer.logic.ClientApp;
import filetransfer.logic.JsonableFile;

public class LocalListAdapter extends LabeledScrollPane.Adapter
{
    private final ClientApp clientApp;

    public LocalListAdapter(ClientApp clientApp)
    {
        this.clientApp = clientApp;
    }

    @Override
    protected void onSetLabeledScrollPane()
    {
        setListItems(getDirectoryItems());
        setTitle("Local Files:");
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
                ListItem<?> item = new FileListItem(file);
                listItems.add(item);
                item.addActionListener(e ->
                    new Thread()
                    {
                        @Override
                        public void run()
                        {
                            clientApp.pushFile(getParentComponent(),file);
                        }
                    }.start());
            }
        }

        // put parent directory as element in list to be returned if it exists
        File parentFile = clientApp.getCurrentDirectory().getAbsoluteFile().getParentFile();
        if(parentFile != null)
        {
            ListItem<?> item = new FolderListItem(new JsonableFile(parentFile.isDirectory(),parentFile.getAbsolutePath(),".."));
            listItems.addFirst(item);
            item.addActionListener(e -> {
                clientApp.setCurrentDirectory(parentFile);
                setListItems(getDirectoryItems());
            });
        }

        return listItems;
    }
}
