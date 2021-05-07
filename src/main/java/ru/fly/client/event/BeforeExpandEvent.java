package ru.fly.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author fil
 */
public class BeforeExpandEvent<T> extends GwtEvent<BeforeExpandEvent.BeforeExpandHandler<T>> {

    private static Type TYPE;

    private T object;

    public BeforeExpandEvent(T object) {
        this.object = object;
    }

    @SuppressWarnings("unchecked")
    public static <T> Type<BeforeExpandHandler<T>> getType() {
        if (TYPE == null) {
            TYPE = new Type<BeforeExpandHandler<T>>();
        }
        return TYPE;
    }

    @Override
    public Type<BeforeExpandHandler<T>> getAssociatedType() {
        return getType();
    }

    @Override
    protected void dispatch(BeforeExpandHandler<T> handler) {
        handler.onBeforeExpand(object);
    }

    public interface BeforeExpandHandler<T> extends EventHandler {
        void onBeforeExpand(T object);
    }

    public interface HasBeforeExpandHandler<T> {
        HandlerRegistration addBeforeExpandHandler(BeforeExpandHandler<T> h);
    }
}
