package filetransfer.logic;

import java.util.Collection;
import java.util.LinkedList;

import filetransfer.gui.LabeledScrollPane;
import filetransfer.gui.FileListItem;
import filetransfer.gui.FolderListItem;
import filetransfer.gui.ListItem;

/**
 * manages the list items that are displayed on the scroll pane used to show
 *   files on the remote server.
 *
 * @file    RemoteListAdapter.java
 *
 * @program comp7005.filetransfer.jar
 *
 * @class   RemoteListAdapter
 *
 * @date    2015-10-02T10:45:08-0800
 *
 * @author  Eric Tsang
 */
public class RemoteListAdapter extends LabeledScrollPane.Adapter
{
    /**
     * reference to the client logic application to use.
     */
    private final ClientLogic clientLogic;

    /**
     * instantiates a new remote list adapter, that uses the passed client logic
     *   object.
     *
     * @method  RemoteListAdapter
     *
     * @date    2015-10-02T10:43:01-0800
     *
     * @author  Eric Tsang
     *
     * @param   clientLogic reference to the client logic object to use to pull
     *   information from the remote host.
     *
     * @return  a new instance of remote list adapter.
     */
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
                        new Thread(() ->
                            clientLogic.setRemoteDirectory(getParentComponent(),file.getAbsolutePath()))
                            .start());
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
