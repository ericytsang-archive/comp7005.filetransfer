package filetransfer.gui.listitem;

import java.net.SocketAddress;

import javax.swing.ImageIcon;

/**
 * Created by Eric on 9/25/2015.
 */
public class ServerListItem extends ListItem<SocketAddress>
{
    private static final ImageIcon DEFAULT_ICON = new ImageIcon("./res/icons/computer.png");

    public ServerListItem(SocketAddress address)
    {
        super(address);
    }

    @Override
    public ImageIcon getListItemIcon()
    {
        return DEFAULT_ICON;
    }

    @Override
    public String getListItemLabel()
    {
        return getListItemModel().toString();
    }
}
