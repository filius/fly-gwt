package ru.fly.client.animation;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import ru.fly.client.EndCallback;

/**
 * User: fil
 * Date: 25.10.15
 */
public class PositionAnimation extends Animation {

    private final Element target;
    private final String side;
    private double from;
    private int to;
    private EndCallback<Void> onComplete;

    public PositionAnimation(Element target, String side, EndCallback<Void> onComplete){
        this.target = target;
        this.side = side;
        this.onComplete = onComplete;
    }

    public void animate(int to, int duration){
        String fromString = target.getStyle().getProperty(side);
        if(fromString == null) {
            this.from = 0;
        }else{
            fromString = fromString.toLowerCase().replace("px", "");
            this.from = Double.valueOf(fromString);
        }
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
        target.getStyle().setProperty(side, from + ((to - from) * progress), Style.Unit.PX);
    }

}
