package ru.fly.client.ui.panel.window.decor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import ru.fly.client.ui.CommonDecor;

/**
 * User: fil
 * Date: 09.01.14
 * Time: 19:59
 */
public class WindowDecor {

    public interface Resources extends ClientBundle {

        @Source({"ru/fly/client/ui/common.css", "window.css"})
        Styles css();

        @Source("cross.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
        ImageResource cross();

    }

    @CssResource.Shared
    public interface Styles extends CommonDecor.Styles {

        String window();

        String header();

        String inner();

        String modal();

        String close();

    }

    public final Resources res = GWT.create(Resources.class);

    public WindowDecor(){
        res.css().ensureInjected();
    }

    public Styles css(){
        return res.css();
    }

}
