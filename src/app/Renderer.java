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

    int width, height, ox, oy;

    OGLBuffers kulPlocha, presHodiny, sud;
    OGLTextRenderer textRenderer = new OGLTextRenderer();

    int kulPlochaShader, presHodinyShader, sudShader, sudLocMat, presHodinyLocMat, kulPlochaLocMat;

    Camera cam = new Camera();
    Mat4 proj;

    @Override
    public void init(GLAutoDrawable glDrawable)
    {
        GL2 gl = glDrawable.getGL().getGL2();

        OGLUtils.printOGLparameters(gl);
        OGLUtils.shaderCheck(gl);

        kulPlochaShader = ShaderUtils.loadProgram(gl, "/shader/kulPlocha");
        presHodinyShader = ShaderUtils.loadProgram(gl, "/shader/presHodiny");
        sudShader = ShaderUtils.loadProgram(gl, "/shader/sud");
        
        createBuffers(gl);

        kulPlochaLocMat = gl.glGetUniformLocation(kulPlochaShader, "mat");
        presHodinyLocMat = gl.glGetUniformLocation(presHodinyShader, "mat");
        sudLocMat = gl.glGetUniformLocation(sudShader, "mat");

        cam = cam.withPosition(new Vec3D(5, 5, 2.5))
                .withAzimuth(Math.PI * 1.25)
                .withZenith(Math.PI * -0.125);

        gl.glEnable(GL2.GL_DEPTH_TEST);
    }

    void createBuffers(GL2 gl)
    {
        kulPlocha = MeshGenerator.createGrid(gl, 20, "inParamPos");
        presHodiny = MeshGenerator.createGrid(gl, 20, "inParamPos");
        sud = MeshGenerator.createGrid(gl, 20, "inParamPos");
    }

    @Override
    public void display(GLAutoDrawable glDrawable)
    {
        GL2 gl = glDrawable.getGL().getGL2();

        gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        float[] mat = ToFloatArray.convert(cam.getViewMatrix().mul(proj));

        //vykresliTrojuhelniky(gl, kulPlocha, kulPlochaShader, kulPlochaLocMat, mat);
        //vykresliTrojuhelniky(gl, presHodiny, presHodinyShader, presHodinyLocMat, mat);
        vykresliTrojuhelniky(gl, sud, sudShader, sudLocMat, mat);
        
        String text = this.getClass().getName() + ": [LMB] camera, WSAD";

        textRenderer.drawStr2D(glDrawable, 3, height - 20, text);
        textRenderer.drawStr2D(glDrawable, width - 90, 3, " (c) PGRF UHK");
    }
    
    public void vykresliTrojuhelniky(GL2 gl, OGLBuffers oglBuffer, int shader, int locMat, float[] mat)
    {
        gl.glUseProgram(shader);
        gl.glUniformMatrix4fv(locMat, 1, false, mat, 0);

        oglBuffer.draw(GL2.GL_TRIANGLES, shader);
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
        gl.glDeleteProgram(kulPlochaShader);
    }

}
