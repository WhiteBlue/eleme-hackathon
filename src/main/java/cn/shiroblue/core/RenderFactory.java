package cn.shiroblue.core;

import cn.shiroblue.modules.DefaultRender;
import cn.shiroblue.modules.Render;

/**
 * Description:
 * <p>
 * ======================
 * by WhiteBlue
 * on 15/11/8
 */
public class RenderFactory {
    private static Render render = null;

    private RenderFactory() {
    }

    public static Render get() {
        if (render == null) {
            render = new DefaultRender();
        }
        return render;
    }

    public static void setDefaultRender(Render newRender) {
        render = newRender;
    }

}
