package ru.fly.client.ui.multipleupload.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author fil
 */
public class UploadCompleteEvent extends GwtEvent<UploadCompleteEvent.UploadCompleteHandler> {

    private static Type<UploadCompleteHandler> TYPE;

    private String statusText;

    public UploadCompleteEvent(String statusText) {
        this.statusText = statusText;
    }

    public static Type<UploadCompleteHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<UploadCompleteHandler>();
        }
        return TYPE;
    }

    @Override
    public Type<UploadCompleteHandler> getAssociatedType() {
        return getType();
    }

    @Override
    protected void dispatch(UploadCompleteHandler handler) {
        handler.onUploadComplete(statusText);
    }

    public interface UploadCompleteHandler extends EventHandler {
        void onUploadComplete(String statusText);
    }

    public interface HasUploadCompleteHandler extends EventHandler {
        HandlerRegistration addUploadCompleteHandler(UploadCompleteHandler h);
    }
}