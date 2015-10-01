package filetransfer.logic;

import java.io.File;
import java.util.LinkedList;

import filetransfer.gui.LabeledScrollPane;
import filetransfer.gui.FileListItem;
import filetransfer.gui.FolderListItem;
import filetransfer.gui.ListItem;

public class LocalListAdapter extends LabeledScrollPane.Adapter
{
    private final ClientLogic clientLogic;

    public LocalListAdapter(ClientLogic clientLogic)
    {
        this.clientLogic = clientLogic;
    }

    @Override
    protected void onSetLabeledScrollPane()
    {
        presentCurrentDirectory();
        setTitle("Local Files:");
    }

    // public interface: server methods

    @SuppressWarnings("ConstantConditions")
    public void presentCurrentDirectory()
    {
        // add all the files in the current directory to a list to be returned
        LinkedList<ListItem> listItems = new LinkedList<>();
        LinkedList<ListItem> folderLis = new LinkedList<>();
        LinkedList<ListItem> fileLis = new LinkedList<>();
        for(File file : clientLogic.getCurrentDirectory().listFiles())
        {
            if(file.isDirectory())
            {
                ListItem<?> item = new FolderListItem(file);
                folderLis.add(item);
                item.addActionListener(e -> {
                    clientLogic.setCurrentDirectory(file);
                    presentCurrentDirectory();
                });
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
                            clientLogic.pushFile(getParentComponent(),file);
                        }
                    }.start());
            }
        }

        // put parent directory as element in list to be returned if it exists
        File parentFile = clientLogic.getCurrentDirectory().getAbsoluteFile().getParentFile();
        if(parentFile != null)
        {
            ListItem<?> item = new FolderListItem(new JsonableFile(parentFile.isDirectory(),parentFile.getAbsolutePath(),".."));
            folderLis.addFirst(item);
            item.addActionListener(e -> {
                clientLogic.setCurrentDirectory(parentFile);
                presentCurrentDirectory();
            });
        }

        listItems.addAll(folderLis);
        listItems.addAll(fileLis);
        setListItems(listItems);
    }
}