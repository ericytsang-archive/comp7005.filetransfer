package filetransfer.logic;

import java.io.File;
import java.util.LinkedList;

import filetransfer.gui.LabeledScrollPane;
import filetransfer.gui.FileListItem;
import filetransfer.gui.FolderListItem;
import filetransfer.gui.ListItem;

/**
 * the local list adapter manages what list items are displayed on the scroll
 *   pane that displays the local files. it also defines the behavior when the
 *   list items are clicked.
 *
 * @method  onSetLabeledScrollPane
 *
 * @file    LocalListAdapter.java
 *
 * @program comp7005.filetransfer.jar
 *
 * @class   LocalListAdapter
 *
 * @date    2015-10-01T16:07:30-0800
 *
 * @author  Eric Tsang
 */
public class LocalListAdapter extends LabeledScrollPane.Adapter
{
    /**
     * reference to the client logic instance.
     */
    private final ClientLogic clientLogic;

    /**
     * instantiates a local list adapter
     *
     * @method  LocalListAdapter
     *
     * @date    2015-10-01T16:07:11-0800
     *
     * @author  Eric Tsang
     *
     * @param   clientLogic reference to the client logic instance.
     *
     * @return  returns a new instance of LocalListAdapter.
     */
    public LocalListAdapter(ClientLogic clientLogic)
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
        presentCurrentDirectory();
        setTitle("Local Files:");
    }

    // public interface: server methods

    /**
     * displays the files of the current directory on the scroll pane.
     *
     * @method  presentCurrentDirectory
     *
     * @date    2015-10-02T09:45:51-0800
     *
     * @author  Eric Tsang
     */
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
