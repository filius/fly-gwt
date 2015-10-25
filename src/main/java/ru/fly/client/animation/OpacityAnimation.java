package ru.fly.client.animation;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import ru.fly.client.EndCallback;

/**
 * User: fil
 * Date: 25.10.15
 */
public class OpacityAnimation extends Animation {

    private Element target;
    private double from;
    private double to;
    private EndCallback<Void> onComplete;

    public OpacityAnimation(Element target, EndCallback<Void> onComplete){
        this.target = target.cast();
        this.onComplete = onComplete;
    }

    public void animate(double to, int duration){
        String current = target.getStyle().getOpacity();
        this.from = current == null ? 0 : Double.valueOf(current);
        this.to = to;
        run(duration);
    }

    @Override
    protected void onComplete() {
        super.onComplete();
        if(onComplete != null) {
            onComplete.onEnd(null);
        }
    }

    @Override
    protected void onUpdate(double progress) {
        target.getStyle().setOpacity(from + ((to - from) * progress));
    }
}
