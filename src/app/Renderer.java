package app;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import meshutils.MeshGenerator;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import oglutils.OGLBuffers;
import oglutils.OGLTextRenderer;
import oglutils.OGLUtils;
import oglutils.ShaderUtils;
import oglutils.ToFloatArray;
import transforms.Camera;
import transforms.Mat4;
import transforms.Mat4PerspRH;
import transforms.Vec3D;

/**
 * GLSL sample:<br/>
 * Draw 3D geometry, use camera and projection transformations<br/>
 * Requires JOGL 2.3.0 or newer
 *
 * @author PGRF FIM UHK
 * @version 2.0
 * @since 2015-09-05
 */
public class Renderer implements GLEventListener, MouseListener,
        MouseMotionListener, KeyListener
{

    int width, height, ox, oy, barva, kartez, sferic, cylindr;

    OGLBuffers kartezsky;
    OGLTextRenderer textRenderer = new OGLTextRenderer();

    int kartezskyShader;
    int kartezskyLocMat;
    int kartezskyLocBarva;
    int kartezskyLocObjekt;

    Camera cam = new Camera();
    Mat4 proj;

    @Override
    public void init(GLAutoDrawable glDrawable)
    {
        GL2 gl = glDrawable.getGL().getGL2();

        OGLUtils.printOGLparameters(gl);
        OGLUtils.shaderCheck(gl);

        kartezskyShader = ShaderUtils.loadProgram(gl, "/shader/kartezsky");

        createBuffers(gl);

        kartezskyLocMat = gl.glGetUniformLocation(kartezskyShader, "mat");
        kartezskyLocBarva = gl.glGetUniformLocation(kartezskyShader, "barva");
        kartezskyLocObjekt = gl.glGetUniformLocation(kartezskyShader, "objekt");

        cam = cam.withPosition(new Vec3D(5, 5, 2.5))
                .withAzimuth(Math.PI * 1.25)
                .withZenith(Math.PI * -0.125);

        gl.glEnable(GL2.GL_DEPTH_TEST);

        barva = 1;
        kartez = 1;
        sferic = 0;
        cylindr = 0;
    }

    void createBuffers(GL2 gl)
    {
        kartezsky = MeshGenerator.createGrid(gl, 20, "inParamPos");
    }

    @Override
    public void display(GLAutoDrawable glDrawable)
    {
        GL2 gl = glDrawable.getGL().getGL2();

        gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        float[] mat = ToFloatArray.convert(cam.getViewMatrix().mul(proj));

        if (kartez > 0)
        {
            gl.glUseProgram(kartezskyShader);
            gl.glUniformMatrix4fv(kartezskyLocMat, 1, false, mat, 0);
            gl.glUniform1f(kartezskyLocBarva, (float) barva);
            gl.glUniform1f(kartezskyLocObjekt, (float) kartez);

            kartezsky.draw(GL2.GL_TRIANGLES, kartezskyShader);
        }
        if (sferic > 0)
        {
            //sférické
        }
        if (cylindr > 0)
        {
            //cylindrické
        }

        String barvaText = "";
        String objektKartezsky = "";
        String objektSfericky = "";
        String objektCylindricky = "";
        
        switch (kartez)
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
        }
        
        switch (sferic)
        {
            case 0:
                objektSfericky = "žádný";
                break;
            case 1:
                objektSfericky = "nic";
                break;
            case 2:
                objektSfericky = "nic";
                break;
            case 3:
                objektSfericky = "nic";
                break;
            case 4:
                objektSfericky = "nic";
                break;
        }
        
        switch (cylindr)
        {
            case 0:
                objektCylindricky = "žádný";
                break;
            case 1:
                objektCylindricky = "nic";
                break;
            case 2:
                objektCylindricky = "nic";
                break;
            case 3:
                objektCylindricky = "nic";
                break;
            case 4:
                objektCylindricky = "nic";
                break;
        }
        
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
        String text = "Ovládání: kamera: [LMB], pohyb: [WASD] nebo šipky, [CTRL] a [Shift], ";
        String barva = "změna barvy: [0] nebo [B]. Nastavená barva: " + barvaText;
        String objekty = "Vykreslované objekty: Kartézský [1]: " + objektKartezsky + "; sférický [2]: " + objektSfericky + ";";
        String objekty2 =  "cylindrický [3]: " + objektCylindricky;

        textRenderer.drawStr2D(glDrawable, 3, height - 20, text);
        textRenderer.drawStr2D(glDrawable, 3, height - 35, barva);
        textRenderer.drawStr2D(glDrawable, width - 90, 3, " (c) PGRF UHK");
        textRenderer.drawStr2D(glDrawable, 3, 20, objekty);
        textRenderer.drawStr2D(glDrawable, 3, 5, objekty2);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
            int height)
    {
        this.width = width;
        this.height = height;
        proj = new Mat4PerspRH(Math.PI / 4, height / (double) width, 0.01, 1000.0);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged)
    {
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        ox = e.getX();
        oy = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        cam = cam.addAzimuth((double) Math.PI * (ox - e.getX()) / width)
                .addZenith((double) Math.PI * (e.getY() - oy) / width);
        ox = e.getX();
        oy = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                cam = cam.forward(1);
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                cam = cam.right(1);
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                cam = cam.backward(1);
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                cam = cam.left(1);
                break;
            case KeyEvent.VK_CONTROL:
                cam = cam.down(1);
                break;
            case KeyEvent.VK_SHIFT:
                cam = cam.up(1);
                break;
            case KeyEvent.VK_SPACE:
                cam = cam.withFirstPerson(!cam.getFirstPerson());
                break;
            case KeyEvent.VK_R:
                cam = cam.mulRadius(0.9f);
                break;
            case KeyEvent.VK_F:
                cam = cam.mulRadius(1.1f);
                break;
            case KeyEvent.VK_B:
            case KeyEvent.VK_NUMPAD0:
                barva++;
                if (barva > 7)
                {
                    barva = 0;
                }
                break;
            case KeyEvent.VK_1:
            case KeyEvent.VK_NUMPAD1:
                kartez++;
                if (kartez > 4)
                {
                    kartez = 0;
                }
                break;
            case KeyEvent.VK_2:
            case KeyEvent.VK_NUMPAD2:
                sferic++;
                if (sferic > 4)
                {
                    sferic = 0;
                }
                break;
            case KeyEvent.VK_3:
            case KeyEvent.VK_NUMPAD3:
                cylindr++;
                if (cylindr > 4)
                {
                    cylindr = 0;
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
    }

    @Override
    public void dispose(GLAutoDrawable glDrawable)
    {
        GL2 gl = glDrawable.getGL().getGL2();
        gl.glDeleteProgram(kartezskyShader);
    }

}
