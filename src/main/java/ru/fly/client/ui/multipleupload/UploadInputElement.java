package ru.fly.client.ui.multipleupload;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import ru.fly.client.F;
import ru.fly.client.ui.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fil
 */
public class UploadInputElement extends Component implements HasChangeHandlers {

    public UploadInputElement() {
        super(Document.get().createFileInputElement());
        setVisible(false);
        getInputElement().setName("files[]");
        getInputElement().setAttribute("multiple", "multiple");
        DOM.sinkEvents(getElement(), Event.ONCHANGE);
    }

    public void click() {
        getInputElement().click();
    }

    public void clear() {
        getInputElement().setValue(null);
    }

    public List<UploadProgressItem> sendTo(String url) {
        List<UploadProgressItem> ret = new ArrayList<>();
        for (JavaScriptObject file : getFiles(getElement())) {
            ret.add(new UploadProgressItem(url + "&uid=" + F.getUID(), file));
        }
        return ret;
    }

    @Override
    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        return addHandler(handler, ChangeEvent.getType());
    }

    // ---------------- privates --------------

    private InputElement getInputElement() {
        return getElement().cast();
    }

    private native JavaScriptObject[] getFiles(Element element) /*-{
        return element.files;
    }-*/;

}
