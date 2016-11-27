/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appUtils;

import com.jogamp.opengl.GLAutoDrawable;
import oglutils.OGLTextRenderer;

/**
 *
 * @author Matthes
 */
public class textUtils
{
    OGLTextRenderer textRenderer;
    int width, height;
    GLAutoDrawable glDraw;

    public textUtils(OGLTextRenderer textRenderer, int width, int height, GLAutoDrawable glDraw)
    {
        this.textRenderer = textRenderer;
        this.width = width;
        this.height = height;
        this.glDraw = glDraw;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }
    
    public void vypisTextOvládání()
    {
        String text = "Ovládání: kamera: [LMB], pohyb: [WASD] nebo šipky, [CTRL] a [Shift], ";
        textRenderer.drawStr2D(glDraw, 3, height - 20, text);
    }
    
    public void vypisTextBarvaBody(int barva, int pocBodu)
    {
        String barvaText = "";
        
        switch (barva)
        {
            case 0:
                barvaText = "černá";
                break;
            case 1:
                barvaText = "červená";
                break;
            case 2:
                barvaText = "zelená";
                break;
            case 3:
                barvaText = "modrá";
                break;
            case 4:
                barvaText = "bílá";
                break;
            case 5:
                barvaText = "podle parametrů";
                break;
            case 6:
                barvaText = "podle pozice";
                break;
            case 7:
                barvaText = "podle normál";
                break;
        }
        
        String barvaBody = "změna barvy: [0] nebo [B]. Nastavená barva: " + barvaText + ", počet bodů -[4] +[5]:" + pocBodu;
        
        textRenderer.drawStr2D(glDraw, 3, height - 35, barvaBody);
    }
    
    public void vypisSvetlo(String svetlo, int typSvetla)
    {
        String svetloText = "Pozice světla [6], vlastní [L]: " + svetlo + "; typ výpočtu [K]: ";
        switch(typSvetla)
        {
            case 0:
                svetloText += "vypnuto";
                break;
            case 1:
                svetloText += "per vertex";
                break;
            case 2:
                svetloText += "per pixel";
                break;
        }
        
        textRenderer.drawStr2D(glDraw, 3, height - 50, svetloText);
    }
    
    public void vypisObjekty(int kartezsky, int sfericky, int cylindricky)
    {
        String objektKartezsky = "";
        String objektSfericky = "";
        String objektCylindricky = "";
        
        switch (kartezsky)
        {
            case 0:
                objektKartezsky = "žádný";
                break;
            case 1:
                objektKartezsky = "kulová plocha";
                break;
            case 2:
                objektKartezsky = "přesípací hodiny";
                break;
            case 3:
                objektKartezsky = "sud";
                break;
            case 4:
                objektKartezsky = "mobius band";
                break;
            case 5:
                objektKartezsky = "turbína";
                break;
        }

        switch (sfericky)
        {
            case 0:
                objektSfericky = "žádný";
                break;
            case 1:
                objektSfericky = "kulová plocha";
                break;
            case 2:
                objektSfericky = "burák";
                break;
            case 3:
                objektSfericky = "list";
                break;
            case 4:
                objektSfericky = "pohár";
                break;
        }

        switch (cylindricky)
        {
            case 0:
                objektCylindricky = "žádný";
                break;
            case 1:
                objektCylindricky = "kulová plocha";
                break;
            case 2:
                objektCylindricky = "sombréro";
                break;
            case 3:
                objektCylindricky = "kliková hřídel";
                break;
            case 4:
                objektCylindricky = "panáček";
                break;
        }
        
        String objekty = "Vykreslované objekty: Kartézský [1]: " + objektKartezsky + "; sférický [2]: " + objektSfericky + ";";
        String objekty2 = "cylindrický [3]: " + objektCylindricky;
        
        textRenderer.drawStr2D(glDraw, 3, 20, objekty);
        textRenderer.drawStr2D(glDraw, 122, 5, objekty2);
    }
    
    public void vypisCopyright()
    {
        textRenderer.drawStr2D(glDraw, width - 90, 3, " (c) PGRF UHK");
    }
}
