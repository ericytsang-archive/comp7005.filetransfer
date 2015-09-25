package filetransfer.gui;

import java.util.Collections;
import java.util.List;

import filetransfer.gui.listitem.ListItem;

/**
 * Created by Eric on 9/25/2015.
 */
public class RemoteListAdapter extends LabeledScrollPane.Adapter
{
    @Override
    public List<ListItem> getInitialListItems()
    {
        return Collections.emptyList();
    }

    @Override
    public String getInitialTitle()
    {
        // todo: implement!
        return "todo: implement";
    }
}
