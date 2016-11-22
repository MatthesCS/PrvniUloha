package meshutils;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL2;

import oglutils.OGLBuffers;
import oglutils.ToFloatArray;
import oglutils.ToIntArray;
import transforms.Vec2D;

public class MeshGenerator
{

    public static OGLBuffers createGrid(final GL2 gl, final int n, final int m, final String attribName)
    {
        int pocCtvercuX = n - 1;
        int pocCtvercuY = m - 1;
        List<Vec2D> vertex = new ArrayList<>();
        List<Integer> index = new ArrayList<>();

        for (int y = 0; y < n; y++)
        {
            for (int x = 0; x < m; x++)
            {
                float a = (float) x / pocCtvercuX;
                float b = (float) y / pocCtvercuY;

                vertex.add(new Vec2D(a, b));
            }
        }

        for (int y = 0; y < pocCtvercuY; y++)
        {
            for (int x = 0; x < pocCtvercuX; x++)
            {
                index.add(y * n + x);
                index.add(y * n + 1 + x);
                index.add((y + 1) * n + x);
                index.add(y * n + 1 + x);
                index.add((y + 1) * n + x);
                index.add((y + 1) * n + 1 + x);
            }
        }

        final OGLBuffers.Attrib[] attributes =
        {
            new OGLBuffers.Attrib(attribName, 2)
        };

        float[] vertexData = ToFloatArray.convert(vertex);
        int[] indexData = ToIntArray.convert(index);

        return new OGLBuffers(gl, vertexData, attributes, indexData);
    }

    public static OGLBuffers createGrid(final GL2 gl, final int n, final String attribName)
    {
        return createGrid(gl, n, n, attribName);
    }
}
