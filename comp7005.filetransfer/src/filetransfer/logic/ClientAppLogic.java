package filetransfer.logic;

import java.awt.Component;
import java.io.File;
import java.util.List;

import filetransfer.gui.listitem.ListItem;

public class ClientAppLogic
{
    private File currentDirectory;

    // public interface: constructors

    public ClientAppLogic()
    {
        currentDirectory = new File("./");
    }

    // public interface: server methods

    public File getCurrentDirectory()
    {
        return currentDirectory;
    }

    public void setCurrentDirectory(File newDirectory)
    {
        // todo: there may need to be some sort of error checking... :o test pris~
        currentDirectory = newDirectory;
    }

    public void promptAddServer(Component parentComponent)
    {
        // todo: implement
    }

    public void promptRemoveServer(Component parentComponent)
    {
        // todo: implement
    }
}
