package ru.fly.client.ui.multipleupload.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;

/**
 * @author fil
 */
public class ProgressEvent extends GwtEvent<ProgressEvent.ProgressHandler> {

    private static Type<ProgressHandler> TYPE;

    private Event nativeEvent;

    public ProgressEvent(Event nativeEvent) {
        this.nativeEvent = nativeEvent;
    }

    public static Type<ProgressHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<ProgressHandler>();
        }
        return TYPE;
    }

    @Override
    public Type<ProgressHandler> getAssociatedType() {
        return getType();
    }

    @Override
    protected void dispatch(ProgressHandler handler) {
        handler.onProgress(nativeEvent);
    }

    public interface ProgressHandler extends EventHandler {
        void onProgress(Event nativeEvent);
    }

    public interface HasProgressHandler extends EventHandler {
        HandlerRegistration addProgressHandler(ProgressHandler h);
    }
}