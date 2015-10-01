package filetransfer.logic;

import java.util.Collection;
import java.util.LinkedList;

import filetransfer.gui.LabeledScrollPane;
import filetransfer.gui.FileListItem;
import filetransfer.gui.FolderListItem;
import filetransfer.gui.ListItem;

public class RemoteListAdapter extends LabeledScrollPane.Adapter
{
    private final ClientLogic clientLogic;

    public RemoteListAdapter(ClientLogic clientLogic)
    {
        this.clientLogic = clientLogic;
    }

    /**
     * template method that is invoked when the adapter is bound to a
     *   labeled scroll pane.
     *
     * @method  onSetLabeledScrollPane
     *
     * @date    2015-10-01T09:47:46-0800
     *
     * @author  Eric Tsang
     */
    @Override
    protected void onSetLabeledScrollPane()
    {
        setTitle("Remote Files:");
    }

    // public interface: server methods

    /**
     * presents the passed files in the scroll pane.
     *
     * @method  present
     *
     * @date    2015-10-01T09:42:16-0800
     *
     * @author  Eric Tsang
     *
     * @param   files a collection f files to display.
     */
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
                            present(clientLogic.pullDirectoryFiles(getParentComponent(),file.getAbsolutePath()));
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
                            clientLogic.pullFile(getParentComponent(),file.getAbsolutePath());
                        }
                    }.start());
            }
        }

        listItems.addAll(folderLis);
        listItems.addAll(fileLis);
        setListItems(listItems);
    }
}
