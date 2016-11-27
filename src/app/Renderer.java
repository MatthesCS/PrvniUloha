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
import appUtils.textUtils;
import oglutils.OGLTexture2D;

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

    int width, height, ox, oy, barva, kartez, sferic, cylindr, pocetBodu, pozSvetla;
    int zadavaniPozice, typSvetla;

    OGLTexture2D texture;
    OGLTexture2D.Viewer textureViewer;
    
    textUtils textUtils;

    Vec3D poziceSvetla, vlastniPoziceSvetla;

    boolean pocetBoduZmenen, vlastniSvetlo, minus;

    OGLBuffers kartezsky, sfericky, cylidnricky, svetelny;
    OGLTextRenderer textRenderer = new OGLTextRenderer();

    int kartezskyShader, sferickyShader, cylindrickyShader;
    int kartezskyLocMat, sferickyLocMat, cylindrickyLocMat;
    int kartezskyLocBarva, sferickyLocBarva, cylindrickyLocBarva;
    int kartezskyLocObjekt, sferickyLocObjekt, cylindrickyLocObjekt;
    int kartezskyLocPozSvetla, sferickyLocPozSvetla, cylindrickyLocPozSvetla;
    int kartezskyLocTypSvetla, sferickyLocTypSvetla, cylindrickyLocTypSvetla;

    int svetelnyShader, svetelnyLocMat, svetelnyLocPozSvetla;

    Camera cam = new Camera();
    Mat4 proj;

    @Override
    public void init(GLAutoDrawable glDrawable)
    {
        GL2 gl = glDrawable.getGL().getGL2();

        OGLUtils.printOGLparameters(gl);
        OGLUtils.shaderCheck(gl);

        kartezskyShader = ShaderUtils.loadProgram(gl, "/shader/kartezsky");
        sferickyShader = ShaderUtils.loadProgram(gl, "/shader/sfericky");
        cylindrickyShader = ShaderUtils.loadProgram(gl, "/shader/cylindricky");
        svetelnyShader = ShaderUtils.loadProgram(gl, "/shader/svetelny");

        pocetBodu = 20;
        createBuffers(gl);

        svetelnyLocMat = gl.glGetUniformLocation(svetelnyShader, "mat");
        svetelnyLocPozSvetla = gl.glGetUniformLocation(svetelnyShader, "poziceSvetla");

        kartezskyLocMat = gl.glGetUniformLocation(kartezskyShader, "mat");
        kartezskyLocBarva = gl.glGetUniformLocation(kartezskyShader, "barva");
        kartezskyLocObjekt = gl.glGetUniformLocation(kartezskyShader, "objekt");
        kartezskyLocPozSvetla = gl.glGetUniformLocation(kartezskyShader, "poziceSvetla");
        kartezskyLocTypSvetla = gl.glGetUniformLocation(kartezskyShader, "s");

        sferickyLocMat = gl.glGetUniformLocation(sferickyShader, "mat");
        sferickyLocBarva = gl.glGetUniformLocation(sferickyShader, "barva");
        sferickyLocObjekt = gl.glGetUniformLocation(sferickyShader, "objekt");
        sferickyLocPozSvetla = gl.glGetUniformLocation(sferickyShader, "poziceSvetla");
        sferickyLocTypSvetla = gl.glGetUniformLocation(sferickyShader, "s");

        cylindrickyLocMat = gl.glGetUniformLocation(cylindrickyShader, "mat");
        cylindrickyLocBarva = gl.glGetUniformLocation(cylindrickyShader, "barva");
        cylindrickyLocObjekt = gl.glGetUniformLocation(cylindrickyShader, "objekt");
        cylindrickyLocPozSvetla = gl.glGetUniformLocation(cylindrickyShader, "poziceSvetla");
        cylindrickyLocTypSvetla = gl.glGetUniformLocation(cylindrickyShader, "s");
        
        texture = new OGLTexture2D(gl, "/textures/bricks.jpg");
        texture .getTexture().setTexParameterf(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        texture .getTexture().setTexParameterf(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);

        cam = cam.withPosition(new Vec3D(5, 5, 2.5))
                .withAzimuth(Math.PI * 1.25)
                .withZenith(Math.PI * -0.125);

        gl.glEnable(GL2.GL_DEPTH_TEST);
        textureViewer = new OGLTexture2D.Viewer(gl);

        barva = 5;
        kartez = 1;
        sferic = 0;
        cylindr = 0;
        typSvetla = 0;

        pozSvetla = 0;
        poziceSvetla = new Vec3D(5, 5, 5);

        pocetBoduZmenen = false;
        vlastniSvetlo = false;
        minus = false;
        zadavaniPozice = 0;

        textUtils = new textUtils(textRenderer, width, height, glDrawable);
    }

    void createBuffers(GL2 gl)
    {
        kartezsky = MeshGenerator.createGrid(gl, pocetBodu, "inParamPos");
        sfericky = MeshGenerator.createGrid(gl, pocetBodu, "inParamPos");
        cylidnricky = MeshGenerator.createGrid(gl, pocetBodu, "inParamPos");

        svetelny = MeshGenerator.createGrid(gl, 10, "inParamPos");
    }

    @Override
    public void display(GLAutoDrawable glDrawable)
    {
        GL2 gl = glDrawable.getGL().getGL2();

        gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        float[] mat = ToFloatArray.convert(cam.getViewMatrix().mul(proj));

        if (pocetBoduZmenen)
        {
            pocetBoduZmenen = false;
            createBuffers(gl);
        }

        if (kartez > 0)
        {
            gl.glUseProgram(kartezskyShader);
            gl.glUniformMatrix4fv(kartezskyLocMat, 1, false, mat, 0);
            gl.glUniform1f(kartezskyLocBarva, (float) barva);
            gl.glUniform1f(kartezskyLocObjekt, (float) kartez);
            gl.glUniform3f(kartezskyLocPozSvetla, (float) poziceSvetla.getX(), (float) poziceSvetla.getY(), (float) poziceSvetla.getZ());
            gl.glUniform1f(kartezskyLocTypSvetla, (float) typSvetla);
            
            texture.bind(kartezskyShader, "texture", 0);
            
            kartezsky.draw(GL2.GL_TRIANGLES, kartezskyShader);
        }
        if (sferic > 0)
        {
            gl.glUseProgram(sferickyShader);
            gl.glUniformMatrix4fv(sferickyLocMat, 1, false, mat, 0);
            gl.glUniform1f(sferickyLocBarva, (float) barva);
            gl.glUniform1f(sferickyLocObjekt, (float) sferic);
            gl.glUniform3f(sferickyLocPozSvetla, (float) poziceSvetla.getX(), (float) poziceSvetla.getY(), (float) poziceSvetla.getZ());
            gl.glUniform1f(sferickyLocTypSvetla, (float) typSvetla);
            
            sfericky.draw(GL2.GL_TRIANGLES, sferickyShader);
        }
        if (cylindr > 0)
        {
            gl.glUseProgram(cylindrickyShader);
            gl.glUniformMatrix4fv(cylindrickyLocMat, 1, false, mat, 0);
            gl.glUniform1f(cylindrickyLocBarva, (float) barva);
            gl.glUniform1f(cylindrickyLocObjekt, (float) cylindr);
            gl.glUniform3f(cylindrickyLocPozSvetla, (float) poziceSvetla.getX(), (float) poziceSvetla.getY(), (float) poziceSvetla.getZ());
            gl.glUniform1f(cylindrickyLocTypSvetla, (float) typSvetla);
            
            cylidnricky.draw(GL2.GL_TRIANGLES, cylindrickyShader);
        }

        if (pozSvetla > 0 || vlastniSvetlo)
        {
            gl.glUseProgram(svetelnyShader);
            gl.glUniformMatrix4fv(svetelnyLocMat, 1, false, mat, 0);
            gl.glUniform3f(svetelnyLocPozSvetla, (float) poziceSvetla.getX(), (float) poziceSvetla.getY(), (float) poziceSvetla.getZ());

            svetelny.draw(GL2.GL_TRIANGLES, svetelnyShader);
        }
        
        textureViewer.view(texture, -1, -1, 0.5);

        String svetlo = "";

        switch (pozSvetla)
        {
            case 0:
                poziceSvetla = new Vec3D();
                svetlo = "vypnuto";
                break;
            case 1:
                poziceSvetla = new Vec3D(5, 5, 5);
                svetlo = "5,5,5";
                break;
            case 2:
                poziceSvetla = new Vec3D(-5, 5, 5);
                svetlo = "-5,5,5";
                break;
            case 3:
                poziceSvetla = new Vec3D(5, -5, 5);
                svetlo = "5,-5,5";
                break;
            case 4:
                poziceSvetla = new Vec3D(-5, -5, 5);
                svetlo = "-5,-5,5";
                break;
            case 5:
                poziceSvetla = new Vec3D(0, 0, 5);
                svetlo = "0,0,5";
                break;
        }
        if (vlastniSvetlo)
        {
            svetlo = vlastniPoziceSvetla.getX() + "," + vlastniPoziceSvetla.getY() + "," + vlastniPoziceSvetla.getZ();
            poziceSvetla = vlastniPoziceSvetla;
        } else if (zadavaniPozice > 0)
        {
            svetlo = "zadávání x: ";
            if (zadavaniPozice > 1)
            {
                svetlo += vlastniPoziceSvetla.getX() + ", y:";
            }
            if (zadavaniPozice > 2)
            {
                svetlo += vlastniPoziceSvetla.getY() + ", z:";
            }
            if (minus)
            {
                svetlo += "-";
            }
            svetlo += ";změna znaménka: [+]";
        }

        textUtils.vypisTextOvládání();
        textUtils.vypisTextBarvaBody(barva, pocetBodu);
        textUtils.vypisCopyright();
        textUtils.vypisSvetlo(svetlo, typSvetla);
        textUtils.vypisObjekty(kartez, sferic, cylindr);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
            int height)
    {
        this.width = width;
        this.height = height;
        proj = new Mat4PerspRH(Math.PI / 4, height / (double) width, 0.01, 1000.0);
        textUtils.setWidth(width);
        textUtils.setHeight(height);
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
        if (zadavaniPozice == 0)
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
                    if (kartez > 5)
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
                case KeyEvent.VK_5:
                case KeyEvent.VK_NUMPAD5:
                    if (pocetBodu < 100)
                    {
                        pocetBodu++;
                    }
                    pocetBoduZmenen = true;
                    break;
                case KeyEvent.VK_4:
                case KeyEvent.VK_NUMPAD4:
                    if (pocetBodu > 10)
                    {
                        pocetBodu--;
                    }
                    pocetBoduZmenen = true;
                    break;
                case KeyEvent.VK_6:
                case KeyEvent.VK_NUMPAD6:
                    pozSvetla++;
                    typSvetla = 1;
                    if (pozSvetla > 5)
                    {
                        pozSvetla = 0;
                        typSvetla = 0;
                    }
                    if (vlastniSvetlo)
                    {
                        vlastniSvetlo = false;
                        pozSvetla = 0;
                        vlastniPoziceSvetla = new Vec3D();
                    }
                    break;
                case KeyEvent.VK_L:
                    pozSvetla = 0;
                    typSvetla = 1;
                    vlastniSvetlo = false;
                    zadavaniPozice = 1;
                    vlastniPoziceSvetla = new Vec3D();
                case KeyEvent.VK_K:
                    if(typSvetla==1)
                    {
                        typSvetla++;
                    }
                    else if(typSvetla==2)
                    {
                        typSvetla--;
                    }
                    break;
            }
        } else
        {
            switch (e.getKeyCode())
            {
                case KeyEvent.VK_0:
                case KeyEvent.VK_NUMPAD0:
                    zadavaniPozice++;
                    break;
                case KeyEvent.VK_1:
                case KeyEvent.VK_NUMPAD1:
                    switch (zadavaniPozice)
                    {
                        case 1:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(-1, 0, 0));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(1, 0, 0));
                            }
                            zadavaniPozice++;
                            break;
                        case 2:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, -1, 0));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 1, 0));
                            }
                            zadavaniPozice++;
                            break;
                        case 3:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 0, -1));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 0, 1));
                            }
                            zadavaniPozice++;
                            break;
                    }
                    minus = false;
                    break;
                case KeyEvent.VK_2:
                case KeyEvent.VK_NUMPAD2:
                    switch (zadavaniPozice)
                    {
                        case 1:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(-2, 0, 0));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(2, 0, 0));
                            }
                            zadavaniPozice++;
                            break;
                        case 2:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, -2, 0));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 2, 0));
                            }
                            zadavaniPozice++;
                            break;
                        case 3:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 0, -2));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 0, 2));
                            }
                            zadavaniPozice++;
                            break;
                    }
                    minus = false;
                    break;
                case KeyEvent.VK_3:
                case KeyEvent.VK_NUMPAD3:
                    switch (zadavaniPozice)
                    {
                        case 1:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(-3, 0, 0));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(3, 0, 0));
                            }
                            zadavaniPozice++;
                            break;

                        case 2:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, -3, 0));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 3, 0));
                            }
                            zadavaniPozice++;
                            break;
                        case 3:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 0, -3));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 0, 3));
                            }
                            zadavaniPozice++;
                            break;
                    }
                    minus = false;
                    break;
                case KeyEvent.VK_4:
                case KeyEvent.VK_NUMPAD4:
                    switch (zadavaniPozice)
                    {
                        case 1:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(-4, 0, 0));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(4, 0, 0));
                            }
                            zadavaniPozice++;
                            break;
                        case 2:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, -4, 0));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 4, 0));
                            }
                            zadavaniPozice++;
                            break;
                        case 3:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 0, -4));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 0, 4));
                            }
                            zadavaniPozice++;
                            break;
                    }
                    minus = false;
                    break;
                case KeyEvent.VK_5:
                case KeyEvent.VK_NUMPAD5:
                    switch (zadavaniPozice)
                    {
                        case 1:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(-5, 0, 0));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(5, 0, 0));
                            }
                            zadavaniPozice++;
                            break;
                        case 2:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, -5, 0));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 5, 0));
                            }
                            zadavaniPozice++;
                            break;
                        case 3:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 0, -5));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 0, 5));
                            }
                            zadavaniPozice++;
                            break;
                    }
                    minus = false;
                    break;
                case KeyEvent.VK_6:
                case KeyEvent.VK_NUMPAD6:
                    switch (zadavaniPozice)
                    {
                        case 1:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(-6, 0, 0));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(6, 0, 0));
                            }
                            zadavaniPozice++;
                            break;
                        case 2:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, -6, 0));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 6, 0));
                            }
                            zadavaniPozice++;
                            break;
                        case 3:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 0, -6));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 0, 6));
                            }
                            zadavaniPozice++;
                            break;
                    }
                    minus = false;
                    break;
                case KeyEvent.VK_7:
                case KeyEvent.VK_NUMPAD7:
                    switch (zadavaniPozice)
                    {
                        case 1:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(-7, 0, 0));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(7, 0, 0));
                            }
                            zadavaniPozice++;
                            break;
                        case 2:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, -7, 0));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 7, 0));
                            }
                            zadavaniPozice++;
                            break;
                        case 3:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 0, -7));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 0, 7));
                            }
                            zadavaniPozice++;
                            break;
                    }
                    minus = false;
                    break;
                case KeyEvent.VK_8:
                case KeyEvent.VK_NUMPAD8:
                    switch (zadavaniPozice)
                    {
                        case 1:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(-8, 0, 0));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(8, 0, 0));
                            }
                            zadavaniPozice++;
                            break;
                        case 2:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, -8, 0));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 8, 0));
                            }
                            zadavaniPozice++;
                            break;
                        case 3:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 0, -8));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 0, 8));
                            }
                            zadavaniPozice++;
                            break;
                    }
                    minus = false;
                    break;
                case KeyEvent.VK_9:
                case KeyEvent.VK_NUMPAD9:
                    switch (zadavaniPozice)
                    {
                        case 1:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(9, 0, 0));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(9.0, 0, 0));
                            }
                            zadavaniPozice++;
                            break;
                        case 2:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, -9, 0));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 9.0, 0));
                            }
                            zadavaniPozice++;
                            break;
                        case 3:
                            if (minus)
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 0, -9));
                            } else
                            {
                                vlastniPoziceSvetla = vlastniPoziceSvetla.add(new Vec3D(0, 0, 9.0));
                            }
                            zadavaniPozice++;
                            break;
                    }
                    minus = false;
                    break;
                case KeyEvent.VK_PLUS:
                case KeyEvent.VK_ADD:
                    minus = !minus;
                    break;
            }
            if (zadavaniPozice > 3)
            {
                zadavaniPozice = 0;
                typSvetla = 1;
                vlastniSvetlo = true;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e
    )
    {
    }

    @Override
    public void keyTyped(KeyEvent e
    )
    {
    }

    @Override
    public void dispose(GLAutoDrawable glDrawable
    )
    {
        GL2 gl = glDrawable.getGL().getGL2();
        gl.glDeleteProgram(kartezskyShader);
    }

}
