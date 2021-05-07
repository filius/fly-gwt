package ru.fly.client.ui.multipleupload.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;

/**
 * @author fil
 */
public class LoadEvent extends GwtEvent<LoadEvent.LoadHandler> {

    private static Type<LoadHandler> TYPE;

    private Event nativeEvent;

    public LoadEvent(Event nativeEvent) {
        this.nativeEvent = nativeEvent;
    }

    public static Type<LoadHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<LoadHandler>();
        }
        return TYPE;
    }

    @Override
    public Type<LoadHandler> getAssociatedType() {
        return getType();
    }

    @Override
    protected void dispatch(LoadHandler handler) {
        handler.onLoad(nativeEvent);
    }

    public interface LoadHandler extends EventHandler {
        void onLoad(Event nativeEvent);
    }

    public interface HasLoadHandler extends EventHandler {
        HandlerRegistration addLoadHandler(LoadHandler h);
    }
}