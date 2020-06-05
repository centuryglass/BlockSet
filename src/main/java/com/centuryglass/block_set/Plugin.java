/**
 * @file Plugin.java
 * 
 * Edit some blocks on start
 * 
 * JDOM Docs: http://www.jdom.org/docs/apidocs/
 * Spigot Docs: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/package-summary.html
 */

package com.centuryglass.block_set;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.DOMBuilder;
import org.xml.sax.SAXException;


public class Plugin extends JavaPlugin 
{
    /**
     * Checks if the application is running as a Minecraft server plugin.
     * 
     * @return  Whether this application was started by a Minecraft server using
     *          this Plugin class.
     */
    public static boolean isRunning()
    {
        return getRunningPlugin() != null;
    }
    
    /**
     * If the application is running as a Minecraft server plugin, this method
     * gets the running Plugin object.
     * 
     * @return  The Plugin, or null if the application isn't running as a server
     *          plugin.
     */
    public static Plugin getRunningPlugin()
    {
        try
        {
            JavaPlugin plugin = JavaPlugin.getProvidingPlugin(Plugin.class);
            return (Plugin) plugin;
        }
        catch (NoClassDefFoundError | IllegalArgumentException e)
        {
            return null;
        }
    }
    
    /**
     * Starts asynchronous server mapping when the plugin is enabled.
     */
    @Override
    public void onEnable()
    {
        // Get the first world
        Server minecraftServer = getServer();
        World firstWorld = minecraftServer.getWorlds().get(0);
        System.out.println("Editing world '" + firstWorld.getName() + "'");
        
        
        // Clear out a large area, creating a solid floor at y=59
        System.out.println("Clearing spawn area");
        for (int y = 40; y < 255; y++)
        {
            System.out.println("Clearing y =" + y);
            for(int x = -100; x < 100; x++)
            {
                for(int z = -100; z < 100; z++)
                {
                    Block editedBlock = firstWorld.getBlockAt(x, y, z);
                    editedBlock.setType(y <= 59 ? Material.STONE : Material.AIR);
                }
            }
        }
        
        // Load xml file from jar resources
        System.out.println("loading xml resource");
        Document document;
        try
        {
            File tempFile = File.createTempFile("blocks", "xml");
            JarResource.copyResourceToFile("blocks.xml", tempFile);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            org.w3c.dom.Document w3cDocument = documentBuilder.parse(tempFile.getAbsolutePath());
            document = new DOMBuilder().build(w3cDocument);
        }
        catch (IOException | SAXException | ParserConfigurationException e)
        {
            System.err.println("Failed to load xml resource from jar");
            e.printStackTrace();
            return;
        }
        
        // Find block data within the document
        System.out.println("setting blocks from xml");
        int blocksSet = 0;
        Element root = document.getRootElement(); // <game>
        for (Element child : root.getChildren())
        {
            if (child.getName().equals("entitySet"))
            {
                for (Element entity : child.getChildren())
                {
                    // Find material:
                    Attribute blockTag = entity.getAttribute("modelName");
                    Material blockType = XMLBlock.findMaterialFromTag(blockTag.getValue());
                    
                    // Find position
                    for (Element property : entity.getChildren())
                    {
                        if (property.getName().equals("property")
                                && property.getAttributeValue("name").equals("Position"))
                        {
                            for (Element pointValue : property.getChildren())
                            {
                                String pointStr = pointValue.getAttributeValue("value");
                                String[] coords = pointStr.split(", ");
                                if (coords.length < 3)
                                {
                                    continue; // Invalid coordinates!
                                }
                                int x = Integer.parseInt(coords[0]);
                                int y = Integer.parseInt(coords[1]);
                                int z = Integer.parseInt(coords[2]);
                                Block editedBlock = firstWorld.getBlockAt(x, y, z);
                                editedBlock.setType(blockType);
                                blocksSet++;
                            }
                        }
                    }                  
                }
            }
        }
        System.out.println("Set " + blocksSet + " blocks from xml");
        
    }
    
    @Override
    public void onDisable() { }
}
