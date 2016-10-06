package ru.fly.client.ui.multipleupload.decor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author fil
 */
public class UploadDecor {

    public interface Resources extends ClientBundle {
        @Source("upload.css")
        Styles css();

        @Source("inbox_upload.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None)
        ImageResource upload16();
    }

    public interface Styles extends CssResource {
        String bar();

        String barBg();

        String progress();

        String label();
    }

    public final Resources res = GWT.create(Resources.class);

    public UploadDecor() {
        res.css().ensureInjected();
    }

    public Styles css() {
        return res.css();
    }


}
