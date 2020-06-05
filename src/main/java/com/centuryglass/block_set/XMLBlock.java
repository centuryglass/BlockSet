/**
 * Define all minecraft materials to read from xml strings
 */
package com.centuryglass.block_set;

import org.bukkit.Material;

public enum XMLBlock {
    DIAMOND_BLOCK("diamond", Material.DIAMOND_BLOCK),
    GOLD_BLOCK("gold", Material.GOLD_BLOCK);
    
    /**
     * Given an xml tag name, search all values for an appropriate minecraft
     * material
     * 
     * @param xmlTag  An xml block tag
     * 
     * @return        The matching material, or Material.AIR if no match is found. 
     */
    public static Material findMaterialFromTag(String xmlTag)
    {
        for (XMLBlock block : XMLBlock.values())
        {
            if (block.getXmlTag().equals(xmlTag))
            {
                return block.getMaterial();
            }
        }
        System.err.println("Couldn't find Material for xml tag '" + xmlTag + "'");
        return Material.AIR;
    }

    public Material getMaterial() { return minecraftMaterial; }
    public String getXmlTag() { return xmlTag; }
    
    
    private final String xmlTag;
    private final Material minecraftMaterial;
    private XMLBlock(String xmlTag, Material minecraftMaterial)
    {
        this.xmlTag = xmlTag;
        this.minecraftMaterial = minecraftMaterial;    
    }
}
