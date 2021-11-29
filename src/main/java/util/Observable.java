package util;

import java.util.ArrayList;
import java.util.List;

public class Observable {

    private boolean changed = false;
    private boolean paused = false;
    private List<Observer> observers;

    public Observable() {
        observers = new ArrayList<>();
    }

    public void pauseNotify() {
        paused = true;
    }

    public void resumeNotify() {
        paused = false;
        notifyObservers();
    }

    public void addObserver(Observer o) {
        if (o != null && !observers.contains(o)) {
            observers.add(o);
        }
    }

    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    public void notifyObservers() {
        if (!paused && hasChanged()) {
            System.out.println("call notify");
            clearChanged();
            for (Observer observer : observers) {
                observer.update();
            }
        }
    }

    public void setChanged() {
        changed = true;
    }

    public void clearChanged() {
        changed = false;
    }

    public boolean hasChanged() {
        return changed;
    }
}
