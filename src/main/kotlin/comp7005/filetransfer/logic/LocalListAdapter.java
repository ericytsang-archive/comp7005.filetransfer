package comp7005.filetransfer.logic;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import comp7005.filetransfer.gui.LabeledScrollPane;
import comp7005.filetransfer.gui.FileListItem;
import comp7005.filetransfer.gui.FolderListItem;
import comp7005.filetransfer.gui.ListItem;

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
        clientLogic.setLocalDirectory(".");
        setTitle("Local Files:");
    }

    // public interface: server methods

    /**
     * displays the files of the current directory on the scroll pane.
     *
     * @method  present
     *
     * @date    2015-10-02T09:45:51-0800
     *
     * @author  Eric Tsang
     *
     * @param   files a collection f files to display.
     */
    @SuppressWarnings("ConstantConditions")
    public void present(List<JsonableFile> files)
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
                                clientLogic.setLocalDirectory(file.getAbsolutePath()))
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
                                clientLogic.pushFile(getParentComponent(),new File(file.getAbsolutePath()));
                            }
                        }.start());
            }
        }

        listItems.addAll(folderLis);
        listItems.addAll(fileLis);
        setListItems(listItems);
    }
}
