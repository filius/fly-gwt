package ru.fly.client.ui.multipleupload;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.xhr.client.XMLHttpRequest;
import ru.fly.client.ui.Component;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.multipleupload.decor.UploadDecor;
import ru.fly.client.ui.multipleupload.event.AbortEvent;
import ru.fly.client.ui.multipleupload.event.ErrorEvent;
import ru.fly.client.ui.multipleupload.event.LoadEvent;
import ru.fly.client.ui.multipleupload.event.ProgressEvent;

/**
 * upload bar with embedded xhr upload logic
 *
 * @author fil
 */
public class UploadProgressItem extends Component {

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final XMLHttpRequest xhr;
    private String url;
    private JavaScriptObject file;
    private String fileName;

    public UploadProgressItem(String url, JavaScriptObject file) {
        super(DOM.createDiv());
        xhr = XMLHttpRequest.create();
        this.url = url;
        this.file = file;
        UploadDecor decor = GWT.create(UploadDecor.class);
        setStyleName(decor.css().progress());
        FElement label = DOM.createDiv().cast();
        label.setClassName(decor.css().label());
        fileName = getName(file);
        label.setInnerText(fileName);
        getElement().appendChild(label);
        FElement bar = DOM.createDiv().cast();
        bar.setClassName(decor.css().bar());
        getElement().appendChild(bar);
        final FElement barBg = DOM.createDiv().cast();
        barBg.setClassName(decor.css().barBg());
        bar.appendChild(barBg);

        setProgressHandler(new ProgressEvent.ProgressHandler() {
            @Override
            public void onProgress(Event nativeEvent) {
                barBg.getStyle().setWidth(getProgressPercent(nativeEvent), Style.Unit.PCT);
            }
        });
    }

    public void send() {
        send(url, file);
    }

    public String getFileName() {
        return fileName;
    }

    public native HandlerRegistration setAbortHandler(AbortEvent.AbortHandler h) /*-{
        this.@ru.fly.client.ui.multipleupload.UploadProgressItem::xhr.onabort = $entry(function (e) {
            h.@ru.fly.client.ui.multipleupload.event.AbortEvent.AbortHandler::onAbort(*)(e);
        });
    }-*/;

    public native HandlerRegistration setErrorHandler(ErrorEvent.ErrorHandler h) /*-{
        this.@ru.fly.client.ui.multipleupload.UploadProgressItem::xhr.onerror = $entry(function (e) {
            h.@ru.fly.client.ui.multipleupload.event.ErrorEvent.ErrorHandler::onError(*)(e);
        });
    }-*/;

    public native HandlerRegistration setLoadHandler(LoadEvent.LoadHandler h) /*-{
        this.@ru.fly.client.ui.multipleupload.UploadProgressItem::xhr.onload = $entry(function (e) {
            h.@ru.fly.client.ui.multipleupload.event.LoadEvent.LoadHandler::onLoad(*)(e);
        });
    }-*/;

    public native String getResponseText() /*-{
        return this.@ru.fly.client.ui.multipleupload.UploadProgressItem::xhr.responseText;
    }-*/;

    // ------------- privates -------------

    private native void send(String url, JavaScriptObject file)/*-{
        var formData = new FormData();
        formData.append("fileUpload", file);
        this.@ru.fly.client.ui.multipleupload.UploadProgressItem::xhr.open("POST", url);
        this.@ru.fly.client.ui.multipleupload.UploadProgressItem::xhr.send(formData);
    }-*/;

    private native HandlerRegistration setProgressHandler(ProgressEvent.ProgressHandler h) /*-{
        this.@ru.fly.client.ui.multipleupload.UploadProgressItem::xhr.upload.onprogress = $entry(function (e) {
            h.@ru.fly.client.ui.multipleupload.event.ProgressEvent.ProgressHandler::onProgress(*)(e);
        });
    }-*/;

    private native String getName(JavaScriptObject file) /*-{
        return file.name;
    }-*/;

    private native float getProgressPercent(Event e) /*-{
        return e.loaded / e.total * 100;
    }-*/;
}
